package su.nightexpress.ama.arena.editor.game;

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
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.game.ArenaGameplayManager;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.editor.ArenaEditorUtils;
import su.nightexpress.ama.kit.Kit;

import java.util.Map;

public class EditorArenaGameplay extends AbstractEditorMenu<AMA, ArenaGameplayManager> {

    private EditorGameCommandList commandList;

    public EditorArenaGameplay(@NotNull ArenaGameplayManager gameplayManager) {
        super(gameplayManager.plugin(), gameplayManager, ArenaEditorUtils.TITLE_GAMEPLAY_EDITOR, 54);

        EditorInput<ArenaGameplayManager, ArenaEditorType> input = (player, game, type, e) -> {
            String msg = StringUtil.color(e.getMessage());

            switch (type) {
                case GAMEPLAY_CHANGE_TIMELEFT -> {
                    int time = StringUtil.getInteger(msg, 0);
                    game.setTimeleft(time);
                }
                case GAMEPLAY_CHANGE_LOBBY_TIME -> {
                    int time = StringUtil.getInteger(msg, 1);
                    game.setLobbyTime(time);
                }
                case GAMEPLAY_CHANGE_SCOREBOARD_ID -> game.setScoreboardId(StringUtil.colorOff(msg));
                case GAMEPLAY_CHANGE_MOB_HIGHLIGHT_AMOUNT -> {
                    double amount = StringUtil.getDouble(msg, 0);
                    game.setMobHighlightAmount(amount);
                }
                case GAMEPLAY_CHANGE_PLAYERS_AMOUNT_MIN -> {
                    int amount = StringUtil.getInteger(msg, 1);
                    game.setPlayerMinAmount(amount);
                }
                case GAMEPLAY_CHANGE_PLAYERS_AMOUNT_MAX -> {
                    int amount = StringUtil.getInteger(msg, 1);
                    game.setPlayerMaxAmount(amount);
                }
                case GAMEPLAY_CHANGE_PLAYERS_DEATH_LIVES -> {
                    int amount = StringUtil.getInteger(msg, 1);
                    game.setPlayerLivesAmount(amount);
                }
                case GAMEPLAY_CHANGE_COMMANDS_ADD_WHITE -> game.getPlayerCommandsAllowed().add(StringUtil.colorOff(msg));
                case GAMEPLAY_CHANGE_BANNED_ITEMS -> {
                    Material material = Material.getMaterial(msg.toUpperCase());
                    if (material == null) {
                        EditorManager.error(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Error_BannedItems).getLocalized());
                        return false;
                    }
                    game.getBannedItems().add(material);
                }
                case GAMEPLAY_CHANGE_ALLOWED_SPAWN_REASONS -> {
                    CreatureSpawnEvent.SpawnReason spawnReason = CollectionsUtil.getEnum(msg, CreatureSpawnEvent.SpawnReason.class);
                    if (spawnReason == null) {
                        EditorManager.error(player, plugin.getMessage(Lang.EDITOR_ERROR_ENUM).getLocalized());
                        return false;
                    }
                    game.getAllowedSpawnReasons().add(spawnReason);
                }
                case GAMEPLAY_CHANGE_KITS_ADD_ALLOWED -> {
                    String id = StringUtil.colorOff(msg);
                    Kit kit = plugin.getKitManager().getKitById(id);
                    if (kit == null && !id.equals(Placeholders.WILDCARD)) {
                        EditorManager.error(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Error_Kits_InvalidKit).getLocalized());
                        return false;
                    }
                    game.getKitsAllowed().add(id.toLowerCase());
                }
                case GAMEPLAY_CHANGE_KITS_ADD_LIMIT -> {
                    String[] split = msg.split(" ");
                    int limit = split.length >= 2 ? StringUtil.getInteger(split[0], -1) : -1;
                    if (limit < 0) {
                        EditorManager.error(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Error_Kits_InvalidLimit).getLocalized());
                        return false;
                    }

                    String id = split[1];
                    Kit kit = plugin.getKitManager().getKitById(id);
                    if (kit == null) {
                        EditorManager.error(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Error_Kits_InvalidKit).getLocalized());
                        return false;
                    }

