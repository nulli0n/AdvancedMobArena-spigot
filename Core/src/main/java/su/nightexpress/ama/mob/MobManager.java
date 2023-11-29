package su.nightexpress.ama.mob;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.PDCUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Keys;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.hologram.HologramManager;
import su.nightexpress.ama.hook.mob.MobProvider;
import su.nightexpress.ama.hook.mob.PluginMobProvider;
import su.nightexpress.ama.hook.mob.impl.InternalMobProvider;
import su.nightexpress.ama.mob.config.MobConfig;
import su.nightexpress.ama.mob.config.MobsConfig;
import su.nightexpress.ama.mob.kill.MobKillReward;
import su.nightexpress.ama.mob.kill.MobKillStreak;

import java.util.*;

public class MobManager extends AbstractManager<AMA> {

    public static final String DIR_MOBS = "/mobs/";

    private Map<String, MobConfig> mobConfigMap;

    public MobManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        this.mobConfigMap = new HashMap<>();
        this.plugin.getConfigManager().extractResources(DIR_MOBS);

        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + DIR_MOBS, false)) {
            MobConfig mob = new MobConfig(plugin, cfg);
            if (mob.load()) {
                this.mobConfigMap.put(mob.getId().toLowerCase(), mob);
            }
            else this.plugin.warn("AMA mob not loaded: '" + cfg.getFile().getName() + "'!");
        }
        plugin.info("Mobs Loaded: " + mobConfigMap.size());
        plugin.getConfig().initializeOptions(MobsConfig.class);

        PluginMobProvider.registerProvider(new InternalMobProvider(this.plugin));
    }

    @Override
    public void onShutdown() {
        this.mobConfigMap.values().forEach(MobConfig::clear);
        this.mobConfigMap.clear();
    }

    /*private void lazyGen() {
        Set<EntityType> available = new HashSet<>();
        available.addAll(EntityInjector.BASIC.keySet());
        available.addAll(EntityInjector.BRAINED.keySet());

        for (EntityType entityType : available) {
            JYML cfg = new JYML(plugin.getDataFolder() + "/mobs/", entityType.name().toLowerCase() + ".yml");
            cfg.set("Name", StringUtil.capitalizeUnderscored(entityType.name().toLowerCase()));
            cfg.set("Name_Visible", true);
            cfg.set("Entity_Type", entityType.name());
            cfg.set("Level.Minimum", 1);
            cfg.set("Level.Maximum", 10);
            cfg.set("Attributes.Base.GENERIC_MAX_HEALTH", 20D);
            cfg.set("Attributes.Per_Level.GENERIC_MAX_HEALTH", 1D);
            cfg.saveChanges();
        }
    }*/

    public boolean createMobConfig(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getMobConfigById(id) != null) return false;

        JYML cfg = new JYML(this.plugin.getDataFolder() + "/mobs/", id + ".yml");
        MobConfig mobConfig = new MobConfig(plugin, cfg);

        mobConfig.setEntityType(EntityType.ZOMBIE);
        mobConfig.setName(StringUtil.capitalizeUnderscored(mobConfig.getEntityType().name().toLowerCase()));
        mobConfig.setNameVisible(true);
        mobConfig.setLevelMin(1);
        mobConfig.setLevelMax(10);
        mobConfig.setBarEnabled(false);
        //mobConfig.setBarTitle("&c&l" + Placeholders.MOB_NAME + " &7&l- &f&l" + Placeholders.MOB_HEALTH + "&7/&f&l" + Placeholders.MOB_HEALTH_MAX);
        mobConfig.setBarStyle(BarStyle.SEGMENTED_12);
        mobConfig.setBarColor(BarColor.RED);
        mobConfig.getAttributes().put(Attribute.GENERIC_MAX_HEALTH, new double[]{20D, 1D});

        mobConfig.save();
        mobConfig.load();
        this.getMobConfigMap().put(mobConfig.getId(), mobConfig);
        return true;
    }

    @NotNull
    public List<String> getMobIds() {
        return new ArrayList<>(this.mobConfigMap.keySet());
    }

    @NotNull
    public Map<String, MobConfig> getMobConfigMap() {
        return this.mobConfigMap;
    }

    @NotNull
    public Collection<MobConfig> getMobConfigs() {
        return this.mobConfigMap.values();
    }

    @Nullable
    public MobConfig getMobConfigById(@NotNull String id) {
        return this.mobConfigMap.get(id.toLowerCase());
    }

    @Nullable
    public LivingEntity spawnMob(@NotNull Arena arena, @NotNull MobFaction faction, @NotNull String mobId, @NotNull Location location, int level) {
        MobConfig customMob = this.getMobConfigById(mobId);
        if (customMob == null) return null;

        EntityType type = customMob.getEntityType();
        LivingEntity entity = plugin.getArenaNMS().spawnMob(arena, faction, type, location);
        if (entity == null) return null;

        customMob.applySettings(entity, level);
        customMob.applyAttributes(entity, level);
        if (customMob.isBarEnabled()) {
            arena.createBossBar(entity, customMob.createBar(entity));
        }
        this.setMobConfig(entity, customMob);
        return entity;
    }

    public static void setArena(@NotNull LivingEntity entity, @NotNull Arena arena) {
        PDCUtil.set(entity, Keys.ENTITY_ARENA_ID, arena.getId());
    }

    public static void setProvider(@NotNull LivingEntity entity, @NotNull MobProvider provider, @NotNull String name) {
        PDCUtil.set(entity, Keys.ENTITY_MOB_ID, (provider.getName() + ":" + name).toLowerCase());
    }

    private void setMobConfig(@NotNull LivingEntity entity, @NotNull MobConfig customMob) {
        PDCUtil.set(entity, Keys.ENTITY_MOB_ID, customMob.getId());
    }

    public static void setLevel(@NotNull LivingEntity entity, int level) {
        PDCUtil.set(entity, Keys.ENTITY_MOB_LEVEL, level);
    }

    @NotNull
    public static String getMobIdProvider(@NotNull LivingEntity entity) {
        return PDCUtil.getString(entity, Keys.ENTITY_MOB_ID).orElse("");
    }

    @NotNull
    public static String getMobId(@NotNull LivingEntity entity) {
        String[] split = getMobIdProvider(entity).split(":");
        return split.length == 2 ? split[1] : "";
    }

    @NotNull
    public static String getMobProvider(@NotNull LivingEntity entity) {
        String[] split = getMobIdProvider(entity).split(":");
        return split[0];
    }

    @Nullable
    public static MobKillReward getMobKillReward(@NotNull LivingEntity entity) {
        if (!MobsConfig.KILL_REWARD_ENABLED.get()) return null;

        var map = MobsConfig.KILL_REWARD_VALUES.get();
        //System.out.println("getMobIdProvider(entity) = '" + getMobIdProvider(entity) + "'");
        return map.getOrDefault(getMobIdProvider(entity).toLowerCase(), map.get(Placeholders.DEFAULT));
    }

    @Nullable
    public static MobKillStreak getMobKillStreak(int amount) {
        if (!MobsConfig.KILL_STREAK_ENABLED.get()) return null;
        return MobsConfig.KILL_STREAK_TABLE.get().get(amount);
    }

    public void displayMobKillReward(@NotNull LivingEntity entity, @NotNull MobKillReward reward) {
        if (!MobsConfig.KILL_REWARD_HOLOGRAM_ENABLED.get()) return;

        HologramManager hologramManager = this.plugin.getHologramManager();
        if (hologramManager == null) return;

        int lifetime = MobsConfig.KILL_REWARD_HOLOGRAM_LIFETIME.get();
        if (lifetime <= 0) return;

        List<String> text = new ArrayList<>();
        reward.getPayment().forEach(((currency, amount) -> {
            text.add(MobsConfig.KILL_REWARD_HOLOGRAM_FORMAT_PAYMENT.get().replace(Placeholders.GENERIC_AMOUNT, currency.format(amount)));
        }));
        if (reward.getScore() > 0) {
            text.add(MobsConfig.KILL_REWARD_HOLOGRAM_FORMAT_SCORE.get().replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(reward.getScore())));
        }

        hologramManager.create(entity.getEyeLocation(), text, lifetime);
    }

    public boolean isArenaEntity(@NotNull Entity entity) {
        return this.getEntityArena(entity) != null;
    }

    @Nullable
    public Arena getEntityArena(@NotNull Entity entity) {
        String id = PDCUtil.getString(entity, Keys.ENTITY_ARENA_ID).orElse(null);
        return id == null ? null : plugin.getArenaManager().getArenaById(id);
    }

    @Nullable
    public MobConfig getEntityMobConfig(@NotNull LivingEntity entity) {
        return this.getMobConfigById(getMobId(entity));
    }

    public static int getEntityLevel(@NotNull LivingEntity entity) {
        return PDCUtil.getInt(entity, Keys.ENTITY_MOB_LEVEL).orElse(0);
    }

    public boolean updateMobBar(@NotNull LivingEntity entity) {
        Arena arena = this.plugin.getMobManager().getEntityArena(entity);
        if (arena == null) return false;

        BossBar bossBar = arena.getBossBar(entity);
        if (bossBar == null) return false;

        MobConfig config = this.getEntityMobConfig(entity);
        if (config == null) return false;

        this.plugin.runTask(task -> {
            double maxHealth = EntityUtil.getAttribute(entity, Attribute.GENERIC_MAX_HEALTH);
            double percent = Math.max(0D, Math.min(1D, entity.getHealth() / maxHealth));

            bossBar.setTitle(config.getBarTitle(entity));
            bossBar.setProgress(percent);
        });

        return true;
    }
}
