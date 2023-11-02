package su.nightexpress.ama.arena.editor.spot;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.spot.ArenaSpot;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

public class SpotSettingsEditor extends EditorMenu<AMA, ArenaSpot> {

    private SpotStatesEditor spotStatesEditor;

    public SpotSettingsEditor(@NotNull ArenaSpot spot) {
        super(spot.plugin(), spot, "Spot Settings [" + spot.getId() + "]", 45);

        this.addReturn(40).setClick((viewer, event) -> {
            spot.getArenaConfig().getSpotManager().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.SPOT_ACTIVE, 4).setClick((viewer, event) -> {
            spot.setActive(!spot.isActive());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            item.setType(spot.isActive() ? (!spot.hasProblems() ? Material.LIME_DYE : Material.PINK_DYE) : Material.GRAY_DYE);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.SPOT_NAME, 20).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapepr -> {
                spot.setName(wrapepr.getText());
                spot.save();
                return true;
            });
        });

        this.addItem(Material.GOLDEN_AXE, EditorLocales.SPOT_SETUP_KIT, 22).setClick((viewer, event) -> {
            plugin.runTask(task -> viewer.getPlayer().closeInventory());
            plugin.getArenaSetupManager().getSpotSetupManager().startSetup(viewer.getPlayer(), spot);
        });

        this.addItem(Material.ITEM_FRAME, EditorLocales.SPOT_STATES, 24).setClick((viewer, event) -> {
            if (spot.getCuboid().isEmpty()) {
                plugin.getMessage(Lang.EDITOR_SPOT_STATE_ERROR_NO_CUBOID).send(viewer.getPlayer());
                return;
            }
            this.getStatesEditor().openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, spot.replacePlaceholders());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }

    @Override
    public void clear() {
        if (this.spotStatesEditor != null) {
            this.spotStatesEditor.clear();
            this.spotStatesEditor = null;
        }
        super.clear();
    }

    @NotNull
    public SpotStatesEditor getStatesEditor() {
        if (this.spotStatesEditor == null) {
            this.spotStatesEditor = new SpotStatesEditor(this.object);
        }
        return this.spotStatesEditor;
    }
}
