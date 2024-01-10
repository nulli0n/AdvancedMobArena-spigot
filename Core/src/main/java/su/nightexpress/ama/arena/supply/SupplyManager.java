package su.nightexpress.ama.arena.supply;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.arena.editor.supply.SupplyListEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;

import java.util.*;

public class SupplyManager extends AbstractConfigHolder<AMA> implements ArenaChild {

    public static final String CONFIG_NAME = "supply.yml";

    private final ArenaConfig              arenaConfig;
    private final Map<String, SupplyChest> chests;

    private SupplyListEditor editor;

    public SupplyManager(@NotNull ArenaConfig arenaConfig, @NotNull JYML cfg) {
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

            SupplyChest supplyChest = new SupplyChest(this.arenaConfig, sId, container.getLocation(), minItems, maxItems, items);
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
        this.getChests().forEach(SupplyChest::clear);
        this.getChestsMap().clear();
    }

    @NotNull
    public SupplyListEditor getEditor() {
        if (this.editor == null) {
            this.editor = new SupplyListEditor(this);
        }
        return editor;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public Map<String, SupplyChest> getChestsMap() {
        return chests;
    }

    @NotNull
    public Collection<SupplyChest> getChests() {
        return this.getChestsMap().values();
    }

    public boolean createChest(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);

        if (this.getChest(id) != null) return false;

        SupplyChest supplyChest = new SupplyChest(this.getArenaConfig(), id);
        this.getChestsMap().put(supplyChest.getId(), supplyChest);
        this.save();
        return true;
    }

    public void removeChest(@NotNull SupplyChest chest) {
        chest.clear();
        this.getChestsMap().remove(chest.getId());
        this.save();
    }

    @Nullable
    public SupplyChest getChest(@NotNull Block block) {
        return this.getChests().stream().filter(supplyChest -> {
            return supplyChest.getLocation() != null && supplyChest.getLocation().getBlock().equals(block);
        }).findFirst().orElse(null);
    }

    @Nullable
    public SupplyChest getChest(@NotNull String id) {
        return this.getChestsMap().get(id.toLowerCase());
    }

    public void emptyChests() {
        this.getChests().forEach(supplyChest -> {
            supplyChest.getContainer().ifPresent(container -> container.getInventory().clear());
        });
    }
}
