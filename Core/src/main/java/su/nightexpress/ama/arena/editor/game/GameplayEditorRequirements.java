package su.nightexpress.ama.arena.editor.game;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.util.ArenaUtils;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.hook.level.PlayerLevelProvider;
import su.nightexpress.ama.hook.level.PluginLevelProvider;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameplayEditorRequirements extends EditorMenu<AMA, ArenaConfig> {

    private static final String TITLE = "Gameplay Editor [Requirements]";

    public GameplayEditorRequirements(@NotNull AMA plugin, @NotNull ArenaConfig config) {
        super(plugin, config, TITLE, 54);

        this.addReturn(49).setClick((viewer, event) -> {
            config.getEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_GLOBALS, 2).setClick((viewer, event) -> {
            config.getGameplaySettings().getEditorGlobals().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.GAMEPLAY_PAGE_REQUIREMENTS, 3).setClick((viewer, event) -> {
            config.getGameplaySettings().getEditorRequirements().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_PLAYERS, 5).setClick((viewer, event) -> {
            config.getGameplaySettings().getEditorPlayers().openNextTick(viewer, 1);
        });

        this.addItem(Material.GRAY_DYE, EditorLocales.GAMEPLAY_PAGE_COMPAT, 6).setClick((viewer, event) -> {
            config.getGameplaySettings().getEditorCompat().openNextTick(viewer, 1);
        });

        this.addItem(Material.DAYLIGHT_DETECTOR, EditorLocales.ARENA_AUTO_STATE_SCHEDULERS, 19).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isLeftClick()) {
                    config.getAutoOpenTimes().clear();
                }
                else if (event.isRightClick()) {
                    config.getAutoCloseTimes().clear();
                }
                this.save(viewer);
                return;
            }

            EditorManager.suggestValues(viewer.getPlayer(), CollectionsUtil.getEnumsList(DayOfWeek.class), false);
            this.handleInput(viewer, Lang.EDITOR_ARENA_ENTER_SCHEDULER_TIME, wrapper -> {
                String[] split = wrapper.getTextRaw().split(" ");
                if (split.length < 2) return false;

                DayOfWeek day = StringUtil.getEnum(split[0], DayOfWeek.class).orElse(null);
                if (day == null) return false;

                LocalTime time;
                try {
                    time = LocalTime.parse(split[1], ArenaUtils.TIME_FORMATTER);
                }
                catch (DateTimeParseException exception) {
                    return false;
                }

                Map<DayOfWeek, Set<LocalTime>> map;
                if (event.isLeftClick()) {
                    map = config.getAutoOpenTimes();
                }
                else map = config.getAutoCloseTimes();

                map.computeIfAbsent(day, k -> new HashSet<>()).add(time);
                config.save();
                return true;
            });
        });

        this.addItem(Material.REDSTONE, EditorLocales.ARENA_PERMISSION_REQUIREMENT, 21).setClick((viewer, event) -> {
            config.setPermissionRequired(!config.isPermissionRequired());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            if (!config.isPermissionRequired()) item.setType(Material.GUNPOWDER);
        });

        this.addItem(Material.GOLD_INGOT, EditorLocales.ARENA_PAYMENT_REQUIREMENT, 23).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                config.getPaymentRequirements().clear();
                this.save(viewer);
                return;
            }

            EditorManager.suggestValues(viewer.getPlayer(), plugin.getCurrencyManager().getCurrencyIds(), false);
            this.handleInput(viewer, Lang.EDITOR_ARENA_ENTER_JOIN_PAYMENT, wrapper -> {
                String[] split = wrapper.getTextRaw().split(" ");
                if (split.length < 2) return false;

                Currency currency = plugin.getCurrencyManager().getCurrency(split[0]);
                if (currency == null) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.ERROR_CURRENCY_INVALID).getLocalized());
                    return false;
                }

                double amount = StringUtil.getDouble(split[1], 0D);
                if (amount <= 0D) {
                    config.getPaymentRequirements().remove(currency);
                }
                else {
                    config.getPaymentRequirements().put(currency, amount);
                }
                config.save();
                return true;
            });
        }).getOptions().addDisplayModifier((viewer, item) -> {
            if (config.getPaymentRequirements().isEmpty()) item.setType(Material.NETHERITE_INGOT);
        });

        this.addItem(Material.EXPERIENCE_BOTTLE, EditorLocales.ARENA_LEVEL_REQUIREMENT, 25).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                config.getLevelRequirements().clear();
                this.save(viewer);
                return;
            }
            if (PluginLevelProvider.getProviders().isEmpty()) {
                return;
            }

            EditorManager.suggestValues(viewer.getPlayer(), PluginLevelProvider.getProvidersMap().keySet(), false);
            this.handleInput(viewer, Lang.EDITOR_ARENA_ENTER_JOIN_LEVEL, wrapper -> {
                String[] split = wrapper.getTextRaw().split(" ");
                if (split.length < 2) return false;

                PlayerLevelProvider provider = PluginLevelProvider.getProvider(split[0]);
                if (provider == null) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ARENA_ERROR_LEVEL_PROVIDER).getLocalized());
                    return false;
                }

                int amount = StringUtil.getInteger(split[1], 0);
                if (amount <= 0) {
                    config.getLevelRequirements().remove(provider);
                }
                else {
                    config.getLevelRequirements().put(provider, amount);
                }
                config.save();
                return true;
            });
        }).getOptions().addDisplayModifier((viewer, item) -> {
            if (config.getLevelRequirements().isEmpty()) item.setType(Material.GLASS_BOTTLE);
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> {
            ItemReplacer.replace(item, Placeholders.forArenaEditor(config).replacer());
        }));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }
}
