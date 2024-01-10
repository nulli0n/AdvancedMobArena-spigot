package su.nightexpress.ama.arena.script.action;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.arena.impl.Arena;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Parameter<V> {

    private final String              name;
    private final Function<String, V> parser;

    private ItemStack icon;
    private BiFunction<Arena, ParameterResult, List<String>> suggestValues;

    public Parameter(@NotNull String name, @NotNull Function<String, V> parser) {
        this.name = name.toLowerCase();
        this.parser = parser;
        this.withIcon(Material.MAP);
        this.withSuggestions((arena, result) -> new ArrayList<>());
    }

    @NotNull
    public Parameter<V> withIcon(@NotNull Material material) {
        return this.withIcon(new ItemStack(material));
    }

    @NotNull
    public Parameter<V> withIcon(@NotNull ItemStack item) {
        this.icon = new ItemStack(item);
        return this;
    }

    @NotNull
    public Parameter<V> withSuggestions(@NotNull BiFunction<Arena, ParameterResult, List<String>> suggestValues) {
        this.suggestValues = suggestValues;
        return this;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public Function<String, V> getParser() {
        return parser;
    }

    @NotNull
    public V parse(@NotNull String str) {
        return this.getParser().apply(str);
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    @NotNull
    public List<String> getSuggestions(@NotNull Arena arena, @NotNull ParameterResult result) {
        return this.suggestValues.apply(arena, result);
    }
}
