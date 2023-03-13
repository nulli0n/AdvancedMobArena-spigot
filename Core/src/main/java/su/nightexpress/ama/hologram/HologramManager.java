package su.nightexpress.ama.hologram;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.api.task.AbstractTask;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.api.hologram.IHologramClick;
import su.nightexpress.ama.api.hologram.IHologramHandler;
import su.nightexpress.ama.api.hologram.IHologramWrapper;
import su.nightexpress.ama.command.hologram.HologramMainCommand;
import su.nightexpress.ama.config.Config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HologramManager extends AbstractManager<AMA> {

    private final IHologramHandler<?>                               handler;
    private final Map<HologramType, Map<UUID, IHologramWrapper<?>>> hologramIds;

    private LifeTask lifeTask;

    public HologramManager(@NotNull AMA plugin, @NotNull IHologramHandler<?> handler) {
        super(plugin);
        this.handler = handler;
        this.hologramIds = new ConcurrentHashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.getCommandManager().getMainCommand().addChildren(new HologramMainCommand(this.plugin));

        this.lifeTask = new LifeTask(this.plugin);
        this.lifeTask.start();
    }

    @Override
    protected void onShutdown() {
        if (this.lifeTask != null) {
            this.lifeTask.stop();
            this.lifeTask = null;
        }
        this.plugin.getCommandManager().getMainCommand().removeChildren(HologramMainCommand.NAME);
        this.hologramIds.values().forEach(holos -> holos.values().forEach(IHologramWrapper::delete));
        this.hologramIds.clear();
    }

    @NotNull
    public static List<String> getFormat(@NotNull HologramType type) {
        return new ArrayList<>(Config.HOLOGRAMS_FORMAT.get().getOrDefault(type, Collections.emptyList()));
    }

    @NotNull
    public IHologramHandler<?> getHandler() {
        return handler;
    }

    @NotNull
    public Map<UUID, IHologramWrapper<?>> getHolograms(@NotNull HologramType hologramType) {
        return this.hologramIds.computeIfAbsent(hologramType, k -> new HashMap<>());
    }

    @NotNull
    public UUID create(@NotNull HologramType hologramType, @NotNull Location location, @NotNull List<String> text) {
        return this.create(hologramType, location, text, -1);
    }

    @NotNull
    public UUID create(@NotNull Location location, @NotNull List<String> text, int lifetime) {
        return this.create(HologramType.DEFAULT, location, text, lifetime);
    }

    @NotNull
    public UUID create(@NotNull HologramType hologramType, @NotNull Location location, @NotNull List<String> text, int lifetime) {
        Location location1 = location.clone();
        UUID uuid = UUID.randomUUID();
        IHologramWrapper<?> wrapper = this.getHandler().create(location1, text, lifetime);
        this.getHolograms(hologramType).put(uuid, wrapper);
        return uuid;
    }

    public void setClick(@NotNull HologramType hologramType, @NotNull UUID uuid, @NotNull IHologramClick click) {
        IHologramWrapper<?> wrapper = this.getHolograms(hologramType).get(uuid);
        if (wrapper != null) {
            wrapper.setClick(click);
        }
    }

    public void update(@NotNull HologramType hologramType, @NotNull UUID uuid, @NotNull List<String> text) {
        IHologramWrapper<?> wrapper = this.getHolograms(hologramType).get(uuid);
        if (wrapper != null) {
            wrapper.setText(text);
        }
    }

    public void delete(@NotNull HologramType hologramType, @NotNull UUID uuid) {
        IHologramWrapper<?> wrapper = this.getHolograms(hologramType).remove(uuid);
        if (wrapper != null) {
            wrapper.delete();
        }
    }

    class LifeTask extends AbstractTask<AMA> {

        public LifeTask(@NotNull AMA plugin) {
            super(plugin, 20L, false);
        }

        @Override
        public void action() {
            for (HologramType hologramType : HologramType.values()) {
                getHolograms(hologramType).values().stream().filter(IHologramWrapper::isDead).forEach(IHologramWrapper::delete);
                getHolograms(hologramType).values().removeIf(IHologramWrapper::isDead);
            }
        }
    }
}
