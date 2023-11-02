package su.nightexpress.ama.arena.editor.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.game.GameplaySettings;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;

public class GameplayEditorPlayers extends EditorMenu<AMA, GameplaySettings> {

    private static final String TITLE = "Gameplay Editor [Mobs & Players]";

    private static final String TEXTURE_HIGHLIGHT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFlMTQ2MWE1NGFhNTRjZjI4ZGQ2YWVhZGFjNzJjOGQ3YzY5MTM5ODFkZWUxM2YyZTMzNTE0MjU2YWQ0YjgyNiJ9fX0=";
    private static final String TEXTURE_LOOT      = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQ1MTg0NTk0M2ZkMGMwN2Y2Mjk3MTFlMzQwMWE3MWEzMWNkMzcxY2MzY2IzNmYzZjk2MzdiMGU3NTljYzQ4YSJ9fX0=";
    private static final String TEXTURE_PLAYERS   = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGZhMDY1ZjA0MjkwZWNmNDMxZjlhYTkwMGFiNmVhMTdiYzM1NGY3MGE1OTZmMTgyNmJiMjM1OTJmODdkZGJhNyJ9fX0=";
    private static final String TEXTURE_LIFES     = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM4ZmI2MzdkNmUxYTdiYThmYTk3ZWU5ZDI5MTVlODQzZThlYzc5MGQ4YjdiZjYwNDhiZTYyMWVlNGQ1OWZiYSJ9fX0=";

    public GameplayEditorPlayers(@NotNull GameplaySettings game) {
        super(game.plugin(), game, TITLE, 54);

        this.addReturn(49).setClick((viewer, event) -> {
            game.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_GLOBALS, 2).setClick((viewer, event) -> {
            game.getEditorGlobals().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_REQUIREMENTS, 3).setClick((viewer, event) -> {
            game.getEditorRequirements().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.GAMEPLAY_PAGE_PLAYERS, 5).setClick((viewer, event) -> {
            game.getEditorPlayers().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_COMPAT, 6).setClick((viewer, event) -> {
            game.getEditorCompat().openNextTick(viewer, 1);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_PLAYERS), EditorLocales.GAMEPLAY_PLAYER_AMOUNT, 10).setClick((viewer, event) -> {
            this.handleInput(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_NUMBER), wrapper -> {
                if (event.isLeftClick()) {
                    game.setPlayerMinAmount(wrapper.asInt(1));
                }
                else {
                    game.setPlayerMaxAmount(wrapper.asInt(1));
                }
                game.save();
                return true;
            });
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_LIFES), EditorLocales.GAMEPLAY_PLAYER_LIFES, 12).setClick((viewer, event) -> {
            if (event.getClick() == ClickType.DROP) {
                game.setPlayerLifes(-1);
                this.save(viewer);
                return;
            }
            if (event.getClick() == ClickType.SWAP_OFFHAND) {
                game.setPlayerRespawnTime(-1);
                this.save(viewer);
                return;
            }

            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, wrapper -> {
                    game.setPlayerLifes(wrapper.asInt(1));
                    game.save();
                    return true;
                });
            }
            else if (event.isRightClick()) {
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_SECONDS, wrapper -> {
                    game.setPlayerRespawnTime(wrapper.asAnyInt(-1));
                    game.save();
                    return true;
                });
            }
        });

        this.addItem(Material.ROTTEN_FLESH, EditorLocales.GAMEPLAY_HUNGER_REGEN, 14).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                game.setHungerEnabled(!game.isHungerEnabled());
            }
            else if (event.isRightClick()) {
                game.setRegenerationEnabled(!game.isRegenerationEnabled());
            }
            this.save(viewer);
        });

        this.addItem(Material.BUNDLE, EditorLocales.GAMEPLAY_KEEP_INVENTORY, 16).setClick((viewer, event) -> {
            game.setKeepInventory(!game.isKeepInventory());
            this.save(viewer);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_HIGHLIGHT), EditorLocales.GAMEPLAY_MOB_HIGHLIGHT, 28).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isLeftClick()) {
                    game.setMobHighlightColor(CollectionsUtil.next(game.getMobHighlightColor(), ChatColor::isColor));
                }
            }
            else {
                if (event.isLeftClick()) {
                    game.setMobHighlightEnabled(!game.isMobHighlightEnabled());
                }
                if (event.isRightClick()) {
                    this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_PERCENT, wrapper -> {
                        game.setMobHighlightAmount(wrapper.asDouble(0D));
                        game.save();
                        return true;
                    });
                    return;
                }
            }
            this.save(viewer);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_LOOT), EditorLocales.GAMEPLAY_MOB_LOOT, 30).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                game.setMobDropLootEnabled(!game.isMobDropLootEnabled());
            }
            else if (event.isRightClick()) {
                game.setMobDropExpEnabled(!game.isMobDropExpEnabled());
            }
            this.save(viewer);
        });

        this.addItem(Material.ARROW, EditorLocales.GAMEPLAY_ITEM_PICK_DROP, 32).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                game.setItemDropEnabled(!game.isItemDropEnabled());
            }
            else if (event.isRightClick()) {
                game.setItemPickupEnabled(!game.isItemPickupEnabled());
            }
            this.save(viewer);
        });

        this.addItem(Material.DAMAGED_ANVIL, EditorLocales.GAMEPLAY_ITEM_DURABILITY, 34).setClick((viewer, event) -> {
            game.setItemDurabilityEnabled(!game.isItemDurabilityEnabled());
            this.save(viewer);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, game.replacePlaceholders());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }
}
