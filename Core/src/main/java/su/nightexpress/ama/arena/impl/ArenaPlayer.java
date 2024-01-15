package su.nightexpress.ama.arena.impl;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.IArenaPlayer;
import su.nightexpress.ama.api.event.ArenaPlayerReadyEvent;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.api.type.PlayerType;
import su.nightexpress.ama.arena.board.ArenaBoard;
import su.nightexpress.ama.arena.board.ArenaBoardConfig;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.arena.reward.Reward;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.impl.SunLightHook;
import su.nightexpress.ama.kit.impl.Kit;
import su.nightexpress.ama.stats.object.StatType;

import java.time.format.DateTimeFormatter;
import java.util.*;

public final class ArenaPlayer implements IArenaPlayer, Placeholder {

    private static final Map<UUID, ArenaPlayer> PLAYER_MAP    = new HashMap<>();
    public static final DateTimeFormatter      FORMAT_STREAK = DateTimeFormatter.ofPattern("ss");

    private final AMA plugin;
    private final Player player;
    private final Arena  arena;
    private final Map<StatType, Integer> stats;
    private final List<Reward>           rewards;
    private final PlaceholderMap         placeholderMap;

    private GameState  state;
    private PlayerType type;
    private ArenaBoard board;
    private Kit        kit;
    private int        lifes;
    private int reviveTime;
    private int        score;
    private int        killStreak;
    private long       killStreakDecay;
    private boolean    dead;
    private boolean    spectator;
    private boolean    transfer;

