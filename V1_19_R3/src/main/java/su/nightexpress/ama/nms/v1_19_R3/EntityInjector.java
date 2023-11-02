package su.nightexpress.ama.nms.v1_19_R3;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.ama.api.arena.IArena;
import su.nightexpress.ama.api.type.MobFaction;
import su.nightexpress.ama.nms.v1_19_R3.mob.*;

import java.util.HashMap;
import java.util.Map;

public class EntityInjector {

    public static final Map<EntityType, net.minecraft.world.entity.EntityType<? extends Mob>> BASIC   = new HashMap<>();
    public static final Map<EntityType, Class<? extends Mob>>                                 BRAINED = new HashMap<>();
    public static final Map<EntityType, Class<? extends Mob>>                                 CUSTOM  = new HashMap<>();

    public static void setup() {
        BRAINED.put(EntityType.PIGLIN, PiglinMob.class);
        BRAINED.put(EntityType.PIGLIN_BRUTE, PiglinBruteMob.class);
        BRAINED.put(EntityType.HOGLIN, HoglinMob.class);
        BRAINED.put(EntityType.ZOGLIN, ZoglinMob.class);
        BRAINED.put(EntityType.GOAT, GoatMob.class);
        BRAINED.put(EntityType.FROG, FrogMob.class);
        BRAINED.put(EntityType.ALLAY, AllayMob.class);
        BRAINED.put(EntityType.WARDEN, WardenMob.class);

        CUSTOM.put(EntityType.SLIME, SlimeMob.class);
        CUSTOM.put(EntityType.MAGMA_CUBE, MagmaCubeMob.class);
        CUSTOM.put(EntityType.IRON_GOLEM, IronGolemMob.class);

        BASIC.put(EntityType.BEE, net.minecraft.world.entity.EntityType.BEE);
        BASIC.put(EntityType.BLAZE, net.minecraft.world.entity.EntityType.BLAZE);
        BASIC.put(EntityType.CAT, net.minecraft.world.entity.EntityType.CAT);
        BASIC.put(EntityType.CAVE_SPIDER, net.minecraft.world.entity.EntityType.CAVE_SPIDER);
        BASIC.put(EntityType.COW, net.minecraft.world.entity.EntityType.COW);
        BASIC.put(EntityType.CHICKEN, net.minecraft.world.entity.EntityType.CHICKEN);
        BASIC.put(EntityType.CREEPER, net.minecraft.world.entity.EntityType.CREEPER);
        BASIC.put(EntityType.DONKEY, net.minecraft.world.entity.EntityType.DONKEY);
        BASIC.put(EntityType.DROWNED, net.minecraft.world.entity.EntityType.DROWNED);
        BASIC.put(EntityType.ELDER_GUARDIAN, net.minecraft.world.entity.EntityType.ELDER_GUARDIAN);
        BASIC.put(EntityType.ENDERMAN, net.minecraft.world.entity.EntityType.ENDERMAN);
        BASIC.put(EntityType.ENDERMITE, net.minecraft.world.entity.EntityType.ENDERMITE);
        BASIC.put(EntityType.EVOKER, net.minecraft.world.entity.EntityType.EVOKER);
        BASIC.put(EntityType.FOX, net.minecraft.world.entity.EntityType.FOX);
        BASIC.put(EntityType.GHAST, net.minecraft.world.entity.EntityType.GHAST);
        BASIC.put(EntityType.GIANT, net.minecraft.world.entity.EntityType.GIANT);
        BASIC.put(EntityType.GUARDIAN, net.minecraft.world.entity.EntityType.GUARDIAN);
        BASIC.put(EntityType.HORSE, net.minecraft.world.entity.EntityType.HORSE);
        BASIC.put(EntityType.HUSK, net.minecraft.world.entity.EntityType.HUSK);
        BASIC.put(EntityType.ILLUSIONER, net.minecraft.world.entity.EntityType.ILLUSIONER);
        //BASIC.put(EntityType.IRON_GOLEM, net.minecraft.world.entity.EntityType.IRON_GOLEM);
        BASIC.put(EntityType.LLAMA, net.minecraft.world.entity.EntityType.LLAMA);
        //BASIC.put(EntityType.MAGMA_CUBE, net.minecraft.world.entity.EntityType.MAGMA_CUBE);
        BASIC.put(EntityType.MULE, net.minecraft.world.entity.EntityType.MULE);
        BASIC.put(EntityType.MUSHROOM_COW, net.minecraft.world.entity.EntityType.MOOSHROOM);
        BASIC.put(EntityType.OCELOT, net.minecraft.world.entity.EntityType.OCELOT);
        BASIC.put(EntityType.PANDA, net.minecraft.world.entity.EntityType.PANDA);
        BASIC.put(EntityType.PIG, net.minecraft.world.entity.EntityType.PIG);
        BASIC.put(EntityType.PILLAGER, net.minecraft.world.entity.EntityType.PILLAGER);
        BASIC.put(EntityType.PHANTOM, net.minecraft.world.entity.EntityType.PHANTOM);
        BASIC.put(EntityType.POLAR_BEAR, net.minecraft.world.entity.EntityType.POLAR_BEAR);
        BASIC.put(EntityType.RABBIT, net.minecraft.world.entity.EntityType.RABBIT);
        BASIC.put(EntityType.RAVAGER, net.minecraft.world.entity.EntityType.RAVAGER);
        BASIC.put(EntityType.SHEEP, net.minecraft.world.entity.EntityType.SHEEP);
        BASIC.put(EntityType.SHULKER, net.minecraft.world.entity.EntityType.SHULKER);
        BASIC.put(EntityType.SILVERFISH, net.minecraft.world.entity.EntityType.SILVERFISH);
        //BASIC.put(EntityType.SLIME, net.minecraft.world.entity.EntityType.SLIME);
        BASIC.put(EntityType.STRAY, net.minecraft.world.entity.EntityType.STRAY);
        BASIC.put(EntityType.SKELETON, net.minecraft.world.entity.EntityType.SKELETON);
        BASIC.put(EntityType.SKELETON_HORSE, net.minecraft.world.entity.EntityType.SKELETON_HORSE);
        BASIC.put(EntityType.SPIDER, net.minecraft.world.entity.EntityType.SPIDER);
        BASIC.put(EntityType.SNOWMAN, net.minecraft.world.entity.EntityType.SNOW_GOLEM);
        BASIC.put(EntityType.TURTLE, net.minecraft.world.entity.EntityType.TURTLE);
        BASIC.put(EntityType.TRADER_LLAMA, net.minecraft.world.entity.EntityType.TRADER_LLAMA);
        BASIC.put(EntityType.VEX, net.minecraft.world.entity.EntityType.VEX);
        BASIC.put(EntityType.VILLAGER, net.minecraft.world.entity.EntityType.VILLAGER);
        BASIC.put(EntityType.VINDICATOR, net.minecraft.world.entity.EntityType.VINDICATOR);
        BASIC.put(EntityType.WANDERING_TRADER, net.minecraft.world.entity.EntityType.WANDERING_TRADER);
        BASIC.put(EntityType.WITCH, net.minecraft.world.entity.EntityType.WITCH);
        BASIC.put(EntityType.WITHER_SKELETON, net.minecraft.world.entity.EntityType.WITHER_SKELETON);
        BASIC.put(EntityType.WITHER, net.minecraft.world.entity.EntityType.WITHER);
        BASIC.put(EntityType.WOLF, net.minecraft.world.entity.EntityType.WOLF);
        BASIC.put(EntityType.ZOMBIE, net.minecraft.world.entity.EntityType.ZOMBIE);
        BASIC.put(EntityType.ZOMBIE_HORSE, net.minecraft.world.entity.EntityType.ZOMBIE_HORSE);
        BASIC.put(EntityType.ZOMBIE_VILLAGER, net.minecraft.world.entity.EntityType.ZOMBIE_VILLAGER);
        BASIC.put(EntityType.ZOMBIFIED_PIGLIN, net.minecraft.world.entity.EntityType.ZOMBIFIED_PIGLIN);
    }

