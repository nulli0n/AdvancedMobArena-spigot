package su.nightexpress.ama.hook.level.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Reflex;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.level.PlayerLevelProvider;

import java.lang.reflect.Method;
import java.util.UUID;

public class MMOCorePlayerLevelProvider implements PlayerLevelProvider {

    @NotNull
    @Override
    public String getName() {
        return HookId.MMOCORE;
    }

    @Override
    public int getLevel(@NotNull Player player) {
        // кусок говна со своими ебанутыми либами, пошел нахуй просто
        Class<?> clazz = Reflex.getClass("net.Indyuce.mmocore.api.player", "PlayerData");
        Method method = Reflex.getMethod(clazz, "get", UUID.class);
        try {
            Object data = method.invoke(null, player.getUniqueId());
            Method getLevel = Reflex.getMethod(data.getClass(), "getLevel");
            return (int) getLevel.invoke(data);
        }
        catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        return 0;
        //PlayerData data = PlayerData.get(player.getUniqueId());
        //return data.getLevel();
    }
}
