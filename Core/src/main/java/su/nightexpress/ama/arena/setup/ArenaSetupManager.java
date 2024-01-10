package su.nightexpress.ama.arena.setup;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.setup.manager.*;

public class ArenaSetupManager {

    private final AMA plugin;

    private ArenaConfigSetupManager configSetupManager;
    private RegionSetupManager      regionSetupManager;
    private RegionSpawnerSetupManager regionSpawnerSetupManager;
    private SpotSetupManager        spotSetupManager;
    private SpotStateSetupManager   spotStateSetupManager;

    public ArenaSetupManager(@NotNull AMA plugin) {
        this.plugin = plugin;
    }

    public void setup() {

    }

    public void shutdown() {
        if (this.spotStateSetupManager != null) {
            this.spotStateSetupManager.shutdown();
            this.spotStateSetupManager = null;
        }
        if (this.spotSetupManager != null) {
            this.spotSetupManager.shutdown();
            this.spotSetupManager = null;
        }
        if (this.regionSpawnerSetupManager != null) {
            this.regionSpawnerSetupManager.shutdown();
        }
        if (this.regionSetupManager != null) {
            this.regionSetupManager.shutdown();
            this.regionSetupManager = null;
        }
        if (this.configSetupManager != null) {
            this.configSetupManager.shutdown();
            this.configSetupManager = null;
        }
    }

    @NotNull
    public ArenaConfigSetupManager getConfigSetupManager() {
        if (this.configSetupManager == null) {
            this.configSetupManager = new ArenaConfigSetupManager(this.plugin);
            this.configSetupManager.setup();
        }
        return this.configSetupManager;
    }

    @NotNull
    public RegionSetupManager getRegionSetupManager() {
        if (this.regionSetupManager == null) {
            this.regionSetupManager = new RegionSetupManager(this.plugin);
            this.regionSetupManager.setup();
        }
        return this.regionSetupManager;
    }

    @NotNull
    public RegionSpawnerSetupManager getRegionSpawnerSetupManager() {
        if (this.regionSpawnerSetupManager == null) {
            this.regionSpawnerSetupManager = new RegionSpawnerSetupManager(this.plugin);
            this.regionSpawnerSetupManager.setup();
        }
        return regionSpawnerSetupManager;
    }

    @NotNull
    public SpotSetupManager getSpotSetupManager() {
        if (this.spotSetupManager == null) {
            this.spotSetupManager = new SpotSetupManager(this.plugin);
            this.spotSetupManager.setup();
        }
        return this.spotSetupManager;
    }

    @NotNull
    public SpotStateSetupManager getSpotStateSetupManager() {
        if (this.spotStateSetupManager == null) {
            this.spotStateSetupManager = new SpotStateSetupManager(this.plugin);
            this.spotStateSetupManager.setup();
        }
        return spotStateSetupManager;
    }
}
