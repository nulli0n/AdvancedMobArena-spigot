package su.nightexpress.ama.api.hologram;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IHologramHandler<E> {

    @NotNull
    IHologramWrapper<E> create(@NotNull Location location, @NotNull List<String> text, int lifetime);

    void setText(@NotNull IHologramWrapper<E> wrapper, @NotNull List<String> text);

    void setClick(@NotNull IHologramWrapper<E> wrapper, @NotNull IHologramClick click);

    void delete(@NotNull IHologramWrapper<E> wrapper);
}
