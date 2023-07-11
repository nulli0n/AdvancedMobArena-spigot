package su.nightexpress.ama.arena.setup;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PDCUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Keys;

import java.util.Arrays;

public enum SetupItemType {

    REGION_CUBOID(new ItemStack(Material.GOLDEN_AXE), "Region Cuboid Selector",
        "Sets the arena region corners.",
        "Left-Click to &fSet 1st\nRight-Click to &fSet 2nd"),
    REGION_SPAWN(new ItemStack(Material.FEATHER), "Region Spawn Tool",
        "Sets the region spawn location.",
        "Location is set at your current location. Not of a clicked block!",
        "Right-Click to &fSet"),
    REGION_SPAWNER(new ItemStack(Material.BLAZE_ROD), "Region Spawner Tool",
        "Sets the mob spawner on a clicked block.",
        "Left-Click to &fAdd Spawner\nRight-Click to &fRemove Spawner"),
    REGION_SAVE(new ItemStack(Material.EMERALD), "Save & Exit",
        "Saves the changes you made and exit the setup mode.",
        "Right-Click to &fSave"),

    SPOT_CUBOID(new ItemStack(Material.GOLDEN_AXE), "Spot Cuboid Selector",
        "Sets the arena spot corners.",
        "Left-Click to &fSet 1st\nRight-Click to &fSet 2nd"),
    SPOT_STATE_PREVIEW(new ItemStack(Material.ITEM_FRAME), "Spot State Preview",
        "Previews the current state.",
        "Right-Click to &fPreview"),
    SPOT_STATE_EXIT(new ItemStack(Material.BARRIER), "Exit",
        "Exit the setup mode.",
        "Right-Click to &fExit"),
    SPOT_SAVE(new ItemStack(Material.EMERALD), "Save & Exit",
        "Saves the changes you made and exit the setup mode.",
        "Right-Click to &fSave"),

    ARENA_LOCATION_LOBBY(new ItemStack(Material.ENDER_PEARL), "Arena Lobby Location",
        "Sets the arena lobby location.",
        "Location is set at your current location. Not of a clicked block!",
        "Right-Click to &fSet"),
    ARENA_LOCATION_SPECTATE(new ItemStack(Material.ENDER_EYE), "Arena Spectate Location",
        "Sets the arena spectate location.",
        "Location is set at your current location. Not of a clicked block!",
        "Right-Click to &fSet"),
    ARENA_LOCATION_LEAVE(new ItemStack(Material.REDSTONE), "Arena Leave Location",
        "Sets the arena leave location.",
        "You may to not set this location. So, players will be teleported back to their original location.",
        "Location is set at your current location. Not of a clicked block!",
        "Right-Click to &fSet"),
    ARENA_EXIT(new ItemStack(Material.BARRIER), "Exit",
        "Exit the setup mode.",
        "Right-Click to &fExit"),
    ;

    private final ItemStack item;

    SetupItemType(@NotNull ItemStack item, @NotNull String name, @NotNull String... lore) {
        this.item = item;
        ItemUtil.mapMeta(this.item, meta -> {
            meta.setDisplayName(Colorizer.apply(name));
            meta.setLore(Colorizer.apply(Arrays.asList(lore)));
        });
    }

    @NotNull
    public ItemStack getItem() {
        ItemStack item = new ItemStack(this.item);
        PDCUtil.set(item, Keys.ITEM_SETUP_TYPE, this.name());
        return item;
    }

    @Nullable
    public static SetupItemType getType(@NotNull ItemStack item) {
        String raw = PDCUtil.getString(item, Keys.ITEM_SETUP_TYPE).orElse(null);
        return raw == null ? null : StringUtil.getEnum(raw, SetupItemType.class).orElse(null);
    }
}
