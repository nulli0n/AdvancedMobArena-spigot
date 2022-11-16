package su.nightexpress.ama.hook.external;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class ProtocolLibHook {

    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    public static void sendServerPacket(@NotNull Player player, @NotNull PacketContainer packet) {
        try {
            PROTOCOL_MANAGER.sendServerPacket(player, packet);
        }
        catch (InvocationTargetException var3) {
            var3.printStackTrace();
        }
    }
}
