package su.nightexpress.ama.arena.wave.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.hook.mob.MobProvider;

public class WaveMob implements Placeholder {

    private final Wave           wave;
    private final PlaceholderMap placeholderMap;

    private MobProvider provider;
    private String      mobId;
    private int         amount;
    private int         level;
    private double      chance;
    private ItemStack icon;

    public WaveMob(@NotNull Wave wave,
                   @NotNull MobProvider provider,
                   @NotNull String mobId, int amount, int level, double chance,
                   @NotNull ItemStack icon) {
        this.wave = wave;
        this.setProvider(provider);
        this.setMobId(mobId);
        this.setAmount(amount);
        this.setLevel(level);
        this.setChance(chance);
        this.setIcon(icon);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.ARENA_WAVE_MOB_ID, this::getMobId)
            .add(Placeholders.ARENA_WAVE_MOB_PROVIDER, () -> this.getProvider().getName())
            .add(Placeholders.ARENA_WAVE_MOB_AMOUNT, () -> String.valueOf(this.getAmount()))
            .add(Placeholders.ARENA_WAVE_MOB_LEVEL, () -> String.valueOf(this.getLevel()))
            .add(Placeholders.ARENA_WAVE_MOB_CHANCE, () -> NumberUtil.format(this.getChance()))
        ;
    }

    @NotNull
    public WaveMob copy() {
        return new WaveMob(
            this.getArenaWave(), this.getProvider(), this.getMobId(),
            this.getAmount(), this.getLevel(), this.getChance(),
            this.getIcon()
        );
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public Wave getArenaWave() {
        return wave;
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

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        if (icon.getType().isAir()) icon = new ItemStack(Material.ZOMBIE_HEAD);

        this.icon = new ItemStack(icon);
    }
}
