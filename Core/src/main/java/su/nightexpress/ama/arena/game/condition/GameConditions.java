package su.nightexpress.ama.arena.game.condition;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GameConditions {

    private static final Map<String, GameCondition<?>> REGISTRY = new HashMap<>();

    public static final GameCondition<Integer> WAVE_NUMBER = register("wave_number", str -> StringUtil.getInteger(str, 0), (event) -> event.getArena().getWaveNumber());

    @NotNull
    public static <T> GameCondition<T> register(@NotNull String name, @NotNull Function<String, T> parser, @NotNull Function<ArenaGameGenericEvent, T> validator) {
        GameCondition<T> condition = new GameCondition<>(name, parser, validator);
        return register(condition);
    }

    @NotNull
    public static <T> GameCondition<T> register(@NotNull GameCondition<T> condition) {
        REGISTRY.put(condition.getName(), condition);
        return condition;
    }

    @Nullable
    public static GameCondition<?> getByName(@NotNull String name) {
        return REGISTRY.get(name.toLowerCase());
    }
}