                    game.getKitsLimits().put(kit.getId(), limit);
                }
                default -> {
                    return true;
                }
            }

            game.save();
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
                            gameplayManager.setTimeleft(-1);
                            break;
                        }
                        EditorManager.startEdit(player, gameplayManager, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_Timeleft).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_CHANGE_LOBBY_TIME -> {
                        EditorManager.startEdit(player, gameplayManager, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_LobbyTime).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_CHANGE_ANNOUNCES -> gameplayManager.setAnnouncesEnabled(!gameplayManager.isAnnouncesEnabled());
                    case GAMEPLAY_CHANGE_SCOREBOARD -> {
                        if (e.isRightClick()) {
                            EditorManager.startEdit(player, gameplayManager, ArenaEditorType.GAMEPLAY_CHANGE_SCOREBOARD_ID, input);
                            EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_ScoreboardId).getLocalized());
                            EditorManager.suggestValues(player, Config.SCOREBOARDS.get().keySet(), true);
                            player.closeInventory();
                            return;
                        }
                        gameplayManager.setScoreboardEnabled(!gameplayManager.isScoreboardEnabled());
                    }
                    //case GAMEPLAY_CHANGE_SHOP -> gameplayManager.setShopEnabled(!gameplayManager.isShopEnabled());
                    case GAMEPLAY_CHANGE_HUNGER -> gameplayManager.setHungerEnabled(!gameplayManager.isHungerEnabled());
                    case GAMEPLAY_CHANGE_REGENERATION -> gameplayManager.setRegenerationEnabled(!gameplayManager.isRegenerationEnabled());
                    case GAMEPLAY_CHANGE_ITEM -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                gameplayManager.setItemDurabilityEnabled(!gameplayManager.isItemDurabilityEnabled());
                            }
                            break;
                        }
                        if (e.isLeftClick()) {
                            gameplayManager.setItemDropEnabled(!gameplayManager.isItemDropEnabled());
                        }
                        else if (e.isRightClick()) {
                            gameplayManager.setItemPickupEnabled(!gameplayManager.isItemPickupEnabled());
                        }
                    }
                    case GAMEPLAY_CHANGE_MOB_DROP -> {
                        if (e.isLeftClick()) {
                            gameplayManager.setMobDropLootEnabled(!gameplayManager.isMobDropLootEnabled());
                        }
                        else if (e.isRightClick()) {
                            gameplayManager.setMobDropExpEnabled(!gameplayManager.isMobDropExpEnabled());
                        }
                    }
                    case GAMEPLAY_CHANGE_MOB_HIGHLIGHT -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                gameplayManager.setMobHighlightColor(CollectionsUtil.switchEnum(gameplayManager.getMobHighlightColor()));
                                while (!gameplayManager.getMobHighlightColor().isColor()) {
                                    gameplayManager.setMobHighlightColor(CollectionsUtil.switchEnum(gameplayManager.getMobHighlightColor()));
                                }
                            }
                        }
                        else {
                            if (e.isLeftClick()) {
                                gameplayManager.setMobHighlightEnabled(!gameplayManager.isMobHighlightEnabled());
                            }
                            if (e.isRightClick()) {
                                EditorManager.startEdit(player, gameplayManager, ArenaEditorType.GAMEPLAY_CHANGE_MOB_HIGHLIGHT_AMOUNT, input);
                                EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_Mob_Highlight_Amount).getLocalized());
                                player.closeInventory();
                                return;
                            }
                        }
                    }
                    case GAMEPLAY_CHANGE_PLAYERS_AMOUNT -> {
                        if (e.isLeftClick()) {
                            EditorManager.startEdit(player, gameplayManager, ArenaEditorType.GAMEPLAY_CHANGE_PLAYERS_AMOUNT_MIN, input);
                        }
                        else if (e.isRightClick()) {
                            EditorManager.startEdit(player, gameplayManager, ArenaEditorType.GAMEPLAY_CHANGE_PLAYERS_AMOUNT_MAX, input);
                        }
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_Players_MinMax).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_CHANGE_PLAYERS_DEATH -> {
						/*if (e.getClick() == ClickType.MIDDLE) {
							gameplayManager.setPlayerExpSavingEnabled(!gameplayManager.isPlayerExpSavingEnabled());
							break;
						}
						*/
                        if (e.isLeftClick()) {
                            gameplayManager.setPlayerDropItemsOnDeathEnabled(!gameplayManager.isPlayerDropItemsOnDeathEnabled());
                        }
                        else if (e.isRightClick()) {
                            EditorManager.startEdit(player, gameplayManager, ArenaEditorType.GAMEPLAY_CHANGE_PLAYERS_DEATH_LIVES, input);
                            EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_Players_Lives).getLocalized());
                            player.closeInventory();
                            return;
                        }
                    }
                    case GAMEPLAY_CHANGE_SPECTATE -> {
                        if (e.isLeftClick()) {
                            gameplayManager.setSpectateEnabled(!gameplayManager.isSpectateEnabled());
                        }
                        else if (e.isRightClick()) {
                            gameplayManager.setSpectateOnDeathEnabled(!gameplayManager.isSpectateOnDeathEnabled());
                        }
                    }
                    case GAMEPLAY_CHANGE_BANNED_ITEMS -> {
                        if (e.isRightClick()) {
                            gameplayManager.getBannedItems().clear();
                            break;
                        }

                        EditorManager.startEdit(player, gameplayManager, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_BannedItems).getLocalized());
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_CHANGE_ALLOWED_SPAWN_REASONS -> {
                        if (e.isRightClick()) {
                            gameplayManager.getAllowedSpawnReasons().clear();
                            break;
                        }

                        EditorManager.startEdit(player, gameplayManager, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_AllowedSpawnReason).getLocalized());
                        EditorManager.suggestValues(player, CollectionsUtil.getEnumsList(CreatureSpawnEvent.SpawnReason.class), true);
                        player.closeInventory();
                        return;
                    }
                    case GAMEPLAY_CHANGE_COMMANDS -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                EditorManager.startEdit(player, gameplayManager, ArenaEditorType.GAMEPLAY_CHANGE_COMMANDS_ADD_WHITE, input);
                                EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_Commands_AddWhite).getLocalized());
                                player.closeInventory();
                                return;
                            }
                            else {
                                gameplayManager.getPlayerCommandsAllowed().clear();
                                break;
                            }
                        }

                        if (e.isLeftClick()) {
                            gameplayManager.setPlayerCommandsEnabled(!gameplayManager.isPlayerCommandsEnabled());
                        }
                    }
                    case GAMEPLAY_CHANGE_KITS -> {
                        if (e.isShiftClick()) {
                            if (e.isLeftClick()) {
                                EditorManager.startEdit(player, gameplayManager, ArenaEditorType.GAMEPLAY_CHANGE_KITS_ADD_ALLOWED, input);
                                EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_Kits_AddLimit).getLocalized());
                                EditorManager.suggestValues(player, plugin.getKitManager().getKitIds(), true);
                                player.closeInventory();
                                return;
                            }
                            else {
                                gameplayManager.getKitsAllowed().clear();
                            }
                        }
                        else {
                            if (e.isLeftClick()) {
                                gameplayManager.setKitsEnabled(!gameplayManager.isKitsEnabled());
                            }
                        }
                    }
                    case GAMEPLAY_CHANGE_KITS_LIMITS -> {
                        if (e.isLeftClick()) {
                            EditorManager.startEdit(player, gameplayManager, ArenaEditorType.GAMEPLAY_CHANGE_KITS_ADD_LIMIT, input);
                            EditorManager.tip(player, plugin.getMessage(Lang.Editor_Arena_Gameplay_Enter_Kits_AddLimit).getLocalized());
                            EditorManager.suggestValues(player, plugin.getKitManager().getKitIds(), true);
                            player.closeInventory();
                            return;
                        }
                        else {
                            gameplayManager.getKitsLimits().clear();
                        }
                    }
                    case GAMEPLAY_CHANGE_PETS_ALLOWED -> gameplayManager.setExternalPetsEnabled(!gameplayManager.isExternalPetsEnabled());
                    case GAMEPLAY_CHANGE_MCMMO_ALLOWED -> gameplayManager.setExternalMcmmoEnabled(!gameplayManager.isExternalMcmmoEnabled());
                    case GAMEPLAY_CHANGE_AUTO_COMMANDS -> {
                        this.getCommandList().open(player, 1);
                        return;
                    }
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
    public void clear() {
        if (this.commandList != null) {
            this.commandList.clear();
            this.commandList = null;
        }
        super.clear();
    }

    @NotNull
    public EditorGameCommandList getCommandList() {
        if (this.commandList == null) {
            this.commandList = new EditorGameCommandList(this.object);
        }
        return commandList;
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(ArenaEditorType.GAMEPLAY_CHANGE_AUTO_COMMANDS, 4);
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