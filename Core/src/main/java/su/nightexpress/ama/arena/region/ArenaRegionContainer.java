package su.nightexpress.ama.arena.region;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.config.EngineConfig;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.IArenaGameEventListener;
import su.nightexpress.ama.api.event.ArenaGameGenericEvent;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.editor.region.EditorRegionContainerSettings;

import java.util.*;
import java.util.function.UnaryOperator;

@Deprecated // TODO Move in global arena scope, add id/name args
public class ArenaRegionContainer implements IArenaGameEventListener, ArenaChild, IEditable, ICleanable {

    private final ArenaRegion                   region;
    private final Location location;
    private final Set<ArenaGameEventTrigger<?>> triggers;

    private int             minItems;
    private int             maxItems;
    private List<ItemStack> items;

    private EditorRegionContainerSettings editor;

    public ArenaRegionContainer(@NotNull ArenaRegion region, @NotNull Location location,
                                @NotNull Set<ArenaGameEventTrigger<?>> triggers,
                                int minItems, int maxItems, @NotNull List<ItemStack> items) {
        this.region = region;
        this.location = location;
        this.triggers = triggers;

        this.setMinItems(minItems);
        this.setMaxItems(maxItems);
        this.setItems(items);
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        Location location = this.getLocation();
        String world = location.getWorld() == null ? "null" : location.getWorld().getName();

        return str -> str
            .replace(Placeholders.CONTAINER_TRIGGERS, Placeholders.format(this.getTriggers()))
            .replace(Placeholders.CONTAINER_REFILL_ITEMS_MIN, String.valueOf(this.getMinItems()))
            .replace(Placeholders.CONTAINER_REFILL_ITEMS_MAX, String.valueOf(this.getMaxItems()))
            .replace(Placeholders.CONTAINER_LOCATION_X, NumberUtil.format(location.getX()))
            .replace(Placeholders.CONTAINER_LOCATION_Y, NumberUtil.format(location.getY()))
            .replace(Placeholders.CONTAINER_LOCATION_Z, NumberUtil.format(location.getZ()))
            .replace(Placeholders.CONTAINER_LOCATION_WORLD, EngineConfig.getWorldName(world))
            ;
    }

    @NotNull
    @Override
    public EditorRegionContainerSettings getEditor() {
        if (this.editor == null) {
            this.editor = new EditorRegionContainerSettings(this);
        }
        return editor;
    }

    @Override
    public boolean onGameEvent(@NotNull ArenaGameGenericEvent gameEvent) {
        if (!this.isReady(gameEvent)) return false;

        this.refill();
        return true;
    }

    public boolean refill() {
        List<ItemStack> items = new ArrayList<>(this.getItems());
        if (items.isEmpty()) return false;

        int min = Math.min(this.getMinItems(), this.getMaxItems());
        int max = Math.max(this.getMinItems(), this.getMaxItems());
        int roll = Math.min(items.size(), Rnd.get(min, max));
        if (roll <= 0) return false;

        Chest chest = this.getChest();
        Inventory inventory = chest.getBlockInventory();

        Collections.shuffle(items);
        while (items.size() > roll) {
            items.remove(0);
        }
        while (items.size() < inventory.getSize()) {
            items.add(new ItemStack(Material.AIR));
        }
        Collections.shuffle(items);

        inventory.clear();
        inventory.setContents(items.toArray(new ItemStack[0]));
        return true;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return this.getRegion().getArenaConfig();
    }

    @NotNull
    public ArenaRegion getRegion() {
        return region;
    }

    @NotNull
    public Chest getChest() {
        return (Chest) this.getLocation().getBlock().getState();
    }

    @NotNull
    public Location getLocation() {
        return this.location.clone();
    }

    @NotNull
    @Override
    public Set<ArenaGameEventTrigger<?>> getTriggers() {
        return triggers;
    }

    public int getMinItems() {
        return this.minItems;
    }

    public void setMinItems(int minItems) {
        this.minItems = minItems;
    }

    public int getMaxItems() {
        return this.maxItems;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    @NotNull
    public List<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(@NotNull List<ItemStack> items) {
        this.items = new ArrayList<>(items);
        this.items.removeIf(item -> item == null || item.getType().isAir());
    }
}
