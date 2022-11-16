package su.nightexpress.ama.nms;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ArenaNMS {
	
    LivingEntity spawnMob(@NotNull EntityType type, @NotNull Location loc);

    void setTarget(@NotNull LivingEntity mob, @Nullable LivingEntity target);
    
    LivingEntity getTarget(@NotNull LivingEntity entity);
    
    //
    
    int visualEntityAdd(@NotNull Player player, @NotNull String name, @NotNull Location loc);
    
    int visualGlowBlockAdd(@NotNull Player player, @NotNull Location loc);
    
    void visualEntityRemove(@NotNull Player player, int... id);
}
