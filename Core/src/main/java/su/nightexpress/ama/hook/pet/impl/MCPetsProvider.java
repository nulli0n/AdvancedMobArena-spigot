package su.nightexpress.ama.hook.pet.impl;

import fr.nocsy.mcpets.api.MCPetsAPI;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.impl.MythicMobsHook;
import su.nightexpress.ama.hook.pet.PetProvider;

public class MCPetsProvider implements PetProvider {

    @NotNull
    @Override
    public String getName() {
        return HookId.MC_PETS;
    }

    @Override
    public boolean isPet(@NotNull LivingEntity entity) {
        ActiveMob mob = MythicMobsHook.getMobInstance(entity);
        if (mob == null) return false;

        return MCPetsAPI.getObjectPets().stream().anyMatch(pet -> pet.getActiveMob() == mob);
    }
}
