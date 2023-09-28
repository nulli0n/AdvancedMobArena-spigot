package su.nightexpress.ama.arena.wave;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Problematic;
import su.nightexpress.ama.arena.editor.wave.WaveManagerEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.hook.mob.MobProvider;
import su.nightexpress.ama.hook.mob.PluginMobProvider;
import su.nightexpress.ama.hook.mob.impl.InternalMobProvider;

import java.util.*;

public class ArenaWaveManager extends AbstractConfigHolder<AMA> implements ArenaChild, Problematic, Placeholder {

    public static final String CONFIG_NAME = "waves.yml";

    private final ArenaConfig                     arenaConfig;
    private final Map<String, ArenaWave>          waves;
    private final PlaceholderMap placeholderMap;

    private int finalRound;
    private int firstRoundCountdown;
    private int roundCountdown;

    private boolean gradualSpawnEnabled;
    private double  gradualSpawnPercentFirst;
    private int     gradualSpawnNextInterval;
    private double  gradualSpawnNextPercent;
    private double  gradualSpawnNextKillPercent;

    private WaveManagerEditor editor;

    public ArenaWaveManager(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.waves = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_PROBLEMS, () -> String.join("\n", this.getProblems()))
            .add(Placeholders.ARENA_WAVES_FIRST_ROUND_COUNTDOWN, () -> NumberUtil.format(this.getFirstRoundCountdown()))
            .add(Placeholders.ARENA_WAVES_ROUND_COUNTDOWN, () -> NumberUtil.format(this.getRoundCountdown()))
            .add(Placeholders.ARENA_WAVES_FINAL_ROUND, () -> String.valueOf(this.getFinalRound()))
            .add(Placeholders.ARENA_WAVES_GRADUAL_ENABLED, () -> LangManager.getBoolean(this.isGradualSpawnEnabled()))
            .add(Placeholders.ARENA_WAVES_GRADUAL_FIRST_PERCENT, () -> NumberUtil.format(this.getGradualSpawnPercentFirst()))
            .add(Placeholders.ARENA_WAVES_GRADUAL_NEXT_PERCENT, () -> NumberUtil.format(this.getGradualSpawnNextPercent()))
            .add(Placeholders.ARENA_WAVES_GRADUAL_NEXT_INTERVAL, () -> NumberUtil.format(this.getGradualSpawnNextInterval()))
            .add(Placeholders.ARENA_WAVES_GRADUAL_NEXT_KILL_PERCENT, () -> NumberUtil.format(this.getGradualSpawnNextKillPercent()))
        ;
    }

    @Override
    public boolean load() {
        this.setFinalRound(cfg.getInt("Final_Wave", 100));
        this.setFirstRoundCountdown(cfg.getInt("Delay.First", 5));
        this.setRoundCountdown(cfg.getInt("Delay.Default", 10));

        String path = "Gradual_Spawn.";
        this.setGradualSpawnEnabled(cfg.getBoolean(path + "Enabled"));
        this.setGradualSpawnPercentFirst(cfg.getDouble(path + "First.Amount_Percent", 50));
        this.setGradualSpawnNextInterval(cfg.getInt(path + "Next.Time_Interval", 5));
        this.setGradualSpawnNextPercent(cfg.getInt(path + "Next.Amount_Percent", 20));
        this.setGradualSpawnNextKillPercent(cfg.getInt(path + "Next.For_Killed_Percent", 10));

        for (String waveId : cfg.getSection("Waves")) {
            String path2 = "Waves." + waveId + ".";

            ArenaWave wave = new ArenaWave(this.arenaConfig, waveId, new HashSet<>());
            for (String sId : cfg.getSection(path2 + "Mobs")) {
                String path3 = path2 + "Mobs." + sId + ".";

                MobProvider provider = PluginMobProvider.getProviders().stream().filter(pv -> pv.getMobNames().contains(sId))
                    .findFirst().orElse(null);

                if (provider == null) {
                    String providerId = cfg.getString(path3 + "Provider", InternalMobProvider.NAME);
                    provider = PluginMobProvider.getProvider(providerId);
                    if (provider == null) {
                        this.plugin().error("Invalid mob provider: '" + providerId + "' in '" + getArenaConfig().getId() + " arena.");
                        continue;
                    }
                }

                String mobId = cfg.getString(path3 + "Mob", sId);
                int amount = cfg.getInt(path3 + "Amount");
                int level = cfg.getInt(path3 + "Level");
                double chance = cfg.getDouble(path3 + "Chance");

                ArenaWaveMob mob = new ArenaWaveMob(wave, provider, mobId, amount, level, chance);
                wave.getMobs().add(mob);
            }

            this.waves.put(wave.getId(), wave);
        }
        return true;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.waves.values().forEach(ArenaWave::clear);
        this.waves.clear();
    }

    @Override
    public void onSave() {
        cfg.set("Final_Wave", this.getFinalRound());

        cfg.set("Delay.First", this.getFirstRoundCountdown());
        cfg.set("Delay.Default", this.getRoundCountdown());

        cfg.set("Gradual_Spawn.Enabled", this.isGradualSpawnEnabled());
        cfg.set("Gradual_Spawn.First.Amount_Percent", this.getGradualSpawnPercentFirst());
        cfg.set("Gradual_Spawn.Next.Amount_Percent", this.getGradualSpawnNextPercent());
        cfg.set("Gradual_Spawn.Next.For_Killed_Percent", this.getGradualSpawnNextKillPercent());
        cfg.set("Gradual_Spawn.Next.Time_Interval", this.getGradualSpawnNextInterval());

        cfg.remove("Waves");
        this.getWaves().forEach((id, wave) -> {
            String path2 = "Waves." + id + ".";
            wave.getMobs().forEach(mob -> {
                String path3 = path2 + "Mobs." + UUID.randomUUID() + ".";
                cfg.set(path3 + "Provider", mob.getProvider().getName());
                cfg.set(path3 + "Mob", mob.getMobId());
                cfg.set(path3 + "Amount", mob.getAmount());
                cfg.set(path3 + "Level", mob.getLevel());
                cfg.set(path3 + "Chance", mob.getChance());
            });
        });
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public WaveManagerEditor getEditor() {
        if (this.editor == null) {
            this.editor = new WaveManagerEditor(this);
        }
        return this.editor;
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @Override
    @NotNull
    public List<String> getProblems() {
        List<String> list = new ArrayList<>();
        if (this.getWaves().isEmpty()) {
            list.add(problem("No Waves Created!"));
        }

        return list;
    }

    public boolean isInfiniteWaves() {
        return this.getFinalRound() <= 0;
    }

    @NotNull
    public Map<String, ArenaWave> getWaves() {
        return this.waves;
    }

    @Nullable
    public ArenaWave getWave(@NotNull String id) {
        return this.getWaves().get(id.toLowerCase());
    }

    public int getFinalRound() {
        return this.finalRound;
    }

    public void setFinalRound(int finalRound) {
        this.finalRound = finalRound <= 0 ? -1 : finalRound;
    }

    public int getRoundCountdown() {
        return roundCountdown;
    }

    public void setRoundCountdown(int roundCountdown) {
        this.roundCountdown = Math.max(1, roundCountdown);
    }

    public int getFirstRoundCountdown() {
        return firstRoundCountdown;
    }

    public void setFirstRoundCountdown(int firstRoundCountdown) {
        this.firstRoundCountdown = Math.max(1, firstRoundCountdown);
    }


    public boolean isGradualSpawnEnabled() {
        return this.gradualSpawnEnabled;
    }

    public void setGradualSpawnEnabled(boolean gradualSpawnEnabled) {
        this.gradualSpawnEnabled = gradualSpawnEnabled;
    }

    public double getGradualSpawnPercentFirst() {
        return this.gradualSpawnPercentFirst;
    }

    public void setGradualSpawnPercentFirst(double gradualSpawnPercentFirst) {
        this.gradualSpawnPercentFirst = gradualSpawnPercentFirst;
    }

    public int getGradualSpawnNextInterval() {
        return gradualSpawnNextInterval;
    }

    public void setGradualSpawnNextInterval(int gradualSpawnNextInterval) {
        this.gradualSpawnNextInterval = gradualSpawnNextInterval;
    }

    public double getGradualSpawnNextPercent() {
        return this.gradualSpawnNextPercent;
    }

    public void setGradualSpawnNextPercent(double gradualSpawnNextPercent) {
        this.gradualSpawnNextPercent = Math.min(gradualSpawnNextPercent, this.getGradualSpawnPercentFirst());
    }

    public double getGradualSpawnNextKillPercent() {
        return gradualSpawnNextKillPercent;
    }

    public void setGradualSpawnNextKillPercent(double gradualSpawnNextKillPercent) {
        this.gradualSpawnNextKillPercent = Math.min(gradualSpawnNextKillPercent, this.getGradualSpawnNextPercent());
    }
}
