package su.nightexpress.ama.arena;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListener;
import su.nightexpress.ama.api.arena.type.*;
import su.nightexpress.ama.api.event.ArenaGameEndEvent;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.api.event.ArenaScoreChangeEvent;
import su.nightexpress.ama.api.event.ArenaWaveCompleteEvent;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.spot.ArenaSpot;
import su.nightexpress.ama.arena.wave.ArenaWaveMob;
import su.nightexpress.ama.arena.wave.ArenaWaveUpcoming;
import su.nightexpress.ama.sign.SignManager;
import su.nightexpress.ama.sign.type.SignType;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractArena implements IPlaceholder {

    protected AMA         plugin;
    protected ArenaConfig config;
    protected ArenaState  state;
    protected Set<IArenaGameEventListener> gameEventListeners;

    protected int  lobbyTimeleft;
    protected long gameTimeleft;
    protected int  gameScore;

    protected Set<ArenaPlayer>  players;
    protected Set<LivingEntity> mobs;
    protected Set<Item>         groundItems;

    protected Team    mobHighlightTeam;
    protected boolean mobsAboutToSpawn;

    protected int gradualMobsTimer;
    protected int gradualMobsPrepare;
    protected int gradualMobsKilled;

    protected int waveNumber;
    protected int  waveNextTimeleft;
    protected int waveMobsTotalAmount;
    protected Set<ArenaWaveUpcoming> waveUpcoming;
    protected Map<String, double[]> waveAmplificatorValues; // [Amount, Level]

    private static final DateTimeFormatter FORMAT_TIMELEFT = DateTimeFormatter.ofPattern("HH:mm:ss");
    // TODO Final Arena Stats Most Damager, Killer, etc

    public AbstractArena(@NotNull ArenaConfig config)  {
        this.plugin = config.plugin();
        this.config = config;
        this.gameEventListeners = new LinkedHashSet<>();
        this.players = new HashSet<>();
        this.mobs = new HashSet<>();
        this.groundItems = new HashSet<>();
        this.waveUpcoming = new HashSet<>();
        this.waveAmplificatorValues = new HashMap<>();

        this.reset();
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> this.getConfig().replacePlaceholders().apply(str)
            .replace(Placeholders.ARENA_STATE, plugin().getLangManager().getEnum(this.getState()))
            .replace(Placeholders.ARENA_PLAYERS, String.valueOf(this.getPlayers().size()))
            .replace(Placeholders.ARENA_PLAYERS_MAX, String.valueOf(this.getConfig().getGameplayManager().getPlayerMaxAmount()))
            .replace(Placeholders.ARENA_MOBS_ALIVE, String.valueOf(this.getMobs().size()))
            .replace(Placeholders.ARENA_MOBS_LEFT, String.valueOf(this.getMobsAmountLeft()))
            .replace(Placeholders.ARENA_MOBS_TOTAL, String.valueOf(this.getWaveMobsTotalAmount()))
            .replace(Placeholders.ARENA_WAVE_NUMBER, String.valueOf(this.getWaveNumber()))
            .replace(Placeholders.ARENA_WAVE_NEXT_IN, String.valueOf(this.getWaveNextTimeleft()))
            .replace(Placeholders.ARENA_TIMELEFT, TimeUtil.getLocalTimeOf(this.getGameTimeleft()).format(FORMAT_TIMELEFT))
            .replace(Placeholders.ARENA_SCORE, String.valueOf(this.getGameScore()))
            ;
    }

    private void reset() {
        this.updateGameEventListeners();
        this.killMobs();
        this.killItems();

        int gameTimeleft = this.getConfig().getGameplayManager().getTimeleft();

        this.setLobbyTimeleft(this.getConfig().getGameplayManager().getLobbyTime());
        this.setGameTimeleft(gameTimeleft > 0 ? gameTimeleft * 1000L * 60L : -1);
        this.setGameScore(0);

        this.setWaveNumber(0);
        this.setWaveNextTimeleft(this.getConfig().getWaveManager().getDelayFirst());
        this.getUpcomingWaves().clear();
        this.getWaveAmplificatorValues().clear();
        this.setWaveMobsTotalAmount(0);

        this.gradualMobsTimer = 0;
        this.setGradualMobsKilled(0);
        this.gradualMobsPrepare = 0;

        if (this.mobHighlightTeam != null) {
            this.mobHighlightTeam.getEntries().forEach(this.mobHighlightTeam::removeEntry);
            this.mobHighlightTeam.unregister();
            this.mobHighlightTeam = null;
        }
        this.mobsAboutToSpawn = false;

        this.emptyContainers();

        this.setState(ArenaState.WAITING);
        this.updateSigns();
        this.updateHolograms();
    }

    public final void stop(@NotNull ArenaEndType type) {
        if (this.getState() == ArenaState.INGAME) {
            ArenaGameEndEvent event = new ArenaGameEndEvent(this, type);
            plugin().getPluginManager().callEvent(event);
        }
        this.onArenaStop(type);
        this.reset();
    }

    protected abstract void onArenaStop(@NotNull ArenaEndType type);

    public void updateSigns() {
        SignManager signManager = this.plugin.getSignManager();
        if (signManager == null) return;

        signManager.update(SignType.ARENA_JOIN);
    }

    public void updateHolograms() {
        this.getConfig().updateHolograms();
    }

    public boolean hasPermission(@NotNull Player player) {
        return !this.getConfig().isPermissionRequired() || player.hasPermission(this.getConfig().getPermission());
    }

    @NotNull
    public AMA plugin() {
        return this.plugin;
    }

    @NotNull
    public ArenaConfig getConfig() {
        return this.config;
    }

    @NotNull
    public String getId() {
        return this.getConfig().getId();
    }

    @NotNull
    public Set<IArenaGameEventListener> getGameEventListeners() {
        return this.gameEventListeners;
    }

    public void onArenaGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        this.getGameEventListeners().forEach(listener -> listener.onGameEvent(gameEvent));

        ArenaGameEventType eventType = gameEvent.getEventType();
        if (eventType == ArenaGameEventType.GAME_END_LOSE || eventType == ArenaGameEventType.GAME_END_TIME
            || eventType == ArenaGameEventType.GAME_END_WIN || eventType == ArenaGameEventType.GAME_START
            || eventType == ArenaGameEventType.PLAYER_JOIN || eventType == ArenaGameEventType.PLAYER_LEAVE) {
            this.updateSigns();
            this.updateHolograms();
        }
    }

    public void updateGameEventListeners() {
        ArenaConfig config = this.getConfig();

        this.getGameEventListeners().clear();
        this.getGameEventListeners().addAll(config.getRegionManager().getRegions().stream().filter(ArenaRegion::isActive).toList());
        config.getSpotManager().getSpots().stream().filter(ArenaSpot::isActive).forEach(spot -> {
            this.getGameEventListeners().addAll(spot.getStates().values());
        });
        this.getGameEventListeners().add(config.getShopManager());
        this.getGameEventListeners().addAll(config.getShopManager().getCategories());
        config.getShopManager().getCategories().forEach(category -> {
            this.getGameEventListeners().addAll(category.getProducts());
        });
        config.getWaveManager().getWaves().values().forEach(arenaWave -> {
            this.getGameEventListeners().addAll(arenaWave.getAmplificators().values());
        });
        config.getRegionManager().getRegions().stream().filter(ArenaRegion::isActive).forEach(region -> {
            this.getGameEventListeners().addAll(region.getWaves());
            this.getGameEventListeners().addAll(region.getContainers());
        });
        this.getGameEventListeners().addAll(config.getRewardManager().getRewards());
        this.getGameEventListeners().addAll(config.getGameplayManager().getAutoCommands());
    }

    public final void tick() {
        this.tickLobby();
        this.tickGame();
    }

    public abstract void tickLobby();

    public abstract void tickGame();

    public abstract boolean canJoin(@NotNull Player player, boolean isMessage);

    public abstract boolean joinLobby(@NotNull Player arenaPlayer);

    public abstract boolean joinSpectate(@NotNull Player player);

    public abstract boolean leaveArena(@NotNull ArenaPlayer arenaPlayer, @NotNull LeaveReason reason);

    @NotNull
    public ArenaState getState() {
        return this.state;
    }

    protected void setState(@NotNull ArenaState state) {
        this.state = state;
    }

    public int getLobbyTimeleft() {
        return this.lobbyTimeleft;
    }

    public void setLobbyTimeleft(int lobbyTimeleft) {
        this.lobbyTimeleft = Math.max(0, lobbyTimeleft);
    }

    public long getGameTimeleft() {
        return this.gameTimeleft;
    }

    public void setGameTimeleft(long gameTimeleft) {
        this.gameTimeleft = gameTimeleft;
    }

    public int getGameScore() {
        return this.gameScore;
    }

    public void setGameScore(int gameScore) {
        this.gameScore = gameScore;
    }

    public void updateGameScore() {
        int oldScore = this.getGameScore();
        int totalScore = 0;
        for (ArenaPlayer arenaPlayer : this.getPlayersIngame()) {
            totalScore += arenaPlayer.getScore();
        }
        this.setGameScore(totalScore);

        ArenaGameEventType eventType = this.getGameScore() > oldScore ? ArenaGameEventType.SCORE_INCREASED : ArenaGameEventType.SCORE_DECREASED;
        ArenaScoreChangeEvent event = new ArenaScoreChangeEvent(this, eventType, oldScore, this.getGameScore());
        plugin().getPluginManager().callEvent(event);
    }

    // TODO Add another stat type = InGameStatType
    // to display stats after the game, and some of them will be recorded to player global stats.
    @NotNull
    @Deprecated
    public ArenaPlayer getHighScore(int pos) {
        pos = Math.max(0, pos - 1);

        Map<ArenaPlayer, Integer> map = new HashMap<>();
        for (ArenaPlayer arenaPlayer : this.getPlayersIngame()) {
            map.put(arenaPlayer, arenaPlayer.getScore());
        }

        int count = 0;
        for (ArenaPlayer arenaPlayer : CollectionsUtil.sortDescent(map).keySet()) {
            if (count == pos) {
                return arenaPlayer;
            }
        }

        return new ArrayList<>(this.getPlayersIngame()).get(0);
    }


    public void broadcast(@NotNull LangMessage message, @NotNull ArenaTargetType targetType) {
        this.getPlayers(targetType).forEach(arenaPlayer -> {
            message.send(arenaPlayer.getPlayer());
        });
    }


    @NotNull
    public Set<ArenaPlayer> getPlayers() {
        return new HashSet<>(this.players);
    }

    protected final void addPlayer(@NotNull ArenaPlayer arenaPlayer) {
        this.players.add(arenaPlayer);
    }

    protected final void removePlayer(@NotNull ArenaPlayer arenaPlayer) {
        this.players.remove(arenaPlayer);
    }

    @NotNull
    public Set<ArenaPlayer> getPlayers(@NotNull ArenaTargetType targetType) {
        if (targetType == ArenaTargetType.PLAYER_ALL) return this.getPlayersIngame();
        if (targetType == ArenaTargetType.PLAYER_RANDOM) return Stream.of(Rnd.get(this.getPlayersIngame())).filter(Objects::nonNull).collect(Collectors.toSet());
        return Collections.emptySet();
    }

    @NotNull
    public Set<ArenaPlayer> getPlayersIngame() {
        return this.getPlayers().stream().filter(Predicate.not(ArenaPlayer::isLateJoined)).collect(Collectors.toSet());
    }

    @NotNull
    public Set<ArenaPlayer> getPlayersLate() {
        return this.getPlayers().stream().filter(ArenaPlayer::isLateJoined).collect(Collectors.toSet());
    }

    @Nullable
    public ArenaPlayer getPlayerRandom() {
        return Rnd.get(new ArrayList<>(this.getPlayersIngame()));
    }

    @NotNull
    public Set<LivingEntity> getMobs() {
        return this.mobs;
    }

    public int getMobsAmountLeft() {
        return this.getUpcomingWaves().stream()
            .filter(Predicate.not(ArenaWaveUpcoming::isAllMobsSpawned))
            .mapToInt(wave -> wave.getPreparedMobs().stream().mapToInt(ArenaWaveMob::getAmount).sum()).sum();
    }

    @Nullable
    public LivingEntity getMobRandom() {
        List<LivingEntity> list = new ArrayList<>(this.getMobs());
        return Rnd.get(list);
    }

    public void killMobs() {
        new HashSet<>(this.getMobs()).forEach(mob -> {
            mob.setLastDamageCause(null);
            mob.setHealth(0);
        });
        this.getMobs().clear();
    }

    public abstract void updateMobTarget(@NotNull LivingEntity entity, boolean force);

    public boolean isAboutToSpawnMobs() {
        return this.mobsAboutToSpawn;
    }

    public void highlightMobs(boolean state) {
        if (this.mobHighlightTeam == null) {
            ScoreboardManager scoreboardManager = plugin.getServer().getScoreboardManager();
            if (scoreboardManager != null) {
                String teamId = this.getId() + "_mob_hl";
                Team teamOld = scoreboardManager.getMainScoreboard().getTeam(teamId);

                this.mobHighlightTeam = teamOld == null ? scoreboardManager.getMainScoreboard().registerNewTeam(teamId) : teamOld;
                this.mobHighlightTeam.setColor(this.getConfig().getGameplayManager().getMobHighlightColor());
            }
        }
        if (this.mobHighlightTeam == null) return;

        if (state) {
            this.getMobs().forEach(entity -> this.mobHighlightTeam.addEntry(entity.getUniqueId().toString()));
        }
        else {
            this.getMobs().forEach(entity -> this.mobHighlightTeam.removeEntry(entity.getUniqueId().toString()));
        }
        this.getMobs().forEach(entity -> entity.setGlowing(state));
    }

    @NotNull
    public Set<Item> getGroundItems() {
        return groundItems;
    }

    public void addGroundItem(@NotNull Item item) {
        this.getGroundItems().removeIf(item2 -> !item2.isValid());
        this.getGroundItems().add(item);
    }

    public void killItems() {
        this.getGroundItems().forEach(Entity::remove);
        this.getGroundItems().clear();
    }

    public int getWaveMobsTotalAmount() {
        return this.waveMobsTotalAmount;
    }

    public void setWaveMobsTotalAmount(int mobsTotalAmount) {
        this.waveMobsTotalAmount = mobsTotalAmount;
    }

    public int getWaveNumber() {
        return this.waveNumber;
    }

    protected void setWaveNumber(int waveNumber) {
        this.waveNumber = waveNumber;
    }

    public int getWaveNextTimeleft() {
        return this.waveNextTimeleft;
    }

    public void setWaveNextTimeleft(int waveNextTimeleft) {
        this.waveNextTimeleft = Math.max(0, waveNextTimeleft);
    }

    public boolean isNextWaveAllowed() {
        return this.getWaveNumber() < 1 || (this.getMobs().isEmpty() && this.getUpcomingWaves().isEmpty());
    }

    public boolean isLatestWave() {
        return this.getWaveNumber() == this.getConfig().getWaveManager().getFinalWave();
    }

    public void skipWave() {
        this.getUpcomingWaves().clear();
        this.killMobs();

        // Call an event that will call arena region and spot triggers.
        ArenaWaveCompleteEvent event = new ArenaWaveCompleteEvent(this);
        plugin().getPluginManager().callEvent(event);

        this.setWaveNextTimeleft(1);
    }

    public abstract void newWave();

    public abstract void spawnMobs(double percent);


    protected int getGradualMobsKilled() {
        return this.gradualMobsKilled;
    }

    protected void setGradualMobsKilled(int gradualMobsKilled) {
        this.gradualMobsKilled = gradualMobsKilled;
    }


    @NotNull
    public Map<String, double[]> getWaveAmplificatorValues() {
        return this.waveAmplificatorValues;
    }

    public double[] getWaveAmplificatorValues(@NotNull String waveId) {
        return this.getWaveAmplificatorValues().computeIfAbsent(waveId.toLowerCase(), d -> new double[2]);
    }

    public double getWaveAmplificatorAmount(@NotNull String waveId) {
        return this.getWaveAmplificatorValues(waveId)[0];
    }

    public void addWaveAmplificatorAmount(@NotNull String waveId, int amount) {
        this.getWaveAmplificatorValues(waveId)[0] += amount;
    }

    public double getWaveAmplificatorLevel(@NotNull String waveId) {
        return this.getWaveAmplificatorValues(waveId)[1];
    }

    public void addWaveAmplificatorLevel(@NotNull String waveId, int amount) {
        this.getWaveAmplificatorValues(waveId)[1] += amount;
    }

    public void emptyContainers() {
        this.getConfig().getRegionManager().getRegions().forEach(ArenaRegion::emptyContainers);
    }

    @NotNull
    public Set<ArenaWaveUpcoming> getUpcomingWaves() {
        return this.waveUpcoming;
    }
}
