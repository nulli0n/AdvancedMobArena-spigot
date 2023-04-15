package su.nightexpress.ama.nms.v1_19_R2;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.Reflex;
import su.nightexpress.ama.nms.ArenaNMS;

import java.util.*;

public class V1_19_R2 implements ArenaNMS {

    public V1_19_R2() {
        EntityInjector.setup();
    }

    class Nax extends GroundPathNavigation {

        public Nax(net.minecraft.world.entity.Mob var0, Level var1) {
            super(var0, var1);
        }

        @Override
        protected boolean canUpdatePath() {
            return true;
        }
    }

    class Zom extends net.minecraft.world.entity.monster.Zombie {

        public Zom(Level world) {
            super(world);
            this.navigation = new Nax(this, world);
        }
    }

    public Set<Block> createPath(@NotNull Block start, @NotNull Block end) {
        Location locStart = start.getLocation();
        Location locEnd = end.getLocation();
        World world = locStart.getWorld();
        ServerLevel level = ((CraftWorld) world).getHandle();

        BlockPos pos = new BlockPos(locEnd.getX(), locEnd.getY(), locEnd.getZ());

        net.minecraft.world.entity.monster.Zombie zombie = new Zom(level);
        zombie.setPos(start.getX(), start.getY(), start.getZ());
        Path path = zombie.getNavigation().createPath(pos, 0);
        if (path == null) return Collections.emptySet();

        Set<Block> blocks = new HashSet<>();
        for (int index = 0; index < path.getNodeCount(); index++) {
            Node node = path.getNode(index);
            blocks.add(world.getBlockAt(node.x, node.y, node.z));
        }

        return blocks;
    }

