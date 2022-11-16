package su.nightexpress.ama.arena.listener;

import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.api.manager.AbstractListener;
import su.nexmedia.engine.utils.ArrayUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.api.arena.type.ArenaLockState;
import su.nightexpress.ama.api.arena.type.ArenaState;
import su.nightexpress.ama.api.arena.type.LeaveReason;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.api.event.ArenaMobDeathEvent;
import su.nightexpress.ama.api.event.ArenaPlayerDeathEvent;
import su.nightexpress.ama.api.event.ArenaPlayerReadyEvent;
import su.nightexpress.ama.arena.*;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.mob.config.MobConfig;
import su.nightexpress.ama.mob.config.MobHealthBar;
import su.nightexpress.ama.mob.MobManager;
import su.nightexpress.ama.mob.config.MobsConfig;
import su.nightexpress.ama.mob.kill.MobKillReward;
import su.nightexpress.ama.mob.kill.MobKillStreak;
import su.nightexpress.ama.stats.object.StatType;

public class ArenaGameplayListener extends AbstractListener<AMA> {

	private final ArenaManager manager;
	
	public ArenaGameplayListener(@NotNull ArenaManager manager) {
		super(manager.plugin());
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onArenaGameEvent(ArenaGameGenericEvent e) {
		e.getArena().onArenaGameEvent(e);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onArenaPlayerReadyEvent(ArenaPlayerReadyEvent e) {
		ArenaPlayer arenaPlayer = e.getArenaPlayer();
		Player player = arenaPlayer.getPlayer();
		boolean isReady = e.isReady();

		LangMessage msg = isReady ? plugin.getMessage(Lang.Arena_Game_Lobby_Ready_True) : plugin.getMessage(Lang.Arena_Game_Lobby_Ready_False);
		arenaPlayer.getArena().getPlayers().forEach(arenaPlayer1 -> {
			msg.replace(arenaPlayer.replacePlaceholders()).send(arenaPlayer1.getPlayer());
		});
	}
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGamePlayerItemDurability(PlayerItemDamageEvent e) {
    	Player player = e.getPlayer();
    	
    	ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
    	if (arenaPlayer == null) return;
    	
    	AbstractArena arena = arenaPlayer.getArena();
    	if (!arena.getConfig().getGameplayManager().isItemDurabilityEnabled()) {
    		e.setCancelled(true);
    	}
    }
    
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onGamePlayerItemDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

    	AbstractArena arena = arenaPlayer.getArena();
    	// Prevent drop lobby or kit items in lobby.
    	if (arena.getConfig().getGameplayManager().isKitsEnabled() &&
    			(arena.getState() != ArenaState.INGAME || arenaPlayer.isLateJoined())) {
    		e.setCancelled(true);
    		return;
    	}

        if (!arena.getConfig().getGameplayManager().isItemDropEnabled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onGamePlayerItemPickup(EntityPickupItemEvent e) {
    	if (!(e.getEntity() instanceof Player player)) return;

		ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

    	AbstractArena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplayManager().isItemPickupEnabled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGamePlayerRegen(EntityRegainHealthEvent e) {
        if (e.getRegainReason() != RegainReason.SATIATED) return;
        if (!(e.getEntity() instanceof Player player)) return;

		ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        AbstractArena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplayManager().isRegenerationEnabled()) {
        	e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGamePlayerHunger(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

		ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

    	AbstractArena arena = arenaPlayer.getArena();
        if (!arena.getConfig().getGameplayManager().isHungerEnabled()) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGamePlayerCmd(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        if (player.hasPermission(Perms.BYPASS_ARENA_GAME_COMMANDS)) return;

        AbstractArena arena = arenaPlayer.getArena();
        if (arena.getConfig().getGameplayManager().isPlayerCommandsEnabled()) return;

        String cmd = StringUtil.extractCommandName(e.getMessage());
        if (ArrayUtil.contains(plugin.getLabels(), cmd)) return;
        if (arena.getConfig().getGameplayManager().getPlayerCommandsAllowed().contains(cmd)) return;

        e.setCancelled(true);
        player.closeInventory();
        plugin.getMessage(Lang.Arena_Game_Restrict_Commands).send(player);
    }

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGamePlayerRegionMove(PlayerMoveEvent e) {
		Location to = e.getTo();
		if (to == null) return;

		Location from = e.getFrom();
		if (to.getX() == from.getX() && to.getZ() == from.getZ()) return;

		Player player = e.getPlayer();
		ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
		if (arenaPlayer == null) return;

		AbstractArena arena = arenaPlayer.getArena();
		ArenaRegion region = arena.getConfig().getRegionManager().getRegion(to);
		if (region == null) return;

		if (region.getState() == ArenaLockState.LOCKED && region.getCuboid().contains(to) && !region.getCuboid().contains(from)) {
			e.setCancelled(true);
		}
	}

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGamePlayerDeath(EntityDamageEvent e) {
    	if (!(e.getEntity() instanceof Player player)) return;

		ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;
        if (player.getHealth() - e.getFinalDamage() > 0D) return;

        e.setDamage(0);
        e.setCancelled(true);

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HAND, EquipmentSlot.OFF_HAND}) {
			ItemStack totem = player.getInventory().getItem(slot);
			if (totem.getType() == Material.TOTEM_OF_UNDYING) {
				totem.setAmount(totem.getAmount() - 1);
				player.playEffect(EntityEffect.TOTEM_RESURRECT);
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 45 * 20, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40 * 20, 0));
				player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 5 * 20, 1));

				return;
			}
		}

        AbstractArena arena = arenaPlayer.getArena();
        PlayerSnapshot.clear(player);

		ArenaPlayerDeathEvent playerDeathEvent = new ArenaPlayerDeathEvent(arena, arenaPlayer);
		plugin.getPluginManager().callEvent(playerDeathEvent);

		if (arena.getConfig().getGameplayManager().isPlayerDropItemsOnDeathEnabled()) {
			for (ItemStack itemStack : player.getInventory().getContents()) {
				if (itemStack == null || itemStack.getType().isAir()) continue;
				player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
			}
			player.getInventory().clear();
		}

        if (arenaPlayer.getLives() > 1) {
        	arenaPlayer.setLives(arenaPlayer.getLives() - 1);
        	arenaPlayer.setKillStreak(0);

        	plugin.getMessage(Lang.Arena_Game_Death_Lives).replace(arenaPlayer.replacePlaceholders()).send(player);

        	ArenaRegion defRegion = arena.getConfig().getRegionManager().getRegionAnyAvailable();
        	if (defRegion != null) {
        		player.teleport(defRegion.getSpawnLocation());
        	}
        	return;
        }

        arenaPlayer.addStats(StatType.DEATHS, 1);

        plugin.getServer().getScheduler().runTask(plugin, () -> {
        	arenaPlayer.leaveArena(LeaveReason.DEATH);

	        if (!arena.getPlayersIngame().isEmpty()) {
		        if (arena.getConfig().getGameplayManager().isSpectateOnDeathEnabled()) {
		        	arena.joinSpectate(player);
		        }

		        for (ArenaPlayer arenaPlayer1 : arena.getPlayers()) {
		        	plugin.getMessage(Lang.Arena_Game_Death_Player).replace(arenaPlayer.replacePlaceholders()).replace(arena.replacePlaceholders()).send(arenaPlayer1.getPlayer());
		        }
	        }
        });
    }

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMobDeath(EntityDeathEvent e) {
		LivingEntity entity = e.getEntity();

		AbstractArena arena = this.plugin.getMobManager().getEntityArena(entity);
		if (arena == null) return;

		if (!arena.getConfig().getGameplayManager().isMobDropLootEnabled()) {
			e.getDrops().clear();
		}
		if (!arena.getConfig().getGameplayManager().isMobDropExpEnabled()) {
			e.setDroppedExp(0);
		}

		String mobId = this.plugin.getMobManager().getMobId(entity);
		MobConfig customMob = this.plugin.getMobManager().getEntityMobConfig(entity);
		if (customMob != null) {
			MobHealthBar healthBar = customMob.getHealthBar();
			if (healthBar.isEnabled()) {
				healthBar.remove(arena.getPlayersIngame(), entity);
			}
		}
		//arena.getMobs().remove(entity);

		ArenaMobDeathEvent mobDeathEvent = new ArenaMobDeathEvent(arena, entity, mobId);

		Player killer = entity.getKiller();
		ArenaPlayer arenaPlayer = killer != null ? ArenaPlayer.getPlayer(killer) : null;
		if (killer != null && arenaPlayer != null) {
			mobDeathEvent.setKiller(arenaPlayer);

			int streak = arenaPlayer.getKillStreak() + 1;

			MobKillReward killReward = this.plugin.getMobManager().getMobKillReward(entity);
			MobKillStreak killStreak = MobManager.getMobKillStreak(streak);

			if (killStreak != null) {
				if (killReward != null) {
					killReward = killReward.multiply(killStreak);
				}
				killStreak.getMessage().send(killer);
				killStreak.executeCommands(killer);
			}
			arenaPlayer.setKillStreak(streak);
			if (streak > 0) {
				arenaPlayer.setKillStreakDecay(MobsConfig.KILL_STREAK_DECAY.get() * 1000L);
			}
			arenaPlayer.addStats(StatType.BEST_KILL_STREAK, 1);
			arenaPlayer.addStats(StatType.MOB_KILLS, 1);

			if (killReward != null) {
				killReward.reward(arenaPlayer);
				this.plugin.getMobManager().displayMobKillReward(entity, killReward);
			}
		}

		plugin.getPluginManager().callEvent(mobDeathEvent);
	}

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGamePlayerBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
		Block block = e.getBlock();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);

    	if (arenaPlayer != null) {
    		e.setCancelled(true);
        	if (block.getType() == Material.TNT) {
        		ItemStack item = e.getItemInHand();
        		item.setAmount(item.getAmount() - 1);
        		TNTPrimed tnt = block.getWorld().spawn(block.getLocation(), TNTPrimed.class);
        		tnt.setSource(player);
        	}
        	return;
    	}

    	if (player.hasPermission(Perms.CREATOR)) return;
    	AbstractArena arena = this.manager.getArenaAtLocation(block.getLocation());
    	if (arena != null) {
    		e.setCancelled(true);
    	}
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameStatsTNT(EntityExplodeEvent e) {
		if (e.getEntity() instanceof TNTPrimed tnt && tnt.getSource() instanceof Player player) {
			ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
			if (arenaPlayer == null) return;

			arenaPlayer.addStats(StatType.TNT_EXPLODED, 1);
		}
	}

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGameStatsConsume(PlayerItemConsumeEvent e) {
    	Player player = e.getPlayer();
    	ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();

        if (meta instanceof PotionMeta) {
        	arenaPlayer.addStats(StatType.POTIONS_DRUNK, 1);
        }
        else {
        	arenaPlayer.addStats(StatType.FOOD_EATEN, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onGameStatsItemBreak(PlayerItemBreakEvent e) {
    	Player player = e.getPlayer();
    	ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        arenaPlayer.addStats(StatType.EQUIPMENT_BROKEN, 1);
    }

    @EventHandler
    public void onGamePlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;

        arenaPlayer.leaveArena(LeaveReason.SELF);
    }

	@EventHandler(priority = EventPriority.HIGHEST)
    public void onGameItemInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;
    	
		ItemStack item = e.getItem();
    	if (item == null || item.getType().isAir()) return;
    	
    	AbstractArena arena = arenaPlayer.getArena();
    	if (arena.getState() != ArenaState.INGAME || arenaPlayer.isLateJoined()) {
    		// Prevent using lobby items on arena signs
    		// and interactable blocks.
    		Block block = e.getClickedBlock();
    		if (block != null && block.getType().isInteractable()) return;

    		LobbyItem lobbyItem = LobbyItem.get(item);
    		if (lobbyItem != null) {
    			lobbyItem.use(arenaPlayer);
				e.setUseItemInHand(Result.DENY);
				return;
			}
    	}
    	
    	if (arena.getConfig().getGameplayManager().getBannedItems().contains(item.getType())) {
			e.setUseItemInHand(Result.DENY);
    	}
	}
}
