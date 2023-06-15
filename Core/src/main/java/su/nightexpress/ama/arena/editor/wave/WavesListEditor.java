package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.ArenaWave;
import su.nightexpress.ama.arena.wave.ArenaWaveManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.IntStream;

public class WavesListEditor extends EditorMenu<AMA, ArenaWaveManager> implements AutoPaged<ArenaWave> {

    public WavesListEditor(@NotNull ArenaWaveManager waveManager) {
        super(waveManager.plugin(), waveManager, EditorHub.TITLE_WAVE_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            waveManager.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.WAVES_WAVE_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ARENA_WAVES_ENTER_WAVE_ID, wrapper -> {
                String id = StringUtil.lowerCaseUnderscore(wrapper.getTextRaw());
                if (waveManager.getWave(id) != null) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ARENA_WAVES_ERROR_WAVE_EXISTS).getLocalized());
                    return false;
                }

                ArenaWave wave = new ArenaWave(waveManager.getArenaConfig(), id, new HashSet<>());
                waveManager.getWaves().put(id, wave);
                waveManager.save();
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
    public List<ArenaWave> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getWaves().values());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaWave wave) {
        ItemStack item = new ItemStack(Material.BLAZE_POWDER);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.WAVES_WAVE_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.WAVES_WAVE_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, wave.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ArenaWave wave) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                wave.clear();
                this.object.getWaves().remove(wave.getId());
                this.object.save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }
            wave.getEditor().openNextTick(viewer, 1);
        };
    }
}
