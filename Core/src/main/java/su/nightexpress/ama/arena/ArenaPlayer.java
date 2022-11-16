package su.nightexpress.ama.arena;

import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.type.ArenaLockState;
import su.nightexpress.ama.api.arena.type.ArenaState;
import su.nightexpress.ama.api.arena.type.LeaveReason;
import su.nightexpress.ama.api.event.ArenaPlayerReadyEvent;
import su.nightexpress.ama.arena.board.ArenaBoard;
import su.nightexpress.ama.arena.board.ArenaBoardConfig;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.reward.ArenaReward;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.external.SunLightHook;
import su.nightexpress.ama.kit.Kit;
import su.nightexpress.ama.stats.object.StatType;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;

public final class ArenaPlayer implements IPlaceholder {

	private static final Map<UUID, ArenaPlayer> PLAYER_MAP = new HashMap<>();
	private static final DateTimeFormatter FORMAT_STREAK = DateTimeFormatter.ofPattern("ss");

	private final Player        player;
    private final AbstractArena     arena;
    private       Kit               kit;
    private       List<ArenaReward> rewards;
    private       int                lives;
    private       int                score;
    private       int                killStreak;
    private       long               killStreakDecay;
    private       boolean            isLateJoined;
    private       boolean            isReady;
	private       ArenaBoard         board;

    private final Map<StatType, Integer> stats;
    private final Map<UUID, BossBar>     mobHealthBar;

    @Nullable
    public static ArenaPlayer getPlayer(@NotNull Player player) {
    	if (Hooks.isCitizensNPC(player)) return null;

    	return ArenaPlayer.getPlayer(player.getUniqueId());
	}

	@Nullable
	public static ArenaPlayer getPlayer(@NotNull UUID uuid) {
    	return PLAYER_MAP.get(uuid);
	}

	@NotNull
	public static Collection<ArenaPlayer> getPlayers() {
    	return PLAYER_MAP.values();
	}

	public static boolean isPlaying(@NotNull Player player) {
    	return ArenaPlayer.getPlayer(player) != null;
	}

    @NotNull
    public static ArenaPlayer create(@NotNull Player player, @NotNull AbstractArena arena) {
    	if (ArenaPlayer.isPlaying(player)) throw new IllegalStateException("This player is already playing other arena!");

    	ArenaPlayer arenaPlayer = new ArenaPlayer(player, arena);
    	PLAYER_MAP.put(player.getUniqueId(), arenaPlayer);
    	return arenaPlayer;
	}

	public static void remove(@NotNull Player player) {
    	PLAYER_MAP.remove(player.getUniqueId());
	}

	private ArenaPlayer(@NotNull Player player, @NotNull AbstractArena arena) {
		this.stats = new HashMap<>();
		this.mobHealthBar = new HashMap<>();
    	this.player = player;
        this.arena = arena;
        this.setKit(null);
        this.setRewards(new ArrayList<>());
        this.setLives(arena.getConfig().getGameplayManager().getPlayerLivesAmount());
        this.setScore(0);
        this.setKillStreak(0);
        this.setLateJoined(arena.getState() == ArenaState.INGAME);
        this.isReady = false;
    }

	@Override
	@NotNull
	public UnaryOperator<String> replacePlaceholders() {
		return str -> str
			.replace(Placeholders.PLAYER_NAME, this.getPlayer().getName())
			.replace(Placeholders.PLAYER_LIVES, String.valueOf(this.getLives()))
			.replace(Placeholders.PLAYER_STREAK, String.valueOf(this.getKillStreak()))
			.replace(Placeholders.PLAYER_STREAK_DECAY, TimeUtil.getLocalTimeOf(this.getKillStreakDecay()).format(FORMAT_STREAK))
			.replace(Placeholders.PLAYER_SCORE, String.valueOf(this.getScore()))
			.replace(Placeholders.PLAYER_KILLS, String.valueOf(this.getStats(StatType.MOB_KILLS)))
			.replace(Placeholders.PLAYER_IS_READY, LangManager.getBoolean(this.isReady()))
			.replace(Placeholders.PLAYER_KIT_NAME, this.getKit() == null ? "-" : this.getKit().getName())
			;
	}

	public boolean leaveArena(@NotNull LeaveReason reason) {
    	return this.getArena().leaveArena(this, reason);
	}

	@NotNull
    public AbstractArena getArena() {
    	return this.arena;
    }
    
    @Nullable
    public Kit getKit() {
        return this.kit;
    }
    
    public void setKit(@Nullable Kit kit) {
        this.kit = kit;
    }
    
    @NotNull
    public List<ArenaReward> getRewards() {
        return this.rewards;
    }
    
    public void setRewards(@NotNull List<ArenaReward> rewards) {
        this.rewards = rewards;
    }
    
    @NotNull
    public Player getPlayer() {
    	return this.player;
    }
    
    public int getLives() {
    	return this.lives;
    }
    
    public void setLives(int lives) {
    	this.lives = Math.max(0, lives);
    }
    
    public void takeLive() {
    	this.setLives(this.getLives() - 1);
    }
    
    public boolean isLateJoined() {
		return isLateJoined;
	}
    
    public void setLateJoined(boolean isLateJoined) {
		this.isLateJoined = isLateJoined;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean ready) {
		isReady = ready;

		ArenaPlayerReadyEvent readyEvent = new ArenaPlayerReadyEvent(this.getArena(), this);
		this.getArena().plugin().getPluginManager().callEvent(readyEvent);
	}

