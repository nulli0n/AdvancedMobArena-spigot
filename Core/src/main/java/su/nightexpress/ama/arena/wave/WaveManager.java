package su.nightexpress.ama.arena.wave;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Inspectable;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.arena.editor.wave.WaveManagerEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.wave.impl.Wave;
import su.nightexpress.ama.arena.wave.impl.WaveMob;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.hook.mob.MobProvider;
import su.nightexpress.ama.hook.mob.PluginMobProvider;
import su.nightexpress.ama.hook.mob.impl.InternalMobProvider;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WaveManager extends AbstractConfigHolder<AMA> implements ArenaChild, Inspectable, Placeholder {

    public static final String CONFIG_NAME = "waves.yml";

    private final ArenaConfig       arenaConfig;
    private final Map<String, Wave> waveMap;
    private final PlaceholderMap    placeholderMap;

    private int finalRound;
    private int firstRoundCountdown;
    private int roundCountdown;

    private boolean gradualSpawnEnabled;
    private double  gradualSpawnPercentFirst;
    private int     gradualSpawnNextInterval;
    private double  gradualSpawnNextPercent;
    private double  gradualSpawnNextKillPercent;

    private WaveManagerEditor editor;

    public WaveManager(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.waveMap = new HashMap<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.ARENA_WAVES_FIRST_ROUND_COUNTDOWN, () -> NumberUtil.format(this.getFirstRoundCountdown()))
            .add(Placeholders.ARENA_WAVES_ROUND_COUNTDOWN, () -> NumberUtil.format(this.getRoundCountdown()))
            .add(Placeholders.ARENA_WAVES_FINAL_ROUND, () -> this.isInfiniteWaves() ? LangManager.getPlain(Lang.OTHER_INFINITY) : NumberUtil.format(this.getFinalRound()))
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

            ItemStack icon = cfg.getItem(path2 + "Icon");

            Wave wave = new Wave(this.arenaConfig, waveId, new HashSet<>());
            wave.setIcon(icon);

            for (String sId : cfg.getSection(path2 + "Mobs")) {
                String path3 = path2 + "Mobs." + sId + ".";

                /*MobProvider provider = PluginMobProvider.getProviders().stream().filter(pv -> pv.getMobNames().contains(sId))
                    .findFirst().orElse(null);

                if (provider == null) {
                    String providerId = cfg.getString(path3 + "Provider", InternalMobProvider.NAME);
                    provider = PluginMobProvider.getProvider(providerId);
                    if (provider == null) {
                        this.plugin().error("Invalid mob provider: '" + providerId + "' in '" + getArenaConfig().getId() + " arena.");
                        continue;
                    }
                }*/

                String providerId = cfg.getString(path3 + "Provider", InternalMobProvider.NAME);
                MobProvider provider = PluginMobProvider.getProvider(providerId);
                if (provider == null) {
                    this.plugin().error("Invalid mob provider: '" + providerId + "' in '" + getArenaConfig().getId() + "' arena.");
                    continue;
                }

                String mobId = cfg.getString(path3 + "Mob", sId);
                if (!provider.getMobNames().contains(mobId)) {
                    this.plugin.warn("Mob Provider '" + provider.getName() + "' does not contains '" + mobId + "' mob! Arena: '" + getArenaConfig().getId() + "', Wave: '" + waveId + "'.");
                }

                int amount = cfg.getInt(path3 + "Amount");
                int level = cfg.getInt(path3 + "Level");
                double chance = cfg.getDouble(path3 + "Chance");
                ItemStack mobIcon = cfg.getItem(path3 + "Icon");

                WaveMob mob = new WaveMob(wave, provider, mobId, amount, level, chance, mobIcon);
                wave.getMobs().add(mob);
            }

            this.waveMap.put(wave.getId(), wave);
        }
        return true;
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.waveMap.values().forEach(Wave::clear);
        this.waveMap.clear();
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
        this.getWaveMap().forEach((id, wave) -> {
            String path2 = "Waves." + id + ".";

            cfg.setItem(path2 + "Icon", wave.getIcon());

            AtomicInteger count = new AtomicInteger(0);
            wave.getMobs().forEach(mob -> {
                String path3 = path2 + "Mobs." + count.getAndIncrement() + ".";
                cfg.set(path3 + "Provider", mob.getProvider().getName());
                cfg.set(path3 + "Mob", mob.getMobId());
                cfg.set(path3 + "Amount", mob.getAmount());
                cfg.set(path3 + "Level", mob.getLevel());
                cfg.set(path3 + "Chance", mob.getChance());
                cfg.setItem(path3 + "Icon", mob.getIcon());
            });
        });
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    @Override
    public Report getReport() {
        Report report = new Report();

        if (this.getWaveMap().isEmpty()) {
            report.addProblem("No waves created!");
        }
        if (this.getWaves().stream().allMatch(wave -> wave.getMobs().isEmpty())) {
            report.addProblem("All waves has no mobs!");
        }
        else {
            this.getWaves().forEach(wave -> {
                if (wave.getMobs().isEmpty()) {
                    report.addWarn("No mobs in '" + wave.getId() + "' wave!");
                }
            });
        }

        if (!report.hasProblems() && !report.hasWarns()) {
            report.addGood("All " + this.getWaves().size() + " waves are fine!");
        }

        return report;
    }

    @Override
    @NotNull
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @NotNull
    public WaveManagerEditor getEditor() {
        if (this.editor == null) {
            this.editor = new WaveManagerEditor(this);
        }
        return this.editor;
    }

    public boolean createWave(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);

        if (this.getWave(id) != null) return false;

        Wave wave = new Wave(this.getArenaConfig(), id, new HashSet<>());
        this.getWaveMap().put(wave.getId(), wave);
        this.save();
        return true;
    }

    public void removeWave(@NotNull Wave wave) {
        wave.clear();
        this.getWaveMap().remove(wave.getId());
        this.save();
    }

    public boolean isInfiniteWaves() {
        return this.getFinalRound() <= 0;
    }

    @NotNull
    public Map<String, Wave> getWaveMap() {
        return this.waveMap;
    }

    @NotNull
    public Collection<Wave> getWaves() {
        return this.getWaveMap().values();
    }

    @Nullable
    public Wave getWave(@NotNull String id) {
        return this.getWaveMap().get(id.toLowerCase());
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
