package su.nightexpress.ama.arena.setup;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.setup.manager.ArenaConfigSetupManager;
import su.nightexpress.ama.arena.setup.manager.RegionSetupManager;
import su.nightexpress.ama.arena.setup.manager.SpotSetupManager;
import su.nightexpress.ama.arena.setup.manager.SpotStateSetupManager;

public class ArenaSetupManager {

    private final AMA plugin;

    private ArenaConfigSetupManager configSetupManager;
    private RegionSetupManager      regionSetupManager;
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
