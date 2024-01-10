package su.nightexpress.ama.stats.config;

import su.nexmedia.engine.api.config.JOption;

public class StatsConfig {

    public static final JOption<Integer> UPDATE_INTERVAL = JOption.create("Update_Interval", 900,
        "Set how often (in seconds) all the plugin stats will be updated.",
        "This will update all stats signs and holograms.",
        "Set -1 to disable."
    );
}
