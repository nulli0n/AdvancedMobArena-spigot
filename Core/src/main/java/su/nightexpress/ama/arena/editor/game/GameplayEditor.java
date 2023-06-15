package su.nightexpress.ama.arena.editor.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.game.ArenaGameplayManager;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;

public class GameplayEditor extends EditorMenu<AMA, ArenaGameplayManager> {

    private static final String TEXTURE_TIMER = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGJjYjIzMGE0MTBlOTNiN2Q0YjVjMjg5NjMxZDYxNGI5MDQ1Mzg0M2Q2ZWQwM2RhZjVlNDAxNWEyZmUxZjU2YiJ9fX0=";
    private static final String TEXTURE_ARROW_UP = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWFkNmM4MWY4OTlhNzg1ZWNmMjZiZTFkYzQ4ZWFlMmJjZmU3NzdhODYyMzkwZjU3ODVlOTViZDgzYmQxNGQifX19";
    private static final String TEXTURE_ARROW_DOWN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODgyZmFmOWE1ODRjNGQ2NzZkNzMwYjIzZjg5NDJiYjk5N2ZhM2RhZDQ2ZDRmNjVlMjg4YzM5ZWI0NzFjZTcifX19";
    private static final String TEXTURE_GLOW_SQUID = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmFlMTQ2MWE1NGFhNTRjZjI4ZGQ2YWVhZGFjNzJjOGQ3YzY5MTM5ODFkZWUxM2YyZTMzNTE0MjU2YWQ0YjgyNiJ9fX0=";
    private static final String TEXTURE_BONE_BAG = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQ1MTg0NTk0M2ZkMGMwN2Y2Mjk3MTFlMzQwMWE3MWEzMWNkMzcxY2MzY2IzNmYzZjk2MzdiMGU3NTljYzQ4YSJ9fX0=";
    private static final String TEXTURE_BARRIER = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==";
    private static final String TEXTURE_SPAWN_EGG = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjE3ODdiNjUyYWY5MTA0YjgzYzljOGUyNmUzNzM0NGUyZmU0Y2I1OWZkYWZiMDRlZmY0ZWRkZDlkN2E5OTk5NCJ9fX0=";
    private static final String TEXTURE_STEVE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGZhMDY1ZjA0MjkwZWNmNDMxZjlhYTkwMGFiNmVhMTdiYzM1NGY3MGE1OTZmMTgyNmJiMjM1OTJmODdkZGJhNyJ9fX0=";
    private static final String TEXTURE_HEART = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM4ZmI2MzdkNmUxYTdiYThmYTk3ZWU5ZDI5MTVlODQzZThlYzc5MGQ4YjdiZjYwNDhiZTYyMWVlNGQ1OWZiYSJ9fX0=";
    private static final String TEXTURE_IRON_HELMET = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVlYjBiZDg1YWFkZGYwZDI5ZWQwODJlYWMwM2ZjYWRlNDNkMGVlODAzYjBlODE2MmFkZDI4YTYzNzlmYjU0ZSJ9fX0=";

