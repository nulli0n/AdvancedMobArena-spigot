package su.nightexpress.ama.arena.editor.reward;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.Menu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.reward.Reward;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

import java.util.Objects;
import java.util.stream.Stream;

public class RewardSettingsEditor extends EditorMenu<AMA, Reward> {

    private static final String TEXTURE_COMMAND = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQwZjQwNjFiZmI3NjdhN2Y5MjJhNmNhNzE3NmY3YTliMjA3MDliZDA1MTI2OTZiZWIxNWVhNmZhOThjYTU1YyJ9fX0=";
    private static final String TEXTURE_ITEMS   = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1MWU1MDU5ODk4MzhlMjcyODdlN2FmYmM3Zjk3ZTc5NmNhYjVmMzU5OGE3NjE2MGMxMzFjOTQwZDBjNSJ9fX0=";

    public RewardSettingsEditor(@NotNull AMA plugin, @NotNull Reward reward) {
        super(plugin, reward, "Reward Editor [" + reward.getId() + "]", 36);

        this.addReturn(31).setClick((viewer, event) -> {
            reward.getArenaConfig().getRewardManager().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.REWARD_NAME, 10).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapper -> {
                reward.setName(wrapper.getText());
                reward.getArenaConfig().getRewardManager().save();
                return true;
            });
        });

        this.addItem(Material.POTION, EditorLocales.REWARD_COMPLETION_REQUIRED, 12).setClick((viewer, event) -> {
            reward.setCompletionRequired(!reward.isCompletionRequired());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            if (!reward.isCompletionRequired()) item.setType(Material.GLASS_BOTTLE);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_COMMAND), EditorLocales.REWARD_COMMANDS, 14).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                reward.getCommands().clear();
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_COMMAND, wrapper -> {
                reward.getCommands().add(wrapper.getText());
                reward.getArenaConfig().getRewardManager().save();
                return true;
            });
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_ITEMS), EditorLocales.REWARD_ITEMS, 16).setClick((viewer, event) -> {
            new ContentEditor(plugin, reward).openNextTick(viewer, 1);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, reward.replacePlaceholders());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.getArenaConfig().getRewardManager().save();
        this.openNextTick(viewer, viewer.getPage());
    }

    private static class ContentEditor extends Menu<AMA> {

        private final Reward reward;

        public ContentEditor(@NotNull AMA plugin, @NotNull Reward reward) {
            super(plugin, "Reward Items", 27);
            this.reward = reward;
        }

        @Override
        public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
            super.onReady(viewer, inventory);
            inventory.setContents(this.reward.getItems().toArray(new ItemStack[this.getOptions().getSize()]));
        }

        @Override
        public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
            super.onClick(viewer, item, slotType, slot, event);
            event.setCancelled(false);
        }

        @Override
        public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
            Inventory inventory = event.getInventory();
            this.reward.setItems(Stream.of(inventory.getContents()).filter(Objects::nonNull).toList());
            this.reward.getArenaConfig().getRewardManager().save();
            this.reward.getEditor().openNextTick(viewer, 1);
            super.onClose(viewer, event);
        }

        @Override
        public boolean isPersistent() {
            return false;
        }
    }
}
