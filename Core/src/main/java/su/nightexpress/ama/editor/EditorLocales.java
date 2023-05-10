package su.nightexpress.ama.editor;

import su.nexmedia.engine.api.editor.EditorLocale;
import su.nightexpress.ama.Placeholders;

public class EditorLocales extends su.nexmedia.engine.api.editor.EditorLocales {

    private static final String PREFIX_OLD = "Editor.ArenaEditorType.";
    private static final String PREFIX = "Editor.";

    public static final EditorLocale ARENA_EDITOR = builder(PREFIX_OLD + "EDITOR_ARENA")
        .name("Arena Editor")
        .text("Create and manage your arenas here!").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale KIT_EDITOR = builder(PREFIX_OLD + "EDITOR_KITS")
        .name("Kit Editor")
        .text("Create and manage your kits here!").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale MOB_EDITOR = builder(PREFIX_OLD + "EDITOR_MOBS")
        .name("Mob Editor")
        .text("Create and manage your mobs here!").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale ARENA_OBJECT = builder(PREFIX_OLD + "ARENA_OBJECT")
        .name(Placeholders.ARENA_NAME + GRAY + "(ID: &f" + Placeholders.ARENA_ID + GRAY + ")")
        .current("Enabled", Placeholders.ARENA_ACTIVE).breakLine()
        .text(Placeholders.GENERIC_PROBLEMS).breakLine()
        .actionsHeader().action("Left-Click", "Edit").action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale ARENA_CREATION = builder(PREFIX_OLD + "ARENA_CREATE")
        .name("Create Arena")
        .build();
}