    public GameplayEditor(@NotNull ArenaGameplayManager game) {
        super(game.plugin(), game, EditorHub.TITLE_GAMEPLAY_EDITOR, 54);

        this.addReturn(49).setClick((viewer, event) -> {
            game.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.CLOCK, EditorLocales.GAMEPLAY_TIMELEFT, 9).setClick((viewer, event) -> {
            if (event.getClick() == ClickType.DROP) {
                game.setTimeleft(-1);
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_SECONDS, wrapper -> {
                game.setTimeleft(wrapper.asInt(0));
                game.save();
                return true;
            });
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_TIMER), EditorLocales.GAMEPLAY_LOBBY_COUNTDOWN, 10).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_SECONDS, wrapper -> {
                game.setLobbyTime(wrapper.asInt());
                game.save();
                return true;
            });
        });

        this.addItem(Material.BELL, EditorLocales.GAMEPLAY_ANNOUNCEMENTS, 11).setClick((viewer, event) -> {
            game.setAnnouncesEnabled(!game.isAnnouncesEnabled());
            this.save(viewer);
        });

        this.addItem(Material.OAK_SIGN, EditorLocales.GAMEPLAY_SCOREBOARD, 12).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                EditorManager.suggestValues(viewer.getPlayer(), Config.SCOREBOARDS.get().keySet(), true);
                this.handleInput(viewer, Lang.EDITOR_ARENA_GAMEPLAY_ENTER_SCOREBOARD_ID, wrapper -> {
                    game.setScoreboardId(wrapper.getTextRaw());
                    game.save();
                    return true;
                });
                return;
            }
            game.setScoreboardEnabled(!game.isScoreboardEnabled());
            this.save(viewer);
        });

        this.addItem(Material.ROTTEN_FLESH, EditorLocales.GAMEPLAY_HUNGER, 13).setClick((viewer, event) -> {
            game.setHungerEnabled(!game.isHungerEnabled());
            this.save(viewer);
        });

        this.addItem(Material.APPLE, EditorLocales.GAMEPLAY_REGENERATION, 14).setClick((viewer, event) -> {
            game.setRegenerationEnabled(!game.isRegenerationEnabled());
            this.save(viewer);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_ARROW_UP), EditorLocales.GAMEPLAY_ITEM_SETTINGS, 15).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isLeftClick()) {
                    game.setItemDurabilityEnabled(!game.isItemDurabilityEnabled());
                }
            }
            else {
                if (event.isLeftClick()) {
                    game.setItemDropEnabled(!game.isItemDropEnabled());
                }
                else if (event.isRightClick()) {
                    game.setItemPickupEnabled(!game.isItemPickupEnabled());
                }
            }
            this.save(viewer);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_GLOW_SQUID), EditorLocales.GAMEPLAY_MOB_HIGHLIGHT, 16).setClick((viewer, event) -> {
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

        this.addItem(ItemUtil.createCustomHead(TEXTURE_BONE_BAG), EditorLocales.GAMEPLAY_MOB_LOOT, 17).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                game.setMobDropLootEnabled(!game.isMobDropLootEnabled());
            }
            else if (event.isRightClick()) {
                game.setMobDropExpEnabled(!game.isMobDropExpEnabled());
            }
            this.save(viewer);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_BARRIER), EditorLocales.GAMEPLAY_BANNED_ITEMS, 18).setClick((viewer, event) -> {
            if (event.getClick() == ClickType.DROP) {
                game.getBannedItems().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_ARENA_GAMEPLAY_ENTER_BANNED_ITEMS, wrapper -> {
                Material material = Material.getMaterial(wrapper.getTextRaw().toUpperCase());
                if (material != null) {
                    game.getBannedItems().add(material);
                }
                game.save();
                return true;
            });
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_SPAWN_EGG), EditorLocales.GAMEPLAY_ALLOWED_SPAWN_REASONS, 19).setClick((viewer, event) -> {
            if (event.getClick() == ClickType.DROP) {
                game.getAllowedSpawnReasons().clear();
                this.save(viewer);
                return;
            }

            EditorManager.suggestValues(viewer.getPlayer(), CollectionsUtil.getEnumsList(CreatureSpawnEvent.SpawnReason.class), true);
            this.handleInput(viewer, Lang.EDITOR_ARENA_GAMEPLAY_ENTER_ALLOWED_SPAWN_REASON, wrapper -> {
                StringUtil.getEnum(wrapper.getTextRaw(), CreatureSpawnEvent.SpawnReason.class).ifPresent(spawnReason -> {
                    game.getAllowedSpawnReasons().add(spawnReason);
                    game.save();
                });
                return true;
            });
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_STEVE), EditorLocales.GAMEPLAY_PLAYER_AMOUNT, 20).setClick((viewer, event) -> {
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

        this.addItem(ItemUtil.createCustomHead(TEXTURE_HEART), EditorLocales.GAMEPLAY_PLAYER_LIFES, 21).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, wrapper -> {
                    game.setPlayerLivesAmount(wrapper.asInt(1));
                    game.save();
                    return true;
                });
            }
            else if (event.isRightClick()) {
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_SECONDS, wrapper -> {
                    game.setPlayerReviveTime(wrapper.asAnyInt(-1));
                    game.save();
                    return true;
                });
            }
        });

        this.addItem(Material.SADDLE, EditorLocales.GAMEPLAY_KEEP_INVENTORY, 22).setClick((viewer, event) -> {
            game.setKeepInventory(!game.isKeepInventory());
            this.save(viewer);
        });

        this.addItem(Material.ENDER_EYE, EditorLocales.GAMEPLAY_SPECTATE, 23).setClick((viewer, event) -> {
            game.setSpectateEnabled(!game.isSpectateEnabled());
            this.save(viewer);
        });

        this.addItem(Material.COMMAND_BLOCK, EditorLocales.GAMEPLAY_COMMANDS, 24).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_COMMAND, wrapper -> {
                    game.getPlayerCommandsAllowed().add(wrapper.getTextRaw());
                    game.save();
                    return true;
                });
                return;
            }

            if (event.isLeftClick()) {
                game.setPlayerCommandsEnabled(!game.isPlayerCommandsEnabled());
            }
            else if (event.getClick() == ClickType.DROP) {
                game.getPlayerCommandsAllowed().clear();
            }
            this.save(viewer);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_IRON_HELMET), EditorLocales.GAMEPLAY_KITS, 25).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                EditorManager.suggestValues(viewer.getPlayer(), plugin.getKitManager().getKitIds(), true);
                this.handleInput(viewer, Lang.EDITOR_KIT_ENTER_ID, wrapper -> {
                    game.getKitsAllowed().add(wrapper.getTextRaw().toLowerCase());
                    game.save();
                    return true;
                });
                return;
            }

            if (event.isLeftClick()) {
                game.setKitsEnabled(!game.isKitsEnabled());
            }
            else if (event.getClick() == ClickType.DROP) {
                game.getKitsAllowed().clear();
            }
            this.save(viewer);
        });

        this.addItem(Material.ARMOR_STAND, EditorLocales.GAMEPLAY_KIT_LIMITS, 26).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                EditorManager.suggestValues(viewer.getPlayer(), plugin.getKitManager().getKitIds(), true);
                this.handleInput(viewer, Lang.EDITOR_ARENA_GAMEPLAY_ENTER_KIT_LIMIT, wrapper -> {
                    String[] split = wrapper.getTextRaw().split(" ");
                    int limit = split.length >= 2 ? StringUtil.getInteger(split[0], -1) : -1;
                    String id = split[1];
                    game.getKitsLimits().put(id.toLowerCase(), limit);
                    game.save();
                    return true;
                });
            }
            else if (event.getClick() == ClickType.DROP) {
                game.getKitsLimits().clear();
                this.save(viewer);
            }
        });

        this.addItem(Material.BONE, EditorLocales.GAMEPLAY_PETS, 27).setClick((viewer, event) -> {
            game.setExternalPetsEnabled(!game.isExternalPetsEnabled());
            this.save(viewer);
        });

        this.addItem(Material.DIAMOND_SWORD, EditorLocales.GAMEPLAY_MCMMO, 28).setClick((viewer, event) -> {
            game.setExternalMcmmoEnabled(!game.isExternalMcmmoEnabled());
            this.save(viewer);
        });

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, game.replacePlaceholders()));
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }
}