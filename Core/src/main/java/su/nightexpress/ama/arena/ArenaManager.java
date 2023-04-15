package su.nightexpress.ama.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.utils.FileUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.listener.ArenaGameplayListener;
import su.nightexpress.ama.arena.listener.ArenaGenericListener;
import su.nightexpress.ama.arena.listener.ArenaMythicListener;
import su.nightexpress.ama.arena.menu.ArenaListMenu;
import su.nightexpress.ama.arena.task.ArenaTickTask;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaManager extends AbstractManager<AMA> {

    public static final String DIR_ARENAS = "/arenas/";

    private final Map<String, Arena> arenas;

    private ArenaListMenu arenaListMenu;
    private ArenaTickTask arenaTickTask;

    public ArenaManager(@NotNull AMA plugin) {
        super(plugin);
        this.arenas = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.runTask(task -> {
            for (File folder : FileUtil.getFolders(plugin.getDataFolder() + DIR_ARENAS)) {
                // OLD DATA START
                File fileOld = new File(folder.getAbsolutePath(), folder.getName() + ".yml");
                File fileNew = new File(folder.getAbsolutePath(), "config.yml");
                if (fileOld.exists()) {
                    if (!fileOld.renameTo(fileNew)) {
                        this.plugin.error("Could not rename arena config: " + fileOld.getName());
                        continue;
                    }
                }
                // OLD DATA END

                JYML cfg = new JYML(folder.getAbsolutePath(), "config.yml");
                ArenaConfig config = new ArenaConfig(plugin, cfg, folder.getName());
                if (config.load()) {
                    this.arenas.put(config.getId(), config.getArena());
                    if (config.hasProblems()) {
                        this.plugin.warn("Arena '" + config.getId() + "' contains some problems! Arena disabled until all problems are fixed.");
                    }
                }
                else this.plugin.error("Arena not loaded: '" + cfg.getFile().getName() + "' !");
            }
            plugin.info("Arenas Loaded: " + arenas.size());
            plugin.getStatsManager().update();
        });

        this.addListener(new ArenaGenericListener(this));
        this.addListener(new ArenaGameplayListener(this));
        if (Hooks.hasMythicMobs()) {
            this.addListener(new ArenaMythicListener(this.plugin));
        }

        this.arenaTickTask = new ArenaTickTask(this);
        this.arenaTickTask.start();
    }

    @Override
    protected void onShutdown() {
        if (this.arenaTickTask != null) {
            this.arenaTickTask.stop();
            this.arenaTickTask = null;
        }

        this.arenas.values().forEach(arena -> {
            arena.stop();
            arena.getConfig().clear();
        });
        this.arenas.clear();

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

    public boolean create(@NotNull String arenaId) {
        if (this.isArenaExists(arenaId)) {
            return false;
        }

        JYML cfg = new JYML(plugin.getDataFolder() + DIR_ARENAS + arenaId, "config.yml");
        ArenaConfig arenaConfig = new ArenaConfig(plugin, cfg, arenaId);
        arenaConfig.setName(StringUtil.capitalizeUnderscored(arenaId));
        arenaConfig.load();
        arenaConfig.save();
        this.getArenasMap().put(arenaConfig.getId(), arenaConfig.getArena());
        return true;
    }

    public void delete(@NotNull Arena arena) {
        if (arena.getConfig().isActive()) {
            return;
        }
        if (FileUtil.deleteRecursive(arena.getConfig().getFile().getParentFile())) {
            arena.getConfig().clear();
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
    public Collection<Arena> getArenas() {
        return this.getArenasMap().values();
    }

    @NotNull
    public Collection<Arena> getArenas(@NotNull Player player) {
        return this.getArenas().stream().filter(arena -> arena.canJoin(player, false)).collect(Collectors.toSet());
    }

    @NotNull
    public Map<String, Arena> getArenasMap() {
        return this.arenas;
    }

    @Nullable
    public Arena getArenaAtLocation(@NotNull Location location) {
        return this.getArenas().stream()
            .filter(arena -> arena.getConfig().getRegionManager().getRegion(location) != null)
            .findFirst().orElse(null);
    }

    @Nullable
    public Arena getArenaById(@NotNull String id) {
        return this.getArenasMap().get(id.toLowerCase());
    }

    @NotNull
    public List<String> getArenaIds() {
        return new ArrayList<>(this.getArenasMap().keySet());
    }
}
