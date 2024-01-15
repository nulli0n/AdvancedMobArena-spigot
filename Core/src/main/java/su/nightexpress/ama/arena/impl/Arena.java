package su.nightexpress.ama.arena.impl;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.*;
import su.nexmedia.engine.utils.random.Rnd;
import su.nexmedia.engine.utils.values.UniParticle;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.arena.info.MobList;
import su.nightexpress.ama.api.arena.info.PlayerList;
import su.nightexpress.ama.api.arena.type.ArenaLocationType;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.api.event.*;
import su.nightexpress.ama.api.type.*;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.arena.region.RegionManager;
import su.nightexpress.ama.arena.util.ArenaUtils;
import su.nightexpress.ama.arena.util.LobbyItem;
import su.nightexpress.ama.arena.util.PlayerSnapshot;
import su.nightexpress.ama.arena.wave.WaveManager;
import su.nightexpress.ama.arena.wave.impl.WaveMob;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.hook.mob.MobProvider;
import su.nightexpress.ama.kit.impl.Kit;
import su.nightexpress.ama.mob.MobManager;
import su.nightexpress.ama.mob.config.MobsConfig;
import su.nightexpress.ama.sign.SignManager;
import su.nightexpress.ama.sign.type.SignType;
import su.nightexpress.ama.stats.object.StatType;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;

public class Arena implements IArena, Placeholder {

    private final AMA                     plugin;
    private final ArenaConfig             config;
    private final MobList                 mobList;
    private final PlayerList<ArenaPlayer> playerList;
    private final Set<Item>               groundItems;
    private final Set<ArenaUpcomingWave>  upcomingWaves;
    private final Map<String, double[]>   waveAmplificatorValues; // [Amount, Level]
    private final Map<String, BossBar>    bossBarMap;
    private final Map<String, Double>     variableMap;
    private final PlaceholderMap          placeholderMap;
    private final PlaceholderMap          placeholderVarMap;

    private GameState  state;
    private GameResult gameResult;
    private long       gameTimeleft;
    private int        gameScore;

    private boolean mobsAboutToSpawn;

    private int lobbyCountdown;
    private int endCountdown;
    private int nextRoundCountdown;

    private int gradualMobsTimer;
    private int gradualMobsPrepare;
    private int gradualMobsKilled;

    private int roundNumber;
    private int skipRounds;
    private int roundTotalMobsAmount;

    public static final DateTimeFormatter FORMAT_TIMELEFT = DateTimeFormatter.ofPattern("HH:mm:ss");
    // TODO Final Arena Stats Most Damager, Killer, etc

    public Arena(@NotNull ArenaConfig config) {
        this.plugin = config.plugin();
        this.config = config;
        this.mobList = new MobList();
        this.playerList = new PlayerList<>();
        this.groundItems = new HashSet<>();
        this.upcomingWaves = new HashSet<>();
        this.waveAmplificatorValues = new HashMap<>();
        this.bossBarMap = new HashMap<>();
        this.variableMap = new HashMap<>();
        this.placeholderMap = Placeholders.forArena(this);
        this.placeholderVarMap = new PlaceholderMap();
    }

    @NotNull
    public ArenaConfig getConfig() {
        return this.config;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return PlaceholderMap.fusion(this.placeholderMap, this.getVariablePlaceholders());
    }

