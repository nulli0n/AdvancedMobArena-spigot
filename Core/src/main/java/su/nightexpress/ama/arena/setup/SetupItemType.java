package su.nightexpress.ama.arena.setup;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.PDCUtil;
import su.nightexpress.ama.Keys;

import java.util.ArrayList;
import java.util.List;

public enum SetupItemType implements EditorButtonType {

    REGION_CUBOID(Material.GOLDEN_AXE, "Region Cuboid Selector",
        EditorButtonType.info("Sets the arena region corners."),
        EditorButtonType.click("Left-Click to &fSet 1st\nRight-Click to &fSet 2nd")),
    REGION_SPAWN(Material.FEATHER, "Region Spawn Tool",
        EditorButtonType.info("Sets the region spawn location."),
        EditorButtonType.warn("Location is set at your current location. Not of a clicked block!"),
        EditorButtonType.click("Right-Click to &fSet")),
    REGION_SPAWNER(Material.BLAZE_ROD, "Region Spawner Tool",
        EditorButtonType.info("Sets the mob spawner on a clicked block."),
        EditorButtonType.click("Left-Click to &fAdd Spawner\nRight-Click to &fRemove Spawner")),
    REGION_SAVE(Material.EMERALD, "Save & Exit",
        EditorButtonType.info("Saves the changes you made and exit the setup mode."),
        EditorButtonType.click("Right-Click to &fSave")),

    SPOT_CUBOID(Material.GOLDEN_AXE, "Spot Cuboid Selector",
        EditorButtonType.info("Sets the arena spot corners."),
        EditorButtonType.click("Left-Click to &fSet 1st\nRight-Click to &fSet 2nd")),
    SPOT_STATE_PREVIEW(Material.ITEM_FRAME, "Spot State Preview",
        EditorButtonType.info("Previews the current state."),
        EditorButtonType.click("Right-Click to &fPreview")),
    SPOT_STATE_EXIT(Material.BARRIER, "Exit",
        EditorButtonType.info("Exit the setup mode."),
        EditorButtonType.click("Right-Click to &fExit")),
    SPOT_SAVE(Material.EMERALD, "Save & Exit",
        EditorButtonType.info("Saves the changes you made and exit the setup mode."),
        EditorButtonType.click("Right-Click to &fSave")),

    ARENA_LOCATION_LOBBY(Material.ENDER_PEARL, "Arena Lobby Location",
        EditorButtonType.info("Sets the arena lobby location."),
        EditorButtonType.warn("Location is set at your current location. Not of a clicked block!"),
        EditorButtonType.click("Right-Click to &fSet")),
    ARENA_LOCATION_SPECTATE(Material.ENDER_EYE, "Arena Spectate Location",
        EditorButtonType.info("Sets the arena spectate location."),
        EditorButtonType.warn("Location is set at your current location. Not of a clicked block!"),
        EditorButtonType.click("Right-Click to &fSet")),
    ARENA_LOCATION_LEAVE(Material.REDSTONE, "Arena Leave Location",
        EditorButtonType.info("Sets the arena leave location."),
        EditorButtonType.note("You may to not set this location. So, players will be teleported back to their original location."),
        EditorButtonType.warn("Location is set at your current location. Not of a clicked block!"),
        EditorButtonType.click("Right-Click to &fSet")),
    ARENA_EXIT(Material.BARRIER, "Exit",
        EditorButtonType.info("Exit the setup mode."),
        EditorButtonType.click("Right-Click to &fExit")),
    ;

    private final Material     material;
    private       String       name;
    private       List<String> lore;

    SetupItemType() {
        this(Material.AIR, "", "");
    }

    SetupItemType(@NotNull Material material, @NotNull String name, @NotNull String... lores) {
        this.material = material;
        this.setName(name);
        this.setLore(EditorButtonType.fineLore(lores));
    }

    @NotNull
    @Override
    public Material getMaterial() {
        return material;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public List<String> getLore() {
        return lore;
    }

    public void setLore(@NotNull List<String> lore) {
        this.lore = Colorizer.apply(new ArrayList<>(lore));
    }

    @Override
    @NotNull
    public ItemStack getItem() {
        ItemStack item = EditorButtonType.super.getItem();
        PDCUtil.set(item, Keys.ITEM_SETUP_TYPE, this.name());
        return item;
    }

    @Nullable
    public static SetupItemType getType(@NotNull ItemStack item) {
        String raw = PDCUtil.getString(item, Keys.ITEM_SETUP_TYPE).orElse(null);
        return raw == null ? null : CollectionsUtil.getEnum(raw, SetupItemType.class);
    }
}
