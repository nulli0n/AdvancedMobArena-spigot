package su.nightexpress.ama.arena.editor.game;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.game.GameplaySettings;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.pet.PluginPetProvider;

public class GameplayEditorCompat extends EditorMenu<AMA, GameplaySettings> {

    private static final String TITLE = "Gameplay Editor [Compatibility]";

    public GameplayEditorCompat(@NotNull GameplaySettings game) {
        super(game.plugin(), game, TITLE, 54);

        this.addReturn(49).setClick((viewer, event) -> {
            game.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_GLOBALS, 2).setClick((viewer, event) -> {
            game.getEditorGlobals().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_REQUIREMENTS, 3).setClick((viewer, event) -> {
            game.getEditorRequirements().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_PLAYERS, 5).setClick((viewer, event) -> {
            game.getEditorPlayers().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.GAMEPLAY_PAGE_COMPAT, 6).setClick((viewer, event) -> {
            game.getEditorCompat().openNextTick(viewer, 1);
        });

        this.addItem(Material.BONE, EditorLocales.GAMEPLAY_PETS, 19).setClick((viewer, event) -> {
            game.setPetsAllowed(!game.isPetsAllowed());
            this.save(viewer);
        }).getOptions().setVisibilityPolicy(viewer -> !PluginPetProvider.getProviders().isEmpty());

        this.addItem(Material.DIAMOND_SWORD, EditorLocales.GAMEPLAY_MCMMO, 21).setClick((viewer, event) -> {
            game.setMcmmoAllowed(!game.isMcmmoAllowed());
            this.save(viewer);
        }).getOptions().setVisibilityPolicy(viewer -> EngineUtils.hasPlugin(HookId.MCMMO));

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, game.replacePlaceholders());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }
}
