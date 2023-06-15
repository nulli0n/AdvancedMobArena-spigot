package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.ArenaWaveManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

public class WaveManagerEditor extends EditorMenu<AMA, ArenaWaveManager> {

    private WavesListEditor            listEditor;
    private WavesGradualSettingsEditor gradualEditor;

    public WaveManagerEditor(@NotNull ArenaWaveManager waveManager) {
        super(waveManager.plugin(), waveManager, EditorHub.TITLE_WAVE_EDITOR, 54);

        this.addReturn(49).setClick((viewer, event) -> {
            waveManager.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.CLOCK, EditorLocales.WAVES_ROUND_INTERVAL, 29).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_SECONDS, wrapper -> {
                if (event.isLeftClick()) {
                    waveManager.setFirstRoundCountdown(wrapper.asInt());
                }
                else {
                    waveManager.setRoundCountdown(wrapper.asInt());
                }
                waveManager.save();
                return true;
            });
        });

        this.addItem(Material.IRON_DOOR, EditorLocales.WAVES_FINAL_ROUND, 33).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                waveManager.setFinalRound(-1);
                waveManager.save();
                this.open(viewer.getPlayer(), viewer.getPage());
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, wrapper -> {
                waveManager.setFinalRound(wrapper.asAnyInt(-1));
                waveManager.save();
                return true;
            });
        });

        this.addItem(Material.BLAZE_POWDER, EditorLocales.WAVES_WAVES, 31).setClick((viewer, event) -> {
            this.getListEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.OAK_SAPLING, EditorLocales.WAVES_GRADUAL, 13).setClick((viewer, event) -> {
            this.getGradualEditor().openNextTick(viewer, 1);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            if (!waveManager.isGradualSpawnEnabled()) {
                item.setType(Material.DEAD_BUSH);
            }
        });

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, waveManager.replacePlaceholders()));
        });
    }

    @Override
    public void clear() {
        if (this.listEditor != null) {
            this.listEditor.clear();
            this.listEditor = null;
        }
        if (this.gradualEditor != null) {
            this.gradualEditor.clear();
            this.gradualEditor = null;
        }
        super.clear();
    }

    @NotNull
    public WavesListEditor getListEditor() {
        if (this.listEditor == null) {
            this.listEditor = new WavesListEditor(this.object);
        }
        return this.listEditor;
    }

    @NotNull
    public WavesGradualSettingsEditor getGradualEditor() {
        if (this.gradualEditor == null) {
            this.gradualEditor = new WavesGradualSettingsEditor(this.object);
        }
        return this.gradualEditor;
    }
}
