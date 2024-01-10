package su.nightexpress.ama.hook.pet.impl;

import net.advancedplugins.pets.api.APAPI;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.pet.PetProvider;

public class AdvancedPetsProvider implements PetProvider {

    @NotNull
    @Override
    public String getName() {
        return HookId.ADVANCED_PETS;
    }

    @Override
    public boolean isPet(@NotNull LivingEntity entity) {
        return APAPI.isPlayerPet(entity);
    }
}
