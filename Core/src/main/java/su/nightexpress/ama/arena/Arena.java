package su.nightexpress.ama.arena;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.EffectUtil;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.type.*;
import su.nightexpress.ama.api.event.*;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.region.ArenaRegionManager;
import su.nightexpress.ama.arena.region.ArenaRegionWave;
import su.nightexpress.ama.arena.wave.ArenaWaveManager;
import su.nightexpress.ama.arena.wave.ArenaWaveMob;
import su.nightexpress.ama.arena.wave.ArenaWaveUpcoming;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.kit.Kit;
import su.nightexpress.ama.kit.KitManager;
import su.nightexpress.ama.stats.object.StatType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arena extends AbstractArena {

	public Arena(@NotNull ArenaConfig config)  {
		super(config);
    }

	@Override
	protected void onArenaStop(@NotNull ArenaEndType type) {
		if (this.getState() == ArenaState.INGAME && this.getConfig().getGameplayManager().isAnnouncesEnabled()) {
			this.plugin.getMessage(Lang.Arena_Game_Announce_End).replace(this.replacePlaceholders()).broadcast();
		}
		this.getPlayers().forEach(arenaPlayer -> this.leaveArena(arenaPlayer, type.getReason()));
	}

	public boolean canJoin(@NotNull Player player, boolean isMessage) {
		// Check if arena is enabled.
		ArenaConfig config = this.getConfig();
		if (!config.isActive() || config.hasProblems()) {
			if (isMessage) plugin.getMessage(Lang.ARENA_ERROR_DISABLED).replace(this.replacePlaceholders()).send(player);
			return false;
		}

		// Check if arena is in-game.
		if (this.getState() == ArenaState.INGAME && !player.hasPermission(Perms.BYPASS_ARENA_JOIN_INGAME)) {
			if (isMessage) plugin.getMessage(Lang.ARENA_JOIN_ERROR_STARTED).replace(this.replacePlaceholders()).send(player);
			return false;
		}

		// Check for max. players.
		int playerMax = config.getGameplayManager().getPlayerMaxAmount();
		if (playerMax > 0 && this.getPlayers().size() >= playerMax) {
			if (isMessage) plugin.getMessage(Lang.ARENA_JOIN_ERROR_MAXIMUM).send(player);
			return false;
		}

		// Check for permission.
		// TODO Bypass permission
		if (this.getConfig().isPermissionRequired() && !this.hasPermission(player)) {
			if (isMessage) plugin.getMessage(Lang.ARENA_JOIN_ERROR_PERMISSION).send(player);
			return false;
		}

		// Check for money.
		// TODO Bypass permission
		if (!this.getConfig().getJoinPaymentRequirements().entrySet().stream().allMatch(entry -> entry.getKey().getBalance(player) >= entry.getValue())) {
			if (isMessage) plugin.getMessage(Lang.ARENA_JOIN_ERROR_PAYMENT).replace(this.replacePlaceholders()).send(player);
			return false;
		}

		return true;
	}

	public boolean joinLobby(@NotNull Player player) {
		if (ArenaPlayer.isPlaying(player)) {
			this.plugin.getMessage(Lang.ARENA_JOIN_ERROR_IN_GAME).send(player);
			return false;
		}
		if (!this.canJoin(player, true)) {
			return false;
		}

		// Call custom plugin event
		ArenaPlayerJoinEvent event = new ArenaPlayerJoinEvent(this, player);
		plugin.getPluginManager().callEvent(event);
		if (event.isCancelled()) return false;

		// Create an ArenaPlayer instance.
		ArenaPlayer arenaPlayer = ArenaPlayer.create(player, this);

		// Save the player inventory, effects, game modes, etc.
		// before teleporting to the arena.
		PlayerSnapshot.doSnapshot(player);

		player.teleport(this.getConfig().getLocation(ArenaLocationType.LOBBY));
		EffectUtil.playEffect(player.getLocation(), Particle.CLOUD, "", 0.1f, 0.25f, 0.1f, 0.15f, 30);

		// Now clear all player's active effects, god modes, etc.
		PlayerSnapshot.clear(player);

		// Adding lobby items
		if (this.getConfig().getGameplayManager().isKitsEnabled()) {
			player.getInventory().clear();

			if (plugin().getKitManager().isSavePurchasedKits()) {
				LobbyItem.give(LobbyItem.Type.KIT_SELECT, player);
			}
			LobbyItem.give(LobbyItem.Type.KIT_SHOP, player);
			LobbyItem.give(LobbyItem.Type.EXIT, player);
			LobbyItem.give(LobbyItem.Type.STATS, player);
			if (!arenaPlayer.isLateJoined()) {
				LobbyItem.give(LobbyItem.Type.READY, player);
			}
		}

		// Send messages
		plugin().getMessage(Lang.Arena_Game_Lobby_Enter).replace(this.replacePlaceholders()).send(player);

		this.getPlayers().forEach(lobbyPlayer -> {
			plugin().getMessage(Lang.Arena_Game_Lobby_Joined).replace(arenaPlayer.replacePlaceholders()).send(lobbyPlayer.getPlayer());
		});

		this.addPlayer(arenaPlayer);
		this.getConfig().payJoinRequirements(this, player);
		this.updateSigns();

		// Prepare to start
		if (this.getState() == ArenaState.INGAME) return true;

		int minPlayers = this.getConfig().getGameplayManager().getPlayerMinAmount();
		if (this.getPlayers().size() < minPlayers) {
			this.getPlayers().forEach(lobbyPlayer -> {
				plugin().getMessage(Lang.Arena_Game_Lobby_MinPlayers).replace("%min%", minPlayers).send(lobbyPlayer.getPlayer());
			});
		}
		return true;
	}

	//@Override
	private void joinGame(@NotNull ArenaPlayer arenaPlayer) {
		// Check if player's kit is valid and kick from the arena if it's not.
		// Do not kick players who joined after the game start, so they can select their kit
		// as long as they want.
		if (!this.validateJoinKit(arenaPlayer, !arenaPlayer.isLateJoined())) return;

		ArenaRegionManager reg = this.getConfig().getRegionManager();
		ArenaRegion regionDefault = arenaPlayer.isLateJoined() ? reg.getRegionAnyAvailable() : reg.getRegionDefault();

		// Check for valid arena's region.
		if (regionDefault == null) {
			if (!arenaPlayer.isLateJoined()) {
				arenaPlayer.leaveArena(LeaveReason.NO_REGION);
			}
			return;
		}

		Player player = arenaPlayer.getPlayer();
		player.teleport(regionDefault.getSpawnLocation());

		// Restore player's health before the game.
		player.setHealth(EntityUtil.getAttribute(player, Attribute.GENERIC_MAX_HEALTH));

		// Add arena scoreboard.
		arenaPlayer.addBoard();
		arenaPlayer.setLateJoined(false);

		plugin().getMessage(Lang.Arena_Game_Notify_Start).send(player);
	}

	@Override
	public boolean joinSpectate(@NotNull Player player) {
		if (ArenaPlayer.isPlaying(player)) {
			plugin.getMessage(Lang.ARENA_JOIN_ERROR_IN_GAME).send(player);
			return false;
		}

		// Check if arena is active and setup.
		if (!this.getConfig().isActive() || this.getConfig().hasProblems()) {
			plugin().getMessage(Lang.ARENA_ERROR_DISABLED).replace(this.replacePlaceholders()).send(player);
			return false;
		}

		// Check for player permissions.
		if (!this.hasPermission(player)) {
			plugin().getMessage(Lang.ARENA_JOIN_ERROR_PERMISSION).send(player);
			return false;
		}

		// Check if spectating is enabled.
		if (!this.getConfig().getGameplayManager().isSpectateEnabled()) {
			plugin().getMessage(Lang.ARENA_JOIN_SPECTATE_ERROR_DISABLED).send(player);
			return false;
		}

		ArenaPlayerSpectateEvent spectateEvent = new ArenaPlayerSpectateEvent(this, player);
		plugin().getPluginManager().callEvent(spectateEvent);
		if (spectateEvent.isCancelled()) return false;

		player.teleport(this.getConfig().getLocation(ArenaLocationType.SPECTATE));
		plugin().getMessage(Lang.ARENA_JOIN_SPECTATE_SUCCESS).replace(this.replacePlaceholders()).send(player);

		return true;
	}

	@Override
	public boolean leaveArena(@NotNull ArenaPlayer arenaPlayer, @NotNull LeaveReason reason) {
		if (!arenaPlayer.getArena().equals(this)) {
			return false;
		}

		Player player = arenaPlayer.getPlayer();
		player.closeInventory(); 		 		// In case if player have opened any arena GUIs.

		arenaPlayer.removeBoard(); 				// Remove scoreboard.
		arenaPlayer.removeMobHealthBars();	 	// Remove boss bars.
		this.removePlayer(arenaPlayer);		// Remove player from the arena.

		if (reason == LeaveReason.SELF && this.isLatestWave() && this.getMobs().isEmpty()) {
			reason = LeaveReason.FINISH;
		}

		if (this.getState() == ArenaState.INGAME) {
			if (reason == LeaveReason.FINISH) {
				arenaPlayer.addStats(StatType.GAMES_WON, 1);
			}
			else {
				if (reason == LeaveReason.DEATH) {
					if (!this.getConfig().getRewardManager().isRetainOnDeath()) {
						arenaPlayer.getRewards().clear();
					}
				}
				else if (reason != LeaveReason.TIMELEFT) {
					if (!this.getConfig().getRewardManager().isRetainOnLeave()) {
						arenaPlayer.getRewards().clear();
					}
				}
				arenaPlayer.addStats(StatType.GAMES_LOST, 1);
			}
			if (this.getWaveNumber() > 0) {
				arenaPlayer.addStats(StatType.GAMES_PLAYED, 1);
			}
		}

		// Restore player data before the game.
		PlayerSnapshot.restore(arenaPlayer);

		// Save stats, give rewards.
		arenaPlayer.saveStats();
		arenaPlayer.getRewards().forEach(reward -> reward.give(player));

		ArenaPlayer.remove(player);
		this.plugin.getMessage(Lang.getLeaveReason(reason)).send(player);

		ArenaPlayerLeaveEvent event = new ArenaPlayerLeaveEvent(this, arenaPlayer);
		plugin.getPluginManager().callEvent(event);

		return true;
	}

	private boolean validateJoinKit(@NotNull ArenaPlayer arenaPlayer, boolean kick) {
		if (!this.getConfig().getGameplayManager().isKitsEnabled()) return true;

		Kit kit = arenaPlayer.getKit();
		if (kit == null) {
			KitManager kitManager = plugin.getKitManager();

			// If kits are saved to account, then try to select random obtained kit.
			if (kitManager.isSavePurchasedKits()) {
				ArenaUser user = plugin.getUserManager().getUserData(arenaPlayer.getPlayer());
				String userKit = Rnd.get(new ArrayList<>(user.getKits()));
				kit = userKit == null ? null : kitManager.getKitById(userKit);
			}

			// If kits are not saved to account or user don't obtain any kit
			// then try to give the default kit by kit settings.
			if (kit == null || !kit.isAvailable(arenaPlayer, false)) {
				kit = plugin.getKitManager().getDefaultKit();
			}

			// If even default kit was fail, then it's unlucky game for this user,
			// he will be kicked from the arena.
			if (kit == null) {
				if (kick) arenaPlayer.leaveArena(LeaveReason.NO_KIT);
				return false;
			}
		}

		arenaPlayer.setKit(kit);
		kit.give(arenaPlayer);
		return true;
	}


	@Override
	public void tickLobby() {
		if (this.getState() == ArenaState.INGAME) return;

		if (this.getState() == ArenaState.WAITING) {
			if (this.getPlayers().size() >= this.getConfig().getGameplayManager().getPlayerMinAmount()) {
				this.setState(ArenaState.READY);
				this.updateGameEventListeners();

				if (this.getConfig().getGameplayManager().isAnnouncesEnabled()) {
					plugin().getMessage(Lang.Arena_Game_Announce_Start)
							.replace(this.replacePlaceholders())
							.replace("%time%", this.getConfig().getGameplayManager().getLobbyTime())
							.broadcast();
				}
			}
		}

		if (this.getState() != ArenaState.READY) return;

		if (this.getPlayers().size() < this.getConfig().getGameplayManager().getPlayerMinAmount()) {
			this.setState(ArenaState.WAITING);
			this.setLobbyTimeleft(this.getConfig().getGameplayManager().getLobbyTime());
			this.updateSigns();
			return;
		}

		int lobbyTimeleft = this.getLobbyTimeleft();
		boolean allReady = this.getPlayers().stream().allMatch(ArenaPlayer::isReady);
		if (Config.LOBBY_READY_DROP_TIMER.get() > 0) {
			if (allReady) {
				if (lobbyTimeleft > Config.LOBBY_READY_DROP_TIMER.get()) {
					lobbyTimeleft = Config.LOBBY_READY_DROP_TIMER.get();
				}
			}
			else {
				if (Config.LOBBY_READY_FREEZE_TIMER_WHEN_DROPPED.get() && lobbyTimeleft > 0
					&& lobbyTimeleft <= Config.LOBBY_READY_DROP_TIMER.get()) {
					return;
				}
			}
		}

		if (lobbyTimeleft <= 0) {
			this.getPlayers().forEach(this::joinGame);
			this.setState(ArenaState.INGAME);

			ArenaGameStartEvent event = new ArenaGameStartEvent(this);
			plugin().getPluginManager().callEvent(event);
			return;
		}

		if (lobbyTimeleft % 15 == 0 || lobbyTimeleft % 10 == 0 || lobbyTimeleft <= 10) {
			for (ArenaPlayer arenaPlayer : this.getPlayers()) {
				plugin().getMessage(Lang.Arena_Game_Lobby_Timer).replace(Placeholders.GENERIC_TIME, lobbyTimeleft).send(arenaPlayer.getPlayer());
			}
		}
		this.setLobbyTimeleft(lobbyTimeleft-1);
	}

	@Override
	public void tickGame() {
		if (this.getState() != ArenaState.INGAME) return;

		// Time is ended, Game Over.
		if (this.getGameTimeleft() > 0) {
			this.setGameTimeleft(Math.max(0L, this.getGameTimeleft() - 1000L));
			if (this.getGameTimeleft() <= 0L) {
				this.stop(ArenaEndType.TIMELEFT);
				return;
			}
		}

		this.tickPlayers();
		this.tickMobs();

		// No players left, stop the game.
		if (this.getPlayersIngame().isEmpty()) {
			this.stop(ArenaEndType.FORCE);
			return;
		}

		this.plugin.runTask(c -> this.showWaveStatus(), true);

		if (this.getWaveNextTimeleft() == 0) {
			this.newWave();

			// Stop game if no regions are available.
			ArenaRegion playRegion = this.getConfig().getRegionManager().getRegionAnyAvailable();
			if (playRegion == null) {
				this.stop(ArenaEndType.NO_REGION);
				return;
			}

			// Move mobs from locked regions to the unlocked and active.
			List<LivingEntity> allMobs = new ArrayList<>(this.getMobs());
			allMobs.forEach(mob -> {
				ArenaRegion region = this.getConfig().getRegionManager().getRegion(mob.getLocation());
				if (region != null && region.getState() == ArenaLockState.LOCKED) {
					mob.teleport(playRegion.getSpawnLocation());
				}
			});
			return;
		}

		if (this.isNextWaveAllowed()) {
			if (this.getWaveNextTimeleft() == this.getConfig().getWaveManager().getDelayDefault()) {

				ArenaWaveCompleteEvent event = new ArenaWaveCompleteEvent(this);
				plugin().getPluginManager().callEvent(event);

				if (this.isLatestWave()) {
					this.getPlayers().stream().map(ArenaPlayer::getPlayer).forEach(player -> {
						plugin().getMessage(Lang.Arena_Game_Wave_Latest).send(player);
					});
				}
			}
			this.setWaveNextTimeleft(this.getWaveNextTimeleft() - 1);
		}
	}

	protected void tickPlayers() {
		for (ArenaPlayer arenaPlayer : this.getPlayersIngame()) {
			arenaPlayer.tick();

			// Notify if player region is inactive anymore.
			if (this.isNextWaveAllowed()) {
				ArenaRegion region = arenaPlayer.getRegion(false);
				if (region == null || region.getState() == ArenaLockState.LOCKED) {
					plugin.getMessage(Lang.ARENA_REGION_LOCKED_NOTIFY).send(arenaPlayer.getPlayer());
				}
			}
		}
	}

	@Override
	public void onArenaGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
		super.onArenaGameEvent(gameEvent);

		if (gameEvent.getEventType() == ArenaGameEventType.REGION_UNLOCKED) {
			ArenaRegionEvent regionEvent = (ArenaRegionEvent) gameEvent;
			this.broadcast(plugin.getMessage(Lang.ARENA_REGION_UNLOCKED_NOTIFY)
				.replace(regionEvent.getArenaRegion().replacePlaceholders()), ArenaTargetType.PLAYER_ALL);
			return;
		}

		if (gameEvent.getEventType() == ArenaGameEventType.MOB_KILLED) {

			if (gameEvent instanceof ArenaMobDeathEvent mobDeathEvent) {
				this.getMobs().remove(mobDeathEvent.getEntity());
			}

			this.countGradual();
		}
	}

	private void countGradual() {
		if (this.getConfig().getWaveManager().isGradualSpawnEnabled()) {
			this.setGradualMobsKilled(this.getGradualMobsKilled() + 1);

			boolean allSpawned = this.getUpcomingWaves().stream().allMatch(ArenaWaveUpcoming::isAllMobsSpawned);
			if (allSpawned) return;

			double lastSpawned = this.getWaveMobsTotalAmount();
			double killedNextPc = this.getConfig().getWaveManager().getGradualSpawnNextKillPercent();
			int killedRaw = this.getGradualMobsKilled();
			int killedNeedRaw = (int) Math.max(1D, lastSpawned * killedNextPc / 100D);

			if (Config.DEBUG_MOB_SPAWN.get()) {
				System.out.println("Game Event: Mob Killed | Gradual Spawning");
				System.out.println("[Gradual] Total Mobs (All Waves): " + lastSpawned);
				System.out.println("[Gradual] Killed (Raw): " + killedRaw);
				System.out.println("[Gradual] Need Killed (Raw): " + killedNeedRaw);
			}
			boolean isEmpty = this.getMobs().isEmpty() && this.gradualMobsPrepare == 0;

			if (killedRaw >= killedNeedRaw || isEmpty) {
				this.gradualMobsPrepare++;
				this.setGradualMobsKilled(0);
				if (Config.DEBUG_MOB_SPAWN.get()) System.out.println("[Gradual] Prepared Groups: " + gradualMobsPrepare);
			}
		}
	}

	protected void tickMobs() {
		if (this.getConfig().getWaveManager().isGradualSpawnEnabled()) {
			if (this.gradualMobsTimer++ % this.getConfig().getWaveManager().getGradualSpawnNextInterval() == 0) {
				this.gradualMobsTimer = 0;

				if (this.gradualMobsPrepare > 0) {
					double nextPc = this.getConfig().getWaveManager().getGradualSpawnNextPercent();
					this.spawnMobs(nextPc);
					this.gradualMobsPrepare--;
				}
			}
		}

		this.getMobs().removeIf(mob -> {
			if (!mob.isValid() || mob.isDead()) {
				this.countGradual();
				return true;
			}
			return false;
		});
		this.getMobs().forEach(mob -> this.updateMobTarget(mob, false));

		// Mobs Highlight feature.
		if (this.getConfig().getGameplayManager().isMobHighlightEnabled()) {
			double mobsLeft = this.getMobs().size();
			double mobsTotal = this.getWaveMobsTotalAmount();
			double mobsPercent = (mobsLeft / mobsTotal) * 100D;
			this.highlightMobs(mobsPercent <= this.getConfig().getGameplayManager().getMobHighlightAmount());
		}
	}

	@Override
	public void spawnMobs(double spawnPercent) {
		this.mobsAboutToSpawn = true;

		if (spawnPercent == 0D) spawnPercent = 100D;
		spawnPercent /= 100D;

		if (Config.DEBUG_MOB_SPAWN.get()) System.out.println("[Spawn Processor] 0. Percent of Total Mobs: " + spawnPercent);

		// Готовим список волн для спавна мобов.
		List<ArenaWaveUpcoming> upcomings = new ArrayList<>(this.getUpcomingWaves());

		// Счетчик количества мобов для спавна для каждой волны региона.
		int[] mobsSpawnPerWave = new int[upcomings.size()];

		// Спавним как минимум одного моба всегда.
		int mobsPlannedTotal = (int) Math.max(1D, (double) this.getWaveMobsTotalAmount() * spawnPercent);

		// Спавним "поровну" мобов от каждой волны.
		// Например: При 30% спавна от 100 мобов (х30) и 3-х волнах с кол-вом мобов [5,15,30] (x50) = [3,10,17]
		// 100% of 100 with wave mobs [30,30,40] = x100, 30/30/40 * 1.0 = 30/30/40 = 100 (100%).
		// 70% of 10 with wave mobs [2,2,6] = x10, 2/2/6 * 0.7 = ~1/~1/~5 = 7 (70%).
		for (int counter = 0; counter < mobsSpawnPerWave.length; counter++) {
			ArenaWaveUpcoming wave = upcomings.get(counter);

			double mobsWaveTotal = wave.getPreparedMobs().stream().mapToInt(ArenaWaveMob::getAmount).sum();
			//double mobsWaveSpawned = wave.getMobsSpawnedAmount().values().stream().mapToInt(i -> i).sum();
			if (Config.DEBUG_MOB_SPAWN.get()) System.out.println("[Spawn Processor] 2. Total Mobs for Wave '" + wave.getRegionWave().getId() + "': " + mobsWaveTotal);
			//System.out.println(wave.getRegionWave().getId() + " mobs have spawned: " + mobsWaveSpawned);

			//mobsWaveTotal -= mobsWaveSpawned;

			mobsSpawnPerWave[counter] = (int) Math.ceil(mobsWaveTotal * spawnPercent);
			if (Arrays.stream(mobsSpawnPerWave).sum() >= mobsPlannedTotal) break;
		}

		if (Config.DEBUG_MOB_SPAWN.get()) System.out.println("[Spawn Processor] 3. Mobs Per Each Wave:" + Arrays.toString(mobsSpawnPerWave));

		// Подгоняем количество мобов для спавна на случай небольших расхождений при счете с процентами.
		int mobsSpawnTotal = Arrays.stream(mobsSpawnPerWave).sum();
		if (mobsSpawnTotal != mobsPlannedTotal) {
			int dif = mobsPlannedTotal - mobsSpawnTotal;
			int[] fineParts = NumberUtil.splitIntoParts(dif, mobsSpawnPerWave.length);

			for (int counter = 0; counter < mobsSpawnPerWave.length; counter++) {
				mobsSpawnPerWave[counter] += fineParts[counter];
			}
		}

		// Подгоняем количество мобов дубль-два xD
		// На случай если кол-ов мобов в массиве больше, чем кол-во мобов в соответствующей ему волне.
		// В таком случае разница прибавляется к другим волнам, где кол-во наоборот больше, чем в массиве.
		int finer = 0;
		for (int counter = 0; counter < mobsSpawnPerWave.length; counter++) {
			ArenaWaveUpcoming wave = upcomings.get(counter);
			int waveToSpawn = mobsSpawnPerWave[counter];
			int waveLeft = wave.getPreparedMobs().stream().mapToInt(ArenaWaveMob::getAmount).sum();
			if (waveToSpawn > waveLeft) {
				finer += (waveToSpawn - waveLeft);
				mobsSpawnPerWave[counter] = waveLeft;
			}
			else if (waveLeft > waveToSpawn && finer > 0) {
				int diff = waveLeft - waveToSpawn;
				finer -= diff;
				mobsSpawnPerWave[counter] += diff;
			}
		}

		if (Config.DEBUG_MOB_SPAWN.get()) {
			System.out.println("[Spawn Processor] 4. Planned For Whole Arena Round:" + mobsPlannedTotal);
			System.out.println("[Spawn Processor] 5. Calculated to Spawn:" + mobsSpawnTotal);
			System.out.println("[Spawn Processor] 6. Mobs Per Each Wave:" + Arrays.toString(mobsSpawnPerWave));
		}


		for (int counterWave = 0; counterWave < mobsSpawnPerWave.length; counterWave++) {
			ArenaWaveUpcoming waveUpcoming = upcomings.get(counterWave);
			ArenaRegionWave regionWave = waveUpcoming.getRegionWave();
			List<Location> spawners = waveUpcoming.getPreparedSpawners();
			int mobsWave = mobsSpawnPerWave[counterWave];

			int[] mobsPerSpawner = NumberUtil.splitIntoParts(mobsWave, spawners.size());
			if (Config.DEBUG_MOB_SPAWN.get()) System.out.println("[Spawn Processor] 7. Mobs Per Region Spawner for '" + regionWave.getArenaWaveIds() + "': " + Arrays.toString(mobsPerSpawner));

			for (int counterSpawner = 0; counterSpawner < mobsPerSpawner.length; counterSpawner++) {
				int mobsSpawner = mobsPerSpawner[counterSpawner];

				for (int countSpawned = 0; countSpawned < mobsSpawner; countSpawned++) {
					ArenaWaveMob waveMob = Rnd.get(waveUpcoming.getPreparedMobs());
					if (waveMob == null) {
						if (Config.DEBUG_MOB_SPAWN.get()) System.out.println("Invalid mob");
						continue;
					}

					int mobsSpawned = Math.min(waveMob.getAmount(), /*mobsSpawner*/ 1);
					waveMob.setAmount(waveMob.getAmount() - mobsSpawned);

					for (int s = 0; s < mobsSpawned; s++) {
						plugin.getMobManager().spawnMob(this, waveMob, spawners.get(counterSpawner));
					}

					if (waveMob.getAmount() <= 0) {
						waveUpcoming.getPreparedMobs().remove(waveMob);
					}
				}
			}
		}

		this.getUpcomingWaves().removeIf(ArenaWaveUpcoming::isAllMobsSpawned);
		this.mobsAboutToSpawn = false;
	}

	@Override
	public void newWave() {
		this.getUpcomingWaves().clear();
		this.gradualMobsTimer = 0;
		this.killMobs();

		if (this.isLatestWave()) {
			this.stop(ArenaEndType.FINISH);
			return;
		}

		this.setWaveNumber(this.getWaveNumber() + 1);

		// Move all players that are outside of the active region
		// to the first active one.
		ArenaRegion regionActive = this.getConfig().getRegionManager().getRegionAnyAvailable();
		this.getPlayersIngame().forEach(arenaPlayer -> {
			arenaPlayer.addStats(StatType.WAVES_PASSED, 1);

			ArenaRegion regionPlayer = arenaPlayer.getRegion(false);
			if ((regionPlayer == null || regionPlayer.getState() == ArenaLockState.LOCKED) && regionActive != null) {
				arenaPlayer.getPlayer().teleport(regionActive.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
			}
		});

		ArenaWaveStartEvent event = new ArenaWaveStartEvent(this);
		plugin().getPluginManager().callEvent(event);

		// Join all late joined players at the start of the new round.
		this.getPlayersLate().forEach(this::joinGame);

		// Set time until next wave
		this.setWaveNextTimeleft(this.getConfig().getWaveManager().getDelayDefault());

		//this.setSpawnedMobsAmount(0);
		this.setGradualMobsKilled(0);

		// New wave is started, store the complete amount of mobs from all upcoming waves.
		// This value is TOTAL amount of mobs that arena is about to spawn this round.
		this.setWaveMobsTotalAmount(this.getMobsAmountLeft());
		/*this.setWaveMobsTotalAmount(this.getUpcomingWaves().stream()
				.filter(Predicate.not(ArenaWaveUpcoming::isAllMobsSpawned))
				.mapToInt(wave -> wave.getPreparedMobs().stream().mapToInt(ArenaWaveMob::getAmount).sum()).sum());*/

		// Spawn mobs for new wave
		ArenaWaveManager waveManager = this.getConfig().getWaveManager();
		this.spawnMobs(waveManager.isGradualSpawnEnabled() ? waveManager.getGradualSpawnPercentFirst() : 100D);

		//this.setLastBossAmount(this.getBosses().size());

		this.getPlayers().stream().map(ArenaPlayer::getPlayer).forEach(player -> {
			plugin().getMessage(Lang.Arena_Game_Wave_Start).replace(this.replacePlaceholders()).send(player);
		});
	}

	protected void showWaveStatus() {
		LangMessage label;
		if (this.isNextWaveAllowed()) {
			label = this.isLatestWave() ? plugin.getMessage(Lang.Arena_Game_Wave_TimerEnd) : plugin.getMessage(Lang.Arena_Game_Wave_Timer);
		}
		else {
			label = plugin.getMessage(Lang.Arena_Game_Wave_Progress);
		}

		this.getPlayers().forEach(arenaPlayer -> label.replace(this.replacePlaceholders()).send(arenaPlayer.getPlayer()));
	}

	@Override
	public void updateMobTarget(@NotNull LivingEntity entity, boolean force) {
		LivingEntity target = plugin.getArenaNMS().getTarget(entity);
		if (target instanceof Player || (!force && target != null)) return;

		ArenaPlayer arenaPlayer = this.getPlayerRandom();
		plugin().getArenaNMS().setTarget(entity, arenaPlayer == null ? null : arenaPlayer.getPlayer());
	}
}
