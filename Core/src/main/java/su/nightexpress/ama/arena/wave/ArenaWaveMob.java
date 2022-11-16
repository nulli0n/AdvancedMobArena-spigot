package su.nightexpress.ama.arena.wave;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.Placeholders;

import java.util.function.UnaryOperator;

public class ArenaWaveMob implements IPlaceholder {

    private final ArenaWave arenaWave;

    private String mobId;
    private int    amount;
    private int    level;
    private double chance;

    public ArenaWaveMob(@NotNull ArenaWave arenaWave, @NotNull String mobId, int amount, int level, double chance) {
        this.arenaWave = arenaWave;
        this.setMobId(mobId);
        this.setAmount(amount);
        this.setLevel(level);
        this.setChance(chance);
    }

    public ArenaWaveMob(@NotNull ArenaWaveMob waveMob) {
        this(waveMob.getArenaWave(), waveMob.getMobId(), waveMob.getAmount(), waveMob.getLevel(), waveMob.getChance());
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.ARENA_WAVE_MOB_ID, this.getMobId())
            .replace(Placeholders.ARENA_WAVE_MOB_AMOUNT, String.valueOf(this.getAmount()))
            .replace(Placeholders.ARENA_WAVE_MOB_LEVEL, String.valueOf(this.getLevel()))
            .replace(Placeholders.ARENA_WAVE_MOB_CHANCE, NumberUtil.format(this.getChance()))
            ;
    }

    @NotNull
    public ArenaWave getArenaWave() {
        return arenaWave;
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
