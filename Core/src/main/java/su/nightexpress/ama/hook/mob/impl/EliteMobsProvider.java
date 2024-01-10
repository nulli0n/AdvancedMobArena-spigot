package su.nightexpress.ama.hook.mob.impl;

import com.magmaguy.elitemobs.config.custombosses.CustomBossesConfig;
import com.magmaguy.elitemobs.config.custombosses.CustomBossesConfigFields;
import com.magmaguy.elitemobs.config.mobproperties.MobPropertiesConfig;
import com.magmaguy.elitemobs.config.mobproperties.MobPropertiesConfigFields;
import com.magmaguy.elitemobs.mobconstructor.EliteEntity;
import com.magmaguy.elitemobs.mobconstructor.custombosses.CustomBossEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.mob.MobProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EliteMobsProvider implements MobProvider {

    @NotNull
    @Override
    public String getName() {
        return HookId.ELITE_MOBS;
    }

    @NotNull
    @Override
    public Optional<LivingEntity> spawn(@NotNull Arena arena, @NotNull String mobId, @NotNull Location location, int level) {
        CustomBossesConfigFields bossFields = CustomBossesConfig.getCustomBoss(mobId);
        if (bossFields != null) {
            CustomBossEntity bossEntity = new CustomBossEntity(bossFields);
            bossEntity.setSpawnLocation(location);
            bossEntity.setLevel(level);
            bossEntity.spawn(false);
            return Optional.of(bossEntity.getLivingEntity());
        }

        EntityType entityType = StringUtil.getEnum(mobId, EntityType.class).orElse(null);
        if (entityType == null || !entityType.isAlive()) return Optional.empty();

        MobPropertiesConfigFields fields = MobPropertiesConfig.getMobProperties().get(entityType);
        if (fields == null || !fields.isEnabled()) return Optional.empty();

        World world = location.getWorld();
        if (world == null) return Optional.empty();

        LivingEntity entity = (LivingEntity) world.spawnEntity(location, entityType);
        EliteEntity eliteEntity = new EliteEntity(entity, level, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return Optional.of(eliteEntity.getLivingEntity());
    }

    @NotNull
    @Override
    public List<String> getMobNames() {
        List<String> list = new ArrayList<>();
        list.addAll(MobPropertiesConfig.getMobProperties().values().stream()
            .filter(MobPropertiesConfigFields::isEnabled)
            .map(MobPropertiesConfigFields::getEntityType).map(Enum::name).map(String::toLowerCase).toList());
        list.addAll(CustomBossesConfig.getCustomBosses().keySet().stream().toList());
        return list;
    }
}
