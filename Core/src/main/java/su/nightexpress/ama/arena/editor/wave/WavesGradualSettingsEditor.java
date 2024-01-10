package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.WaveManager;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

public class WavesGradualSettingsEditor extends EditorMenu<AMA, WaveManager> {

    public WavesGradualSettingsEditor(@NotNull WaveManager waveManager) {
        super(waveManager.plugin(), waveManager, "Gradual Wave Spawn [" + waveManager.getArenaConfig().getId() + "]", 45);

        this.addReturn(40).setClick((viewer, event) -> {
            waveManager.getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.WAVES_GRADUAL_ENABLED, 4).setClick((viewer, event) -> {
            waveManager.setGradualSpawnEnabled(!waveManager.isGradualSpawnEnabled());
            waveManager.save();
            this.openNextTick(viewer, viewer.getPage());
        }).getOptions().addDisplayModifier((viewer, item) -> {
            item.setType(waveManager.isGradualSpawnEnabled() ? Material.LIME_DYE : Material.GRAY_DYE);
        });

        this.addItem(Material.MELON_SEEDS, EditorLocales.WAVES_GRADUAL_FIRST_PERCENT, 19).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_PERCENT, wraper -> {
                waveManager.setGradualSpawnPercentFirst(wraper.asInt());
                waveManager.save();
                return true;
            });
        });

        this.addItem(Material.PUMPKIN_SEEDS, EditorLocales.WAVES_GRADUAL_NEXT_PERCENT, 21).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_PERCENT, wraper -> {
                waveManager.setGradualSpawnNextPercent(wraper.asInt());
                waveManager.save();
                return true;
            });
        });

        this.addItem(Material.CLOCK, EditorLocales.WAVES_GRADUAL_NEXT_INTERVAL, 23).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_SECONDS, wraper -> {
                waveManager.setGradualSpawnNextInterval(wraper.asInt());
                waveManager.save();
                return true;
            });
        });

        this.addItem(Material.ROTTEN_FLESH, EditorLocales.WAVES_GRADUAL_NEXT_KILL_PERCENT, 25).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_PERCENT, wraper -> {
                waveManager.setGradualSpawnNextKillPercent(wraper.asInt());
                waveManager.save();
                return true;
            });
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, waveManager.replacePlaceholders());
        }));
    }
}