    @Nullable
    public static Mob spawnEntity(@NotNull IArena arena, @NotNull MobFaction faction, @NotNull EntityType type, @NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        ServerLevel level = ((CraftWorld) world).getHandle();
        return createBrainedOrCustom(arena, faction, type, level, location);
    }

    @Nullable
    private static Mob createBrainedOrCustom(@NotNull IArena arena, @NotNull MobFaction faction, @NotNull EntityType type,
                                             @NotNull ServerLevel level, @NotNull Location location) {
        Class<? extends Mob> clazz = BRAINED.getOrDefault(type, CUSTOM.get(type));
        if (clazz == null) return createBasic(arena, faction, type, level, location);

        try {
            Mob entity = clazz.getConstructor(ServerLevel.class, IArena.class, MobFaction.class).newInstance(level, arena, faction);
            level.addFreshEntity(entity, null);
            return entity;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Nullable
    private static Mob createBasic(@NotNull IArena arena, @NotNull MobFaction faction, @NotNull EntityType type,
                                   @NotNull ServerLevel level, @NotNull Location location) {
        net.minecraft.world.entity.EntityType<? extends Mob> typez = BASIC.get(type);
        if (typez == null) return null;

        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        MobSpawnType spawnType = MobSpawnType.COMMAND;

        Mob mob = typez.create(level, null, null, blockPos, spawnType, false, false);
        if (mob != null) {
            level.addFreshEntity(mob, null);
        }
        return mob;
    }
}