    @Override
    public LivingEntity spawnMob(@NotNull EntityType type, @NotNull Location loc) {
        net.minecraft.world.entity.Mob eIns = (net.minecraft.world.entity.Mob) EntityInjector.spawnEntity(type, loc);
        if (eIns == null) return null;

        Entity bukkitEntity = eIns.getBukkitEntity();
        LivingEntity eLiving = (LivingEntity) bukkitEntity;

        this.registerAttribute(eIns, Attributes.ARMOR);
        this.registerAttribute(eIns, Attributes.ARMOR_TOUGHNESS);
        this.setAttribute(eIns, Attributes.ATTACK_DAMAGE, 1D);
        this.registerAttribute(eIns, Attributes.ATTACK_KNOCKBACK);
        this.registerAttribute(eIns, Attributes.ATTACK_SPEED);
        this.setAttribute(eIns, Attributes.FOLLOW_RANGE, 64D);
        this.registerAttribute(eIns, Attributes.FLYING_SPEED);
        this.registerAttribute(eIns, Attributes.JUMP_STRENGTH);
        this.registerAttribute(eIns, Attributes.KNOCKBACK_RESISTANCE);
        this.registerAttribute(eIns, Attributes.MAX_HEALTH);
        this.registerAttribute(eIns, Attributes.MOVEMENT_SPEED);

        if (!(eIns instanceof PathfinderMob pathfinderMob)) return eLiving;

        if (eLiving instanceof Animals) {
            eIns.goalSelector.getAvailableGoals().clear();
            eIns.goalSelector.addGoal(0, new FloatGoal(eIns));
            eIns.goalSelector.addGoal(2, new PathfinderAttack(pathfinderMob));
            eIns.goalSelector.addGoal(8, new LookAtPlayerGoal(pathfinderMob, net.minecraft.world.entity.player.Player.class, 8.0F));
        }
        else if (eIns instanceof net.minecraft.world.entity.monster.Drowned drowned) {
            drowned.goalSelector.getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof ZombieAttackGoal goal1);
            drowned.goalSelector.addGoal(3, new ZombieAttackGoal(drowned, 1D, false));
        }
        eIns.targetSelector.getAvailableGoals().clear();
        eIns.targetSelector.addGoal(1, new HurtByTargetGoal(pathfinderMob, net.minecraft.world.entity.player.Player.class));
        eIns.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(pathfinderMob, net.minecraft.world.entity.player.Player.class, true, e -> true));
        eIns.setAggressive(true);
        return eLiving;
    }

    private void registerAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Attribute att) {
        AttributeInstance instance = handle.getAttribute(att);

        if (instance == null) {
            // Hardcode to register missing entity's attributes.
            AttributeSupplier provider = (AttributeSupplier) Reflex.getFieldValue(handle.getAttributes(), "d");
            if (provider == null) return;

            @SuppressWarnings("unchecked")
            Map<Attribute, AttributeInstance> aMap = (Map<Attribute, AttributeInstance>) Reflex.getFieldValue(provider, "a");
            if (aMap == null) return;

            Map<Attribute, AttributeInstance> aMap2 = new HashMap<>(aMap);
            aMap2.put(att, new AttributeInstance(att, var1 -> {

            }));
            Reflex.setFieldValue(provider, "a", aMap2);
            //System.out.println("Injected Attribute: " + att.getDescriptionId());
        }
    }

    private void setAttribute(@NotNull net.minecraft.world.entity.LivingEntity handle, @NotNull Attribute attribute, double value) {
        this.registerAttribute(handle, attribute);

        AttributeInstance instance = handle.getAttribute(attribute);
        if (instance == null) {
            //System.out.println("Could not create attribute instance: " + attribute.getDescriptionId());
            return;
        }
        instance.setBaseValue(value);
    }

    @Override
    public void setTarget(@NotNull LivingEntity entity, @Nullable LivingEntity target) {
        CraftLivingEntity craftLiving = (CraftLivingEntity) entity;
        if (!(craftLiving.getHandle() instanceof net.minecraft.world.entity.Mob insentient)) return;

        if (target == null) {
            insentient.setTarget(null);
            return;
        }
        insentient.setTarget(((CraftLivingEntity) target).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
    }

    @Override
    public LivingEntity getTarget(@Nullable LivingEntity entity) {
        CraftLivingEntity craftLiving = (CraftLivingEntity) entity;
        if (craftLiving == null || !(craftLiving.getHandle() instanceof net.minecraft.world.entity.Mob insentient)) return null;

        net.minecraft.world.entity.LivingEntity target = insentient.getTarget();
        return target == null ? null : (LivingEntity) target.getBukkitEntity();
    }

    @Override
    public int visualEntityAdd(@NotNull Player player, @NotNull String name, @NotNull Location loc) {
        org.bukkit.World w = loc.getWorld();
        if (w == null) return -1;

        ServerLevel world = ((CraftWorld) w).getHandle();
        net.minecraft.world.entity.decoration.ArmorStand entity = new net.minecraft.world.entity.decoration.ArmorStand(net.minecraft.world.entity.EntityType.ARMOR_STAND, world);
        ArmorStand armorStand = (ArmorStand) entity.getBukkitEntity();

        entity.moveTo(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        entity.setYHeadRot(0);
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        entity.getBukkitEntity().setCustomName(Colorizer.apply(name));
        armorStand.setSmall(true);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setSilent(true);

        ClientboundAddEntityPacket spawnEntityLiving = new ClientboundAddEntityPacket(entity);
        ((CraftPlayer) player).getHandle().connection.send(spawnEntityLiving);

        ClientboundSetEntityDataPacket entityMetadata = new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().packDirty());
        ((CraftPlayer) player).getHandle().connection.send(entityMetadata);

        return entity.getId();
    }

    @Override
    public int visualGlowBlockAdd(@NotNull Player player, @NotNull Location loc) {
        org.bukkit.World w = loc.getWorld();
        if (w == null) return -1;

        ServerLevel world = ((CraftWorld) w).getHandle();
        net.minecraft.world.entity.monster.Shulker entity = new net.minecraft.world.entity.monster.Shulker(net.minecraft.world.entity.EntityType.SHULKER, world);
        Shulker shulker = (Shulker) entity.getBukkitEntity();

        entity.moveTo(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        entity.setYHeadRot(0);
        shulker.setInvisible(true);
        shulker.setInvulnerable(true);
        shulker.setGravity(false);
        shulker.setCustomNameVisible(true);
        shulker.setSilent(true);
        shulker.setGlowing(true);

        ClientboundAddEntityPacket spawnEntityLiving = new ClientboundAddEntityPacket(entity);
        ((CraftPlayer) player).getHandle().connection.send(spawnEntityLiving);

        ClientboundSetEntityDataPacket entityMetadata = new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData().packDirty());
        ((CraftPlayer) player).getHandle().connection.send(entityMetadata);

        return entity.getId();
    }

    @Override
    public void visualEntityRemove(@NotNull Player player, int... ids) {
        ClientboundRemoveEntitiesPacket packetPlayOutEntityDestroy = new ClientboundRemoveEntitiesPacket(ids);
        ((CraftPlayer) player).getHandle().connection.send(packetPlayOutEntityDestroy);
    }
}
