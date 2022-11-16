package su.nightexpress.ama.data;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserManager;
import su.nightexpress.ama.AMA;

import java.util.UUID;

public class ArenaUserManager extends AbstractUserManager<AMA, ArenaUser> {

    public ArenaUserManager(@NotNull AMA plugin) {
        super(plugin, plugin);
    }

    @Override
    @NotNull
    protected ArenaUser createData(@NotNull UUID uuid, @NotNull String name) {
        return new ArenaUser(this.plugin, uuid, name);
    }
}
