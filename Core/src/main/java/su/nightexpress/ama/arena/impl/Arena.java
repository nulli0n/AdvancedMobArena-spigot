package su.nightexpress.ama.arena.impl;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.api.particle.SimpleParticle;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.*;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.arena.type.ArenaLocationType;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.api.event.*;
import su.nightexpress.ama.arena.region.ArenaRegion;
import su.nightexpress.ama.arena.region.ArenaRegionManager;
import su.nightexpress.ama.arena.type.GameResult;
import su.nightexpress.ama.arena.type.GameState;
import su.nightexpress.ama.arena.type.PlayerType;
import su.nightexpress.ama.arena.util.ArenaUtils;
import su.nightexpress.ama.arena.util.LobbyItem;
import su.nightexpress.ama.arena.util.PlayerSnapshot;
import su.nightexpress.ama.arena.wave.ArenaWaveManager;
import su.nightexpress.ama.arena.wave.ArenaWaveMob;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.hook.mob.MobProvider;
import su.nightexpress.ama.kit.Kit;
import su.nightexpress.ama.kit.KitManager;
import su.nightexpress.ama.mob.MobManager;
import su.nightexpress.ama.sign.SignManager;
import su.nightexpress.ama.sign.type.SignType;
import su.nightexpress.ama.stats.object.StatType;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Arena implements Placeholder {

    private final AMA         plugin;
    private final ArenaConfig config;
    private final Set<ArenaPlayer>  players;
    private final Set<LivingEntity> mobs;
    private final Set<LivingEntity> allyMobs;
    private final Set<Item>              groundItems;
    private final Set<ArenaUpcomingWave> waveUpcoming;
    private final Map<String, double[]>  waveAmplificatorValues; // [Amount, Level]
    private final PlaceholderMap placeholderMap;

    private GameState  state;
    private GameResult gameResult;
    private int        lobbyCountdown;
    private int        endCountdown;
    private long       gameTimeleft;
    private int        gameScore;

    private boolean mobsAboutToSpawn;

    private int gradualMobsTimer;
    private int gradualMobsPrepare;
    private int gradualMobsKilled;

    private int waveNumber;
    private int waveNextTimeleft;
    private int waveMobsTotalAmount;

    private static final DateTimeFormatter FORMAT_TIMELEFT = DateTimeFormatter.ofPattern("HH:mm:ss");
    // TODO Final Arena Stats Most Damager, Killer, etc

    public Arena(@NotNull ArenaConfig config) {
        this.plugin = config.plugin();
        this.config = config;
        this.players = new HashSet<>();
        this.mobs = new HashSet<>();
        this.allyMobs = new HashSet<>();
        this.groundItems = new HashSet<>();
        this.waveUpcoming = new HashSet<>();
        this.waveAmplificatorValues = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.ARENA_STATE, () -> plugin().getLangManager().getEnum(this.getState()))
            .add(Placeholders.ARENA_PLAYERS, () -> String.valueOf(this.getPlayers(PlayerType.REAL).size()))
            .add(Placeholders.ARENA_REAL_PLAYERS, () -> String.valueOf(this.getPlayers(PlayerType.REAL).size()))
            .add(Placeholders.ARENA_GHOST_PLAYERS, () -> String.valueOf(this.getPlayers(PlayerType.GHOST).size()))
            .add(Placeholders.ARENA_DEAD_PLAYERS, () -> String.valueOf(this.getDeadPlayers().size()))
            .add(Placeholders.ARENA_ALIVE_PLAYERS, () -> String.valueOf(this.getAlivePlayers().size()))
            .add(Placeholders.ARENA_PLAYERS_MAX, () -> String.valueOf(this.getConfig().getGameplayManager().getPlayerMaxAmount()))
            .add(Placeholders.ARENA_MOBS_ALIVE, () -> String.valueOf(this.getMobs().size()))
            .add(Placeholders.ARENA_MOBS_LEFT, () -> String.valueOf(this.getMobsAwaitingSpawn()))
            .add(Placeholders.ARENA_MOBS_TOTAL, () -> String.valueOf(this.getWaveMobsTotalAmount()))
            .add(Placeholders.ARENA_WAVE_NUMBER, () -> String.valueOf(this.getWaveNumber()))
            .add(Placeholders.ARENA_WAVE_NEXT_IN, () -> String.valueOf(this.getWaveNextTimeleft()))
            .add(Placeholders.ARENA_END_COUNTDOWN, () -> String.valueOf(this.getEndCountdown()))
            .add(Placeholders.ARENA_TIMELEFT, () -> {
                if (this.getConfig().getGameplayManager().hasTimeleft()) {
                    return TimeUtil.getLocalTimeOf(this.getGameTimeleft()).format(FORMAT_TIMELEFT);
                }
                else {
                    return LangManager.getPlain(Lang.OTHER_INFINITY);
                }
            })
            .add(Placeholders.ARENA_SCORE, () -> String.valueOf(this.getGameScore()))
        ;
        this.placeholderMap.getKeys().addAll(this.getConfig().getPlaceholders().getKeys());
    }

    @NotNull
    public AMA plugin() {
        return this.plugin;
    }

    @NotNull
    public ArenaConfig getConfig() {
        return this.config;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public String getId() {
        return this.getConfig().getId();
    }

    @NotNull
    public GameState getState() {
        return this.state;
    }

    private void setState(@NotNull GameState state) {
        this.state = state;
    }

    public int getLobbyCountdown() {
        return this.lobbyCountdown;
    }

    public void setLobbyCountdown(int lobbyCountdown) {
        this.lobbyCountdown = Math.max(0, lobbyCountdown);
    }

    public int getEndCountdown() {
        return endCountdown;
    }

    public void setEndCountdown(int endCountdown, @NotNull GameResult gameResult) {
        this.endCountdown = endCountdown;
        this.gameResult = gameResult;
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

    @NotNull
    public Set<ArenaUpcomingWave> getUpcomingWaves() {
        return this.waveUpcoming;
    }

    private int getGradualMobsKilled() {
        return this.gradualMobsKilled;
    }

    private void setGradualMobsKilled(int gradualMobsKilled) {
        this.gradualMobsKilled = gradualMobsKilled;
    }

    @NotNull
    public Map<String, double[]> getWaveAmplificatorValues() {
        return this.waveAmplificatorValues;
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

    private void setWaveNumber(int waveNumber) {
        this.waveNumber = waveNumber;
    }

    public int getWaveNextTimeleft() {
        return this.waveNextTimeleft;
    }

    public void setWaveNextTimeleft(int waveNextTimeleft) {
        this.waveNextTimeleft = Math.max(0, waveNextTimeleft);
    }

    /*@NotNull
    @Deprecated
    public Set<ArenaPlayer> getPlayers() {
        return new HashSet<>(this.players);
    }*/

    @NotNull
    public Set<LivingEntity> getMobs() {
        return this.mobs;
    }

    @NotNull
    public Set<LivingEntity> getAllyMobs() {
        return allyMobs;
    }

    @NotNull
    public Set<Item> getGroundItems() {
        return groundItems;
    }

    void reset() {
        this.killMobs(true);
        this.killGroundItems();

        int gameTimeleft = this.getConfig().getGameplayManager().getTimeleft();

        this.setLobbyCountdown(this.getConfig().getGameplayManager().getLobbyTime());
        this.setEndCountdown(-1, GameResult.NONE);
        this.setGameTimeleft(gameTimeleft > 0 ? gameTimeleft * 1000L * 60L : -1);
        this.setGameScore(0);

        this.setWaveNumber(0);
        this.setWaveNextTimeleft(this.getConfig().getWaveManager().getFirstRoundCountdown());
        this.getUpcomingWaves().clear();
        this.getWaveAmplificatorValues().clear();
        this.setWaveMobsTotalAmount(0);

        this.gradualMobsTimer = 0;
        this.setGradualMobsKilled(0);
        this.gradualMobsPrepare = 0;

        ArenaUtils.getHighlightTeam(this, false).ifPresent(team -> {
            team.getEntries().forEach(team::removeEntry);
            team.unregister();
        });

        this.mobsAboutToSpawn = false;

        this.emptySupplyChests();
        this.setState(GameState.WAITING);
        this.updateSigns();
        this.updateHolograms();
    }

    public void stop() {
        if (this.getState() == GameState.INGAME) {
            ArenaGameEndEvent event = new ArenaGameEndEvent(this, this.gameResult);
            this.plugin.getPluginManager().callEvent(event);

            if (this.getConfig().getGameplayManager().isAnnouncesEnabled()) {
                this.plugin.getMessage(Lang.Arena_Game_Announce_End).replace(this.replacePlaceholders()).broadcast();
            }
        }

        this.getPlayers(PlayerType.ALL).forEach(arenaPlayer -> {
            if (!arenaPlayer.isGhost() && this.gameResult == GameResult.VICTORY) {
                arenaPlayer.addStats(StatType.GAMES_WON, 1);
            }

            if (this.getWaveNumber() > 0) {
                arenaPlayer.addStats(StatType.GAMES_PLAYED, 1);
            }

            this.leaveArena(arenaPlayer);
        });

        this.reset();
    }

    public void updateSigns() {
        SignManager signManager = this.plugin.getSignManager();
        if (signManager == null) return;

        signManager.update(SignType.ARENA_JOIN);
    }

    public void updateHolograms() {
        this.getConfig().updateHolograms();
    }

    public void updateGameScore() {
        int oldScore = this.getGameScore();
        int totalScore = this.getPlayers(GameState.INGAME, PlayerType.REAL).stream().mapToInt(ArenaPlayer::getScore).sum();
        this.setGameScore(totalScore);

        //ArenaGameEventType eventType = this.getGameScore() > oldScore ? ArenaGameEventType.SCORE_INCREASED : ArenaGameEventType.SCORE_DECREASED;
        ArenaScoreChangeEvent event = new ArenaScoreChangeEvent(this, oldScore, this.getGameScore());
        plugin.getPluginManager().callEvent(event);
    }

    /*@Deprecated
    // For arena internal mobs - we should use our own mob's goals.
    // For external plugin mobs - they should have their own target goals.
    public void updateMobTarget(@NotNull LivingEntity entity, boolean force) {
        LivingEntity target = plugin.getArenaNMS().getTarget(entity);
        if (target instanceof Player || (!force && target != null)) return;

        ArenaPlayer arenaPlayer = this.getPlayerRandom();
        plugin.getArenaNMS().setTarget(entity, arenaPlayer == null ? null : arenaPlayer.getPlayer());
    }*/

    public void emptySupplyChests() {
        this.getConfig().getSupplyManager().emptyChests();
    }

    public boolean hasPermission(@NotNull Player player) {
        return !this.getConfig().isPermissionRequired() || player.hasPermission(this.getConfig().getPermission());
    }

    public boolean isAboutToSpawnMobs() {
        return this.mobsAboutToSpawn;
    }

    public boolean isAboutToEnd() {
        return this.getEndCountdown() >= 0;
    }

    public boolean isAwaitingNewRound() {
        return this.getWaveNumber() < 1 || (this.getMobs().isEmpty() && this.getUpcomingWaves().isEmpty());
    }

    public boolean isLatestWave() {
        return this.getWaveNumber() == this.getConfig().getWaveManager().getFinalRound();
    }

    // TODO Add another stat type = InGameStatType
    // to display stats after the game, and some of them will be recorded to player global stats.
    @NotNull
    @Deprecated
    public ArenaPlayer getHighScore(int pos) {
        pos = Math.max(0, pos - 1);

        Map<ArenaPlayer, Integer> map = new HashMap<>();
        for (ArenaPlayer arenaPlayer : this.getPlayers(GameState.INGAME, PlayerType.ALL)) {
            map.put(arenaPlayer, arenaPlayer.getScore());
        }

        int count = 0;
        for (ArenaPlayer arenaPlayer : CollectionsUtil.sortDescent(map).keySet()) {
            if (count == pos) {
                return arenaPlayer;
            }
        }

        return new ArrayList<>(this.getPlayers(GameState.INGAME, PlayerType.ALL)).get(0);
    }

    @NotNull
    public Set<ArenaPlayer> getPlayers(@NotNull ArenaTargetType targetType, @NotNull PlayerType playerType) {
        if (targetType == ArenaTargetType.PLAYER_ALL) return this.getPlayers(GameState.INGAME, playerType);
        if (targetType == ArenaTargetType.PLAYER_RANDOM)
            return Stream.of(Rnd.get(this.getPlayers(GameState.INGAME, playerType))).filter(Objects::nonNull).collect(Collectors.toSet());
        return Collections.emptySet();
    }

    @NotNull
    public Set<ArenaPlayer> getPlayers(@NotNull PlayerType playerType) {
        Set<ArenaPlayer> players = new HashSet<>(this.players);
        if (playerType == PlayerType.GHOST) {
            players.removeIf(ArenaPlayer::isReal);
        }
        else if (playerType == PlayerType.REAL) {
            players.removeIf(ArenaPlayer::isGhost);
        }
        return players;
    }

    @NotNull
    public Set<ArenaPlayer> getPlayers(@NotNull GameState state, @NotNull PlayerType playerType) {
        return this.getPlayers(playerType).stream().filter(arenaPlayer -> arenaPlayer.getState() == state).collect(Collectors.toSet());
    }

    @NotNull
    public Set<ArenaPlayer> getDeadPlayers() {
        return this.getPlayers(GameState.INGAME, PlayerType.REAL).stream().filter(ArenaPlayer::isDead).collect(Collectors.toSet());
    }

    @NotNull
    public Set<ArenaPlayer> getAlivePlayers() {
        return this.getPlayers(GameState.INGAME, PlayerType.REAL).stream().filter(Predicate.not(ArenaPlayer::isDead)).collect(Collectors.toSet());
    }

    @Nullable
    public ArenaPlayer getPlayerRandom() {
        Set<ArenaPlayer> alive = this.getAlivePlayers();
        return alive.isEmpty() ? null : Rnd.get(new ArrayList<>(alive));
    }

    public int getMobsAwaitingSpawn() {
        return this.getUpcomingWaves().stream()
            .filter(Predicate.not(ArenaUpcomingWave::isAllMobsSpawned)).mapToInt(ArenaUpcomingWave::getMobsAmount).sum();
    }

    @Nullable
    public LivingEntity getMobRandom() {
        List<LivingEntity> list = new ArrayList<>(this.getMobs());
        return list.isEmpty() ? null : Rnd.get(list);
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

    public void onArenaGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (this.getState() == GameState.INGAME) {
            this.getConfig().getScriptManager().getScripts().forEach(gameScript -> {
                gameScript.onArenaEvent(gameEvent);
            });
        }

        ArenaGameEventType eventType = gameEvent.getEventType();
        if (eventType == ArenaGameEventType.GAME_END_LOSE || eventType == ArenaGameEventType.GAME_END_TIME
            || eventType == ArenaGameEventType.GAME_END_WIN || eventType == ArenaGameEventType.GAME_START
            || eventType == ArenaGameEventType.PLAYER_JOIN || eventType == ArenaGameEventType.PLAYER_LEAVE) {
            this.updateSigns();
            this.updateHolograms();
        }

        if (gameEvent.getEventType() == ArenaGameEventType.REGION_UNLOCKED) {
            ArenaRegionEvent regionEvent = (ArenaRegionEvent) gameEvent;
            this.broadcast(ArenaTargetType.PLAYER_ALL, plugin.getMessage(Lang.ARENA_REGION_UNLOCKED_NOTIFY)
                .replace(regionEvent.getArenaRegion().replacePlaceholders()));
            return;
        }

        if (gameEvent.getEventType() == ArenaGameEventType.MOB_KILLED) {

            if (gameEvent instanceof ArenaMobDeathEvent mobDeathEvent) {
                this.getMobs().remove(mobDeathEvent.getEntity());
            }

            this.countGradual();
        }
    }

    public final void tick() {
        this.tickLobby();
        this.tickGame();
    }

    public void tickLobby() {
        if (this.getState() == GameState.INGAME) return;

        if (this.getState() == GameState.WAITING) {
            if (this.getPlayers(PlayerType.REAL).size() >= this.getConfig().getGameplayManager().getPlayerMinAmount()) {
                this.setState(GameState.READY);

                if (this.getConfig().getGameplayManager().isAnnouncesEnabled()) {
                    plugin().getMessage(Lang.Arena_Game_Announce_Start)
                        .replace(this.replacePlaceholders())
                        .replace(Placeholders.GENERIC_TIME, this.getConfig().getGameplayManager().getLobbyTime())
                        .broadcast();
                }
            }
        }

        if (this.getState() != GameState.READY) return;

        if (this.getPlayers(PlayerType.REAL).size() < this.getConfig().getGameplayManager().getPlayerMinAmount()) {
            this.setState(GameState.WAITING);
            this.setLobbyCountdown(this.getConfig().getGameplayManager().getLobbyTime());
            this.updateSigns();
            return;
        }

        int lobbyCountdown = this.getLobbyCountdown();
        boolean allReady = this.getPlayers(PlayerType.REAL).stream().allMatch(ArenaPlayer::isReady);
        if (Config.LOBBY_READY_DROP_TIMER.get() > 0) {
            if (allReady) {
                if (lobbyCountdown > Config.LOBBY_READY_DROP_TIMER.get()) {
                    lobbyCountdown = Config.LOBBY_READY_DROP_TIMER.get();
                }
            }
            else {
                if (Config.LOBBY_READY_FREEZE_TIMER_WHEN_DROPPED.get() && lobbyCountdown > 0
                    && lobbyCountdown <= Config.LOBBY_READY_DROP_TIMER.get()) {
                    return;
                }
            }
        }

        if (lobbyCountdown <= 0) {
            this.getPlayers(PlayerType.REAL).forEach(this::joinGame);
            this.setState(GameState.INGAME);

            ArenaGameStartEvent event = new ArenaGameStartEvent(this);
            plugin().getPluginManager().callEvent(event);
            return;
        }

        if (lobbyCountdown % 15 == 0 || lobbyCountdown % 10 == 0 || lobbyCountdown <= 10) {
            for (ArenaPlayer arenaPlayer : this.getPlayers(PlayerType.ALL)) {
                plugin().getMessage(Lang.Arena_Game_Lobby_Timer).replace(Placeholders.GENERIC_TIME, lobbyCountdown).send(arenaPlayer.getPlayer());
            }
        }
        this.setLobbyCountdown(lobbyCountdown - 1);
    }

    private void tickGame() {
        if (this.getState() != GameState.INGAME) return;

        if (this.isAboutToEnd()) {
            --this.endCountdown;
            this.showGameStatus();
            if (this.endCountdown <= 0) {
                this.stop();
            }
            return;
        }

        this.showGameStatus();

        // Time is ended, Game Over.
        if (this.getGameTimeleft() > 0) {
            this.setGameTimeleft(Math.max(0L, this.getGameTimeleft() - 1000L));
            if (this.getGameTimeleft() <= 0L) {
                this.setEndCountdown(Config.ARENA_END_COUNTDOWN_DEFEAT.get(), GameResult.DEFEAT);
                this.broadcast(plugin.getMessage(Lang.ARENA_GAME_END_TIMEOUT).replace(this.replacePlaceholders()));
                return;
            }
        }

        this.tickMobs();
        if (!this.tickPlayers()) return;

        if (!this.getConfig().getGameplayManager().isItemPickupEnabled()) {
            this.burnGroundItems();
        }

        if (this.getWaveNextTimeleft() == 0) {
            this.newWave();

            // Stop game if no regions are available.
            ArenaRegion playRegion = this.getConfig().getRegionManager().getFirstUnlocked();
            if (playRegion == null) {
                this.stop();
                return;
            }
            return;
        }

        if (this.isAwaitingNewRound()) {
            if (this.getWaveNextTimeleft() == this.getConfig().getWaveManager().getRoundCountdown()) {

                ArenaWaveCompleteEvent event = new ArenaWaveCompleteEvent(this);
                plugin.getPluginManager().callEvent(event);

                // Join all late joined players at the start of the new round and revive dead ones.
                this.getDeadPlayers().forEach(ArenaPlayer::revive);
                this.getPlayers(GameState.READY, PlayerType.REAL).forEach(this::joinGame);

                if (this.isLatestWave()) {
                    this.setEndCountdown(Config.ARENA_END_COUNTDOWN_VICTORY.get(), GameResult.VICTORY);
                    this.broadcast(plugin.getMessage(Lang.ARENA_GAME_END_COMPLETED).replace(this.replacePlaceholders()));
                    return;
                }
            }
            this.setWaveNextTimeleft(this.getWaveNextTimeleft() - 1);
        }
    }

    private boolean tickPlayers() {
        // No players left, stop the game.
        /*if (this.getPlayers(GameState.INGAME, PlayerType.REAL).isEmpty()) {
            this.stop();
            return false;
        }*/

        if (this.getAlivePlayers().isEmpty() && this.getDeadPlayers().stream().noneMatch(ArenaPlayer::isAutoRevive)) {
            this.setEndCountdown(Config.ARENA_END_COUNTDOWN_DEFEAT.get(), GameResult.DEFEAT);
            this.broadcast(plugin.getMessage(Lang.ARENA_GAME_END_ALL_DEAD).replace(this.replacePlaceholders()));
            return false;
        }

        for (ArenaPlayer arenaPlayer : this.getPlayers(GameState.INGAME, PlayerType.ALL)) {
            arenaPlayer.tick();

            // Notify if player region is inactive anymore.
            if (this.isAwaitingNewRound() && !arenaPlayer.isGhost()) {
                ArenaRegion region = arenaPlayer.getRegion();
                if (region == null || region.isLocked()) {
                    plugin.getMessage(Lang.ARENA_REGION_LOCKED_NOTIFY).send(arenaPlayer.getPlayer());
                }
            }
        }

        return true;
    }

    private void tickMobs() {
        if (this.getConfig().getWaveManager().isGradualSpawnEnabled()) {
            if (++this.gradualMobsTimer % this.getConfig().getWaveManager().getGradualSpawnNextInterval() == 0) {
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
        this.getMobs().forEach(mob -> {
            if (plugin.getArenaNMS().getTarget(mob) == null) {
                ArenaPlayer arenaPlayer = this.getPlayerRandom();
                if (arenaPlayer != null) {
                    plugin.getArenaNMS().setTarget(mob, arenaPlayer.getPlayer());
                }
            }
        });
        //this.getMobs().forEach(mob -> this.updateMobTarget(mob, false));

        // Mobs Highlight feature.
        if (this.getConfig().getGameplayManager().isMobHighlightEnabled()) {
            double mobsLeft = this.getMobs().size();
            double mobsTotal = this.getWaveMobsTotalAmount();
            double mobsPercent = (mobsLeft / mobsTotal) * 100D;
            this.highlightMobs(mobsPercent <= this.getConfig().getGameplayManager().getMobHighlightAmount());
        }
    }

    private void showGameStatus() {
        if (this.isAboutToEnd()) {
            this.broadcast(plugin.getMessage(Lang.ARENA_GAME_STATUS_ENDING).replace(this.replacePlaceholders()));
            return;
        }

        LangMessage message;
        if (this.isAwaitingNewRound()) {
            message = plugin.getMessage(Lang.ARENA_GAME_STATUS_ROUND_PREPARE).replace(this.replacePlaceholders());
        }
        else {
            message = plugin.getMessage(Lang.ARENA_GAME_STATUS_ROUND_ACTIVE).replace(this.replacePlaceholders());
        }
        this.getAlivePlayers().forEach(arenaPlayer -> message.send(arenaPlayer.getPlayer()));
    }

    public boolean checkJoinRequirements(@NotNull Arena arena, @NotNull Player player) {
        ArenaConfig config = this.getConfig();
        if (config.isPermissionRequired() && !this.hasPermission(player)) return false;

        if (!config.getJoinPaymentRequirements().entrySet().stream().allMatch(entry -> {
            return entry.getKey().getBalance(player) >= entry.getValue();
        })) return false;

        if (!config.getJoinLevelRequirements().entrySet().stream().allMatch(entry -> {
            return entry.getKey().getLevel(player) >= entry.getValue();
        })) return false;

        return true;
    }

    public void payJoinRequirements(@NotNull Arena arena, @NotNull Player player) {
        this.getConfig().getJoinPaymentRequirements().forEach((currency, amount) -> currency.take(player, amount));
    }

    public boolean canJoin(@NotNull Player player, boolean notify) {
        // Check if arena is enabled.
        ArenaConfig config = this.getConfig();
        if (!config.isActive() || config.hasProblems()) {
            if (notify) plugin.getMessage(Lang.ARENA_ERROR_DISABLED).replace(this.replacePlaceholders()).send(player);
            return false;
        }

        if (this.isAboutToEnd()) {
            if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_ENDING).replace(this.replacePlaceholders()).send(player);
            return false;
        }

        // Check if arena is in-game.
        if (this.getState() == GameState.INGAME && !player.hasPermission(Perms.BYPASS_ARENA_JOIN_INGAME)) {
            if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_STARTED).replace(this.replacePlaceholders()).send(player);
            return false;
        }

        // Check for max. players.
        int playerMax = config.getGameplayManager().getPlayerMaxAmount();
        if (playerMax > 0 && this.getPlayers(PlayerType.REAL).size() >= playerMax) {
            if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_MAXIMUM).send(player);
            return false;
        }

        // Check for permission.
        if (this.getConfig().isPermissionRequired() && !this.hasPermission(player)) {
            if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_PERMISSION).send(player);
            return false;
        }

        if (!player.hasPermission(Perms.BYPASS_ARENA_JOIN_PAYMENT)) {
            if (!this.getConfig().getJoinPaymentRequirements().entrySet().stream().allMatch(entry -> entry.getKey().getBalance(player) >= entry.getValue())) {
                if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_PAYMENT).replace(this.replacePlaceholders()).send(player);
                return false;
            }
        }

        if (!player.hasPermission(Perms.BYPASS_ARENA_JOIN_LEVEL)) {
            if (!this.getConfig().getJoinLevelRequirements().entrySet().stream().allMatch(entry -> entry.getKey().getLevel(player) >= entry.getValue())) {
                if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_LEVEL).replace(this.replacePlaceholders()).send(player);
                return false;
            }
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

        // Save the player inventory, effects, game modes, etc. before teleporting to the arena.
        PlayerSnapshot.doSnapshot(player);

        arenaPlayer.setTransfer(true);
        player.teleport(this.getConfig().getLocation(ArenaLocationType.LOBBY));
        SimpleParticle.of(Particle.CLOUD).play(player.getLocation(), 0.25, 0.15, 30);
        arenaPlayer.setTransfer(false);

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
            LobbyItem.give(LobbyItem.Type.READY, player);
        }

        // Send messages
        plugin().getMessage(Lang.Arena_Game_Lobby_Enter).replace(this.replacePlaceholders()).send(player);

        this.getPlayers(PlayerType.ALL).forEach(lobbyPlayer -> {
            plugin().getMessage(Lang.Arena_Game_Lobby_Joined).replace(arenaPlayer.replacePlaceholders()).send(lobbyPlayer.getPlayer());
        });

        this.players.add(arenaPlayer);
        this.payJoinRequirements(this, player);
        this.updateSigns();

        // Prepare to start
        if (this.getState() == GameState.INGAME) return true;

        int minPlayers = this.getConfig().getGameplayManager().getPlayerMinAmount();
        int players = this.getPlayers(PlayerType.REAL).size();

        if (players == 1) {
            this.setLobbyCountdown(this.getConfig().getGameplayManager().getLobbyTime());
        }
        if (players < minPlayers) {
            this.getPlayers(PlayerType.ALL).forEach(lobbyPlayer -> {
                plugin().getMessage(Lang.Arena_Game_Lobby_MinPlayers).replace("%min%", minPlayers).send(lobbyPlayer.getPlayer());
            });
        }
        return true;
    }

    private void joinGame(@NotNull ArenaPlayer arenaPlayer) {
        // Check if player's kit is valid and kick from the arena if it's not.
        // Do not kick players who joined after the game start, so they can select their kit as long as they want.
        if (this.getConfig().getGameplayManager().isKitsEnabled()) {
            Kit kit = arenaPlayer.getKit();
            if (kit == null) {
                KitManager kitManager = plugin.getKitManager();

                // If kits are saved to account, then try to select random obtained kit.
                if (kitManager.isSavePurchasedKits()) {
                    ArenaUser user = plugin.getUserManager().getUserData(arenaPlayer.getPlayer());
                    if (!user.getKits().isEmpty()) {
                        String userKit = Rnd.get(new ArrayList<>(user.getKits()));
                        kit = kitManager.getKitById(userKit);
                    }
                }

                // If kits are not saved to account or user don't obtain any kit
                // then try to give the default kit by kit settings.
                if (kit == null || !kit.isAvailable(arenaPlayer, false)) {
                    kit = plugin.getKitManager().getDefaultKit();
                }

                // If even default kit was fail, then it's unlucky game for this user, he will be kicked from the arena.
                if (kit == null) {
                    arenaPlayer.leaveArena();
                    plugin.getMessage(Lang.ARENA_JOIN_ERROR_NO_KIT).send(arenaPlayer.getPlayer());
                    return;
                }
            }
            arenaPlayer.setKit(kit);
            kit.give(arenaPlayer);
        }

        ArenaRegionManager reg = this.getConfig().getRegionManager();
        ArenaRegion regionDefault = this.getWaveNumber() > 0 ? reg.getFirstUnlocked() : reg.getDefaultRegion();

        // Check for valid arena's region.
        if (regionDefault == null) {
            arenaPlayer.leaveArena();
            plugin.getMessage(Lang.ARENA_JOIN_ERROR_NO_REGION).send(arenaPlayer.getPlayer());
            return;
        }

        arenaPlayer.setTransfer(true);
        Player player = arenaPlayer.getPlayer();
        player.teleport(regionDefault.getSpawnLocation());
        arenaPlayer.setTransfer(false);

        // Restore player's health before the game.
        player.setHealth(EntityUtil.getAttribute(player, Attribute.GENERIC_MAX_HEALTH));

        // Add arena scoreboard.
        arenaPlayer.addBoard();
        arenaPlayer.setState(GameState.INGAME);

        plugin().getMessage(Lang.Arena_Game_Notify_Start).send(player);
    }

    public boolean joinSpectate(@NotNull Player player) {
        if (ArenaPlayer.isPlaying(player)) {
            this.plugin.getMessage(Lang.ARENA_JOIN_ERROR_IN_GAME).send(player);
            return false;
        }

        // Check if arena is active and setup.
        if (!this.getConfig().isActive() || this.getConfig().hasProblems()) {
            this.plugin.getMessage(Lang.ARENA_ERROR_DISABLED).replace(this.replacePlaceholders()).send(player);
            return false;
        }

        // Check for player permissions.
        if (!this.hasPermission(player)) {
            this.plugin.getMessage(Lang.ARENA_JOIN_ERROR_PERMISSION).send(player);
            return false;
        }

        // Check if spectating is enabled.
        if (!this.getConfig().getGameplayManager().isSpectateEnabled()) {
            this.plugin.getMessage(Lang.ARENA_JOIN_SPECTATE_ERROR_DISABLED).send(player);
            return false;
        }

        if (this.getState() != GameState.INGAME) {
            this.plugin.getMessage(Lang.ARENA_SPECTATE_ERROR_NOTHING).replace(this.replacePlaceholders()).send(player);
            return false;
        }

        ArenaPlayerSpectateEvent spectateEvent = new ArenaPlayerSpectateEvent(this, player);
        this.plugin.getPluginManager().callEvent(spectateEvent);
        if (spectateEvent.isCancelled()) return false;

        PlayerSnapshot.doSnapshot(player);

        ArenaPlayer arenaPlayer = ArenaPlayer.create(player, this);
        arenaPlayer.setGhost();
        arenaPlayer.setLifes(0);
        arenaPlayer.setState(GameState.INGAME);

        player.teleport(this.getConfig().getLocation(ArenaLocationType.SPECTATE));
        this.plugin.getMessage(Lang.ARENA_JOIN_SPECTATE_SUCCESS).replace(this.replacePlaceholders()).send(player);
        this.players.add(arenaPlayer);

        PlayerSnapshot.clear(player);
        player.setGameMode(GameMode.SPECTATOR);

        return true;
    }

    public boolean leaveArena(@NotNull ArenaPlayer arenaPlayer) {
        if (!arenaPlayer.getArena().equals(this)) {
            return false;
        }

        Player player = arenaPlayer.getPlayer();
        player.closeInventory();                // In case if player have opened any arena GUIs.

        arenaPlayer.setTransfer(true);
        arenaPlayer.removeBoard();                // Remove scoreboard.
        ArenaUtils.removeMobBossBars(player);
        this.players.remove(arenaPlayer);        // Remove player from the arena.

        if (!this.isAboutToEnd() && !this.getConfig().getRewardManager().isRetainOnLeave()) {
            arenaPlayer.getRewards().clear();
        }

        // Restore player data before the game.
        PlayerSnapshot.restore(arenaPlayer);

        // Save stats, give rewards.
        arenaPlayer.saveStats();
        arenaPlayer.getRewards().forEach(reward -> reward.give(player));
        ArenaPlayer.remove(player);

        ArenaPlayerLeaveEvent event = new ArenaPlayerLeaveEvent(this, arenaPlayer);
        this.plugin.getPluginManager().callEvent(event);
        this.plugin.getMessage(Lang.ARENA_GAME_LEAVE_INFO).replace(this.replacePlaceholders()).send(player);

        return true;
    }

    public void broadcast(@NotNull LangMessage message) {
        this.broadcast(PlayerType.ALL, message);
    }

    public void broadcast(@NotNull PlayerType playerType, @NotNull LangMessage message) {
        this.getPlayers(playerType).forEach(arenaPlayer -> {
            message.send(arenaPlayer.getPlayer());
        });
    }

    public void broadcast(@NotNull ArenaTargetType targetType, @NotNull LangMessage message) {
        this.getPlayers(targetType, PlayerType.ALL).forEach(arenaPlayer -> {
            message.send(arenaPlayer.getPlayer());
        });
    }

    public void broadcast(@NotNull ArenaTargetType targetType, @NotNull String message) {
        this.getPlayers(targetType, PlayerType.ALL).forEach(arenaPlayer -> {
            arenaPlayer.getPlayer().sendMessage(message);
        });
    }

    public void runCommand(@NotNull String command, @NotNull ArenaTargetType targetType) {
        if (targetType == ArenaTargetType.GLOBAL) {
            this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command);
            return;
        }

        this.getPlayers(targetType, PlayerType.REAL).forEach(arenaPlayer -> {
            PlayerUtil.dispatchCommand(arenaPlayer.getPlayer(), command);
        });
    }

    public void addGroundItem(@NotNull Item item) {
        this.getGroundItems().removeIf(item2 -> !item2.isValid());
        this.getGroundItems().add(item);
        if (this.getState() == GameState.INGAME && !this.getConfig().getGameplayManager().isItemPickupEnabled()) {
            item.setPickupDelay(Short.MAX_VALUE);
        }
    }

    public void burnGroundItems() {
        SimpleParticle particle = SimpleParticle.of(Particle.SMOKE_NORMAL);
        this.getGroundItems().forEach(item -> {
            if (item.isValid() && item.isOnGround()) {
                particle.play(item.getLocation(), 0.1, 0.05, 15);
                item.remove();
            }
        });
    }

    public void killGroundItems() {
        this.getGroundItems().forEach(Entity::remove);
        this.getGroundItems().clear();
    }

    public void highlightMobs(boolean state) {
        ArenaUtils.getHighlightTeam(this, true).ifPresent(team -> {
            this.getMobs().stream().map(mob -> mob.getUniqueId().toString()).forEach(id -> {
                if (state) team.addEntry(id);
                else team.removeEntry(id);
            });
            this.getMobs().forEach(entity -> entity.setGlowing(state));
        });
    }

    public void killMobs(boolean withAlly) {
        Set<LivingEntity> mobs = new HashSet<>(this.getMobs());
        if (withAlly) mobs.addAll(this.getAllyMobs());

        mobs.forEach(mob -> {
            mob.getLocation();
            mob.setRemoveWhenFarAway(true);
            mob.remove();
        });
        this.getMobs().clear();
        if (withAlly) this.getAllyMobs().clear();
    }

    public void skipWave() {
        this.getUpcomingWaves().clear();
        this.killMobs(false);

        // Call an event that will call arena region and spot triggers.
        ArenaWaveCompleteEvent event = new ArenaWaveCompleteEvent(this);
        this.plugin.getPluginManager().callEvent(event);

        this.setWaveNextTimeleft(1);
    }

    @Deprecated
    public void injectWave(@NotNull ArenaUpcomingWave wave) {
        // TODO prepared mob class
        this.getUpcomingWaves().add(wave);
        this.setWaveMobsTotalAmount(this.getWaveMobsTotalAmount() + wave.getMobsAmount());

        ArenaWaveManager waveManager = this.getConfig().getWaveManager();
        if (!this.getConfig().getWaveManager().isGradualSpawnEnabled()) {
            this.spawnMobs(100D);
        }
    }

    public void newWave() {
        this.getUpcomingWaves().clear();
        this.gradualMobsTimer = 0;
        this.killMobs(this.isLatestWave());

        if (this.isLatestWave()) {
            //this.stop(ArenaEndType.FINISH);
            return;
        }

        this.setWaveNumber(this.getWaveNumber() + 1);
        this.setWaveMobsTotalAmount(0);

        // Move all players that are outside of the active region to the first active one.
        ArenaRegion regionActive = this.getConfig().getRegionManager().getFirstUnlocked();
        this.getPlayers(GameState.INGAME, PlayerType.REAL).forEach(arenaPlayer -> {
            arenaPlayer.addStats(StatType.WAVES_PASSED, 1);

            ArenaRegion regionPlayer = arenaPlayer.getRegion();
            if ((regionPlayer == null || regionPlayer.isLocked()) && regionActive != null) {
                arenaPlayer.getPlayer().teleport(regionActive.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        });

        ArenaWaveStartEvent event = new ArenaWaveStartEvent(this);
        this.plugin.getPluginManager().callEvent(event);

        // Set time until next wave
        this.setWaveNextTimeleft(this.getConfig().getWaveManager().getRoundCountdown());

        //this.setSpawnedMobsAmount(0);
        this.setGradualMobsKilled(0);

        ArenaWaveManager waveManager = this.getConfig().getWaveManager();
        if (waveManager.isGradualSpawnEnabled()) {
            this.spawnMobs(waveManager.getGradualSpawnPercentFirst());
        }

        // New wave is started, store the complete amount of mobs from all upcoming waves.
        // This value is TOTAL amount of mobs that arena is about to spawn this round.
        //this.setWaveMobsTotalAmount(this.getMobsAwaitingSpawn());

        // Spawn mobs for new wave
        //ArenaWaveManager waveManager = this.getConfig().getWaveManager();
        //this.spawnMobs(waveManager.isGradualSpawnEnabled() ? waveManager.getGradualSpawnPercentFirst() : 100D);

        // TODO
        // Quick fix for Piglings target, due to PiglinAi class with a lot of shit
        this.getMobs().forEach(mob -> {
            if (mob instanceof PiglinAbstract piglinAbstract) {
                ArenaPlayer arenaPlayer = this.getPlayerRandom();
                if (arenaPlayer == null) return;
                this.plugin.getArenaNMS().setTarget(piglinAbstract, arenaPlayer.getPlayer());
            }
        });

        this.updateHolograms();

        this.getPlayers(PlayerType.ALL).stream().map(ArenaPlayer::getPlayer).forEach(player -> {
            plugin().getMessage(Lang.Arena_Game_Wave_Start).replace(this.replacePlaceholders()).send(player);
        });
    }

    @Nullable
    public LivingEntity spawnMob(@NotNull ArenaWaveMob waveMob, @NotNull Location location) {
        String mobId = waveMob.getMobId();
        int level = waveMob.getLevel();
        Location spawn = location.getBlock().getRelative(BlockFace.UP).getLocation();

        MobProvider provider = waveMob.getProvider();
        LivingEntity mob = provider.spawn(waveMob.getMobId(), spawn, level).orElse(null);
        if (mob == null) {
            this.plugin.warn("Could not spawn '" + mobId + "' from the '" + waveMob.getArenaWave().getId() + "' arena wave in '" + this.getId() + "' arena!");
            return null;
        }

        mob.setRemoveWhenFarAway(false);
        MobManager.setArena(mob, this);
        MobManager.setLevel(mob, level);
        this.getMobs().add(mob);
        return mob;
    }

    public void spawnMobs(double spawnPercent) {
        this.mobsAboutToSpawn = true;

        if (spawnPercent == 0D) spawnPercent = 100D;
        spawnPercent /= 100D;

        if (Config.DEBUG_MOB_SPAWN.get()) System.out.println("[Spawn Processor] 0. Percent of Total Mobs: " + spawnPercent);

        // Готовим список волн для спавна мобов.
        List<ArenaUpcomingWave> upcomings = new ArrayList<>(this.getUpcomingWaves());

        // Счетчик количества мобов для спавна для каждой волны региона.
        int[] mobsSpawnPerWave = new int[upcomings.size()];

        // Спавним как минимум одного моба всегда.
        int mobsPlannedTotal = (int) Math.max(1D, (double) this.getWaveMobsTotalAmount() * spawnPercent);

        // Спавним "поровну" мобов от каждой волны.
        // Например: При 30% спавна от 100 мобов (х30) и 3-х волнах с кол-вом мобов [5,15,30] (x50) = [3,10,17]
        // 100% of 100 with wave mobs [30,30,40] = x100, 30/30/40 * 1.0 = 30/30/40 = 100 (100%).
        // 70% of 10 with wave mobs [2,2,6] = 2/2/6 * 0.7 = ~1/~1/~5 = 7 (70%).
        for (int counter = 0; counter < mobsSpawnPerWave.length; counter++) {
            ArenaUpcomingWave wave = upcomings.get(counter);

            double mobsWaveTotal = wave.getPreparedMobs().stream().mapToInt(ArenaWaveMob::getAmount).sum();
            //double mobsWaveSpawned = wave.getMobsSpawnedAmount().values().stream().mapToInt(i -> i).sum();
            if (Config.DEBUG_MOB_SPAWN.get())
                System.out.println("[Spawn Processor] 2. Total Mobs for Wave #" + getWaveNumber() + ": " + mobsWaveTotal);
            //System.out.println(wave.getRegionWave().getId() + " mobs have spawned: " + mobsWaveSpawned);

            //mobsWaveTotal -= mobsWaveSpawned;

            mobsSpawnPerWave[counter] = (int) Math.ceil(mobsWaveTotal * spawnPercent);
            if (Arrays.stream(mobsSpawnPerWave).sum() >= mobsPlannedTotal) break;
        }

        if (Config.DEBUG_MOB_SPAWN.get())
            System.out.println("[Spawn Processor] 3. Mobs Per Each Wave:" + Arrays.toString(mobsSpawnPerWave));

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
            ArenaUpcomingWave wave = upcomings.get(counter);
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
            ArenaUpcomingWave waveUpcoming = upcomings.get(counterWave);
            //ArenaRegionWave regionWave = waveUpcoming.getRegionWave();
            List<Location> spawners = waveUpcoming.getPreparedSpawners();
            int mobsWave = mobsSpawnPerWave[counterWave];

            int[] mobsPerSpawner = NumberUtil.splitIntoParts(mobsWave, spawners.size());
            if (Config.DEBUG_MOB_SPAWN.get())
                System.out.println("[Spawn Processor] 7. Mobs Per Region Spawner for '" + waveUpcoming.toString() + "': " + Arrays.toString(mobsPerSpawner));

            for (int counterSpawner = 0; counterSpawner < mobsPerSpawner.length; counterSpawner++) {
                int mobsSpawner = mobsPerSpawner[counterSpawner];

                for (int countSpawned = 0; countSpawned < mobsSpawner; countSpawned++) {
                    if (waveUpcoming.getPreparedMobs().isEmpty()) {
                        if (Config.DEBUG_MOB_SPAWN.get()) System.out.println("Invalid mob");
                        continue;
                    }

                    ArenaWaveMob preparedMob = Rnd.get(waveUpcoming.getPreparedMobs());
                    int mobsSpawned = Math.min(preparedMob.getAmount(), /*mobsSpawner*/ 1);
                    preparedMob.setAmount(preparedMob.getAmount() - mobsSpawned);

                    for (int count = 0; count < mobsSpawned; count++) {
                        this.spawnMob(preparedMob, spawners.get(counterSpawner));
                    }

                    if (preparedMob.getAmount() <= 0) {
                        waveUpcoming.getPreparedMobs().remove(preparedMob);
                    }
                }
            }
        }

        this.getUpcomingWaves().removeIf(ArenaUpcomingWave::isAllMobsSpawned);
        this.mobsAboutToSpawn = false;
    }

    private void countGradual() {
        if (this.getConfig().getWaveManager().isGradualSpawnEnabled()) {
            this.setGradualMobsKilled(this.getGradualMobsKilled() + 1);

            boolean allSpawned = this.getUpcomingWaves().stream().allMatch(ArenaUpcomingWave::isAllMobsSpawned);
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


}
