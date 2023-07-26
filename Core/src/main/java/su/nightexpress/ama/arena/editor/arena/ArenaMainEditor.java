package su.nightexpress.ama.arena.editor.arena;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.util.ArenaUtils;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.hook.level.PlayerLevelProvider;
import su.nightexpress.ama.hook.level.PluginLevelProvider;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ArenaMainEditor extends EditorMenu<AMA, ArenaConfig> {

    public ArenaMainEditor(@NotNull AMA plugin, @NotNull ArenaConfig config) {
        super(plugin, config, EditorHub.TITLE_ARENA_EDITOR, 45);

        this.addReturn(40).setClick((viewer, event) -> {
            plugin.getEditor().getArenaEditor().openNextTick(viewer, 1);
        });

        this.addItem(Material.LIME_DYE, EditorLocales.ARENA_ACTIVE, 4).setClick((viewer, event) -> {
            config.setActive(!config.isActive());
            this.save(viewer);
        }).getOptions().addDisplayModifier((viewer, item) -> {
            item.setType(config.isActive() ? (!config.hasProblems() ? Material.LIME_DYE : Material.PINK_DYE) : Material.GRAY_DYE);
        });

        this.addItem(Material.NAME_TAG, EditorLocales.ARENA_NAME, 6).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NAME, wrapper -> {
                config.setName(wrapper.getText());
                config.save();
                return true;
            });
        });

        this.addItem(Material.DAYLIGHT_DETECTOR, EditorLocales.ARENA_AUTO_STATE_SCHEDULERS, 2).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isLeftClick()) {
                    config.getAutoOpenTimes().clear();
                }
                else {
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

        this.addItem(Material.GOLDEN_AXE, EditorLocales.ARENA_SETUP_KIT, 13).setClick((viewer, event) -> {
            plugin.getArenaSetupManager().getConfigSetupManager().startSetup(viewer.getPlayer(), config);
            plugin.runTask(task -> viewer.getPlayer().closeInventory());
        });

        this.addItem(Material.REDSTONE_TORCH, EditorLocales.ARENA_PERMISSION_REQUIREMENT, 10).setClick((viewer, event) -> {
            config.setPermissionRequired(!config.isPermissionRequired());
            this.save(viewer);
        });

        this.addItem(Material.GOLD_NUGGET, EditorLocales.ARENA_PAYMENT_REQUIREMENT, 16).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                config.getJoinPaymentRequirements().clear();
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

                double amount = StringUtil.getDouble(split[1], -1D);
                if (amount < 0D) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ERROR_NUMBER_GENERIC).getLocalized());
                    return false;
                }

                config.getJoinPaymentRequirements().put(currency, amount);
                config.save();
                return true;
            });
        });

        this.addItem(Material.ENCHANTING_TABLE, EditorLocales.ARENA_LEVEL_REQUIREMENT, 15).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                config.getJoinLevelRequirements().clear();
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

                int amount = StringUtil.getInteger(split[1], -1);
                if (amount < 0) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_ERROR_NUMBER_GENERIC).getLocalized());
                    return false;
                }

                config.getJoinLevelRequirements().put(provider, amount);
                config.save();
                return true;
            });
        });

        this.addItem(Material.EXPERIENCE_BOTTLE, EditorLocales.ARENA_GAMEPLAY_MANAGER, 21).setClick((viewer, event) -> {
            config.getGameplayManager().getEditor().openNextTick(viewer, 1);
        }).getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, config.getGameplayManager().replacePlaceholders()));

        this.addItem(Material.MAP, EditorLocales.ARENA_REGION_MANAGER, 22).setClick((viewer, event) -> {
            config.getRegionManager().getEditor().openNextTick(viewer, 1);
        }).getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, config.getRegionManager().replacePlaceholders()));

        this.addItem(Material.BLAZE_POWDER, EditorLocales.ARENA_WAVE_MANAGER, 23).setClick((viewer, event) -> {
            config.getWaveManager().getEditor().openNextTick(viewer.getPlayer(), 1);
        }).getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, config.getWaveManager().replacePlaceholders()));

        this.addItem(Material.GOLD_INGOT, EditorLocales.ARENA_REWARD_MANAGER, 30).setClick((viewer, event) -> {
            this.plugin.runTask(task -> config.getRewardManager().getEditor().open(viewer.getPlayer(), 1));
        }).getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, config.getRewardManager().replacePlaceholders()));

        this.addItem(Material.EMERALD, EditorLocales.ARENA_SHOP_MANAGER, 31).setClick((viewer, event) -> {
            this.plugin.runTask(task -> config.getShopManager().getEditor().open(viewer.getPlayer(), 1));
        }).getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, config.getShopManager().replacePlaceholders()));

        this.addItem(Material.COMPASS, EditorLocales.ARENA_SPOT_MANAGER, 32).setClick((viewer, event) -> {
            this.plugin.runTask(task -> config.getSpotManager().getEditor().open(viewer.getPlayer(), 1));
        }).getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, config.getSpotManager().replacePlaceholders()));

        this.addItem(Material.CHEST, EditorLocales.ARENA_SUPPLY_MANAGER, 34).setClick((viewer, event) -> {
            this.plugin.runTask(task -> config.getSupplyManager().getEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.CHAIN_COMMAND_BLOCK, EditorLocales.ARENA_SCRIPT_MANAGER, 28).setClick((viewer, event) -> {
            this.plugin.runTask(task -> config.getScriptManager().getEditor().open(viewer.getPlayer(), 1));
        });

        this.getItems().forEach(menuItem -> menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replace(item, config.replacePlaceholders())));
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.openNextTick(viewer, viewer.getPage());
    }
}
