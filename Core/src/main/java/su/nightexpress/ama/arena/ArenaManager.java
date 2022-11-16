package su.nightexpress.ama.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.utils.FileUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.type.ArenaEndType;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.listener.ArenaGameplayListener;
import su.nightexpress.ama.arena.listener.ArenaGenericListener;
import su.nightexpress.ama.arena.menu.ArenaListMenu;
import su.nightexpress.ama.arena.task.ArenaTickTask;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaManager extends AbstractManager<AMA> {

    public static final String DIR_ARENAS = "/arenas/";

    private Map<String, AbstractArena> arenas;

    private ArenaListMenu arenaListMenu;
    private ArenaTickTask arenaTickTask;

    public ArenaManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        this.arenas = new HashMap<>();

        this.plugin.runTask(c -> {
            for (File folder : FileUtil.getFolders(plugin.getDataFolder() + DIR_ARENAS)) {
                JYML cfg = new JYML(folder.getAbsolutePath(), folder.getName() + ".yml");
                ArenaConfig config = new ArenaConfig(plugin, cfg);

                this.arenas.put(config.getId(), config.getArena());
                if (config.hasProblems()) {
                    this.plugin.warn("Arena '" + config.getId() + "' contains some problems! Arena disabled until all problems are fixed.");
                }
            }
            plugin.info("Arenas Loaded: " + arenas.size());
            plugin.getStatsManager().update();
        }, false);

        this.addListener(new ArenaGenericListener(this));
        this.addListener(new ArenaGameplayListener(this));

        this.arenaTickTask = new ArenaTickTask(this);
        this.arenaTickTask.start();
    }

    @Override
    public void onShutdown() {
        this.arenas.values().forEach(arena -> arena.stop(ArenaEndType.FORCE));
        this.arenas.clear();

        if (this.arenaTickTask != null) {
            this.arenaTickTask.stop();
            this.arenaTickTask = null;
        }
        if (this.arenaListMenu != null) {
            this.arenaListMenu.clear();
            this.arenaListMenu = null;
        }
    }

    @NotNull
    public ArenaListMenu getArenaListMenu() {
        if (this.arenaListMenu == null) {
            this.arenaListMenu = new ArenaListMenu(this.plugin);
        }
        return arenaListMenu;
    }

    public void delete(@NotNull AbstractArena arena) {
        if (arena.getConfig().isActive()) {
            return;
        }
        if (FileUtil.deleteRecursive(arena.getConfig().getFile().getParentFile())) {
            arena.getConfig().shutdown();
            this.arenas.remove(arena.getId());
        }

        // Re-build GUI to apply arena changes (remove deleted arenas)
        if (this.arenaListMenu != null) {
            this.arenaListMenu.clear();
            this.arenaListMenu = null;
        }
    }

    public boolean isArenaExists(@NotNull String id) {
        return this.getArenaById(id) != null;
    }

    @NotNull
    public Collection<AbstractArena> getArenas() {
        return this.getArenasMap().values();
    }

    @NotNull
    public Collection<AbstractArena> getArenas(@NotNull Player player) {
        return this.getArenas().stream().filter(arena -> arena.canJoin(player, false)).collect(Collectors.toSet());
    }

    @NotNull
    public Map<String, AbstractArena> getArenasMap() {
        return this.arenas;
    }

    @Nullable
    public AbstractArena getArenaAtLocation(@NotNull Location loc) {
        return this.getArenas().stream()
            .filter(arena -> arena.getConfig().getRegionManager().getRegions().stream().anyMatch(reg -> reg.getCuboid().contains(loc)))
            .findFirst().orElse(null);
    }

    @Nullable
    public AbstractArena getArenaById(@NotNull String id) {
        return this.getArenasMap().get(id.toLowerCase());
    }

    @NotNull
    public List<String> getArenaIds() {
        return new ArrayList<>(this.getArenasMap().keySet());
    }
}
