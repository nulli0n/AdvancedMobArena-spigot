package su.nightexpress.ama.arena.script.action;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Parameters {

    private static final Map<String, Parameter<?>> REGISTRY = new HashMap<>();

    public static final Parameter<String> WAVE = asString("wave");
    public static final Parameter<String> SPAWNERS = asString("spawners");
    public static final Parameter<String> REGION = asString("region");
    public static final Parameter<String> SHOP_CATEGORY = asString("shop_category");
    public static final Parameter<String> SHOP_PRODUCT = asString("shop_product");
    public static final Parameter<String> REWARD = asString("reward");
    public static final Parameter<String> SPOT = asString("spot");
    public static final Parameter<String> STATE = asString("state");
    public static final Parameter<String>          NAME   = asString("name");
    public static final Parameter<String>          MESSAGE   = asString("message");
    public static final Parameter<ArenaTargetType> TARGET = register("target",
        str -> StringUtil.getEnum(str, ArenaTargetType.class).orElse(ArenaTargetType.GLOBAL));
    public static final Parameter<Integer>         AMOUNT = asInt("amount");

    @NotNull
    public static Optional<Parameter<?>> getByName(@NotNull String name) {
        return Optional.ofNullable(REGISTRY.get(name.toLowerCase()));
    }

    @NotNull
    public static Collection<Parameter<?>> getParameters() {
        return REGISTRY.values();
    }

    @NotNull
    public static <V> Parameter<V> register(@NotNull String name, @NotNull Function<String, V> parser) {
        Parameter<V> parameter = new Parameter<>(name, parser);
        REGISTRY.put(parameter.getName(), parameter);
        return parameter;
    }

    @NotNull
    public static Parameter<Integer> asInt(@NotNull String name) {
        Function<String, Integer> parser = str -> StringUtil.getInteger(str, 0, true);
        return register(name, parser);
    }

    @NotNull
    public static Parameter<String> asString(@NotNull String name) {
        Function<String, String> parser = Colorizer::strip;
        return register(name, parser);
    }
}
