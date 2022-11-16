package su.nightexpress.ama.arena.setup.manager;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.api.manager.IListener;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.setup.SetupItemType;

public abstract class AbstractSetupManager<T> extends AbstractManager<AMA> implements IListener {

    protected Player player;
    protected T object;

    protected ItemStack[] inventorySave;

    public AbstractSetupManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    public void registerListeners() {
        this.plugin.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    protected void onLoad() {
        this.registerListeners();
    }

    @Override
    protected void onShutdown() {
        this.unregisterListeners();
    }

    @NotNull
    public T getObject() {
        return object;
    }

    public boolean isEditing(@NotNull Player player) {
        return player.equals(this.player);
    }

    public void startSetup(@NotNull Player player, @NotNull T object) {
        if (this.player != null || this.isEditing(player)) return;

        this.player = player;
        this.object = object;
        this.inventorySave = player.getInventory().getContents();
        player.getInventory().clear();

        this.onSetupStart(player, object);
        this.updateVisuals();
    }

    public void endSetup(@NotNull Player player) {
        if (!this.isEditing(player)) return;

        this.removeVisuals();
        this.onSetupEnd(player, this.object);

        player.getInventory().setContents(this.inventorySave);
        this.player = null;
        this.inventorySave = null;
    }

    protected abstract void onSetupStart(@NotNull Player player, @NotNull T object);

    protected abstract void onSetupEnd(@NotNull Player player, @NotNull T object);

    protected abstract void handleItem(
            @NotNull PlayerInteractEvent e, @NotNull Player player, @NotNull T object,
            @NotNull ItemStack item, @NotNull SetupItemType itemType);

    protected abstract void updateVisuals();

    protected abstract void removeVisuals();



    @EventHandler(priority = EventPriority.NORMAL)
    public void onItemClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!this.isEditing(player)) return;

        ItemStack item = e.getItem();
        if (item == null || item.getType().isAir()) return;

        SetupItemType itemType = SetupItemType.getType(item);
        if (itemType == null) return;

        e.setUseItemInHand(Event.Result.DENY);
        e.setUseInteractedBlock(Event.Result.DENY);

        this.handleItem(e, player, this.getObject(), item, itemType);
        this.removeVisuals();
        if (this.isEditing(player)) {
            this.updateVisuals();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRegionUserQuit(PlayerQuitEvent e) {
        this.endSetup(e.getPlayer());
    }
}
