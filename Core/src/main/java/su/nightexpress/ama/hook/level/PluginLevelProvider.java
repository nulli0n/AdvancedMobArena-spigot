package su.nightexpress.ama.hook.level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.ama.api.ArenaAPI;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PluginLevelProvider {

    private static final Map<String, LevelProvider> PROVIDERS = new HashMap<>();

    public static void registerProvider(@NotNull LevelProvider provider) {
        PROVIDERS.put(provider.getName().toLowerCase(), provider);
        ArenaAPI.PLUGIN.info("Registered level provider: " + provider.getName());
    }

    @Nullable
    public static LevelProvider getProvider(@NotNull String name) {
        return PROVIDERS.get(name.toLowerCase());
    }

    @NotNull
    public static Map<String, LevelProvider> getProvidersMap() {
        return PROVIDERS;
    }

    @NotNull
    public static Collection<LevelProvider> getProviders() {
        return getProvidersMap().values();
    }
}