    @NotNull
    public PlaceholderMap getVariablePlaceholders() {
        return placeholderVarMap;
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
        return this.upcomingWaves;
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

    public int getRoundTotalMobsAmount() {
        return this.roundTotalMobsAmount;
    }

    public void setRoundTotalMobsAmount(int mobsTotalAmount) {
        this.roundTotalMobsAmount = mobsTotalAmount;
    }

    public int getRoundNumber() {
        return this.roundNumber;
    }

    private void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getNextRoundCountdown() {
        return this.nextRoundCountdown;
    }

    public void setNextRoundCountdown(int nextRoundCountdown) {
        this.nextRoundCountdown = Math.max(0, nextRoundCountdown);
    }

    public int getSkipRounds() {
        return skipRounds;
    }

    public void setSkipRounds(int skipRounds) {
        this.skipRounds = Math.max(0, skipRounds);
    }

    @NotNull
    @Override
    public MobList getMobs() {
        return this.mobList;
    }

    @NotNull
    public Set<Item> getGroundItems() {
        return groundItems;
    }

    @NotNull
    public Map<String, Double> getVariableMap() {
        return variableMap;
    }

    void reset() {
        this.killMobs();
        this.killGroundItems();
        this.removeBossBars();
        this.getVariableMap().clear();
        this.getVariablePlaceholders().clear();

        int gameTimeleft = this.getConfig().getGameplaySettings().getTimeleft();

        this.setLobbyCountdown(this.getConfig().getGameplaySettings().getLobbyTime());
        this.setEndCountdown(-1, GameResult.NONE);
        this.setGameTimeleft(gameTimeleft > 0 ? gameTimeleft * 1000L * 60L : -1);
        this.setGameScore(0);

        this.setRoundNumber(0);
        this.setSkipRounds(0);
        this.setNextRoundCountdown(this.getConfig().getWaveManager().getFirstRoundCountdown());
        this.getUpcomingWaves().clear();
        this.getWaveAmplificatorValues().clear();
        this.setRoundTotalMobsAmount(0);

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

            if (this.getConfig().getGameplaySettings().isAnnouncesEnabled()) {
                this.plugin.getMessage(Lang.Arena_Game_Announce_End).replace(this.replacePlaceholders()).broadcast();
            }
        }

        this.getPlayers().all().forEach(arenaPlayer -> {
            if (!arenaPlayer.isGhost() && this.gameResult == GameResult.VICTORY) {
                arenaPlayer.addStats(StatType.GAMES_WON, 1);
            }

            if (!arenaPlayer.isGhost() || arenaPlayer.isDead()) {
                if (this.getRoundNumber() > 0) {
                    arenaPlayer.addStats(StatType.GAMES_PLAYED, 1);
                }
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
        int totalScore = this.getPlayers().select(GameState.INGAME, PlayerType.REAL).stream().mapToInt(ArenaPlayer::getScore).sum();
        this.setGameScore(totalScore);

        ArenaScoreChangeEvent event = new ArenaScoreChangeEvent(this, oldScore, this.getGameScore());
        plugin.getPluginManager().callEvent(event);
    }

    public void emptySupplyChests() {
        this.getConfig().getSupplyManager().emptyChests();
    }

    public boolean hasPermission(@NotNull Player player) {
        return !this.getConfig().isPermissionRequired() || player.hasPermission(this.getConfig().getPermission());
    }

    public boolean isAboutToSpawnMobs() {
        return this.mobsAboutToSpawn;
    }

    public boolean isAboutToSkipRound() {
        return this.getSkipRounds() > 0;
    }

    public boolean isAboutToEnd() {
        return this.getEndCountdown() >= 0;
    }

    public boolean isAwaitingNewRound() {
        return this.getRoundNumber() < 1 || (!this.getMobs().hasAliveEnemies() && this.getUpcomingWaves().isEmpty());
    }

    public boolean isFinalRound() {
        return this.getRoundNumber() == this.getConfig().getWaveManager().getFinalRound();
    }

    // TODO Add another stat type = InGameStatType
    // to display stats after the game, and some of them will be recorded to player global stats.
    @NotNull
    @Deprecated
    public ArenaPlayer getHighScore(int pos) {
        pos = Math.max(0, pos - 1);

        Map<ArenaPlayer, Integer> map = new HashMap<>();
        for (ArenaPlayer arenaPlayer : this.getPlayers().select(GameState.INGAME)) {
            map.put(arenaPlayer, arenaPlayer.getScore());
        }

        int count = 0;
        for (ArenaPlayer arenaPlayer : CollectionsUtil.sortDescent(map).keySet()) {
            if (count == pos) {
                return arenaPlayer;
            }
        }

        return new ArrayList<>(this.getPlayers().select(GameState.INGAME)).get(0);
    }

    @NotNull
    public PlayerList<ArenaPlayer> getPlayers() {
        return playerList;
    }

    public int countKits(@NotNull Kit kit) {
        return (int) this.getPlayers().players().stream().filter(arenaPlayer -> arenaPlayer.getKit() == kit).count();
    }

    public int getMobsAwaitingSpawn() {
        return this.getUpcomingWaves().stream()
            .filter(Predicate.not(ArenaUpcomingWave::isAllMobsSpawned)).mapToInt(ArenaUpcomingWave::getMobsAmount).sum();
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

    @NotNull
    public Map<String, BossBar> getBossBarMap() {
        return bossBarMap;
    }

    @Nullable
    public BossBar getBossBar(@NotNull LivingEntity mob) {
        return getBossBar(mob.getUniqueId().toString());
    }

    @Nullable
    public BossBar getBossBar(@NotNull String id) {
        return this.getBossBarMap().get(id);
    }

    public void createBossBar(@NotNull LivingEntity mob, @NotNull BossBar bossBar) {
        this.createBossBar(mob.getUniqueId().toString(), bossBar);
    }

    public void createBossBar(@NotNull String id, @NotNull BossBar bossBar) {
        this.getPlayers().select(PlayerType.REAL).stream().map(ArenaPlayer::getPlayer).forEach(bossBar::addPlayer);
        this.getBossBarMap().put(id.toLowerCase(), bossBar);
    }

    public void removeBossBar(@NotNull LivingEntity mob) {
        this.removeBossBar(mob.getUniqueId().toString());
    }

    public void removeBossBar(@NotNull String id) {
        BossBar bossBar = this.getBossBarMap().remove(id.toLowerCase());
        if (bossBar == null) return;

        bossBar.removeAll();
    }

    public void removeBossBars(@NotNull Player player) {
        this.getBossBarMap().values().forEach(bossBar -> bossBar.removePlayer(player));
    }

    public void removeBossBars() {
        this.getBossBarMap().values().forEach(BossBar::removeAll);
        this.getBossBarMap().clear();
    }

    // Custom variables - Start

    public void createVariable(@NotNull String name, double initial) {
        if (this.hasVariable(name)) return;

        String id = name.toLowerCase();

        this.getVariableMap().put(id, initial);
        this.getVariablePlaceholders()
            .add(Placeholders.ARENA_VARIABLE.apply(id), () -> NumberUtil.format(this.getVariable(id)))
            .add(Placeholders.ARENA_VARIABLE_RAW.apply(id), () -> String.valueOf(this.getVariable(id)));
    }

    public void setVariable(@NotNull String name, double value) {
        if (!this.hasVariable(name)) return;

        this.getVariableMap().put(name.toLowerCase(), value);
    }

    public void incVariable(@NotNull String name, double amount) {
        this.setVariable(name, this.getVariable(name) + Math.abs(amount));
    }

    public void decVariable(@NotNull String name, double amount) {
        this.setVariable(name, this.getVariable(name) - Math.abs(amount));
    }

    public boolean hasVariable(@NotNull String name) {
        return this.getVariableMap().containsKey(name.toLowerCase());
    }

    public double getVariable(@NotNull String name) {
        return this.getVariableMap().getOrDefault(name.toLowerCase(), 0D);
    }

    // Custom Variables - End

    public void onArenaGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        this.getConfig().getScriptManager().getScripts().forEach(gameScript -> {
            if (!gameScript.isInGameOnly() || this.getState() == GameState.INGAME) {
                gameScript.onArenaEvent(gameEvent);
            }
        });

        GameEventType eventType = gameEvent.getEventType();
        if (eventType == GameEventType.GAME_END_LOSE || eventType == GameEventType.GAME_END_TIME
            || eventType == GameEventType.GAME_END_WIN || eventType == GameEventType.GAME_START
            || eventType == GameEventType.PLAYER_JOIN || eventType == GameEventType.PLAYER_LEAVE) {
            this.updateSigns();
            this.updateHolograms();
        }

        if (gameEvent.getEventType() == GameEventType.REGION_UNLOCKED) {
            ArenaRegionEvent regionEvent = (ArenaRegionEvent) gameEvent;
            this.broadcast(ArenaTargetType.PLAYER_ALL, plugin.getMessage(Lang.ARENA_REGION_UNLOCKED_NOTIFY)
                .replace(regionEvent.getArenaRegion().replacePlaceholders()));
            return;
        }

        if (gameEvent.getEventType() == GameEventType.MOB_KILLED) {

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

    public void tickOpenCloseTimes() {
        if (this.getConfig().hasProblems()) return;

        if (!this.getConfig().isActive() && this.tickTimes(this.getConfig().getAutoOpenTimes())) {
            this.getConfig().setActive(true);
        }
        else if (this.getConfig().isActive() && this.tickTimes(this.getConfig().getAutoCloseTimes())) {
            this.getConfig().setActive(false);
        }
        else return;

        this.getConfig().save();
        this.plugin.getMessage(this.getConfig().isActive() ? Lang.ARENA_SCHEDULER_OPEN_ANNOUNCE : Lang.ARENA_SCHEDULER_CLOSE_ANNOUNCE)
            .replace(this.getConfig().replacePlaceholders())
            .broadcast();
    }

    private boolean tickTimes(@NotNull Map<DayOfWeek, Set<LocalTime>> map) {
        if (map.isEmpty()) return false;

        LocalDateTime closest = LocalDateTime.now();
        while (!map.containsKey(closest.getDayOfWeek())) {
            closest = closest.plusDays(1);
        }

        Set<LocalTime> times = map.get(closest.getDayOfWeek());
        if (times == null || times.isEmpty()) return false;

        LocalTime timeNow = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        return times.stream().anyMatch(stored -> stored.truncatedTo(ChronoUnit.MINUTES).equals(timeNow));
    }

    public void tickLobby() {
        if (this.getState() == GameState.INGAME) return;

        if (this.getState() == GameState.WAITING) {
            if (this.getPlayers().select(PlayerType.REAL).size() >= this.getConfig().getGameplaySettings().getPlayerMinAmount()) {
                this.setState(GameState.READY);

                if (this.getConfig().getGameplaySettings().isAnnouncesEnabled()) {
                    plugin.getMessage(Lang.Arena_Game_Announce_Start)
                        .replace(this.replacePlaceholders())
                        .replace(Placeholders.GENERIC_TIME, this.getConfig().getGameplaySettings().getLobbyTime())
                        .broadcast();
                }
            }
        }

        if (this.getState() != GameState.READY) return;

        if (this.getPlayers().select(PlayerType.REAL).size() < this.getConfig().getGameplaySettings().getPlayerMinAmount()) {
            this.setState(GameState.WAITING);
            this.setLobbyCountdown(this.getConfig().getGameplaySettings().getLobbyTime());
            this.updateSigns();
            return;
        }

        int lobbyCountdown = this.getLobbyCountdown();
        boolean allReady = this.getPlayers().select(PlayerType.REAL).stream().allMatch(ArenaPlayer::isReady);
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
            this.getPlayers().select(PlayerType.REAL).forEach(this::joinGame);
            this.setState(GameState.INGAME);

            ArenaGameStartEvent event = new ArenaGameStartEvent(this);
            plugin.getPluginManager().callEvent(event);
            return;
        }

        if (lobbyCountdown % 15 == 0 || lobbyCountdown % 10 == 0 || lobbyCountdown <= 10) {
            for (ArenaPlayer arenaPlayer : this.getPlayers().all()) {
                plugin.getMessage(Lang.ARENA_LOBBY_COUNTDOWN).replace(Placeholders.GENERIC_TIME, lobbyCountdown).send(arenaPlayer.getPlayer());
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

        if (!this.getConfig().getGameplaySettings().isItemPickupEnabled()) {
            this.burnGroundItems();
        }

        if (this.getNextRoundCountdown() == 0) {
            this.newRound();

            // Stop game if no regions are available.
            Region playRegion = this.getConfig().getRegionManager().getFirstUnlocked();
            if (playRegion == null) {
                this.stop();
                return;
            }
            return;
        }

        if (this.isAboutToSkipRound()) {
            this.getUpcomingWaves().clear();
            this.killMobs(MobFaction.ENEMY);
        }

        if (this.isAwaitingNewRound()) {
            if (this.getNextRoundCountdown() == this.getConfig().getWaveManager().getRoundCountdown()) {

                ArenaWaveCompleteEvent event = new ArenaWaveCompleteEvent(this);
                plugin.getPluginManager().callEvent(event);

                // Join all late joined players at the start of the new round and revive dead ones.
                this.getPlayers().getDead().forEach(ArenaPlayer::revive);
                this.getPlayers().select(GameState.READY, PlayerType.REAL).forEach(this::joinGame);

                if (this.isFinalRound()) {
                    this.setEndCountdown(Config.ARENA_END_COUNTDOWN_VICTORY.get(), GameResult.VICTORY);
                    this.broadcast(plugin.getMessage(Lang.ARENA_GAME_END_COMPLETED).replace(this.replacePlaceholders()));
                    return;
                }
            }
            if (this.isAboutToSkipRound()) {
                this.setNextRoundCountdown(1);
                this.setSkipRounds(this.getSkipRounds() - 1);
            }
            this.setNextRoundCountdown(this.getNextRoundCountdown() - 1);
        }
    }

    private boolean tickPlayers() {
        if (!this.getPlayers().hasAlive() && this.getPlayers().getDead().stream().noneMatch(ArenaPlayer::isAutoRevive)) {
            this.setEndCountdown(Config.ARENA_END_COUNTDOWN_DEFEAT.get(), GameResult.DEFEAT);
            this.broadcast(plugin.getMessage(Lang.ARENA_GAME_END_ALL_DEAD).replace(this.replacePlaceholders()));
            return false;
        }

        for (ArenaPlayer arenaPlayer : this.getPlayers().select(GameState.INGAME)) {
            arenaPlayer.tick();

            // Notify if player region is inactive anymore.
            if (this.isAwaitingNewRound() && !arenaPlayer.isGhost()) {
                Region region = arenaPlayer.getRegion();
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

        this.getMobs().getEnemies().removeIf(mob -> {
            if (!mob.isValid() || mob.isDead()) {
                this.countGradual();
                return true;
            }
            return false;
        });

        // Mobs Highlight feature.
        if (this.getConfig().getGameplaySettings().isMobHighlightEnabled()) {
            double mobsLeft = this.getMobs().getEnemies().size();
            double mobsTotal = this.getRoundTotalMobsAmount();
            double mobsPercent = (mobsLeft / mobsTotal) * 100D;
            this.highlightMobs(mobsPercent <= this.getConfig().getGameplaySettings().getMobHighlightAmount());
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
        this.getPlayers().getAlive().forEach(arenaPlayer -> message.send(arenaPlayer.getPlayer()));
    }

    /*public boolean checkJoinRequirements(@NotNull Arena arena, @NotNull Player player) {
        ArenaConfig config = this.getConfig();
        if (config.isPermissionRequired() && !this.hasPermission(player)) return false;

        if (!config.getPaymentRequirements().entrySet().stream().allMatch(entry -> {
            return entry.getKey().getHandler().getBalance(player) >= entry.getValue();
        })) return false;

        if (!config.getLevelRequirements().entrySet().stream().allMatch(entry -> {
            return entry.getKey().getLevel(player) >= entry.getValue();
        })) return false;

        return true;
    }*/

    public void payJoinRequirements(@NotNull Arena arena, @NotNull Player player) {
        this.getConfig().getPaymentRequirements().forEach((currency, amount) -> currency.getHandler().take(player, amount));
    }

    public void refundJoinPayments(@NotNull Player player) {
        this.getConfig().getPaymentRequirements().forEach((currency, amount) -> currency.getHandler().give(player, amount));
    }

    public void setJoinCooldown(@NotNull Player player) {
        if (!player.hasPermission(Perms.BYPASS_ARENA_JOIN_COOLDOWN)) {
            int cooldown = this.getConfig().getJoinCooldown();
            if (cooldown > 0) {
                long expireDate = System.currentTimeMillis() + cooldown * 1000L;
                ArenaUser user = this.plugin.getUserManager().getUserData(player);
                user.setArenaCooldown(this, expireDate);
                this.plugin.getUserManager().saveUser(user);
            }
        }
    }

    public boolean canJoin(@NotNull Player player, boolean notify) {
        ArenaConfig config = this.getConfig();
        if (!config.isActive() || config.hasProblems()) {
            if (notify) plugin.getMessage(Lang.ARENA_ERROR_DISABLED).replace(this.replacePlaceholders()).send(player);
            return false;
        }

        if (this.isAboutToEnd()) {
            if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_ENDING).replace(this.replacePlaceholders()).send(player);
            return false;
        }

        if (this.getState() == GameState.INGAME && !player.hasPermission(Perms.BYPASS_ARENA_JOIN_INGAME)) {
            if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_STARTED).replace(this.replacePlaceholders()).send(player);
            return false;
        }

        int playerMax = config.getGameplaySettings().getPlayerMaxAmount();
        if (playerMax > 0 && this.getPlayers().select(PlayerType.REAL).size() >= playerMax) {
            if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_MAXIMUM).send(player);
            return false;
        }

        if (this.getConfig().isPermissionRequired() && !this.hasPermission(player)) {
            if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_PERMISSION).send(player);
            return false;
        }

        if (!player.hasPermission(Perms.BYPASS_ARENA_JOIN_COOLDOWN)) {
            ArenaUser user = this.plugin.getUserManager().getUserData(player);
            if (user.isOnCooldown(this)) {
                if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_COOLDOWN)
                    .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTimeLeft(user.getArenaCooldown(this)))
                    .replace(this.replacePlaceholders())
                    .send(player);
                return false;
            }
        }

        if (!player.hasPermission(Perms.BYPASS_ARENA_JOIN_PAYMENT)) {
            if (!this.getConfig().getPaymentRequirements().entrySet().stream().allMatch(entry -> entry.getKey().getHandler().getBalance(player) >= entry.getValue())) {
                if (notify) plugin.getMessage(Lang.ARENA_JOIN_ERROR_PAYMENT).replace(this.replacePlaceholders()).send(player);
                return false;
            }
        }

        if (!player.hasPermission(Perms.BYPASS_ARENA_JOIN_LEVEL)) {
            if (!this.getConfig().getLevelRequirements().entrySet().stream().allMatch(entry -> entry.getKey().getLevel(player) >= entry.getValue())) {
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
        ArenaPlayer arenaPlayer = ArenaPlayer.create(this.plugin, player, this);

        // Save the player inventory, effects, game modes, etc. before teleporting to the arena.
        PlayerSnapshot snapshot = PlayerSnapshot.doSnapshot(player);

        arenaPlayer.setTransfer(true);
        player.teleport(this.getConfig().getLocation(ArenaLocationType.LOBBY));
        UniParticle.of(Particle.CLOUD).play(player.getLocation(), 0.25, 0.15, 30);
        arenaPlayer.setTransfer(false);

        // Pay for join before cleaning.
        this.payJoinRequirements(this, player);

        // Now clear all player's active effects, god modes, etc.
        PlayerSnapshot.clear(player);

        // Adding lobby items
        if (this.getConfig().getGameplaySettings().isKitsEnabled()) {
            player.getInventory().clear();

            //if (Config.KITS_PERMANENT_PURCHASES.get()) {
                LobbyItem.give(LobbyItem.Type.KIT_SELECT, player);
            //}
            LobbyItem.give(LobbyItem.Type.KIT_SHOP, player);
            LobbyItem.give(LobbyItem.Type.EXIT, player);
            LobbyItem.give(LobbyItem.Type.STATS, player);
            LobbyItem.give(LobbyItem.Type.READY, player);
        }
        else {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType().isAir()) continue;
                if (this.getConfig().getGameplaySettings().isBannedItem(item)) {
                    snapshot.getConfiscate().add(new ItemStack(item));
                    item.setAmount(0);
                }
            }
            if (!snapshot.getConfiscate().isEmpty()) {
                this.plugin.getMessage(Lang.ARENA_LOBBY_CONFISACATE).send(player);
            }
        }

        this.plugin.getMessage(Lang.ARENA_LOBBY_JOIN).replace(this.replacePlaceholders()).send(player);
        this.broadcast(plugin.getMessage(Lang.ARENA_LOBBY_PLAYER_JOINED).replace(arenaPlayer.replacePlaceholders()));

        this.getPlayers().add(arenaPlayer);
        this.updateSigns();

        // Prepare to start
        if (this.getState() == GameState.INGAME) return true;

        int minPlayers = this.getConfig().getGameplaySettings().getPlayerMinAmount();
        int players = this.getPlayers().select(PlayerType.REAL).size();
        int need = minPlayers - players;

        if (players == 1) {
            this.setLobbyCountdown(this.getConfig().getGameplaySettings().getLobbyTime());
        }
        if (need > 0) {
            this.broadcast(plugin.getMessage(Lang.ARENA_LOBBY_MIN_PLAYERS).replace(Placeholders.GENERIC_AMOUNT, need));
        }
        return true;
    }

    private void joinGame(@NotNull ArenaPlayer arenaPlayer) {
        // Check if player's kit is valid and kick from the arena if it's not.
        if (this.getConfig().getGameplaySettings().isKitsEnabled()) {
            Kit kit = arenaPlayer.getKit();
            if (kit == null) {
                kit = this.plugin.getKitManager().getAnyAvailable(arenaPlayer);

                // If even default kit was fail, then it's unlucky game for this user, he will be kicked from the arena.
                if (kit == null) {
                    arenaPlayer.leaveArena();
                    plugin.getMessage(Lang.ARENA_JOIN_ERROR_NO_KIT).send(arenaPlayer.getPlayer());
                    return;
                }
                arenaPlayer.setKit(kit);
            }

            Player player = arenaPlayer.getPlayer();
            kit.give(player);
            kit.applyPotionEffects(player);
            kit.applyAttributeModifiers(player);
        }

        RegionManager reg = this.getConfig().getRegionManager();
        Region regionDefault = this.getRoundNumber() > 0 ? reg.getFirstUnlocked() : reg.getDefaultRegion();

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

        plugin.getMessage(Lang.Arena_Game_Notify_Start).send(player);
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
        if (!this.getConfig().getGameplaySettings().isSpectateEnabled()) {
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

        ArenaPlayer arenaPlayer = ArenaPlayer.create(this.plugin, player, this);
        arenaPlayer.setType(PlayerType.GHOST);
        arenaPlayer.setSpectator(true);
        arenaPlayer.setLifes(0);
        arenaPlayer.setState(GameState.INGAME);

        player.teleport(this.getConfig().getLocation(ArenaLocationType.SPECTATE));
        this.plugin.getMessage(Lang.ARENA_JOIN_SPECTATE_SUCCESS).replace(this.replacePlaceholders()).send(player);
        this.getPlayers().add(arenaPlayer);

        PlayerSnapshot.clear(player);
        player.setGameMode(GameMode.SPECTATOR);

        return true;
    }

    public boolean leaveArena(@NotNull ArenaPlayer arenaPlayer) {
        if (!arenaPlayer.getArena().equals(this)) {
            return false;
        }

        Player player = arenaPlayer.getPlayer();
        player.closeInventory();
        arenaPlayer.setTransfer(true);
        arenaPlayer.removeBoard();
        this.removeBossBars(player);
        this.getPlayers().remove(arenaPlayer);

        if (!this.isAboutToEnd() && !this.getConfig().getRewardManager().isKeepOnLeave()) {
            arenaPlayer.getRewards().clear();
        }

        // Remove kit effects.
        Kit kit = arenaPlayer.getKit();
        if (kit != null) {
            kit.resetPotionEffects(player);
            kit.resetAttributeModifiers(player);

            if (!Config.KITS_PERMANENT_PURCHASES.get() && this.getState() == GameState.INGAME && !kit.isFree()) {
                ArenaUser user = this.plugin.getUserManager().getUserData(player);
                user.removeKit(kit);
            }
        }

        // Restore player data.
        PlayerSnapshot.restore(arenaPlayer);

        // Save stats, give rewards.
        arenaPlayer.saveStats();
        arenaPlayer.getRewards().forEach(reward -> reward.give(player));
        ArenaPlayer.remove(player);

        // Refund join payments.
        if (this.getState() != GameState.INGAME) {
            this.refundJoinPayments(player);
        }
        else {
            this.setJoinCooldown(player);
        }

        ArenaPlayerLeaveEvent event = new ArenaPlayerLeaveEvent(this, arenaPlayer);
        this.plugin.getPluginManager().callEvent(event);
        this.plugin.getMessage(Lang.ARENA_GAME_LEAVE_INFO).replace(this.replacePlaceholders()).send(player);
        return true;
    }

    public void broadcast(@NotNull LangMessage message) {
        this.getPlayers().all().forEach(arenaPlayer -> {
            message.send(arenaPlayer.getPlayer());
        });
    }

    public void broadcast(@NotNull PlayerType playerType, @NotNull LangMessage message) {
        this.getPlayers().select(playerType).forEach(arenaPlayer -> {
            message.send(arenaPlayer.getPlayer());
        });
    }

    public void broadcast(@NotNull ArenaTargetType targetType, @NotNull LangMessage message) {
        if (targetType == ArenaTargetType.GLOBAL) {
            message.broadcast();
            return;
        }

        this.getPlayers().select(targetType).forEach(arenaPlayer -> {
            message.send(arenaPlayer.getPlayer());
        });
    }

    public void broadcast(@NotNull ArenaTargetType targetType, @NotNull String message) {
        if (targetType == ArenaTargetType.GLOBAL) {
            this.plugin.getServer().broadcastMessage(message);
            return;
        }

        this.getPlayers().select(targetType).forEach(arenaPlayer -> {
            arenaPlayer.getPlayer().sendMessage(message);
        });
    }

    public void runCommand(@NotNull String command, @NotNull ArenaTargetType targetType) {
        if (targetType == ArenaTargetType.GLOBAL) {
            this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), command);
            return;
        }

        this.getPlayers().select(targetType).forEach(arenaPlayer -> {
            PlayerUtil.dispatchCommand(arenaPlayer.getPlayer(), command);
        });
    }

    public void addGroundItem(@NotNull Item item) {
        this.getGroundItems().removeIf(item2 -> !item2.isValid());
        this.getGroundItems().add(item);
        if (this.getState() == GameState.INGAME && !this.getConfig().getGameplaySettings().isItemPickupEnabled()) {
            item.setPickupDelay(Short.MAX_VALUE);
        }
    }

    public void burnGroundItems() {
        UniParticle particle = UniParticle.of(Particle.SMOKE_NORMAL);
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
            this.getMobs().getEnemies().stream().map(mob -> mob.getUniqueId().toString()).forEach(id -> {
                if (state) team.addEntry(id);
                else team.removeEntry(id);
            });
            this.getMobs().getEnemies().forEach(entity -> entity.setGlowing(state));
        });
    }

