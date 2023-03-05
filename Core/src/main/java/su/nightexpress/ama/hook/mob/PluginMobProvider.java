package su.nightexpress.ama.hook.mob;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.ama.api.ArenaAPI;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PluginMobProvider {

    private static final Map<String, MobProvider> PROVIDERS = new HashMap<>();

    public static void registerProvider(@NotNull MobProvider provider) {
        PROVIDERS.put(provider.getName().toLowerCase(), provider);
        ArenaAPI.PLUGIN.info("Registered mob provider: " + provider.getName());
    }

    @Nullable
    public static MobProvider getProvider(@NotNull String name) {
        return PROVIDERS.get(name.toLowerCase());
    }

    @NotNull
    public static Map<String, MobProvider> getProvidersMap() {
        return PROVIDERS;
    }

    @NotNull
    public static Collection<MobProvider> getProviders() {
        return getProvidersMap().values();
    }
}
