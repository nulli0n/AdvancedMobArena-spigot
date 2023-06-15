package su.nightexpress.ama.arena.supply;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.arena.editor.supply.SupplyChestSettingsEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ArenaSupplyChest implements ArenaChild, Placeholder, ICleanable {

    private final ArenaConfig arenaConfig;
    private final String      id;
    private final PlaceholderMap placeholderMap;

    private Location        location;
    private int             minItems;
    private int             maxItems;
    private List<ItemStack> items;

    private SupplyChestSettingsEditor editor;

    public ArenaSupplyChest(@NotNull ArenaConfig arenaConfig, @NotNull String id) {
        this(arenaConfig, id, null, 1, 10, new ArrayList<>());
    }

    public ArenaSupplyChest(
        @NotNull ArenaConfig arenaConfig,
        @NotNull String id,
        @Nullable Location location,
        int minItems,
        int maxItems,
        @NotNull List<ItemStack> items
    ) {
        this.arenaConfig = arenaConfig;
        this.id = id.toLowerCase();

        this.setLocation(location);
        this.setMinItems(minItems);
        this.setMaxItems(maxItems);
        this.setItems(items);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.SUPPLY_CHEST_ID, this::getId)
            .add(Placeholders.SUPPLY_CHEST_REFILL_ITEMS_MIN, () -> String.valueOf(this.getMinItems()))
            .add(Placeholders.SUPPLE_CHEST_REFILL_ITEMS_MAX, () -> String.valueOf(this.getMaxItems()))
            .add(Placeholders.SUPPLY_CHEST_LOCATION_X, () -> {
                return this.getLocation() == null ? "?" : NumberUtil.format(this.getLocation().getX());
            })
            .add(Placeholders.SUPPLY_CHEST_LOCATION_Y, () -> {
                return this.getLocation() == null ? "?" : NumberUtil.format(this.getLocation().getY());
            })
            .add(Placeholders.SUPPLY_CHEST_LOCATION_Z, () -> {
                return this.getLocation() == null ? "?" : NumberUtil.format(this.getLocation().getZ());
            })
            .add(Placeholders.SUPPLY_CHEST_LOCATION_WORLD, () -> {
                Location location1 = this.getLocation();
                return location1 == null || location1.getWorld() == null ? "?" : LangManager.getWorld(location1.getWorld());
            })
        ;
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    @NotNull
    public SupplyChestSettingsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new SupplyChestSettingsEditor(this);
        }
        return editor;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return this.arenaConfig;
    }

    @NotNull
    public String getId() {
        return id;
    }

    public boolean hasContainer() {
        return this.getContainer().isPresent();
    }

    public boolean refill() {
        Container container = this.getContainer().orElse(null);
        if (container == null) return false;

        List<ItemStack> items = new ArrayList<>(this.getItems());
        if (items.isEmpty()) return false;

        int min = Math.min(this.getMinItems(), this.getMaxItems());
        int max = Math.max(this.getMinItems(), this.getMaxItems());
        int roll = Math.min(items.size(), Rnd.get(min, max));
        if (roll <= 0) return false;

        Inventory inventory = container.getInventory();

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
    public Optional<Container> getContainer() {
        if (this.getLocation() == null) return Optional.empty();
        if (this.getLocation().getBlock().getState() instanceof Container container) {
            return Optional.of(container);
        }
        return Optional.empty();
    }

    @Nullable
    public Location getLocation() {
        return this.location;
    }

    public void setLocation(@Nullable Location location) {
        this.location = location;
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
        this.getItems().removeIf(item -> item == null || item.getType().isAir());
    }
}
