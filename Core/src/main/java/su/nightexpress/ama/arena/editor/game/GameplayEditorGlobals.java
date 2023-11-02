package su.nightexpress.ama.arena.editor.game;

import org.bukkit.Material;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.*;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.game.GameplaySettings;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.hook.HookId;

public class GameplayEditorGlobals extends EditorMenu<AMA, GameplaySettings> {

    private static final String TITLE = "Gameplay Editor [Globals]";

    private static final String TEXTURE_CLOCK_1     = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNiOGYwNjg4NWQxZGFhZmQyNmNkOTViMzQ4MmNiNTI1ZDg4MWE2N2UwZDI0NzE2MWI5MDhkOTNkNTZkMTE0ZiJ9fX0=";
    private static final String TEXTURE_CLOCK_2     = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2NhMWE0OGQyZDIzMWZhNzFiYTVmN2M0MGZkYzEwZDNmMmU5OGM1YTYzYzAxNzMyMWU2NzgxMzA4YjhhNTc5MyJ9fX0=";
    private static final String TEXTURE_BARRIER     = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==";
    private static final String TEXTURE_SPAWN_EGG   = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjE3ODdiNjUyYWY5MTA0YjgzYzljOGUyNmUzNzM0NGUyZmU0Y2I1OWZkYWZiMDRlZmY0ZWRkZDlkN2E5OTk5NCJ9fX0=";
    private static final String TEXTURE_IRON_HELMET = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVlYjBiZDg1YWFkZGYwZDI5ZWQwODJlYWMwM2ZjYWRlNDNkMGVlODAzYjBlODE2MmFkZDI4YTYzNzlmYjU0ZSJ9fX0=";
    private static final String TEXTURE_COMMAND     = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQwZjQwNjFiZmI3NjdhN2Y5MjJhNmNhNzE3NmY3YTliMjA3MDliZDA1MTI2OTZiZWIxNWVhNmZhOThjYTU1YyJ9fX0=";

    public GameplayEditorGlobals(@NotNull GameplaySettings game) {
        super(game.plugin(), game, TITLE, 54);

        this.addReturn(49).setClick((viewer, event) -> {
            game.getArenaConfig().getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.GAMEPLAY_PAGE_GLOBALS, 2).setClick((viewer, event) -> {
            game.getEditorGlobals().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_REQUIREMENTS, 3).setClick((viewer, event) -> {
            game.getEditorRequirements().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_PLAYERS, 5).setClick((viewer, event) -> {
            game.getEditorPlayers().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_COMPAT, 6).setClick((viewer, event) -> {
            game.getEditorCompat().openNextTick(viewer, 1);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_CLOCK_1), EditorLocales.GAMEPLAY_TIMELEFT, 10).setClick((viewer, event) -> {
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

        this.addItem(ItemUtil.createCustomHead(TEXTURE_CLOCK_2), EditorLocales.GAMEPLAY_LOBBY_COUNTDOWN, 12).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_SECONDS, wrapper -> {
                game.setLobbyTime(wrapper.asInt());
                game.save();
                return true;
            });
        });

        this.addItem(Material.BELL, EditorLocales.GAMEPLAY_ANNOUNCEMENTS, 14).setClick((viewer, event) -> {
            game.setAnnouncesEnabled(!game.isAnnouncesEnabled());
            this.save(viewer);
        });

        this.addItem(Material.ENDER_EYE, EditorLocales.GAMEPLAY_SPECTATE, 16).setClick((viewer, event) -> {
            game.setSpectateEnabled(!game.isSpectateEnabled());
            this.save(viewer);
        });

        this.addItem(Material.PAINTING, EditorLocales.GAMEPLAY_SCOREBOARD, 8).setClick((viewer, event) -> {
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
        }).getOptions().setVisibilityPolicy(viewer -> EngineUtils.hasPlugin(HookId.PROTOCOL_LIB));

        this.addItem(ItemUtil.createCustomHead(TEXTURE_IRON_HELMET), EditorLocales.GAMEPLAY_KITS, 28).setClick((viewer, event) -> {
            game.setKitsEnabled(!game.isKitsEnabled());
            this.save(viewer);
        });

        this.addItem(Material.ARMOR_STAND, EditorLocales.GAMEPLAY_ALLOWED_KITS, 30).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                EditorManager.suggestValues(viewer.getPlayer(), plugin.getKitManager().getKitIds(), false);
                this.handleInput(viewer, Lang.EDITOR_ARENA_GAMEPLAY_ENTER_KIT_LIMIT, wrapper -> {
                    String[] split = wrapper.getTextRaw().split(" ");
                    int limit = split.length >= 2 ? StringUtil.getInteger(split[0], -1) : -1;
                    String kitId = split[1];
                    game.getKitsLimits().put(kitId.toLowerCase(), limit);
                    game.save();
                    return true;
                });
            }
            else if (event.getClick() == ClickType.DROP) {
                game.getKitsLimits().clear();
                this.save(viewer);
            }
        }).getOptions().setVisibilityPolicy(viewer -> game.isKitsEnabled());

        this.addItem(ItemUtil.createCustomHead(TEXTURE_BARRIER), EditorLocales.GAMEPLAY_BANNED_ITEMS, 30).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                game.getBannedItems().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_ARENA_GAMEPLAY_ENTER_BANNED_ITEMS, wrapper -> {
                Material material = Material.getMaterial(wrapper.getTextRaw().toUpperCase());
                if (material != null) {
                    game.getBannedItems().add(material);
                    game.save();
                }
                return true;
            });
        }).getOptions().setVisibilityPolicy(viewer -> !game.isKitsEnabled());

        this.addItem(ItemUtil.createCustomHead(TEXTURE_COMMAND), EditorLocales.GAMEPLAY_COMMANDS, 32).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_COMMAND, wrapper -> {
                    game.getCommandWhitelist().add(wrapper.getTextRaw());
                    game.save();
                    return true;
                });
                return;
            }

            if (event.isRightClick()) {
                game.getCommandWhitelist().add(Placeholders.WILDCARD);
            }
            else if (event.getClick() == ClickType.DROP) {
                game.getCommandWhitelist().clear();
            }
            this.save(viewer);
        });

        this.addItem(ItemUtil.createCustomHead(TEXTURE_SPAWN_EGG), EditorLocales.GAMEPLAY_ALLOWED_SPAWN_REASONS, 34).setClick((viewer, event) -> {
            if (event.getClick() == ClickType.DROP) {
                game.getAllowedSpawnReasons().clear();
                this.save(viewer);
                return;
            }

            EditorManager.suggestValues(viewer.getPlayer(), CollectionsUtil.getEnumsList(CreatureSpawnEvent.SpawnReason.class), true);
            this.handleInput(viewer, Lang.EDITOR_ARENA_GAMEPLAY_ENTER_SPAWN_REASON, wrapper -> {
                StringUtil.getEnum(wrapper.getTextRaw(), CreatureSpawnEvent.SpawnReason.class).ifPresent(spawnReason -> {
                    game.getAllowedSpawnReasons().add(spawnReason);
                    game.save();
                });
                return true;
            });
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