    public void killMobs() {
        for (MobFaction faction : MobFaction.values()) {
            this.killMobs(faction);
        }
    }

    public void killMobs(@NotNull MobFaction faction) {
        this.getMobs().removeAll(faction);
        if (faction == MobFaction.ENEMY) {
            this.getPlayers().all().forEach(arenaPlayer -> this.removeBossBars(arenaPlayer.getPlayer()));
        }
    }

    public void injectWave(@NotNull ArenaUpcomingWave wave) {
        this.getUpcomingWaves().add(wave);
        this.setRoundTotalMobsAmount(this.getRoundTotalMobsAmount() + wave.getMobsAmount());

        //WaveManager waveManager = this.getConfig().getWaveManager();
        if (!this.getConfig().getWaveManager().isGradualSpawnEnabled()) {
            this.spawnMobs(100D);
        }
    }

    public void newRound() {
        this.getUpcomingWaves().clear();
        this.gradualMobsTimer = 0;
        if (this.isFinalRound()) {
            this.killMobs();
        }
        else this.killMobs(MobFaction.ENEMY);

        if (this.isFinalRound()) {
            return;
        }

        this.setRoundNumber(this.getRoundNumber() + 1);
        this.setRoundTotalMobsAmount(0);

        ArenaWaveStartEvent event = new ArenaWaveStartEvent(this);
        this.plugin.getPluginManager().callEvent(event);

        // Move all players that are outside of the active region to the first active one.
        Region regionActive = this.getConfig().getRegionManager().getFirstUnlocked();
        this.getPlayers().select(GameState.INGAME, PlayerType.REAL).forEach(arenaPlayer -> {
            arenaPlayer.addStats(StatType.WAVES_PASSED, 1);

            Region regionPlayer = arenaPlayer.getRegion();
            if ((regionPlayer == null || regionPlayer.isLocked()) && regionActive != null) {
                arenaPlayer.getPlayer().teleport(regionActive.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }
        });

        // Set time until next wave
        this.setNextRoundCountdown(this.getConfig().getWaveManager().getRoundCountdown());

        //this.setSpawnedMobsAmount(0);
        this.setGradualMobsKilled(0);

        WaveManager waveManager = this.getConfig().getWaveManager();
        if (waveManager.isGradualSpawnEnabled()) {
            this.spawnMobs(waveManager.getGradualSpawnPercentFirst());
        }

        this.updateHolograms();

        this.getPlayers().all().stream().map(ArenaPlayer::getPlayer).forEach(player -> {
            plugin.getMessage(Lang.Arena_Game_Wave_Start).replace(this.replacePlaceholders()).send(player);
        });
    }

    @Nullable
    public LivingEntity spawnMob(@NotNull WaveMob waveMob, @NotNull Location location) {
        String mobId = waveMob.getMobId();
        int level = waveMob.getLevel();
        Location spawn = location.getBlock().getRelative(BlockFace.UP).getLocation();

        double offset = Math.abs(MobsConfig.SPAWN_OFFSET.get());
        if (offset != 0D) {
            double xAdd = Rnd.getDouble(offset);
            double zAdd = Rnd.getDouble(offset);
            if (Rnd.nextBoolean()) xAdd = (-xAdd);
            if (Rnd.nextBoolean()) zAdd = (-zAdd);

            spawn = spawn.add(xAdd, 0, zAdd);
        }

        MobProvider provider = waveMob.getProvider();
        LivingEntity mob = provider.spawn(this, waveMob.getMobId(), spawn, level).orElse(null);
        if (mob == null) {
            this.plugin.warn("Could not spawn '" + mobId + "' from the '" + waveMob.getArenaWave().getId() + "' arena wave in '" + this.getId() + "' arena!");
            return null;
        }

        mob.setRemoveWhenFarAway(false);
        MobManager.setArena(mob, this);
        MobManager.setProvider(mob, provider, mobId);
        MobManager.setLevel(mob, level);
        this.getMobs().getEnemies().add(mob);
        return mob;
    }

    @Nullable
    public LivingEntity spawnAllyMob(@NotNull EntityType type, @NotNull Location location, @Nullable Player owner) {
        LivingEntity ally = this.plugin.getArenaNMS().spawnMob(this, MobFaction.ALLY, type, location);
        if (ally == null) return null;

        if (owner == null) {
            ArenaPlayer random = this.getPlayers().getRandom();
            if (random != null) owner = random.getPlayer();
        }
        if (owner != null) {
            plugin.getArenaNMS().setFollowGoal(ally, owner);
        }

        MobManager.setArena(ally, this);
        this.getMobs().getAllies().add(ally);
        return ally;
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
        int mobsPlannedTotal = (int) Math.max(1D, (double) this.getRoundTotalMobsAmount() * spawnPercent);

        // Спавним "поровну" мобов от каждой волны.
        // Например: При 30% спавна от 100 мобов (х30) и 3-х волнах с кол-вом мобов [5,15,30] (x50) = [3,10,17]
        // 100% of 100 with wave mobs [30,30,40] = x100, 30/30/40 * 1.0 = 30/30/40 = 100 (100%).
        // 70% of 10 with wave mobs [2,2,6] = 2/2/6 * 0.7 = ~1/~1/~5 = 7 (70%).
        for (int counter = 0; counter < mobsSpawnPerWave.length; counter++) {
            ArenaUpcomingWave wave = upcomings.get(counter);

            double mobsWaveTotal = wave.getPreparedMobs().stream().mapToInt(WaveMob::getAmount).sum();
            //double mobsWaveSpawned = wave.getMobsSpawnedAmount().values().stream().mapToInt(i -> i).sum();
            if (Config.DEBUG_MOB_SPAWN.get())
                System.out.println("[Spawn Processor] 2. Total Mobs for Wave #" + getRoundNumber() + ": " + mobsWaveTotal);
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
            int waveLeft = wave.getPreparedMobs().stream().mapToInt(WaveMob::getAmount).sum();
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
                System.out.println("[Spawn Processor] 7. Mobs Per Region Spawner for '" + waveUpcoming + "': " + Arrays.toString(mobsPerSpawner));

            for (int counterSpawner = 0; counterSpawner < mobsPerSpawner.length; counterSpawner++) {
                int mobsSpawner = mobsPerSpawner[counterSpawner];

                for (int countSpawned = 0; countSpawned < mobsSpawner; countSpawned++) {
                    if (waveUpcoming.getPreparedMobs().isEmpty()) {
                        if (Config.DEBUG_MOB_SPAWN.get()) System.out.println("Invalid mob");
                        continue;
                    }

                    WaveMob preparedMob = Rnd.get(waveUpcoming.getPreparedMobs());
                    int mobsSpawned = Math.min(preparedMob.getAmount(), /*mobsSpawner*/ 1);
                    preparedMob.setAmount(preparedMob.getAmount() - mobsSpawned);

                    for (int count = 0; count < mobsSpawned; count++) {
                        this.spawnMob(preparedMob, spawners.get(counterSpawner));
                        if (mobsWave < spawners.size()) {
                            Collections.shuffle(spawners);
                        }
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

            double lastSpawned = this.getRoundTotalMobsAmount();
            double killedNextPc = this.getConfig().getWaveManager().getGradualSpawnNextKillPercent();
            int killedRaw = this.getGradualMobsKilled();
            int killedNeedRaw = (int) Math.max(1D, lastSpawned * killedNextPc / 100D);

            if (Config.DEBUG_MOB_SPAWN.get()) {
                System.out.println("Game Event: Mob Killed | Gradual Spawning");
                System.out.println("[Gradual] Total Mobs (All Waves): " + lastSpawned);
                System.out.println("[Gradual] Killed (Raw): " + killedRaw);
                System.out.println("[Gradual] Need Killed (Raw): " + killedNeedRaw);
            }
            boolean isEmpty = !this.getMobs().hasAliveEnemies() && this.gradualMobsPrepare == 0;

            if (killedRaw >= killedNeedRaw || isEmpty) {
                this.gradualMobsPrepare++;
                this.setGradualMobsKilled(0);
                if (Config.DEBUG_MOB_SPAWN.get()) System.out.println("[Gradual] Prepared Groups: " + gradualMobsPrepare);
            }
        }
    }
}
