package su.nightexpress.ama.arena;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.FileUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.listener.ArenaGameplayListener;
import su.nightexpress.ama.arena.listener.ArenaGenericListener;
import su.nightexpress.ama.arena.listener.ArenaMythicListener;
import su.nightexpress.ama.arena.menu.ArenaListMenu;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.arena.task.ArenaOpenTask;
import su.nightexpress.ama.arena.task.ArenaTickTask;
import su.nightexpress.ama.arena.util.ArenaCuboid;
import su.nightexpress.ama.arena.util.BlockPos;
import su.nightexpress.ama.arena.util.Cuboid;
import su.nightexpress.ama.hook.HookId;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ArenaManager extends AbstractManager<AMA> {

    public static final String DIR_ARENAS = "/arenas/";

    private final Map<String, Arena> arenas;

    private ArenaListMenu arenaListMenu;
    private ArenaTickTask arenaTickTask;
    private ArenaOpenTask arenaOpenTask;

    public ArenaManager(@NotNull AMA plugin) {
        super(plugin);
        this.arenas = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.runTask(task -> {
            for (File folder : FileUtil.getFolders(plugin.getDataFolder() + DIR_ARENAS)) {
                JYML cfg = new JYML(folder.getAbsolutePath(), "config.yml");
                ArenaConfig config = new ArenaConfig(plugin, cfg, folder.getName());
                if (config.load()) {
                    this.arenas.put(config.getId(), config.getArena());
                    if (config.hasProblems()) {
                        this.plugin.warn("*".repeat(30));
                        this.plugin.warn("There are major issues in '" + config.getId() + "' arena!");
                        this.plugin.warn("Arena will be disabled for players until they are fixed.");
                        this.plugin.warn("Please, take a look at Arena Editor for details.");
                        this.plugin.warn("*".repeat(30));
                    }
                }
                else this.plugin.error("Arena not loaded: '" + cfg.getFile().getName() + "' !");
            }
            plugin.info("Arenas Loaded: " + arenas.size());
            plugin.getStatsManager().update();
        });

        this.addListener(new ArenaGenericListener(this));
        this.addListener(new ArenaGameplayListener(this));
        if (EngineUtils.hasPlugin(HookId.MYTHIC_MOBS)) {
            this.addListener(new ArenaMythicListener(this.plugin));
        }

        this.arenaTickTask = new ArenaTickTask(this);
        this.arenaTickTask.start();

        this.arenaOpenTask = new ArenaOpenTask(this);
        this.arenaOpenTask.start();
    }

    @Override
    protected void onShutdown() {
        if (this.arenaOpenTask != null) this.arenaOpenTask.stop();
        if (this.arenaTickTask != null) this.arenaTickTask.stop();

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

    public boolean create(@NotNull World world, @NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.isArenaExists(id)) {
            return false;
        }

        JYML cfg = new JYML(plugin.getDataFolder() + DIR_ARENAS + id, "config.yml");
        ArenaConfig arenaConfig = new ArenaConfig(plugin, cfg, id);
        arenaConfig.setWorld(world);
        arenaConfig.setName(StringUtil.capitalizeUnderscored(id));
        arenaConfig.setProtectionZone(new Cuboid(BlockPos.empty(), BlockPos.empty()));
        arenaConfig.setIcon(new ItemStack(Material.MAP));
        arenaConfig.saveBasics();
        arenaConfig.load();
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
    public Arena getArena(@NotNull Player player) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        return arenaPlayer == null ? null : arenaPlayer.getArena();
    }

    @Nullable
    public Arena getArenaAtLocation(@NotNull Location location) {
        return this.getArenas().stream()
            .filter(arena -> arena.getConfig().isProtected(location))
            .findFirst().orElse(null);
    }

    @Nullable
    public Arena getArenaByChunk(@NotNull Chunk chunk) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        for (Arena arena : this.getArenas()) {
            for (Region region : arena.getConfig().getRegionManager().getRegions()) {
                ArenaCuboid cuboid = region.getCuboid().orElse(null);
                if (cuboid == null) continue;

                int chunkMinX = cuboid.getMin().getBlockX() >> 4;
                int chunkMinZ = cuboid.getMin().getBlockZ() >> 4;

                int chunkMaxX = cuboid.getMax().getBlockX() >> 4;
                int chunkMaxZ = cuboid.getMax().getBlockZ() >> 4;

                if (chunkX >= chunkMinX && chunkX <= chunkMaxX && chunkZ >= chunkMinZ && chunkZ <= chunkMaxZ) {
                    return arena;
                }
            }
        }
        return null;
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
