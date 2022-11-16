package su.nightexpress.ama.hologram.handler;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import com.gmail.filoghost.holographicdisplays.api.line.TouchableLine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.hologram.IHologramClick;
import su.nightexpress.ama.api.hologram.IHologramHandler;
import su.nightexpress.ama.api.hologram.IHologramWrapper;

import java.util.List;

public class HologramDisplaysHandler implements IHologramHandler<Hologram> {

    @NotNull
    public IHologramWrapper<Hologram> create(@NotNull Location location, @NotNull List<String> text, int lifetime) {
        Hologram hologram = HologramsAPI.createHologram(ArenaAPI.PLUGIN, location);
        IHologramWrapper<Hologram> wrapper = new IHologramWrapper<>(this, hologram, lifetime);
        this.setText(wrapper, text);
        return wrapper;
    }

    @Override
    public void setText(@NotNull IHologramWrapper<Hologram> wrapper, @NotNull List<String> text) {
        Hologram hologram = wrapper.hologram();
        hologram.clearLines();
        // тут тоже блять ничего адекватного не нашлось
        text.forEach(line -> {
            if (line.toLowerCase().startsWith("icon:")) {
                Material material = Material.getMaterial(line.substring("icon:".length()).trim().toUpperCase());
                if (material == null) return;

                hologram.appendItemLine(new ItemStack(material));
            }
            else hologram.appendTextLine(line);
        });
    }

    @Override
    public void setClick(@NotNull IHologramWrapper<Hologram> wrapper, @NotNull IHologramClick click) {
        Hologram hologram = wrapper.hologram();
        // TODO какая-то хуита с апи, нет нужных методов
        try {
            int count = 0;
            HologramLine line;
            while ((line = hologram.getLine(count++)) != null) {
                if (line instanceof TouchableLine touchableLine) {
                    touchableLine.setTouchHandler((click::onClick));
                }
            }
        }
        catch (Exception ignored) { }
    }

    @Override
    public void delete(@NotNull IHologramWrapper<Hologram> wrapper) {
        wrapper.hologram().delete();
    }
}
