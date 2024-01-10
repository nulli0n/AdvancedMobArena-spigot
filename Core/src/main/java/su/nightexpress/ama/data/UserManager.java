package su.nightexpress.ama.data;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserManager;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.data.impl.ArenaUser;

import java.util.UUID;

public class UserManager extends AbstractUserManager<AMA, ArenaUser> {

    public UserManager(@NotNull AMA plugin) {
        super(plugin, plugin);
    }

    @Override
    @NotNull
    protected ArenaUser createData(@NotNull UUID uuid, @NotNull String name) {
        return new ArenaUser(this.plugin, uuid, name);
    }
}
