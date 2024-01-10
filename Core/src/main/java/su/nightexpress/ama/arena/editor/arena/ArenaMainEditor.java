package su.nightexpress.ama.arena.editor.arena;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

public class ArenaMainEditor extends EditorMenu<AMA, ArenaConfig> {

    public ArenaMainEditor(@NotNull AMA plugin, @NotNull ArenaConfig config) {
        super(plugin, config, "Arena Editor [" + config.getId() + "]", 54);

        this.addReturn(49).setClick((viewer, event) -> {
            plugin.getEditor().getArenaEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.ARENA_ACTIVE, 4).setClick((viewer, event) -> {
            config.setActive(!config.isActive());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            item.setType(config.isActive() ? (!config.hasProblems() ? Material.LIME_DYE : Material.PINK_DYE) : Material.GRAY_DYE);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.ARENA_NAME, 10).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapper -> {
                config.setName(wrapper.getText());
                config.save();
                return true;
            });
        });

        this.addItem(Material.GOLDEN_AXE, EditorLocales.ARENA_SETUP_KIT, 16).setClick((viewer, event) -> {
            plugin.getArenaSetupManager().getConfigSetupManager().startSetup(viewer.getPlayer(), config);
            plugin.runTask(task -> viewer.getPlayer().closeInventory());
        });


        this.addItem(Material.EXPERIENCE_BOTTLE, EditorLocales.ARENA_GAMEPLAY_SETTINGS, 19).setClick((viewer, event) -> {
            config.getGameplaySettings().getEditorGlobals().openNextTick(viewer, 1);
        });

        this.addItem(Material.OAK_FENCE, EditorLocales.ARENA_REGION_MANAGER, 21).setClick((viewer, event) -> {
            config.getRegionManager().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.BLAZE_POWDER, EditorLocales.ARENA_WAVE_MANAGER, 23).setClick((viewer, event) -> {
            config.getWaveManager().getEditor().openNextTick(viewer.getPlayer(), 1);
        });

        this.addItem(Material.GOLD_INGOT, EditorLocales.ARENA_REWARD_MANAGER, 25).setClick((viewer, event) -> {
            config.getRewardManager().getEditor().openNextTick(viewer, 1);
        });


        this.addItem(Material.CHAIN_COMMAND_BLOCK, EditorLocales.ARENA_SCRIPT_MANAGER, 28).setClick((viewer, event) -> {
            config.getScriptManager().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.EMERALD, EditorLocales.ARENA_SHOP_MANAGER, 30).setClick((viewer, event) -> {
            config.getShopManager().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.COMPASS, EditorLocales.ARENA_SPOT_MANAGER, 32).setClick((viewer, event) -> {
            config.getSpotManager().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.CHEST, EditorLocales.ARENA_SUPPLY_MANAGER, 34).setClick((viewer, event) -> {
            config.getSupplyManager().getEditor().openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, Placeholders.forArenaAll(config).replacer());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }
}
