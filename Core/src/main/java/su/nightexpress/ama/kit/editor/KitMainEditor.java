package su.nightexpress.ama.kit.editor;

import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.Menu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.kit.Kit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class KitMainEditor extends EditorMenu<AMA, Kit> {

    private static final String TITLE = "Kit Editor";

    private static final String TEXTURE_POTION    = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTUyMjE4NTkyNjcwODUzNWRjMmRkNjAyNGNkZGU4MzE3ZWMzNTBlNzRjM2NhMzY0NWU5OTAyYjJjNzg3MGJhNSJ9fX0=";
    private static final String TEXTURE_COMMAND   = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQwZjQwNjFiZmI3NjdhN2Y5MjJhNmNhNzE3NmY3YTliMjA3MDliZDA1MTI2OTZiZWIxNWVhNmZhOThjYTU1YyJ9fX0=";
    private static final String TEXTURE_INVENTORY = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1MWU1MDU5ODk4MzhlMjcyODdlN2FmYmM3Zjk3ZTc5NmNhYjVmMzU5OGE3NjE2MGMxMzFjOTQwZDBjNSJ9fX0=";
    private static final String TEXTURE_ARMOR     = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVlYjBiZDg1YWFkZGYwZDI5ZWQwODJlYWMwM2ZjYWRlNDNkMGVlODAzYjBlODE2MmFkZDI4YTYzNzlmYjU0ZSJ9fX0=";

    public KitMainEditor(@NotNull Kit kit) {
        super(kit.plugin(), kit, TITLE + " [" + kit.getId() + "]", 45);

        this.addReturn(40).setClick((viewer, event) -> {
            plugin.getEditor().getKitEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.KIT_DEFAULT, 4).setClick((viewer, event) -> {
            kit.setDefault(!kit.isDefault());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            item.setType(kit.isDefault() ? Material.LIME_DYE : Material.GRAY_DYE);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.KIT_NAME, 9).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapper -> {
                kit.setName(wrapper.getText());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.MAP, EditorLocales.KIT_DESCRIPTION, 11).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                kit.getDescription().clear();
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_DESCRIPTION, wrapper -> {
                kit.getDescription().add(wrapper.getText());
                kit.save();
                return true;
            });
        });

        this.addItem(Material.ITEM_FRAME, EditorLocales.KIT_ICON, 13).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                PlayerUtil.addItem(viewer.getPlayer(), kit.getIcon());
                return;
            }
            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().isAir()) return;

            kit.setIcon(cursor);
            event.getView().setCursor(null);
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            item.setType(kit.getIcon().getType());
        });

        this.addItem(Material.REDSTONE, EditorLocales.KIT_PERMISSION, 15).setClick((viewer, event) -> {
            kit.setPermissionRequired(!kit.isPermissionRequired());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            if (!kit.isPermissionRequired()) item.setType(Material.GUNPOWDER);
        });

        this.addItem(Material.GOLD_INGOT, EditorLocales.KIT_COST, 17).setClick((viewer, event) -> {
            if (event.getClick() == ClickType.DROP) {
                kit.setCost(0);
                this.save(viewer);
                return;
            }

            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_KIT_ENTER_COST, wrapper -> {
                    kit.setCost(wrapper.asDouble());
                    kit.save();
                    return true;
                });
            }
            else if (event.isRightClick()) {
                EditorManager.suggestValues(viewer.getPlayer(), plugin.getCurrencyManager().getCurrencyIds(), true);
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_CURRENCY, wrapper -> {
                    Currency currency = plugin.getCurrencyManager().getCurrency(wrapper.getTextRaw());
                    if (currency == null) {
                        EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.ERROR_CURRENCY_INVALID).getLocalized());
                        return false;
                    }
                    kit.setCurrency(currency);
                    kit.save();
                    return true;
                });
            }
        }).getOptions().addDisplayModifier((viewer, item) -> {
            if (kit.isFree()) item.setType(Material.NETHERITE_INGOT);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_COMMAND), EditorLocales.KIT_COMMANDS, 19).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                kit.getCommands().clear();
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_COMMAND, wrapper -> {
                kit.getCommands().add(wrapper.getText());
                kit.save();
                return true;
            });
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_POTION), EditorLocales.KIT_POTIONS, 21).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                kit.getPotionEffects().clear();
                this.save(viewer);
                return;
            }

            EditorManager.suggestValues(viewer.getPlayer(), Stream.of(PotionEffectType.values()).map(PotionEffectType::getName).toList(), false);
            this.handleInput(viewer, Lang.EDITOR_KIT_ENTER_EFFECT, wrapper -> {
                String[] split = wrapper.getTextRaw().split(" ");
                PotionEffectType effectType = PotionEffectType.getByName(split[0].toUpperCase());
                if (effectType == null) return true;

                int amp = split.length >= 2 ? StringUtil.getInteger(split[1], 1) : 1;
                PotionEffect potionEffect = new PotionEffect(effectType, Integer.MAX_VALUE, Math.max(0, amp - 1));
                kit.getPotionEffects().add(potionEffect);
                kit.save();
                return true;
            });
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_ARMOR), EditorLocales.KIT_ARMOR, 23).setClick((viewer, event) -> {
            new ContentEditor(kit, 9).openNextTick(viewer, 1);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_INVENTORY), EditorLocales.KIT_INVENTORY, 25).setClick((viewer, event) -> {
            new ContentEditor(kit, 27).openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemReplacer.replace(item, kit.replacePlaceholders()));
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }

    private static class ContentEditor extends Menu<AMA> {

        private final Kit     kit;
        private final boolean isArmor;

        public ContentEditor(@NotNull Kit kit, int size) {
            super(kit.plugin(), "Kit Content [" + kit.getId() + "]", size);
            this.kit = kit;
            this.isArmor = size == 9;
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        @Override
        public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
            super.onReady(viewer, inventory);

            List<ItemStack> items = new ArrayList<>(Arrays.asList(this.isArmor ? this.kit.getArmor() : this.kit.getItems()));
            if (this.isArmor) items.addAll(Arrays.asList(this.kit.getExtras()));

            //inventory.setContents(this.isArmor ? this.kit.getArmor() : this.kit.getItems());
            inventory.setContents(items.toArray(new ItemStack[0]));
        }

        @Override
        public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
            super.onClick(viewer, item, slotType, slot, event);
            event.setCancelled(false);
        }

        @Override
        public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
            Inventory inventory = event.getInventory();
            ItemStack[] items = new ItemStack[this.isArmor ? 4 : 27];

            for (int slot = 0; slot < items.length; slot++) {
                ItemStack item = inventory.getItem(slot);
                if (item == null) continue;

                items[slot] = item;
            }

            if (this.isArmor) {
                this.kit.setArmor(items);
                this.kit.setExtras(new ItemStack[]{inventory.getItem(4)});
            }
            else this.kit.setItems(items);

            this.kit.save();
            this.kit.getEditor().openNextTick(viewer, 1);
            super.onClose(viewer, event);
        }
    }
}
