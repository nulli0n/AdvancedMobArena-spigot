package su.nightexpress.ama.arena.wave;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.hook.mob.MobProvider;

public class ArenaWaveMob implements Placeholder {

    private final ArenaWave arenaWave;
    private final PlaceholderMap placeholderMap;

    private MobProvider provider;
    private String      mobId;
    private int         amount;
    private int         level;
    private double      chance;

    public ArenaWaveMob(@NotNull ArenaWave arenaWave,
                        @NotNull MobProvider provider,
                        @NotNull String mobId, int amount, int level, double chance) {
        this.arenaWave = arenaWave;
        this.setProvider(provider);
        this.setMobId(mobId);
        this.setAmount(amount);
        this.setLevel(level);
        this.setChance(chance);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.ARENA_WAVE_MOB_ID, this::getMobId)
            .add(Placeholders.ARENA_WAVE_MOB_PROVIDER, () -> this.getProvider().getName())
            .add(Placeholders.ARENA_WAVE_MOB_AMOUNT, () -> String.valueOf(this.getAmount()))
            .add(Placeholders.ARENA_WAVE_MOB_LEVEL, () -> String.valueOf(this.getLevel()))
            .add(Placeholders.ARENA_WAVE_MOB_CHANCE, () -> NumberUtil.format(this.getChance()))
        ;
    }

    public ArenaWaveMob(@NotNull ArenaWaveMob waveMob) {
        this(waveMob.getArenaWave(), waveMob.getProvider(), waveMob.getMobId(), waveMob.getAmount(), waveMob.getLevel(), waveMob.getChance());
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public ArenaWave getArenaWave() {
        return arenaWave;
    }

    @NotNull
    public MobProvider getProvider() {
        return provider;
    }

    public void setProvider(@NotNull MobProvider provider) {
        this.provider = provider;
    }

    @NotNull
    public String getMobId() {
        return this.mobId;
    }

    public void setMobId(@NotNull String mobId) {
        this.mobId = mobId;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, amount);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = Math.max(1, level);
    }

    public double getChance() {
        return this.chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }
}