    @Nullable
    public static ArenaPlayer getPlayer(@NotNull Player player) {
        if (EntityUtil.isNPC(player)) return null;

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
    public static ArenaPlayer create(@NotNull AMA plugin, @NotNull Player player, @NotNull Arena arena) {
        if (ArenaPlayer.isPlaying(player)) throw new IllegalStateException("This player is already playing other arena!");

        ArenaPlayer arenaPlayer = new ArenaPlayer(plugin, player, arena);
        PLAYER_MAP.put(player.getUniqueId(), arenaPlayer);
        return arenaPlayer;
    }

    public static void remove(@NotNull Player player) {
        PLAYER_MAP.remove(player.getUniqueId());
    }

    private ArenaPlayer(@NotNull AMA plugin, @NotNull Player player, @NotNull Arena arena) {
        this.plugin = plugin;
        this.stats = new HashMap<>();
        this.rewards = new ArrayList<>();
        this.player = player;
        this.arena = arena;
        this.state = GameState.WAITING;
        this.setType(PlayerType.REAL);
        this.setKit(null);
        this.setLifes(arena.getConfig().getGameplaySettings().getPlayerLifes());
        this.setScore(0);
        this.setKillStreak(0);
        this.setDead(false);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.PLAYER_NAME, this.getPlayer().getName())
            .add(Placeholders.PLAYER_DISPLAY_NAME, this.getPlayer().getDisplayName())
            .add(Placeholders.PLAYER_LIVES, () -> String.valueOf(this.getLifes()))
            .add(Placeholders.PLAYER_STREAK, () -> String.valueOf(this.getKillStreak()))
            .add(Placeholders.PLAYER_STREAK_DECAY, () -> TimeUtil.getLocalTimeOf(this.getKillStreakDecay()).format(FORMAT_STREAK))
            .add(Placeholders.PLAYER_SCORE, () -> String.valueOf(this.getScore()))
            .add(Placeholders.PLAYER_KILLS, () -> NumberUtil.format(this.getStats(StatType.MOB_KILLS)))
            .add(Placeholders.PLAYER_IS_READY, () -> LangManager.getBoolean(this.isReady()))
            .add(Placeholders.PLAYER_KIT_NAME, () -> this.getKit() == null ? "-" : this.getKit().getName())
        ;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public boolean leaveArena() {
        return this.getArena().leaveArena(this);
    }

    public void tick() {
        if (!this.isDead() && !this.isGhost()) {
            if (!this.getArena().isAwaitingNewRound()) {
                this.setKillStreakDecay(this.getKillStreakDecay() - 1000L);
                if (this.getKillStreakDecay() == 0) {
                    this.setKillStreak(0);
                    this.getStats().remove(StatType.BEST_KILL_STREAK);
                }
            }

            if (this.kit != null) {
                this.kit.applyPotionEffects(this.player);
            }
        }
        if (this.board != null) {
            this.board.update();
        }

        if (this.isDead()) {
            if (this.isGhost()) {
                this.plugin.getMessage(Lang.ARENA_GAME_STATUS_DEAD_NO_LIFES).send(this.getPlayer());
            }
            else {
                this.plugin.getMessage(Lang.ARENA_GAME_STATUS_DEAD_WITH_LIFES).send(this.getPlayer());
            }

            if (this.isAutoRevive()) {
                if (this.getReviveTime() == 0) {
                    this.revive();
                    this.setReviveTime(-1);
                }
                else {
                    this.setReviveTime(this.getReviveTime() - 1);
                }
            }
        }
        else if (this.isGhost()) {
            this.plugin.getMessage(Lang.ARENA_GAME_STATUS_SPECTATE).send(this.getPlayer());
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

    public void onDeath() {
        this.setDead(true);
        this.takeLive();

        Arena arena = this.getArena();

        if (this.isOutOfLifes()) {
            this.setReviveTime(-1);
            if (!arena.getConfig().getRewardManager().isKeepOnDeath()) {
                this.getRewards().clear();
            }
            if (arena.getPlayers().hasAlive()) {
                this.plugin.getMessage(Lang.ARENA_GAME_DEATH_NO_LIFES).replace(this.replacePlaceholders()).send(this.getPlayer());
            }
            this.setType(PlayerType.GHOST);
            if (!arena.getConfig().getWaveManager().isInfiniteWaves()) {
                this.addStats(StatType.GAMES_LOST, 1);
            }

            this.plugin.runTask(task -> {
                if (arena.getConfig().getGameplaySettings().isLeaveOnDeath()) {
                    arena.leaveArena(this);
                }
            });

            arena.removeBossBars(this.getPlayer());
        }
        else {
            if (this.kit != null) {
                this.kit.resetPotionEffects(this.player);
                this.kit.resetAttributeModifiers(this.player);
            }

            this.setReviveTime(arena.getConfig().getGameplaySettings().getPlayerRespawnTime());
            if (arena.getPlayers().hasAlive()) {
                this.plugin.getMessage(Lang.ARENA_GAME_DEATH_WITH_LIFES).replace(this.replacePlaceholders()).send(this.getPlayer());
            }
        }

        this.getPlayer().setGameMode(GameMode.SPECTATOR);
        this.addStats(StatType.DEATHS, 1);
        this.setKillStreak(0);
        this.setKillStreakDecay(0);

        arena.broadcast(plugin.getMessage(Lang.ARENA_GAME_INFO_PLAYER_DEATH)
            .replace(arena.replacePlaceholders())
            .replace(this.replacePlaceholders())
        );
    }

    public void revive() {
        if (!this.isDead() || this.isGhost()) return;

        Region defRegion = this.getArena().getConfig().getRegionManager().getFirstUnlocked();
        if (defRegion != null && defRegion.getSpawnLocation() != null) {
            this.getPlayer().teleport(defRegion.getSpawnLocation());
        }

        this.setDead(false);
        this.getPlayer().setGameMode(GameMode.SURVIVAL);

        if (this.kit != null) {
            this.kit.applyPotionEffects(this.player);
            this.kit.applyAttributeModifiers(this.player);
        }

        if (this.getLifes() == 1) {
            this.plugin.getMessage(Lang.ARENA_GAME_REVIVE_NO_LIFES).replace(this.replacePlaceholders()).send(this.getPlayer());
        }
        else {
            this.plugin.getMessage(Lang.ARENA_GAME_REVIVE_WITH_LIFES).replace(this.replacePlaceholders()).send(this.getPlayer());
        }
    }

    public boolean isAutoRevive() {
        return this.isDead() && !this.isGhost() && this.getReviveTime() >= 0;
    }

    public boolean isOutOfLifes() {
        return this.getLifes() < 1;
    }

    @NotNull
    public Arena getArena() {
        return this.arena;
    }

    @NotNull
    public GameState getState() {
        return state;
    }

    public void setState(@NotNull GameState state) {
        this.state = state;

        if (this.getState() == GameState.READY || this.getState() == GameState.WAITING) {
            ArenaPlayerReadyEvent readyEvent = new ArenaPlayerReadyEvent(this.getArena(), this);
            plugin.getPluginManager().callEvent(readyEvent);

            this.getArena().broadcast(plugin.getMessage(this.isReady() ? Lang.ARENA_GAME_INFO_PLAYER_READY : Lang.ARENA_GAME_INFO_PLAYER_NOT_READY)
                .replace(this.replacePlaceholders()));
        }
    }

    @NotNull
    @Override
    public PlayerType getType() {
        return type;
    }

    @Override
    public void setType(@NotNull PlayerType type) {
        this.type = type;
    }

    @Nullable
    public Kit getKit() {
        return this.kit;
    }

    public void setKit(@Nullable Kit kit) {
        this.kit = kit;
    }

    @NotNull
    public List<Reward> getRewards() {
        return this.rewards;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    public int getReviveTime() {
        return reviveTime;
    }

    public void setReviveTime(int reviveTime) {
        this.reviveTime = reviveTime;
    }

    public int getLifes() {
        return this.lifes;
    }

    public void setLifes(int lifes) {
        this.lifes = Math.max(0, lifes);
    }

    public void takeLive() {
        this.setLifes(this.getLifes() - 1);
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public boolean isSpectator() {
        return spectator;
    }

    @Override
    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    /*public boolean isGhost() {
        return this.ghost;
    }

    public boolean isReal() {
        return !this.isGhost();
    }

    public void setGhost() {
        this.ghost = true;
    }

    public void setReal() {
        this.ghost = false;
    }*/

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }

    public boolean isReady() {
        return this.getState() == GameState.READY;
    }

    public boolean isInGame() {
        return this.getState() == GameState.INGAME;
    }

    public void addBoard() {
        if (!this.arena.getConfig().getGameplaySettings().isScoreboardEnabled()) return;

        ArenaBoardConfig boardConfig = Config.SCOREBOARDS.get().get(this.getArena().getConfig().getGameplaySettings().getScoreboardId());
        if (boardConfig == null) return;

        if (EngineUtils.hasPlugin(HookId.SUNLIGHT)) {
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

    /**
     * @return Returns ArenaRegion where player is.
     */
    @Nullable
    public Region getRegion() {
        Location location = this.getPlayer().getLocation();
        return this.getArena().getConfig().getRegionManager().getRegion(location);
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = Math.max(0, score);
        this.getArena().updateGameScore();
    }

    @Override
    public void gainScore(int amount) {
        this.setScore(this.getScore() + amount);
    }

    @Override
    public void addScore(int amount) {
        this.gainScore(Math.abs(amount));
    }

    @Override
    public void removeScore(int amount) {
        this.gainScore(-Math.abs(amount));
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
        ArenaUser user = this.plugin.getUserManager().getUserData(player);

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
