package su.nightexpress.ama.arena.editor.region;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

public class RegionMainEditor extends EditorMenu<AMA, Region> {

    private RegionSpawnersEditor spawnersEditor;

    public RegionMainEditor(@NotNull Region region) {
        super(region.plugin(), region, "Region Editor [" + region.getId() + "]", 45);

        this.addReturn(40).setClick((viewer, event) -> {
           region.getArenaConfig().getRegionManager().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.REGION_ACTIVE, 4).setClick((viewer, event) -> {
            region.setActive(!region.isActive());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> item.setType(region.isActive() ? (!region.hasProblems() ? Material.LIME_DYE : Material.PINK_DYE) : Material.GRAY_DYE));

        this.addItem(Material.NAME_TAG, EditorLocales.REGION_NAME, 19).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapper -> {
                region.setName(wrapper.getTextColored());
                region.save();
                return true;
            });
        });

        this.addItem(Material.CRAFTING_TABLE, EditorLocales.REGION_SETUP_KIT, 21).setClick((viewer, event) -> {
            if (region.isActive()) {
                plugin.getMessage(Lang.SETUP_REGION_ERROR_ENABLED).send(viewer.getPlayer());
                return;
            }
            plugin.runTask(task -> viewer.getPlayer().closeInventory());
            plugin.getArenaSetupManager().getRegionSetupManager().startSetup(viewer.getPlayer(), region);
        });

        this.addItem(Material.SPAWNER, EditorLocales.REGION_SPAWNERS, 23).setClick((viewer, event) -> {
            if (region.isActive()) {
                plugin.getMessage(Lang.SETUP_REGION_ERROR_ENABLED).send(viewer.getPlayer());
                return;
            }
            this.getSpawnersEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_BANNER, EditorLocales.REGION_DEFAULT, 25).setClick((viewer, event) -> {
            Region defaultRegion = region.getArenaConfig().getRegionManager().getDefaultRegion();
            if (defaultRegion != null && region != defaultRegion) return;

            region.setDefault(!region.isDefault());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            if (region.isDefault()) item.setType(Material.LIME_BANNER);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, region.replacePlaceholders());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }

    @Override
    public void clear() {
        if (this.spawnersEditor != null) this.spawnersEditor.clear();
        super.clear();
    }

    @NotNull
    public RegionSpawnersEditor getSpawnersEditor() {
        if (this.spawnersEditor == null) {
            this.spawnersEditor = new RegionSpawnersEditor(this.plugin, this.object);
        }
        return spawnersEditor;
    }
}
