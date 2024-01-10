package su.nightexpress.ama.hologram.handler;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.hologram.IHologramClick;
import su.nightexpress.ama.api.hologram.IHologramHandler;
import su.nightexpress.ama.api.hologram.IHologramWrapper;

import java.util.List;
import java.util.UUID;

public class HologramDecentHandler implements IHologramHandler<Hologram> {

    @NotNull
    @Override
    public IHologramWrapper<Hologram> create(@NotNull Location location, @NotNull List<String> text, int lifetime) {
        Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), location, text);
        return new IHologramWrapper<>(this, hologram, lifetime);
    }

    @Override
    public void setText(@NotNull IHologramWrapper<Hologram> wrapper, @NotNull List<String> text) {
        Hologram hologram = wrapper.hologram();
        HologramPage page = hologram.getPage(0);
        for (int index = 0; index < page.getLines().size(); index++) {
            page.removeLine(index);
        }
        text.forEach(line -> DHAPI.addHologramLine(hologram, line));
    }

    @Override
    public void setClick(@NotNull IHologramWrapper<Hologram> wrapper, @NotNull IHologramClick click) {
        Hologram hologram = wrapper.hologram();
        HologramPage page = hologram.getPage(0);
        // да идите нахуй, что это за дерьмо
        /*page.addAction(ClickType.LEFT, new Action(new ActionType("ama_click") {
            @Override
            public boolean execute(Player player, String... strings) {
                return false;
            }
        }, ""));*/
    }

    @Override
    public void delete(@NotNull IHologramWrapper<Hologram> wrapper) {
        wrapper.hologram().delete();
    }
}
