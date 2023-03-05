package su.nightexpress.ama.hook.level.impl;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.level.PlayerLevelProvider;

public class MMOCorePlayerLevelProvider implements PlayerLevelProvider {

    @NotNull
    @Override
    public String getName() {
        return HookId.MMOCORE;
    }

    @Override
    public int getLevel(@NotNull Player player) {
        return PlayerData.get(player.getUniqueId()).getLevel();
    }
}
