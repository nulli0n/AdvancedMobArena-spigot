package su.nightexpress.ama.mob.editor;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.mob.MobManager;
import su.nightexpress.ama.mob.config.MobConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class MobListEditor extends EditorMenu<AMA, MobManager> implements AutoPaged<MobConfig> {

    public MobListEditor(@NotNull MobManager mobManager) {
        super(mobManager.plugin(), mobManager, EditorHub.TITLE_MOB_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            this.plugin.getEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.MOB_CREATE, 41).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.Editor_Mob_Enter_Create, wrapper -> {
                String id = StringUtil.lowerCaseUnderscore(wrapper.getTextRaw());
                if (mobManager.getMobById(id) != null) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.Editor_Mob_Error_Exist).getLocalized());
                    return false;
                }

                MobConfig mob = new MobConfig(plugin, plugin.getDataFolder() + "/mobs/" + id + ".yml", EntityType.ZOMBIE);
                mob.save();
                mobManager.getMobsMap().put(mob.getId(), mob);
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
    public List<MobConfig> getObjects(@NotNull Player player) {
        return new ArrayList<>(plugin.getMobManager().getMobs().stream().sorted(Comparator.comparing(MobConfig::getId)).toList());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull MobConfig mob) {
        Material material = Material.getMaterial(mob.getEntityType().name() + "_SPAWN_EGG");
        if (mob.getEntityType() == EntityType.MUSHROOM_COW) material = Material.MOOSHROOM_SPAWN_EGG;
        if (material == null) material = Material.BAT_SPAWN_EGG;

        ItemStack item = new ItemStack(material);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.MOB_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.MOB_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, mob.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull MobConfig mob) {
        return (viewer, event) -> {
            if (event.isShiftClick()) {
                if (mob.getFile().delete()) {
                    mob.clear();
                    this.object.getMobsMap().remove(mob.getId());
                    this.openNextTick(viewer, viewer.getPage());
                }
                return;
            }
            mob.getEditor().openNextTick(viewer, 1);
        };
    }
}
