package su.nightexpress.ama.mob;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.hooks.external.MythicMobsHook;
import su.nexmedia.engine.utils.PDCUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Keys;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.util.ArenaUtils;
import su.nightexpress.ama.hologram.HologramManager;
import su.nightexpress.ama.hook.mob.PluginMobProvider;
import su.nightexpress.ama.hook.mob.impl.InternalMobProvider;
import su.nightexpress.ama.mob.config.MobConfig;
import su.nightexpress.ama.mob.config.MobsConfig;
import su.nightexpress.ama.mob.kill.MobKillReward;
import su.nightexpress.ama.mob.kill.MobKillStreak;

import java.util.*;

public class MobManager extends AbstractManager<AMA> {

    private Map<String, MobConfig> mobs;

    public MobManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        this.mobs = new HashMap<>();
        this.plugin.getConfigManager().extractResources("/mobs/");

		/*for (EntityType entityType : EntityType.values()) {
			if (!entityType.isAlive() || !entityType.isSpawnable()) continue;

			ArenaCustomMob mob = new ArenaCustomMob(plugin, plugin.getDataFolder() + "/mobs/" + entityType.name().toLowerCase() + ".yml", entityType);
			mob.save();
		}*/

        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + "/mobs/", false)) {
            try {
                MobConfig mob = new MobConfig(plugin, cfg);
                this.mobs.put(mob.getId().toLowerCase(), mob);
            }
            catch (Exception ex) {
                plugin.error("Could not load mob: " + cfg.getFile().getName());
                ex.printStackTrace();
            }
        }
        plugin.info("Mobs Loaded: " + mobs.size());
        plugin.getConfig().initializeOptions(MobsConfig.class);

        // TODO Config option to not use internal mobs provider
        PluginMobProvider.registerProvider(new InternalMobProvider(this.plugin));
    }

    @Override
    public void onShutdown() {
        this.mobs.values().forEach(MobConfig::clear);
        this.mobs.clear();
    }

    @NotNull
    public List<String> getMobIds() {
        return new ArrayList<>(this.mobs.keySet());
    }

    @NotNull
    public Map<String, MobConfig> getMobsMap() {
        return this.mobs;
    }

    @NotNull
    public Collection<MobConfig> getMobs() {
        return this.mobs.values();
    }

    @Nullable
    public MobConfig getMobById(@NotNull String id) {
        return this.mobs.get(id.toLowerCase());
    }

    @Nullable
    public LivingEntity spawnMob(@NotNull String mobId, @NotNull Location location, int level) {
        MobConfig customMob = this.getMobById(mobId);
        if (customMob == null) return null;

        EntityType type = customMob.getEntityType();
        LivingEntity entity = plugin.getArenaNMS().spawnMob(type, location);
        if (entity == null) return null;

        customMob.applySettings(entity, level);
        customMob.applyAttributes(entity, level);
        if (customMob.isBarEnabled()) {
            Arena arena = this.plugin.getArenaManager().getArenaAtLocation(location);
            if (arena != null) {
                ArenaUtils.addMobBossBar(arena, entity, customMob.createOrUpdateBar(entity));
            }
        }
        this.setMobConfig(entity, customMob);
        return entity;
    }

    /*@Nullable
    @Deprecated
    public LivingEntity spawnMob(@NotNull Arena arena, @NotNull ArenaWaveMob waveMob, @NotNull Location loc2) {
        String mobId = waveMob.getMobId();
        Location loc = loc2.clone().add(0, 1, 0); // Fix block position
        MobConfig customMob = this.getMobById(mobId);
        int level = waveMob.getLevel();

        LivingEntity entity;

        if (Hooks.hasMythicMobs() && MythicMobsHook.getMobConfig(mobId) != null) {
            entity = (LivingEntity) MythicMobsHook.spawnMythicMob(mobId, loc, level);
            if (entity == null) return null;
        }
        else if (customMob != null) {
            EntityType type = customMob.getEntityType();
            entity = plugin.getArenaNMS().spawnMob(type, loc);

            if (entity == null) {
                World world = loc.getWorld();
                if (world == null) return null;

                Entity e = world.spawnEntity(loc, type);
                if (!(e instanceof LivingEntity)) {
                    e.remove();
                    return null;
                }

                entity = (LivingEntity) e;
            }
            customMob.applySettings(entity, level);
            customMob.applyAttributes(entity, level);
            if (customMob.isBarEnabled()) {
                ArenaUtils.addMobBossBar(arena, entity, customMob.createOrUpdateBar(entity));
            }
            this.setMobConfig(entity, customMob);
        }
        else return null;

        this.setArena(entity, arena); // Add Arena meta
        this.setLevel(entity, level);

        arena.getMobs().add(entity);
        //arena.updateMobTarget(entity, true);

        entity.setRemoveWhenFarAway(false);
        return entity;
    }*/

    public static void setArena(@NotNull LivingEntity entity, @NotNull Arena arena) {
        PDCUtil.set(entity, Keys.ENTITY_ARENA_ID, arena.getId());
    }

	/*public static void setOutsider(@NotNull LivingEntity entity) {
		PDCUtil.setData(entity, Keys.ENTITY_OUTSIDER, true);
	}*/

    private void setMobConfig(@NotNull LivingEntity entity, @NotNull MobConfig customMob) {
        PDCUtil.set(entity, Keys.ENTITY_MOB_ID, customMob.getId());
    }

    public static void setLevel(@NotNull LivingEntity entity, int level) {
        PDCUtil.set(entity, Keys.ENTITY_MOB_LEVEL, level);
    }

    @NotNull
    @Deprecated // TODO Use provider name & PDC
    public String getMobId(@NotNull LivingEntity entity) {
        if (Hooks.hasMythicMobs() && MythicMobsHook.isMythicMob(entity)) {
            return MythicMobsHook.getMobInternalName(entity).toLowerCase();
        }
        MobConfig customMob = this.getEntityMobConfig(entity);
        if (customMob != null) {
            return customMob.getId();
        }
        return entity.getType().name().toLowerCase();
    }

    @Nullable
    public MobKillReward getMobKillReward(@NotNull LivingEntity entity) {
        if (!MobsConfig.KILL_REWARD_ENABLED.get()) return null;
        return MobsConfig.KILL_REWARD_VALUES.get().getOrDefault(this.getMobId(entity), MobsConfig.KILL_REWARD_VALUES.get().get(Placeholders.DEFAULT));
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
        reward.payment().forEach(((currency, amount) -> {
            text.add(MobsConfig.KILL_REWARD_HOLOGRAM_FORMAT_PAYMENT.get().replace(Placeholders.GENERIC_AMOUNT, currency.format(amount)));
        }));
        if (reward.score() > 0) {
            text.add(MobsConfig.KILL_REWARD_HOLOGRAM_FORMAT_SCORE.get().replace(Placeholders.GENERIC_AMOUNT, String.valueOf(reward.score())));
        }

        hologramManager.create(entity.getEyeLocation(), text, lifetime);
    }

    public boolean isArenaEntity(@NotNull Entity entity) {
        return this.getEntityArena(entity) != null;
    }

	/*public static boolean isOutsider(@NotNull Entity entity) {
		return PDCUtil.getBooleanData(entity, Keys.ENTITY_OUTSIDER);
	}*/

    public boolean isCustomEntity(@NotNull Entity entity) {
        return this.getEntityMobConfig(entity) != null;
    }

    @Nullable
    public Arena getEntityArena(@NotNull Entity entity) {
        String id = PDCUtil.getString(entity, Keys.ENTITY_ARENA_ID).orElse(null);
        return id == null ? null : plugin.getArenaManager().getArenaById(id);
    }

    @Nullable
    public MobConfig getEntityMobConfig(@NotNull Entity entity) {
        String id = PDCUtil.getString(entity, Keys.ENTITY_MOB_ID).orElse(null);
        return id == null ? null : this.getMobById(id);
    }

    @Deprecated // TODO Only PDC
    public int getEntityLevel(@NotNull Entity entity) {
        if (Hooks.hasMythicMobs() && MythicMobsHook.isMythicMob(entity)) {
            return (int) MythicMobsHook.getMobLevel(entity);
        }
        return PDCUtil.getInt(entity, Keys.ENTITY_MOB_LEVEL).orElse(0);
    }
}
