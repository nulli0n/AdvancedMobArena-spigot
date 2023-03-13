package su.nightexpress.ama.editor;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListener;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListenerState;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.config.Lang;

import java.util.Set;

public class ArenaEditorUtils {

    public static final String TITLE_EDITOR          = "AdvancedMobArena Editor";
    public static final String TITLE_ARENA_EDITOR    = "Arena Editor";
    public static final String TITLE_GAMEPLAY_EDITOR = "Arena Gameplay Editor";
    public static final String TITLE_REGION_EDITOR   = "Arena Region Editor";
    public static final String TITLE_REWARD_EDITOR   = "Arena Reward Editor";
    public static final String TITLE_SHOP_EDITOR     = "Arena Shop Editor";
    public static final String TITLE_SPOT_EDITOR     = "Arena Spot Editor";
    public static final String TITLE_WAVE_EDITOR     = "Arena Wave Editor";
    public static final String TITLE_KIT_EDITOR      = "Arena Kit Editor";
    public static final String TITLE_MOB_EDITOR      = "Arena Mob Editor";
    public static final String TITLE_SCRIPT_EDITOR = "Arena Script Editor";

    @Deprecated
    public static void handleTriggersClick(
        @NotNull Player player,
        @NotNull IArenaGameEventListener listener,
        @NotNull ArenaEditorType editorType,
        boolean doClean) {

        if (doClean) {
            if (listener instanceof IArenaGameEventListenerState listenerState) {
                LockState state = LockState.fromEditor(editorType);
                listenerState.getStateTriggers(state).clear();
            }
            else {
                listener.getTriggers().clear();
            }
            return;
        }

        EditorInput<IArenaGameEventListener, ArenaEditorType> input = (player2, listener2, type, e) -> {
            String msg = Colorizer.strip(e.getMessage());
            return ArenaEditorUtils.handleTriggersInput(player2, listener2, type, msg);
        };

        EditorManager.startEdit(player, listener, editorType, input);
        ArenaAPI.PLUGIN.getMessage(Lang.Editor_Tip_Triggers).send(player);
        EditorManager.tip(player, ArenaAPI.PLUGIN.getMessage(Lang.Editor_Enter_Triggers).getLocalized());
        EditorManager.suggestValues(player, CollectionsUtil.getEnumsList(ArenaGameEventType.class), false);
        player.closeInventory();
    }

    @Deprecated
    public static boolean handleTriggersInput(
        @NotNull Player player,
        @NotNull IArenaGameEventListener listener,
        @NotNull ArenaEditorType editorType,
        @NotNull String msg) {

        if (!(listener instanceof ArenaChild arenaObject)) {
            EditorManager.error(player, "The object is not related to the arena config!");
            return false;
        }

        ArenaConfig arenaConfig = arenaObject.getArenaConfig();

        String[] split = Colorizer.strip(msg).split(" ");
        if (split.length < 2) {
            EditorManager.error(player, ArenaAPI.PLUGIN.getMessage(Lang.Editor_Error_Triggers).getLocalized());
            return false;
        }

        ArenaGameEventTrigger<?> trigger = ArenaGameEventTrigger.parse(split[0], split[1]);
        if (trigger == null) {
            EditorManager.error(player, ArenaAPI.PLUGIN.getMessage(Lang.Editor_Error_Triggers).getLocalized());
            return false;
        }

        Set<ArenaGameEventTrigger<?>> triggers;
        if (listener instanceof IArenaGameEventListenerState listenerState) {
            LockState state = LockState.fromEditor(editorType);
            //listenerState.getStateTriggers(state).add(trigger);
            triggers = listenerState.getStateTriggers(state);
        }
        else {
            //listener.getTriggers().add(trigger);
            triggers = listener.getTriggers();
        }
        triggers.removeIf(triggerHas -> triggerHas.getType() == trigger.getType());
        triggers.add(trigger);

        arenaConfig.save();
        return true;
    }
}
