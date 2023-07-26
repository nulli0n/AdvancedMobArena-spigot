package su.nightexpress.ama.nms;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.IArena;
import su.nightexpress.ama.api.type.MobFaction;

public interface ArenaMob {

    @NotNull IArena getArena();

    @NotNull MobFaction getFaction();
}
