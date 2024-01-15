package su.nightexpress.ama.kit.editor;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.*;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.kit.impl.Kit;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class KitAttrbiutesEditor extends EditorMenu<AMA, Kit> implements AutoPaged<Attribute> {

    public KitAttrbiutesEditor(@NotNull AMA plugin, @NotNull Kit kit) {
        super(plugin, kit, "Attributes [" + kit.getId() + "]", 45);

        this.addReturn(39).setClick((viewer, event) -> kit.getEditor().openNextTick(viewer.getPlayer(), 1));
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.KIT_ATTRIBUTE_CREATE, 41).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), Stream.of(Kit.ATTRIBUTES).map(Enum::name).toList(), true);
            this.handleInput(viewer, Lang.EDITOR_KIT_ENTER_ATTRIBUTE, wrapper -> {
                Attribute attribute = StringUtil.getEnum(wrapper.getTextRaw(), Attribute.class).orElse(null);
                if (attribute == null || !ArrayUtil.contains(Kit.ATTRIBUTES, attribute)) return true;

                kit.setAttribute(attribute, AttributeModifier.Operation.ADD_NUMBER, 0);
                kit.save();
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
    public List<Attribute> getObjects(@NotNull Player player) {
        return this.object.getAttributeMap().keySet().stream().sorted(Comparator.comparing(Enum::name)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Attribute attribute) {
        ItemStack item = new ItemStack(this.getForAttribute(attribute));
        if (item.getType().isAir()) return item;

        var pair = this.object.getAttribute(attribute);
        if (pair == null) return item;

        ItemReplacer.create(item).hideFlags().trimmed()
            .readLocale(EditorLocales.KIT_ATTRIBUTE_OBJECT)
            .replace(Placeholders.GENERIC_NAME, plugin.getLangManager().getEnum(attribute))
            .replace(Placeholders.GENERIC_TYPE, pair.getSecond().name())
            .replace(Placeholders.GENERIC_VALUE, NumberUtil.format(pair.getFirst()))
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Attribute attribute) {
        return (viewer, event) -> {
            var pair = this.object.getAttribute(attribute);
            if (pair == null) return;

            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, wrapper -> {
                    this.object.setAttribute(attribute, pair.getSecond(), wrapper.asAnyDouble(pair.getFirst()));
                    this.object.save();
                    return true;
                });
                return;
            }

            if (event.isRightClick()) {
                AttributeModifier.Operation operation = CollectionsUtil.next(pair.getSecond(), o -> o != AttributeModifier.Operation.MULTIPLY_SCALAR_1);
                this.object.setAttribute(attribute, operation, pair.getFirst());
                this.object.save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }

            if (event.isShiftClick() && event.isRightClick()) {
                this.object.getAttributeMap().remove(attribute);
                this.object.save();
                this.openNextTick(viewer, viewer.getPage());
            }
        };
    }

    @NotNull
    private Material getForAttribute(@NotNull Attribute attribute) {
        return switch (attribute) {
            case GENERIC_ARMOR -> Material.IRON_CHESTPLATE;
            case GENERIC_MAX_HEALTH -> Material.APPLE;
            case GENERIC_ATTACK_SPEED -> Material.GOLDEN_SWORD;
            case GENERIC_ATTACK_DAMAGE -> Material.IRON_SWORD;
            case GENERIC_MAX_ABSORPTION -> Material.GOLDEN_APPLE;
            case GENERIC_MOVEMENT_SPEED -> Material.LEATHER_BOOTS;
            case GENERIC_ARMOR_TOUGHNESS -> Material.NETHERITE_INGOT;
            case GENERIC_KNOCKBACK_RESISTANCE -> Material.SHIELD;
            case GENERIC_ATTACK_KNOCKBACK -> Material.FEATHER;
            default -> Material.AIR;
        };
    }
}
