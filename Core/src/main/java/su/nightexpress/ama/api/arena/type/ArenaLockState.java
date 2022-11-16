package su.nightexpress.ama.api.arena.type;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.editor.ArenaEditorType;

public enum ArenaLockState {

    UNLOCKED, LOCKED;

    @NotNull
    public ArenaLockState getOpposite() {
        return this == LOCKED ? UNLOCKED : LOCKED;
    }

    public static ArenaLockState fromEditor(@NotNull ArenaEditorType editorType) {
        return switch (editorType) {
            case REGION_CHANGE_TRIGGERS_LOCKED,
                SHOP_CATEGORY_CHANGE_TRIGGERS_LOCKED,
                SHOP_CHANGE_TRIGGERS_LOCKED,
                SHOP_PRODUCT_CHANGE_TRIGGERS_LOCKED -> LOCKED;
            case REGION_CHANGE_TRIGGERS_UNLOCKED, SHOP_CATEGORY_CHANGE_TRIGGERS_UNLOCKED,
                SHOP_CHANGE_TRIGGERS_UNLOCKED, SHOP_PRODUCT_CHANGE_TRIGGERS_UNLOCKED -> UNLOCKED;
            default -> null;
        };
    }
}
