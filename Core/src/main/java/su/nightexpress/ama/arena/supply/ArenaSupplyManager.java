package su.nightexpress.ama.arena.supply;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.arena.editor.supply.SupplyChestListEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.*;

public class ArenaSupplyManager extends AbstractConfigHolder<AMA> implements ArenaChild {

    public static final String CONFIG_NAME = "supply.yml";

    private final ArenaConfig arenaConfig;
    private final Map<String, ArenaSupplyChest> chests;

    private SupplyChestListEditor editor;

    public ArenaSupplyManager(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
        super(arenaConfig.plugin(), cfg);
        this.arenaConfig = arenaConfig;
        this.chests = new HashMap<>();
    }

    @Override
    public boolean load() {
        for (String sId : cfg.getSection("Chests")) {
            String path = "Chests." + sId + ".";

            Location location = cfg.getLocation(path + "Location");
            if (location == null || !(location.getBlock().getState() instanceof Container container)) {
                plugin.error("No valid container at location of the '" + sId + "' supply chest!");
                continue;
            }

            int minItems = cfg.getInt(path + "Refill.Items.Min");
            int maxItems = cfg.getInt(path + "Refill.Items.Max");
            List<ItemStack> items = Arrays.asList(cfg.getItemsEncoded(path + "Items"));

            ArenaSupplyChest supplyChest = new ArenaSupplyChest(this.arenaConfig, sId, container.getLocation(), minItems, maxItems, items);
            this.getArenaConfig().getSupplyManager().getChestsMap().put(supplyChest.getId(), supplyChest);
        }
        return true;
    }

    @Override
    public void onSave() {
        cfg.remove("Chests");
        this.getChests().forEach(chest -> {
            String path = "Chests." + chest.getId() + ".";

            cfg.set(path + "Location", chest.getLocation());
            cfg.set(path + "Refill.Items.Min", chest.getMinItems());
            cfg.set(path + "Refill.Items.Max", chest.getMaxItems());
            cfg.setItemsEncoded(path + "Items", chest.getItems());
        });
    }

    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getChests().forEach(ArenaSupplyChest::clear);
        this.getChestsMap().clear();
    }

    @NotNull
    public SupplyChestListEditor getEditor() {
        if (this.editor == null) {
            this.editor = new SupplyChestListEditor(this);
        }
        return editor;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public Map<String, ArenaSupplyChest> getChestsMap() {
        return chests;
    }

    @NotNull
    public Collection<ArenaSupplyChest> getChests() {
        return this.getChestsMap().values();
    }

    public boolean createChest(@NotNull String id) {
        if (this.getChest(id) != null) return false;

        ArenaSupplyChest supplyChest = new ArenaSupplyChest(this.getArenaConfig(), id);
        this.getChestsMap().put(supplyChest.getId(), supplyChest);
        this.save();
        return true;
    }

    @Nullable
    public ArenaSupplyChest getChest(@NotNull Block block) {
        return this.getChests().stream().filter(supplyChest -> {
            return supplyChest.getLocation() != null && supplyChest.getLocation().getBlock().equals(block);
        }).findFirst().orElse(null);
    }

    @Nullable
    public ArenaSupplyChest getChest(@NotNull String id) {
        return this.getChestsMap().get(id.toLowerCase());
    }

    public void emptyChests() {
        this.getChests().forEach(supplyChest -> {
            supplyChest.getContainer().ifPresent(container -> container.getInventory().clear());
        });
    }
}
