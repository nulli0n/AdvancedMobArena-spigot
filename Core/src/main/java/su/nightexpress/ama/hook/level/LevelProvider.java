package su.nightexpress.ama.hook.level;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface LevelProvider {

    @NotNull String getName();

    int getLevel(@NotNull Player player);
}
