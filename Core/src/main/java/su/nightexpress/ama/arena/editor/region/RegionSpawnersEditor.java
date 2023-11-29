package su.nightexpress.ama.arena.editor.region;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.Pair;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.region.Region;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.List;
import java.util.stream.IntStream;

public class RegionSpawnersEditor extends EditorMenu<AMA, Region> implements AutoPaged<String> {

    public RegionSpawnersEditor(@NotNull AMA plugin, @NotNull Region region) {
        super(plugin, region, "Region Spawners [" + region.getId() + "]", 45);

        this.addReturn(39).setClick((viewer, event) -> {
            region.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.REGION_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_REGION_ENTER_GROUP_ID, wrapper -> {
                region.getMobSpawners(wrapper.getTextRaw());
                return true;
            });
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<String> getObjects(@NotNull Player player) {
        return this.object.getMobSpawners().keySet().stream().sorted(String::compareTo).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull String group) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemReplacer.create(item).readLocale(EditorLocales.REGION_SPAWNERS_OBJECT).trimmed().hideFlags()
            .replace(Placeholders.GENERIC_NAME, StringUtil.capitalizeUnderscored(group))
            .replace(Placeholders.GENERIC_AMOUNT, () -> NumberUtil.format(this.object.getMobSpawners(group).size()))
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull String group) {
        return (viewer, event) -> {
            if (event.isRightClick() && event.isShiftClick()) {
                this.object.getMobSpawners().remove(group);
                this.object.save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }

            this.plugin.getArenaSetupManager().getRegionSpawnerSetupManager().startSetup(viewer.getPlayer(), Pair.of(this.object, group));
            this.plugin.runTask(task -> viewer.getPlayer().closeInventory());
        };
    }
}
