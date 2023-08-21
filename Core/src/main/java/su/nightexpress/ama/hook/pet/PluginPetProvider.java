package su.nightexpress.ama.hook.pet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.ama.api.ArenaAPI;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PluginPetProvider {

    private static final Map<String, PetProvider> PROVIDERS = new HashMap<>();

    public static void registerProvider(@NotNull PetProvider provider) {
        if (!PROVIDERS.containsKey(provider.getName().toLowerCase())) {
            PROVIDERS.put(provider.getName().toLowerCase(), provider);
            ArenaAPI.PLUGIN.info("Registered pet provider: " + provider.getName());
        }
    }

    @Nullable
    public static PetProvider getProvider(@NotNull String name) {
        return PROVIDERS.get(name.toLowerCase());
    }

    @NotNull
    public static Map<String, PetProvider> getProvidersMap() {
        return PROVIDERS;
    }

    @NotNull
    public static Collection<PetProvider> getProviders() {
        return getProvidersMap().values();
    }
}
