package su.nightexpress.ama.hook.pet.impl;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.pet.PetProvider;
import su.nightexpress.combatpets.api.pet.IPetHolder;

public class CombatPetsProvider implements PetProvider {

    @NotNull
    @Override
    public String getName() {
        return HookId.COMBAT_PETS;
    }

    @Override
    public boolean isPet(@NotNull LivingEntity entity) {
        return IPetHolder.ofMob(entity) != null;
    }
}
