package su.nightexpress.ama.arena.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.PDCUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Keys;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.type.GameState;
import su.nightexpress.ama.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.BiConsumer;

public class LobbyItem {

    private final Type      type;
    private final boolean   isEnabled;
    private final ItemStack item;
    private final int       slot;

    public LobbyItem(@NotNull Type type, boolean isEnabled, @NotNull ItemStack item, int slot) {
        this.type = type;
        this.isEnabled = isEnabled;
        this.item = new ItemStack(item);
        this.slot = slot;

        PDCUtil.set(this.item, Keys.ITEM_LOBBY_TYPE, this.getType().name());
    }

    @NotNull
    public Type getType() {
        return type;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    @NotNull
    public ItemStack getItem() {
        return new ItemStack(item);
    }

    public int getSlot() {
        return slot;
    }

    public void use(@NotNull ArenaPlayer arenaPlayer) {
        if (!this.isEnabled()) return;

        this.getType().usage.accept(arenaPlayer.getArena(), arenaPlayer);
    }

    public void give(@NotNull Player player) {
        if (!this.isEnabled()) return;

        player.getInventory().setItem(this.getSlot(), this.getItem());
    }

    @Nullable
    public static LobbyItem get(@NotNull ItemStack item) {
        Type type = getType(item);
        return type == null ? null : Config.LOBBY_ITEMS.get().get(type);
    }

    @Nullable
    public static LobbyItem get(@NotNull Type type) {
        return Config.LOBBY_ITEMS.get().get(type);
    }

    @Nullable
    public static Type getType(@NotNull ItemStack item) {
        String raw = PDCUtil.getString(item, Keys.ITEM_LOBBY_TYPE).orElse(null);
        return raw == null ? null : StringUtil.getEnum(raw, Type.class).orElse(null);
    }

    public static void give(@NotNull Type type, @NotNull Player player) {
        LobbyItem lobbyItem = get(type);
        if (lobbyItem != null) {
            lobbyItem.give(player);
        }
    }

    public static void write(@NotNull LobbyItem lobbyItem, @NotNull JYML cfg, @NotNull String path) {
        ItemStack item = lobbyItem.getItem();
        PDCUtil.remove(item, Keys.ITEM_LOBBY_TYPE);

        cfg.set(path + ".Enabled", lobbyItem.isEnabled());
        cfg.set(path + ".Slot", lobbyItem.getSlot());
        cfg.setItem(path + ".Item", item);
    }

    public enum Type {

        KIT_SELECT((arena, arenaPlayer) -> ArenaAPI.getKitManager().getSelectMenu().open(arenaPlayer.getPlayer(), 1)),
        KIT_SHOP((arena, arenaPlayer) -> ArenaAPI.getKitManager().getShopMenu().open(arenaPlayer.getPlayer(), 1)),
        EXIT((arena, arenaPlayer) -> arenaPlayer.leaveArena()),
        STATS((arena, arenaPlayer) -> ArenaAPI.getStatsManager().getStatsMenu().open(arenaPlayer.getPlayer(), 1)),
        READY((arena, arenaPlayer) -> arenaPlayer.setState(arenaPlayer.isReady() ? GameState.WAITING : GameState.READY));

        private final BiConsumer<Arena, ArenaPlayer> usage;

        Type(@NotNull BiConsumer<Arena, ArenaPlayer> usage) {
            this.usage = usage;
        }

        public int getDefaultSlot() {
            return switch (this) {
                case KIT_SHOP -> 2;
                case EXIT -> 8;
                case READY -> 6;
                case KIT_SELECT -> 4;
                case STATS -> 0;
            };
        }

        @NotNull
        public ItemStack getDefaultItem() {
            ItemStack item = new ItemStack(Material.EMERALD);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return item;

            Material material = switch (this) {
                case KIT_SHOP -> {
                    meta.setDisplayName(Colorizer.apply("&d&lKit Shop&7 (Right-Click)"));
                    meta.setLore(Colorizer.apply(new ArrayList<>(Collections.singletonList("&7Purchase and try new kits!"))));
                    yield Material.ENDER_CHEST;
                }
                case KIT_SELECT -> {
                    meta.setDisplayName(Colorizer.apply("&6&lKit Selector&7 (Right-Click)"));
                    meta.setLore(Colorizer.apply(new ArrayList<>(Collections.singletonList("&7Select your kit to fight with!"))));
                    yield Material.CHEST;
                }
                case STATS -> {
                    meta.setDisplayName(Colorizer.apply("&b&lStatistics&7 (Right-Click)"));
                    meta.setLore(Colorizer.apply(new ArrayList<>(Collections.singletonList("&7Check your game stats!"))));
                    yield Material.MAP;
                }
                case READY -> {
                    meta.setDisplayName(Colorizer.apply("&a&lReady State&7 (Right-Click)"));
                    meta.setLore(Colorizer.apply(new ArrayList<>(Collections.singletonList("&7Are you ready to play?"))));
                    yield Material.LIME_DYE;
                }
                case EXIT -> {
                    meta.setDisplayName(Colorizer.apply("&c&lExit&7 (Right-Click)"));
                    meta.setLore(Colorizer.apply(new ArrayList<>(Collections.singletonList("&7Leaving so soon?"))));
                    yield Material.REDSTONE;
                }
            };

            meta.addItemFlags(ItemFlag.values());
            item.setItemMeta(meta);
            item.setType(material);
            return item;
        }
    }
}
