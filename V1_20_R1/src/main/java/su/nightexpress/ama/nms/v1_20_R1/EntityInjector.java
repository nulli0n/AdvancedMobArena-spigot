package su.nightexpress.ama.nms.v1_20_R1;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EntityInjector {

    private static final Map<EntityType, net.minecraft.world.entity.EntityType<?>> TYPES = new HashMap<>();

    public static void setup() {
        for (EntityType eType : EntityType.values()) {
            if (!eType.isAlive() || !eType.isSpawnable()) continue;

            net.minecraft.world.entity.EntityType<?> typez = switch (eType) {
                case ELDER_GUARDIAN -> net.minecraft.world.entity.EntityType.ELDER_GUARDIAN;
                case WITHER_SKELETON -> net.minecraft.world.entity.EntityType.WITHER_SKELETON;
                case STRAY -> net.minecraft.world.entity.EntityType.STRAY;
                case HUSK -> net.minecraft.world.entity.EntityType.HUSK;
                case ZOMBIE_VILLAGER -> net.minecraft.world.entity.EntityType.ZOMBIE_VILLAGER;
                case SKELETON_HORSE -> net.minecraft.world.entity.EntityType.SKELETON_HORSE;
                case ZOMBIE_HORSE -> net.minecraft.world.entity.EntityType.ZOMBIE_HORSE;
                case DONKEY -> net.minecraft.world.entity.EntityType.DONKEY;
                case MULE -> net.minecraft.world.entity.EntityType.MULE;
                case EVOKER -> net.minecraft.world.entity.EntityType.EVOKER;
                case VEX -> net.minecraft.world.entity.EntityType.VEX;
                case VINDICATOR -> net.minecraft.world.entity.EntityType.VINDICATOR;
                case ILLUSIONER -> net.minecraft.world.entity.EntityType.ILLUSIONER;
                case CREEPER -> net.minecraft.world.entity.EntityType.CREEPER;
                case SKELETON -> net.minecraft.world.entity.EntityType.SKELETON;
                case SPIDER -> net.minecraft.world.entity.EntityType.SPIDER;
                case GIANT -> net.minecraft.world.entity.EntityType.GIANT;
                case ZOMBIE -> net.minecraft.world.entity.EntityType.ZOMBIE;
                case SLIME -> net.minecraft.world.entity.EntityType.SLIME;
                case GHAST -> net.minecraft.world.entity.EntityType.GHAST;
                case ZOMBIFIED_PIGLIN -> net.minecraft.world.entity.EntityType.ZOMBIFIED_PIGLIN;
                case ENDERMAN -> net.minecraft.world.entity.EntityType.ENDERMAN;
                case CAVE_SPIDER -> net.minecraft.world.entity.EntityType.CAVE_SPIDER;
                case SILVERFISH -> net.minecraft.world.entity.EntityType.SILVERFISH;
                case BLAZE -> net.minecraft.world.entity.EntityType.BLAZE;
                case MAGMA_CUBE -> net.minecraft.world.entity.EntityType.MAGMA_CUBE;
                case ENDER_DRAGON -> net.minecraft.world.entity.EntityType.ENDER_DRAGON;
                case WITHER -> net.minecraft.world.entity.EntityType.WITHER;
                case WITCH -> net.minecraft.world.entity.EntityType.WITCH;
                case ENDERMITE -> net.minecraft.world.entity.EntityType.ENDERMITE;
                case GUARDIAN -> net.minecraft.world.entity.EntityType.GUARDIAN;
                case SHULKER -> net.minecraft.world.entity.EntityType.SHULKER;
                case PIG -> net.minecraft.world.entity.EntityType.PIG;
                case SHEEP -> net.minecraft.world.entity.EntityType.SHEEP;
                case COW -> net.minecraft.world.entity.EntityType.COW;
                case CHICKEN -> net.minecraft.world.entity.EntityType.CHICKEN;
                case WOLF -> net.minecraft.world.entity.EntityType.WOLF;
                case MUSHROOM_COW -> net.minecraft.world.entity.EntityType.MOOSHROOM;
                case SNOWMAN -> net.minecraft.world.entity.EntityType.SNOW_GOLEM;
                case OCELOT -> net.minecraft.world.entity.EntityType.OCELOT;
                case IRON_GOLEM -> net.minecraft.world.entity.EntityType.IRON_GOLEM;
                case HORSE -> net.minecraft.world.entity.EntityType.HORSE;
                case RABBIT -> net.minecraft.world.entity.EntityType.RABBIT;
                case POLAR_BEAR -> net.minecraft.world.entity.EntityType.POLAR_BEAR;
                case LLAMA -> net.minecraft.world.entity.EntityType.LLAMA;
                case PARROT -> net.minecraft.world.entity.EntityType.PARROT;
                case VILLAGER -> net.minecraft.world.entity.EntityType.VILLAGER;
                case TURTLE -> net.minecraft.world.entity.EntityType.TURTLE;
                case PHANTOM -> net.minecraft.world.entity.EntityType.PHANTOM;
                case DROWNED -> net.minecraft.world.entity.EntityType.DROWNED;
                case CAT -> net.minecraft.world.entity.EntityType.CAT;
                case PANDA -> net.minecraft.world.entity.EntityType.PANDA;
                case PILLAGER -> net.minecraft.world.entity.EntityType.PILLAGER;
                case RAVAGER -> net.minecraft.world.entity.EntityType.RAVAGER;
                case TRADER_LLAMA -> net.minecraft.world.entity.EntityType.TRADER_LLAMA;
                case WANDERING_TRADER -> net.minecraft.world.entity.EntityType.WANDERING_TRADER;
                case FOX -> net.minecraft.world.entity.EntityType.FOX;
                case BEE -> net.minecraft.world.entity.EntityType.BEE;
                case HOGLIN -> net.minecraft.world.entity.EntityType.HOGLIN;
                case PIGLIN -> net.minecraft.world.entity.EntityType.PIGLIN;
                case STRIDER -> net.minecraft.world.entity.EntityType.STRIDER;
                case ZOGLIN -> net.minecraft.world.entity.EntityType.ZOGLIN;
                case PIGLIN_BRUTE -> net.minecraft.world.entity.EntityType.PIGLIN_BRUTE;
                case AXOLOTL -> net.minecraft.world.entity.EntityType.AXOLOTL;
                case GOAT -> net.minecraft.world.entity.EntityType.GOAT;
                case WARDEN -> net.minecraft.world.entity.EntityType.WARDEN;
                case FROG -> net.minecraft.world.entity.EntityType.FROG;
                case ALLAY -> net.minecraft.world.entity.EntityType.ALLAY;
                default -> null;
            };

            TYPES.put(eType, typez);
        }
    }

    @Nullable
    public static Entity spawnEntity(@NotNull EntityType type, @NotNull Location location) {
        net.minecraft.world.entity.EntityType<?> typez = TYPES.get(type);
        if (typez == null) return null;

        org.bukkit.World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) return null;

        ServerLevel world = ((CraftWorld) bukkitWorld).getHandle();
        Entity entity = typez.spawn(world,
            new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
            MobSpawnType.COMMAND, CreatureSpawnEvent.SpawnReason.CUSTOM);
        if (entity == null) return null;

        //world.addFreshEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.getBukkitEntity().teleport(location);

        return entity;
    }
}
