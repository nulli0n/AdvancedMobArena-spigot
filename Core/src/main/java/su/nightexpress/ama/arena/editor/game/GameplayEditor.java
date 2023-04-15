package su.nightexpress.ama.arena.editor.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.api.editor.EditorInput;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.editor.AbstractEditorMenu;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.game.ArenaGameplayManager;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;

import java.util.Map;

public class GameplayEditor extends AbstractEditorMenu<AMA, ArenaGameplayManager> {

    public GameplayEditor(@NotNull ArenaGameplayManager game) {
        super(game.plugin(), game, ArenaEditorHub.TITLE_GAMEPLAY_EDITOR, 54);

        EditorInput<ArenaGameplayManager, ArenaEditorType> input = (player, game1, type, e) -> {
            String msg = e.getMessage();

            switch (type) {
                case GAMEPLAY_CHANGE_TIMELEFT -> game1.setTimeleft(StringUtil.getInteger(Colorizer.strip(msg), 0));
                case GAMEPLAY_CHANGE_LOBBY_TIME -> game1.setLobbyTime(StringUtil.getInteger(Colorizer.strip(msg), 0));
                case GAMEPLAY_CHANGE_SCOREBOARD_ID -> game1.setScoreboardId(Colorizer.strip(msg));
                case GAMEPLAY_CHANGE_MOB_HIGHLIGHT_AMOUNT -> game1.setMobHighlightAmount(StringUtil.getDouble(Colorizer.strip(msg), 0));
                case GAMEPLAY_CHANGE_PLAYERS_AMOUNT_MIN -> game1.setPlayerMinAmount(StringUtil.getInteger(Colorizer.strip(msg), 0));
                case GAMEPLAY_CHANGE_PLAYERS_AMOUNT_MAX -> game1.setPlayerMaxAmount(StringUtil.getInteger(Colorizer.strip(msg), 0));
                case GAMEPLAY_CHANGE_PLAYERS_DEATH_LIVES -> game1.setPlayerLivesAmount(StringUtil.getInteger(Colorizer.strip(msg), 0));
                case GAMEPLAY_CHANGE_COMMANDS_ADD_WHITE -> game1.getPlayerCommandsAllowed().add(Colorizer.strip(msg));
                case GAMEPLAY_CHANGE_BANNED_ITEMS -> {
                    Material material = Material.getMaterial(Colorizer.strip(msg).toUpperCase());
                    if (material != null) {
                        game1.getBannedItems().add(material);
                    }
                }
                case GAMEPLAY_CHANGE_ALLOWED_SPAWN_REASONS -> StringUtil.getEnum(Colorizer.strip(msg), CreatureSpawnEvent.SpawnReason.class).ifPresent(spawnReason -> {
                    game1.getAllowedSpawnReasons().add(spawnReason);
                });
                case GAMEPLAY_CHANGE_KITS_ADD_ALLOWED -> game1.getKitsAllowed().add(Colorizer.strip(msg).toLowerCase());
                case GAMEPLAY_CHANGE_KITS_ADD_LIMIT -> {
                    String[] split = Colorizer.strip(msg).split(" ");
                    int limit = split.length >= 2 ? StringUtil.getInteger(split[0], -1) : -1;
                    String id = split[1];
                    game1.getKitsLimits().put(id.toLowerCase(), limit);
                }
                default -> {
                    return true;
                }
            }

            game1.save();
            return true;
        };

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.RETURN) {
                    this.object.getArenaConfig().getEditor().open(player, 1);
                }
            }
            else if (type instanceof ArenaEditorType type2) {
                switch (type2) {
                    case GAMEPLAY_CHANGE_TIMELEFT -> {
                        if (e.isRightClick()) {
                            game.setTimeleft(-1);
                            break;
                        }
                        EditorManager.startEdit(player, game, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_SECONDS).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_CHANGE_LOBBY_TIME -> {
                        EditorManager.startEdit(player, game, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_SECONDS).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_CHANGE_ANNOUNCES -> game.setAnnouncesEnabled(!game.isAnnouncesEnabled());
                    case GAMEPLAY_CHANGE_SCOREBOARD -> {
                        if (e.isRightClick()) {
                            EditorManager.startEdit(player, game, ArenaEditorType.GAMEPLAY_CHANGE_SCOREBOARD_ID, input);
                            EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_ARENA_GAMEPLAY_ENTER_SCOREBOARD_ID).getLocalized());
                            EditorManager.suggestValues(player, Config.SCOREBOARDS.get().keySet(), true);
                            player.closeInventory();
                            return;
                        }
                        game.setScoreboardEnabled(!game.isScoreboardEnabled());
                    }
                    case GAMEPLAY_CHANGE_HUNGER -> game.setHungerEnabled(!game.isHungerEnabled());
                    case GAMEPLAY_CHANGE_REGENERATION -> game.setRegenerationEnabled(!game.isRegenerationEnabled());
                    case GAMEPLAY_CHANGE_ITEM -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                game.setItemDurabilityEnabled(!game.isItemDurabilityEnabled());
                            }
                            break;
                        }
                        if (e.isLeftClick()) {
                            game.setItemDropEnabled(!game.isItemDropEnabled());
                        }
                        else if (e.isRightClick()) {
                            game.setItemPickupEnabled(!game.isItemPickupEnabled());
                        }
                    }
                    case GAMEPLAY_CHANGE_MOB_DROP -> {
                        if (e.isLeftClick()) {
                            game.setMobDropLootEnabled(!game.isMobDropLootEnabled());
                        }
                        else if (e.isRightClick()) {
                            game.setMobDropExpEnabled(!game.isMobDropExpEnabled());
                        }
                    }
                    case GAMEPLAY_CHANGE_MOB_HIGHLIGHT -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                game.setMobHighlightColor(CollectionsUtil.next(game.getMobHighlightColor(), ChatColor::isColor));
                            }
                        }
                        else {
                            if (e.isLeftClick()) {
                                game.setMobHighlightEnabled(!game.isMobHighlightEnabled());
                            }
                            if (e.isRightClick()) {
                                EditorManager.startEdit(player, game, ArenaEditorType.GAMEPLAY_CHANGE_MOB_HIGHLIGHT_AMOUNT, input);
                                EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_PERCENT).getLocalized());
                                player.closeInventory();
                                return;
                            }
                        }
                    }
                    case GAMEPLAY_CHANGE_PLAYERS_AMOUNT -> {
                        if (e.isLeftClick()) {
                            type2 = ArenaEditorType.GAMEPLAY_CHANGE_PLAYERS_AMOUNT_MIN;
                        }
                        else if (e.isRightClick()) {
                            type2 = ArenaEditorType.GAMEPLAY_CHANGE_PLAYERS_AMOUNT_MAX;
                        }
                        EditorManager.startEdit(player, game, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_NUMBER).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_CHANGE_PLAYERS_DEATH -> {
                        if (e.isLeftClick()) {
                            game.setPlayerDropItemsOnDeathEnabled(!game.isPlayerDropItemsOnDeathEnabled());
                        }
                        else if (e.isRightClick()) {
                            EditorManager.startEdit(player, game, ArenaEditorType.GAMEPLAY_CHANGE_PLAYERS_DEATH_LIVES, input);
                            EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_NUMBER).getLocalized());
                            player.closeInventory();
                            return;
                        }
                    }
                    case GAMEPLAY_CHANGE_SPECTATE -> {
                        if (e.isLeftClick()) {
                            game.setSpectateEnabled(!game.isSpectateEnabled());
                        }
                        else if (e.isRightClick()) {
                            game.setSpectateOnDeathEnabled(!game.isSpectateOnDeathEnabled());
                        }
                    }
                    case GAMEPLAY_CHANGE_BANNED_ITEMS -> {
                        if (e.isRightClick()) {
                            game.getBannedItems().clear();
                            break;
                        }

                        EditorManager.startEdit(player, game, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_ARENA_GAMEPLAY_ENTER_BANNED_ITEMS).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_CHANGE_ALLOWED_SPAWN_REASONS -> {
                        if (e.isRightClick()) {
                            game.getAllowedSpawnReasons().clear();
                            break;
                        }

                        EditorManager.startEdit(player, game, type2, input);
                        EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_ARENA_GAMEPLAY_ENTER_ALLOWED_SPAWN_REASON).getLocalized());
                        EditorManager.suggestValues(player, CollectionsUtil.getEnumsList(CreatureSpawnEvent.SpawnReason.class), true);
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_CHANGE_COMMANDS -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                EditorManager.startEdit(player, game, ArenaEditorType.GAMEPLAY_CHANGE_COMMANDS_ADD_WHITE, input);
                                EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_GENERIC_ENTER_COMMAND).getLocalized());
                                player.closeInventory();
                                return;
                            }
                            else {
                                game.getPlayerCommandsAllowed().clear();
                                break;
                            }
                        }

                        if (e.isLeftClick()) {
                            game.setPlayerCommandsEnabled(!game.isPlayerCommandsEnabled());
                        }
                    }
                    case GAMEPLAY_CHANGE_KITS -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                EditorManager.startEdit(player, game, ArenaEditorType.GAMEPLAY_CHANGE_KITS_ADD_ALLOWED, input);
                                EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_ARENA_GAMEPLAY_ENTER_KIT_LIMIT).getLocalized());
                                EditorManager.suggestValues(player, plugin.getKitManager().getKitIds(), true);
                                player.closeInventory();
                                return;
                            }
                            else {
                                game.getKitsAllowed().clear();
                            }
                        }
                        else {
                            if (e.isLeftClick()) {
                                game.setKitsEnabled(!game.isKitsEnabled());
                            }
                        }
                    }
                    case GAMEPLAY_CHANGE_KITS_LIMITS -> {
                        if (e.isLeftClick()) {
                            EditorManager.startEdit(player, game, ArenaEditorType.GAMEPLAY_CHANGE_KITS_ADD_LIMIT, input);
                            EditorManager.prompt(player, plugin.getMessage(Lang.EDITOR_ARENA_GAMEPLAY_ENTER_KIT_LIMIT).getLocalized());
                            EditorManager.suggestValues(player, plugin.getKitManager().getKitIds(), true);
                            player.closeInventory();
                            return;
                        }
                        else {
                            game.getKitsLimits().clear();
                        }
                    }
                    case GAMEPLAY_CHANGE_PETS_ALLOWED -> game.setExternalPetsEnabled(!game.isExternalPetsEnabled());
                    case GAMEPLAY_CHANGE_MCMMO_ALLOWED -> game.setExternalMcmmoEnabled(!game.isExternalMcmmoEnabled());
                    default -> {
                        return;
                    }
                }
                this.object.save();
                this.open(player, 1);
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_TIMELEFT, 9);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_LOBBY_TIME, 10);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_ANNOUNCES, 11);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_SCOREBOARD, 12);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_HUNGER, 13);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_REGENERATION, 14);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_ITEM, 15);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_MOB_HIGHLIGHT, 16);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_MOB_DROP, 17);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_BANNED_ITEMS, 18);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_ALLOWED_SPAWN_REASONS, 19);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_PLAYERS_AMOUNT, 20);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_PLAYERS_DEATH, 21);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_SPECTATE, 22);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_COMMANDS, 23);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_KITS, 24);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_KITS_LIMITS, 25);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_PETS_ALLOWED, 26);
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_MCMMO_ALLOWED, 27);

        map.put(MenuItemType.RETURN, 49);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}