package su.nightexpress.ama.editor;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.editor.arena.ArenaListEditor;
import su.nightexpress.ama.kit.editor.KitsListEditor;
import su.nightexpress.ama.mob.editor.MobListEditor;

public class EditorHub extends EditorMenu<AMA, AMA> {

    public static final String TITLE_EDITOR          = "AdvancedMobArena Editor";
    public static final String TITLE_ARENA_EDITOR    = "Arena Editor";
    public static final String TITLE_GAMEPLAY_EDITOR = "Arena Gameplay Editor";
    public static final String TITLE_REGION_EDITOR   = "Arena Region Editor";
    public static final String TITLE_SUPPLY_EDITOR = "Arena Supply Editor";
    public static final String TITLE_REWARD_EDITOR   = "Arena Reward Editor";
    public static final String TITLE_SHOP_EDITOR     = "Arena Shop Editor";
    public static final String TITLE_SPOT_EDITOR     = "Arena Spot Editor";
    public static final String TITLE_WAVE_EDITOR     = "Arena Wave Editor";
    public static final String TITLE_KIT_EDITOR      = "Arena Kit Editor";
    public static final String TITLE_MOB_EDITOR      = "Arena Mob Editor";
    public static final String TITLE_SCRIPT_EDITOR   = "Arena Script Editor";

    private ArenaListEditor arenaEditor;
    private KitsListEditor kitEditor;
    private MobListEditor  mobEditor;

    public EditorHub(@NotNull AMA plugin) {
        super(plugin, plugin, TITLE_EDITOR, 36);

        this.addExit(31);

        this.addItem(Material.MAP, EditorLocales.ARENA_EDITOR, 11).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getArenaEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.ARMOR_STAND, EditorLocales.KIT_EDITOR, 13).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getKitEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.ZOMBIE_HEAD, EditorLocales.MOB_EDITOR, 15).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getMobEditor().open(viewer.getPlayer(), 1));
        });
    }

    @NotNull
    public ArenaListEditor getArenaEditor() {
        if (this.arenaEditor == null) {
            this.arenaEditor = new ArenaListEditor(this.plugin.getArenaManager());
        }
        return this.arenaEditor;
    }

    @NotNull
    public KitsListEditor getKitEditor() {
        if (this.kitEditor == null) {
            this.kitEditor = new KitsListEditor(plugin.getKitManager());
        }
        return this.kitEditor;
    }

    @NotNull
    public MobListEditor getMobEditor() {
        if (this.mobEditor == null) {
            this.mobEditor = new MobListEditor(plugin.getMobManager());
        }
        return mobEditor;
    }

    @Override
    public void clear() {
        if (this.arenaEditor != null) {
            this.arenaEditor.clear();
            this.arenaEditor = null;
        }
        if (this.mobEditor != null) {
            this.mobEditor.clear();
            this.mobEditor = null;
        }
        if (this.kitEditor != null) {
            this.kitEditor.clear();
            this.kitEditor = null;
        }
        super.clear();
    }
}
