package su.nightexpress.ama.arena.setup;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.LocationUtil;
import su.nightexpress.ama.api.ArenaAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ArenaSetupUtils {

    private static final Map<Player, List<Integer>> VISUALS_MAP = new WeakHashMap<>();

    public static void removeVisuals(@NotNull Player player) {
        List<Integer> list = VISUALS_MAP.remove(player);
        if (list == null) return;
        list.forEach(id -> ArenaAPI.getArenaNMS().visualEntityRemove(player, id));
    }

    public static void addVisualText(@NotNull Player player, @NotNull String name, @NotNull Location loc) {
        List<Integer> list = VISUALS_MAP.computeIfAbsent(player, k -> new ArrayList<>());

        Location clone = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
        int id = ArenaAPI.getArenaNMS().visualEntityAdd(player, name, LocationUtil.getCenter(LocationUtil.getFirstGroundBlock(clone.add(0, 1, 0)), false));
        list.add(id);
    }

    public static void addVisualBlock(@NotNull Player player, @NotNull Location location) {
        List<Integer> list = VISUALS_MAP.computeIfAbsent(player, k -> new ArrayList<>());

        Location clone = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
        int id = ArenaAPI.getArenaNMS().visualGlowBlockAdd(player, LocationUtil.getCenter(clone, false));
        list.add(id);
    }
}
