package su.nightexpress.ama.mob.config;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.ArenaPlayer;

import java.util.Set;

public class MobHealthBar {

    private boolean  isEnabled;
    private String   title;
    private BarStyle style;
    private BarColor color;

    public MobHealthBar(
        boolean isEnabled,
        @NotNull String title,
        @NotNull BarStyle style,
        @NotNull BarColor color
    ) {
        this.setEnabled(isEnabled);
        this.setTitle(title);
        this.setStyle(style);
        this.setColor(color);
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void setTitle(@NotNull String title) {
        this.title = StringUtil.color(title);
    }

    @NotNull
    public String getTitle() {
        return this.title;
    }

    @NotNull
    public BarStyle getStyle() {
        return this.style;
    }

    public void setStyle(@NotNull BarStyle style) {
        this.style = style;
    }

    @NotNull
    public BarColor getColor() {
        return this.color;
    }

    public void setColor(@NotNull BarColor color) {
        this.color = color;
    }

    @NotNull
    private String replaceTitle(@NotNull String str, @NotNull LivingEntity boss) {
        return str
            .replace(Placeholders.MOB_HEALTH, NumberUtil.format(boss.getHealth()))
            .replace(Placeholders.MOB_HEALTH_MAX, NumberUtil.format(getMaxHealth(boss)))
            .replace(Placeholders.MOB_NAME, boss.getCustomName() != null ? boss.getCustomName() : boss.getName())
            ;
    }

    public void create(@NotNull Set<ArenaPlayer> players, @NotNull LivingEntity boss) {
        String title = this.replaceTitle(this.getTitle(), boss);

        BossBar bar = Bukkit.getServer().createBossBar(title, this.getColor(), this.getStyle(), BarFlag.DARKEN_SKY);
        bar.setProgress(1D);
        bar.setVisible(true);

        players.forEach(arenaPlayer -> arenaPlayer.addMobHealthBar(boss, bar));
    }

    public void update(@NotNull Set<ArenaPlayer> players, @NotNull LivingEntity boss) {
        String title = this.replaceTitle(this.getTitle(), boss);
        double percent = Math.max(0D, Math.min(1D, boss.getHealth() / this.getMaxHealth(boss)));

        for (ArenaPlayer arenaPlayer : players) {
            BossBar bar = arenaPlayer.getMobHealthBar(boss.getUniqueId());
            if (bar == null) continue;

            bar.setTitle(title);
            bar.setProgress(percent);
        }
    }

    public void remove(@NotNull Set<ArenaPlayer> players, @NotNull LivingEntity boss) {
        players.forEach(arenaPlayer -> arenaPlayer.removeMobHealthBar(boss));
    }

    private double getMaxHealth(@NotNull LivingEntity entity) {
        AttributeInstance aInstance = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (aInstance == null) return entity.getHealth();

        return aInstance.getValue();
    }
}
