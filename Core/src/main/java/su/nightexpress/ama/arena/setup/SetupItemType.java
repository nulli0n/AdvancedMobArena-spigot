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

import static su.nexmedia.engine.utils.Colors2.*;

import java.util.Arrays;

public enum SetupItemType {

    REGION_CUBOID(new ItemStack(Material.GOLDEN_AXE), YELLOW + BOLD + "Cuboid Selector",
        GRAY + "Left-Click - " + WHITE + "1st corner",
        GRAY + "Right-Click - " + WHITE + "2nd corner"
    ),

    REGION_SPAWN(new ItemStack(Material.FEATHER), YELLOW + BOLD + "Player Spawn Tool" + GRAY + " (Right-Click)",
        GRAY + "Location is set at your current location. Not of a clicked block!",
        GRAY + "Right-Click to " + WHITE + "Set"
    ),

    REGION_SPAWNER(new ItemStack(Material.BLAZE_ROD), YELLOW + BOLD + "Mob Spawner Tool",
        GRAY + "Left-Click to " + WHITE + "Add Spawner",
        GRAY + "Right-Click to " + WHITE + "Remove Spawner"
    ),

    REGION_SAVE(new ItemStack(Material.EMERALD), GREEN + BOLD + "Save & Exit" + GRAY + " (Right-Click)"),

    SPOT_CUBOID(new ItemStack(Material.GOLDEN_AXE), "Cuboid Selector",
        GRAY + "Left-Click - " + WHITE + "1st corner",
        GRAY + "Right-Click - " + WHITE + "2nd corner"
    ),

    SPOT_STATE_PREVIEW(new ItemStack(Material.ITEM_FRAME), YELLOW + BOLD + "State Preview" + GRAY + " (Right-Click)"),

    SPOT_STATE_EXIT(new ItemStack(Material.BARRIER), RED + BOLD + "Exit" + GRAY + " (Right-Click)"),

    SPOT_SAVE(new ItemStack(Material.EMERALD), GREEN + BOLD  + "Save & Exit" + GRAY + " (Right-Click)"),

    ARENA_PROTECTION_ZONE(new ItemStack(Material.DIAMOND_AXE), CYAN + BOLD + "Protection Selector" + GRAY + " (LMB / RMB)"),

    ARENA_LOCATION_LOBBY(new ItemStack(Material.ENDER_PEARL), YELLOW + BOLD + "Lobby Location" + GRAY + " (Right-Click)"),

    ARENA_LOCATION_SPECTATE(new ItemStack(Material.ENDER_EYE), YELLOW + BOLD + "Spectate Location" + GRAY + " (Right-Click)"),

    ARENA_LOCATION_LEAVE(new ItemStack(Material.REDSTONE), YELLOW + BOLD + "Exit/Leave Location" + GRAY + " (Right-Click)"),

    ARENA_EXIT(new ItemStack(Material.BARRIER), RED + BOLD + "Exit" + GRAY + " (Right-Click)"),
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
