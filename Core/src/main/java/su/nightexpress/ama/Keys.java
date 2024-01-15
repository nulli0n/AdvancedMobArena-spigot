package su.nightexpress.ama;

import org.bukkit.NamespacedKey;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.arena.impl.Arena;

public class Keys {

    public static final NamespacedKey ENTITY_ARENA_ID  = new NamespacedKey(ArenaAPI.PLUGIN, "entity.arena_id");
    public static final NamespacedKey ENTITY_MOB_ID    = new NamespacedKey(ArenaAPI.PLUGIN, "entity.mob_id");
    public static final NamespacedKey ENTITY_MOB_LEVEL = new NamespacedKey(ArenaAPI.PLUGIN, "entity.mob_level");
    public static final NamespacedKey ENTITY_OUTSIDER  = new NamespacedKey(ArenaAPI.PLUGIN, "entity.outsider");

    public static final NamespacedKey SIGN_TYPE          = new NamespacedKey(ArenaAPI.PLUGIN, "sign.type");
    public static final NamespacedKey SIGN_ARENA_ID      = new NamespacedKey(ArenaAPI.PLUGIN, "sign.arena_id");
    public static final NamespacedKey SIGN_KIT_ID        = new NamespacedKey(ArenaAPI.PLUGIN, "sign.kit_id");
    public static final NamespacedKey SIGN_STAT_TYPE     = new NamespacedKey(ArenaAPI.PLUGIN, "sign.stat_type");
    public static final NamespacedKey SIGN_STAT_POSITION = new NamespacedKey(ArenaAPI.PLUGIN, "sign.stat_position");

    public static final NamespacedKey ITEM_LOBBY_TYPE = new NamespacedKey(ArenaAPI.PLUGIN, "item.lobby_type");
    public static final NamespacedKey ITEM_SETUP_TYPE = new NamespacedKey(ArenaAPI.PLUGIN, "item.setup_type");
    public static final NamespacedKey ITEM_KIT_NAME = new NamespacedKey(ArenaAPI.PLUGIN, "item.kit_name");
}