	@Nullable
    public BossBar getMobHealthBar(@NotNull LivingEntity mob) {
    	return this.getMobHealthBar(mob.getUniqueId());
    }
    
    @Nullable
    public BossBar getMobHealthBar(@NotNull UUID id) {
    	return mobHealthBar.get(id);
    }
    
    public void addMobHealthBar(@NotNull LivingEntity boss, @NotNull BossBar bar) {
    	if (this.getMobHealthBar(boss) != null) return;
    	
    	bar.addPlayer(this.getPlayer());
    	this.mobHealthBar.put(boss.getUniqueId(), bar);
    }
    
    public void removeMobHealthBar(@NotNull LivingEntity boss) {
    	BossBar bar = this.getMobHealthBar(boss);
    	if (bar == null) return;
    	
    	bar.removePlayer(this.getPlayer());
    	this.mobHealthBar.remove(boss.getUniqueId());
    }
    
    public void removeMobHealthBars() {
    	this.mobHealthBar.values().forEach(bar -> bar.removePlayer(this.getPlayer()));
    	this.mobHealthBar.clear();
    }
    
    public void addBoard() {
    	if (!this.arena.getConfig().getGameplayManager().isScoreboardEnabled()) return;
        
    	ArenaBoardConfig boardConfig = Config.SCOREBOARDS.get().get(this.getArena().getConfig().getGameplayManager().getScoreboardId());
    	if (boardConfig == null) return;

    	if (Hooks.hasPlugin(HookId.SUNLIGHT)) {
			SunLightHook.disableBoard(this.getPlayer());
		}

    	this.board = new ArenaBoard(this, boardConfig);
    	this.board.create();
    }
    
	public void removeBoard() {
		if (this.board != null) {
			this.board.remove();
			this.board = null;
		}
	}

    public void tick() {
    	if (!this.getArena().isNextWaveAllowed()) {
			this.setKillStreakDecay(this.getKillStreakDecay() - 1000L);
			if (this.getKillStreakDecay() == 0) {
				this.setKillStreak(0);
				this.getStats().remove(StatType.BEST_KILL_STREAK);
			}
		}
    	
    	if (this.kit != null) {
    		this.kit.applyPotionEffects(this.player);
    	}
    	if (this.board != null) {
    		this.board.update();
    	}

    	// TODO
		/*V1_19_R1 nms = (V1_19_R1) this.getArena().plugin.getArenaNMS();
    	Block end = this.getArena().getConfig().getRegionManager().getRegionDefault().getSpawnLocation().getBlock();
		Set<Block> path = nms.createPath(this.getPlayer().getLocation().getBlock(), end);
		System.out.println(path);
		path.stream().map(block -> block.getRelative(BlockFace.UP)).forEach(block -> {
			EffectUtil.playEffect(block.getLocation(), Particle.REDSTONE, "", 0, 0, 0, 0.1, 5);
		});*/
    }
    
	/**
     * @return Returns ArenaRegion where player is.
     */
    @Nullable
    public ArenaRegion getRegion(boolean includeNearby) {
    	Location loc = this.getPlayer().getLocation();
    	
    	ArenaRegion region = this.getArena().getConfig().getRegionManager().getRegion(loc);
    	if (region != null || !includeNearby) {
    		return region;
    	}

    	// Get nearest region to player if he is outside of any
		return this.getArena().getConfig().getRegionManager().getRegions().stream()
				.filter(reg -> reg.getState() == ArenaLockState.UNLOCKED)
				.min((r1, r2) -> (int) (loc.distance(r1.getSpawnLocation()) - loc.distance(r2.getSpawnLocation())))
				.orElse(null);
    }
    
    public int getScore() {
		return score;
	}
    
    public void setScore(int score) {
		this.score = Math.max(0, score);
		this.getArena().updateGameScore();
	}
    
    public void addScore(int amount) {
    	this.setScore(this.getScore() + amount);
    }
    
    public int getKillStreak() {
		return killStreak;
	}
    
    public void setKillStreak(int killStreak) {
		this.killStreak = killStreak;
	}
    
    public long getKillStreakDecay() {
		return killStreakDecay;
	}
    
    public void setKillStreakDecay(long killStreakDecay) {
		this.killStreakDecay = Math.max(0, killStreakDecay);
	}
    
    @NotNull
    public Map<StatType, Integer> getStats() {
    	return this.stats;
    }
    
    public int getStats(@NotNull StatType type) {
    	return this.stats.getOrDefault(type, 0);
    }
    
    public void addStats(@NotNull StatType type, int amount) {
    	this.stats.put(type, amount + this.getStats(type));
    }
    
	public void saveStats() {
    	String arena = this.getArena().getId();
    	Player player = this.getPlayer();
    	ArenaUser user = this.getArena().plugin().getUserManager().getUserData(player);
    	
    	Map<StatType, Integer> gameStats = this.getStats();
    	Map<StatType, Integer> userStats = user.getStats(arena);

    	gameStats.forEach((stat, amount) -> {
    		userStats.merge(stat, amount, (oldVal, newVal) -> {
    			if (stat == StatType.WAVES_PASSED || stat == StatType.BEST_KILL_STREAK) {
    				return Math.max(oldVal, newVal);
    			}
    			return oldVal + newVal;
    		});
    	});
    }
}
