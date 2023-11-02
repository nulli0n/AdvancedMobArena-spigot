package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.impl.Wave;
import su.nightexpress.ama.arena.wave.WaveManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class WavesListEditor extends EditorMenu<AMA, WaveManager> implements AutoPaged<Wave> {

    public WavesListEditor(@NotNull WaveManager waveManager) {
        super(waveManager.plugin(), waveManager, "Waves Editor [" + waveManager.getWaves().size() + " waves]", 45);

        this.addReturn(39).setClick((viewer, event) -> {
            waveManager.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.WAVES_WAVE_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_WAVES_ENTER_WAVE_ID, wrapper -> {
                if (!waveManager.createWave(wrapper.getTextRaw())) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ARENA_WAVES_ERROR_WAVE_EXISTS).getLocalized());
                    return false;
                }
                return true;
            });
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<Wave> getObjects(@NotNull Player player) {
        return this.object.getWaves().stream().sorted(Comparator.comparing(Wave::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Wave wave) {
        ItemStack item = wave.getIcon();
        ItemReplacer.create(item).readLocale(EditorLocales.WAVES_WAVE_OBJECT).trimmed().hideFlags()
            .replace(wave.getPlaceholders())
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Wave wave) {
        return (viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                wave.setIcon(cursor);
                event.getView().setCursor(null);
                this.object.save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }

            if (event.isShiftClick() && event.isRightClick()) {
                this.object.removeWave(wave);
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            wave.getEditor().openNextTick(viewer, 1);
        };
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);

        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}
