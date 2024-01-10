package su.nightexpress.ama.editor;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.editor.arena.ArenaListEditor;
import su.nightexpress.ama.kit.editor.KitsListEditor;
import su.nightexpress.ama.mob.editor.MobListEditor;

public class EditorHub extends EditorMenu<AMA, AMA> {

    public static final String TITLE              = "AdvancedMobArena Editor";

    @Deprecated public static final String TITLE_MOB_EDITOR      = "Arena Mob Editor";

    private static final String TEXTURE_ARENAS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTc5YWU3MGNiMjIxZjQxMzJkOTkyZDEyOTlhYTBhYjdjZGYxMTkzMzM2MGE0YTVjNjIwODM4ZTBlZjJmYjBhMyJ9fX0=";
    private static final String TEXTURE_KITS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVlYjBiZDg1YWFkZGYwZDI5ZWQwODJlYWMwM2ZjYWRlNDNkMGVlODAzYjBlODE2MmFkZDI4YTYzNzlmYjU0ZSJ9fX0=";
    private static final String TEXTURE_MOBS = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzgzYWFhZWUyMjg2OGNhZmRhYTFmNmY0YTBlNTZiMGZkYjY0Y2QwYWVhYWJkNmU4MzgxOGMzMTJlYmU2NjQzNyJ9fX0=";

    private ArenaListEditor arenaEditor;
    private KitsListEditor kitEditor;
    private MobListEditor  mobEditor;

    public EditorHub(@NotNull AMA plugin) {
        super(plugin, plugin, TITLE, 36);

        this.addExit(31);

        this.addItem(ItemUtil.createCustomHead(TEXTURE_ARENAS), EditorLocales.ARENA_EDITOR, 11).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getArenaEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_KITS), EditorLocales.KIT_EDITOR, 13).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getKitEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_MOBS), EditorLocales.MOB_EDITOR, 15).setClick((viewer, event) -> {
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
