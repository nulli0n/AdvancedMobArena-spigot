package su.nightexpress.ama.hook.impl;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ProtocolLibHook {

    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    public static void sendServerPacket(@NotNull Player player, @NotNull PacketContainer packet) {
        PROTOCOL_MANAGER.sendServerPacket(player, packet);
    }
}
