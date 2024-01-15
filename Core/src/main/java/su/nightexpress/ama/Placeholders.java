package su.nightexpress.ama;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.api.type.PlayerType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.util.ArenaUtils;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.kit.impl.Kit;

import java.util.function.Function;
import java.util.stream.Collectors;

public class Placeholders extends su.nexmedia.engine.utils.Placeholders {

    public static final String WIKI_AMA = "https://github.com/nulli0n/AdvancedMobArena-spigot/wiki/";
    public static final String WIKI_AMA_PLACEHOLDERS = WIKI_AMA + "Internal-Placeholders";

    public static final String GENERIC_TIME    = "%time%";
    public static final String GENERIC_NAME    = "%name%";
    public static final String GENERIC_VALUE   = "%value%";
    public static final String GENERIC_PRICE   = "%price%";
    public static final String GENERIC_AMOUNT  = "%amount%";
    public static final String GENERIC_MESSAGE = "%message%";
    public static final String GENERIC_TYPE    = "%type%";
    public static final String GENERIC_STATE   = "%state%";
    public static final String GENERIC_CURRENT = "%current%";
    public static final String GENERIC_MAX     = "%max%";

    public static final String CURRENCY_NAME = "%currency_name%";
    public static final String CURRENCY_ID   = "%currency_id%";

    public static final String ARENA_REPORT          = "%arena_report%";
    public static final String ARENA_REPORT_GAMEPLAY = "%arena_report_gameplay%";
    public static final String ARENA_REPORT_REGIONS = "%arena_report_regions%";
    public static final String ARENA_REPORT_WAVES  = "%arena_report_waves%";
    public static final String ARENA_REPORT_REWARDS  = "%arena_report_rewards%";
    public static final String ARENA_REPORT_SHOP  = "%arena_report_shop%";
    public static final String ARENA_REPORT_SPOTS  = "%arena_report_spots%";
    public static final String ARENA_REPORT_SCRIPTS  = "%arena_report_scripts%";
    public static final String ARENA_ID                     = "%arena_id%";
    public static final String ARENA_ACTIVE                 = "%arena_active%";
    public static final String ARENA_NAME                   = "%arena_name%";
    public static final String ARENA_OPEN_TIMES          = "%arena_auto_state_open_times%";
    public static final String ARENA_CLOSE_TIMES         = "%arena_auto_state_close_times%";
    public static final String ARENA_JOIN_COOLDOWN = "%arena_join_cooldown%";
    public static final String ARENA_PAYMENT_REQUIREMENT = "%arena_requirement_payment%";
    public static final String ARENA_LEVEL_REQUIREMENT   = "%arena_requirement_level%";
    public static final String ARENA_PERMISSION          = "%arena_permission%";
    public static final String ARENA_PERMISSION_REQUIREMENT = "%arena_requirement_permission%";
    public static final String ARENA_STATE                  = "%arena_state%";
    @Deprecated public static final String ARENA_PLAYERS                = "%arena_players%";
    public static final String ARENA_REAL_PLAYERS                = "%arena_real_players%";
    public static final String ARENA_GHOST_PLAYERS                = "%arena_ghost_players%";
    public static final String ARENA_ALIVE_PLAYERS                = "%arena_alive_players%";
    public static final String ARENA_DEAD_PLAYERS                = "%arena_dead_players%";
    public static final String ARENA_PLAYERS_MAX            = "%arena_players_max%";
    public static final String ARENA_MOBS_ALIVE             = "%arena_mobs_alive%";
    public static final String ARENA_MOBS_LEFT              = "%arena_mobs_left%";
    public static final String ARENA_MOBS_TOTAL             = "%arena_mobs_total%";
    public static final String ARENA_WAVE_NUMBER            = "%arena_wave_number%";
    public static final String ARENA_WAVE_NEXT_IN           = "%arena_wave_next_in%";
    public static final String ARENA_END_COUNTDOWN = "%arena_end_countdown%";
    public static final String ARENA_TIMELEFT               = "%arena_timeleft%";
    public static final String ARENA_SCORE                  = "%arena_score%";

    public static final String ARENA_WAVE_ID   = "%arena_wave_id%";
    public static final String ARENA_WAVE_MOBS = "%arena_wave_mobs%";

    public static final String ARENA_WAVES_FIRST_ROUND_COUNTDOWN = "%arena_waves_delay_first%";
    public static final String ARENA_WAVES_ROUND_COUNTDOWN = "%arena_waves_delay_default%";
    public static final String ARENA_WAVES_FINAL_ROUND     = "%arena_waves_final_wave%";
    public static final String ARENA_WAVES_GRADUAL_ENABLED = "%arena_waves_gradual_enabled%";
    public static final String ARENA_WAVES_GRADUAL_FIRST_PERCENT     = "%arena_waves_gradual_first_percent%";
    public static final String ARENA_WAVES_GRADUAL_NEXT_PERCENT      = "%arena_waves_gradual_next_percent%";
    public static final String ARENA_WAVES_GRADUAL_NEXT_INTERVAL     = "%arena_waves_gradual_next_interval%";
    public static final String ARENA_WAVES_GRADUAL_NEXT_KILL_PERCENT = "%arena_waves_gradual_next_kill_percent%";

    public static final String ARENA_WAVE_MOB_ID     = "%arena_wave_mob_id%";
    public static final String ARENA_WAVE_MOB_PROVIDER = "%arena_wave_mob_provider%";
    public static final String ARENA_WAVE_MOB_AMOUNT = "%arena_wave_mob_amount%";
    public static final String ARENA_WAVE_MOB_LEVEL  = "%arena_wave_mob_level%";
    public static final String ARENA_WAVE_MOB_CHANCE = "%arena_wave_mob_chance%";

    public static final Function<String, String> ARENA_VARIABLE = var -> "%arena_var_" + var + "%";
    public static final Function<String, String> ARENA_VARIABLE_RAW = var -> "%arena_raw_var_" + var + "%";

    public static final String GAMEPLAY_TIMELEFT     = "%gameplay_timeleft%";
    public static final String GAMEPLAY_LOBBY_TIME              = "%gameplay_lobby_time%";
    public static final String GAMEPLAY_ANNOUNCEMENTS           = "%gameplay_announcements%";
    public static final String GAMEPLAY_SCOREBOARD_ENABLED      = "%gameplay_scoreboard_enabled%";
    public static final String GAMEPLAY_SCOREBOARD_ID           = "%gameplay_scoreboard_id%";
    public static final String GAMEPLAY_SCOREBOARD_CHECK        = "%gameplay_scoreboard_check%";
    public static final String GAMEPLAY_HUNGER_ENABLED          = "%gameplay_hunger_enabled%";
    public static final String GAMEPLAY_REGENERATION_ENABLED    = "%gameplay_regeneration_enabled%";
    public static final String GAMEPLAY_LEAVE_ON_DEATH = "%gameplay_leave_on_death%";
    public static final String GAMEPLAY_ITEM_DROP_ENABLED       = "%gameplay_item_drop_enabled%";
    public static final String GAMEPLAY_ITEM_PICKUP_ENABLED     = "%gameplay_item_pickup_enabled%";
    public static final String GAMEPLAY_ITEM_DURABULITY_ENABLED = "%gameplay_item_durability_enabled%";
    public static final String GAMEPLAY_MOB_DROP_ITEMS          = "%gameplay_mob_drop_items%";
    public static final String GAMEPLAY_MOB_DROP_EXP            = "%gameplay_mob_drop_exp%";
    public static final String GAMEPLAY_MOB_HIGHLIGHT_ENABLED   = "%gameplay_mob_highlight_enabled%";
    public static final String GAMEPLAY_MOB_HIGHLIGHT_AMOUNT    = "%gameplay_mob_highlight_amount%";
    public static final String GAMEPLAY_MOB_HIGHLIGHT_COLOR     = "%gameplay_mob_highlight_color%";
    public static final String GAMEPLAY_BANNED_ITEMS            = "%gameplay_banned_items%";
    public static final String GAMEPLAY_ALLOWED_SPAWN_REASONS   = "%gameplay_allowed_spawn_reasons%";
    public static final String GAMEPLAY_PLAYER_AMOUNT_MIN       = "%gameplay_players_amount_min%";
    public static final String GAMEPLAY_PLAYER_AMOUNT_MAX       = "%gameplay_players_amount_max%";
    public static final String GAMEPLAY_KEEP_INVENTORY          = "%gameplay_keep_inventory%";
    public static final String GAMEPLAY_PLAYER_LIFES_AMOUNT     = "%gameplay_player_lifes_amount%";
    public static final String GAMEPLAY_PLAYER_REVIVE_TIME      = "%gameplay_player_revive_time%";
    public static final String GAMEPLAY_SPECTATE_ENABLED        = "%gameplay_spectate_enabled%";
    public static final String GAMEPLAY_COMMAND_WHITELIST       = "%gameplay_command_whitelist%";
    public static final String GAMEPLAY_KITS_ENABLED            = "%gameplay_kits_enabled%";
    public static final String GAMEPLAY_KITS_LIMITS             = "%gameplay_kits_limits%";
    public static final String GAMEPLAY_PETS_ALLOWED            = "%gameplay_pets_allowed%";
    public static final String GAMEPLAY_PETS_CHECK              = "%gameplay_pets_check%";
    public static final String GAMEPLAY_MCMMO_ALLOWED           = "%gameplay_mcmmo_allowed%";

    public static final String REGION_REPORT  = "%region_report%";
    public static final String REGION_FILE    = "%region_file%";
    public static final String REGION_ID      = "%region_id%";
    public static final String REGION_NAME    = "%region_name%";
    public static final String REGION_ACTIVE  = "%region_active%";
    public static final String REGION_DEFAULT = "%region_default%";
    public static final String REGION_STATE   = "%region_state%";

    public static final String SUPPLY_CHEST_ID               = "%supply_chest_id";
    public static final String SUPPLY_CHEST_LOCATION_X       = "%supply_chest_location_x%";
    public static final String SUPPLY_CHEST_LOCATION_Y       = "%supply_chest_location_y%";
    public static final String SUPPLY_CHEST_LOCATION_Z       = "%supply_chest_location_z%";
    public static final String SUPPLY_CHEST_LOCATION_WORLD   = "%supply_chest_location_world%";
    public static final String SUPPLY_CHEST_REFILL_ITEMS_MIN = "%supply_chest_refill_items_min%";
    public static final String SUPPLE_CHEST_REFILL_ITEMS_MAX = "%supply_chest_refill_items_max%";

    public static final String REWARD_MANAGER_KEEP_ON_DEATH = "%reward_manager_keep_on_death%";
    public static final String REWARD_MANAGER_KEEP_ON_LEAVE = "%reward_manager_keep_on_leave%";

    public static final String REWARD_REPORT               = "%reward_report%";
    public static final String REWARD_ID                   = "%reward_id%";
    public static final String REWARD_NAME                 = "%reward_name%";
    public static final String REWARD_COMPLETTION_REQUIRED = "%reward_completion_required%";
    public static final String REWARD_COMMANDS             = "%reward_commands%";

    public static final String SHOP_MANAGER_IS_ACTIVE            = "%shop_manager_is_active%";
    public static final String SHOP_MANAGER_HIDE_OTHER_KIT_ITEMS = "%shop_manager_hide_other_kit_items%";

    public static final String SHOP_CATEGORY_REPORT        = "%shop_category_report%";
    public static final String SHOP_CATEGORY_ID            = "%shop_category_id%";
    public static final String SHOP_CATEGORY_NAME          = "%shop_category_name%";
    public static final String SHOP_CATEGORY_DESCRIPTION   = "%shop_category_description%";
    public static final String SHOP_CATEGORY_KITS_REQUIRED = "%shop_category_kits_required%";

    public static final String SHOP_PRODUCT_REPORT        = "%shop_product_report%";
    public static final String SHOP_PRODUCT_ID            = "%shop_product_id%";
    public static final String SHOP_PRODUCT_NAME          = "%shop_product_name%";
    public static final String SHOP_PRODUCT_DESCRIPTION   = "%shop_product_description%";
    public static final String SHOP_PRODUCT_COMMANDS      = "%shop_product_commands%";
    public static final String SHOP_PRODUCT_KITS_REQUIRED = "%shop_product_kits_required%";
    public static final String SHOP_PRODUCT_PRICE         = "%shop_product_price%";
    public static final String SHOP_PRODUCT_CURRENCY      = "%shop_product_currency%";

    public static final String SCRIPT_CATEGORY_ID                  = "%script_category_id%";
    public static final String SCRIPT_CATEGORY_PRIORITY                  = "%script_category_priority%";
    public static final String SCRIPT_ID                           = "%script_id%";
    public static final String SCRIPT_EVENT_TYPE                   = "%script_event_type%";
    public static final String SCRIPT_IN_GAME_ONLY = "%script_in_game_only%";
    public static final String SCRIPT_ACTION_NAME                  = "%script_action_name%";
    public static final String SCRIPT_ACTION_PARAMS                = "%script_action_parameters%";
    public static final String SCRIPT_PARAMETER_NAME = "%script_parameter_name%";
    public static final String SCRIPT_PARAMETER_VALUE = "%script_parameter_value%";
    public static final String SCRIPT_CONDITION_SECTION_ID         = "%script_condition_section_id%";
    public static final String SCRIPT_CONDITION_SECTION_CONDITIONS = "%script_condition_section_conditions%";

    public static final String SPOT_REPORT = "%spot_report%";
    public static final String SPOT_ID       = "%spot_id%";
    public static final String SPOT_NAME     = "%spot_name%";
    public static final String SPOT_ACTIVE   = "%spot_active%";
    public static final String SPOT_STATE_ID = "%spot_state_id%";

    public static final String MOB_ID               = "%mob_id%";
    public static final String MOB_NAME             = "%mob_name%";
    public static final String MOB_HEALTH           = "%mob_health%";
    public static final String MOB_HEALTH_MAX       = "%mob_health_max%";
    public static final String MOB_NAME_VISIBLE     = "%mob_name_visible%";
    public static final String MOB_ENTITY_TYPE      = "%mob_entity_type%";
    public static final String MOB_LEVEL            = "%mob_level%";
    public static final String MOB_LEVEL_MIN        = "%mob_level_min%";
    public static final String MOB_LEVEL_MAX        = "%mob_level_max%";
    public static final String MOB_BOSSBAR_ENABLED  = "%mob_bossbar_enabled%";
    @Deprecated public static final String MOB_BOSSBAR_TITLE    = "%mob_bossbar_title%";
    public static final String MOB_BOSSBAR_COLOR    = "%mob_bossbar_color%";
    public static final String MOB_BOSSBAR_STYLE    = "%mob_bossbar_style%";
    public static final String MOB_ATTRIBUTES_BASE  = "%mob_attributes_base%";
    public static final String MOB_ATTRIBUTES_LEVEL = "%mob_attributes_level%";
    public static final String MOB_STYLE_TYPE       = "%mob_style_type%";
    public static final String MOB_STYLE_VALUE      = "%mob_style_value%";

    public static final String KIT_ID             = "%kit_id%";
    public static final String KIT_NAME           = "%kit_name%";
    public static final String KIT_DESCRIPTION    = "%kit_description%";
    public static final String KIT_PERMISSION     = "%kit_permission%";
    public static final String KIT_IS_DEFAULT     = "%kit_is_default%";
    public static final String KIT_IS_PERMISSION  = "%kit_is_permission%";
    public static final String KIT_COMMANDS       = "%kit_commands%";
    public static final String KIT_POTION_EFFECTS = "%kit_potion_effects%";
    public static final String KIT_COST           = "%kit_cost%";
    @Deprecated public static final String KIT_ICON_MATERIAL   = "%kit_icon_material%";
    public static final String KIT_CURRENCY       = "%kit_currency%";

    public static final String STATS_SCORE_POSITION = "%score_position%";
    public static final String STATS_SCORE_NAME     = "%score_name%";
    public static final String STATS_SCORE_AMOUNT   = "%score_amount%";
    public static final String STATS_SCORE_TYPE     = "%score_stat_type%";

    public static final String PLAYER_NAME         = "%player_name%";
    public static final String PLAYER_LIVES        = "%player_lives%";
    public static final String PLAYER_STREAK       = "%player_streak%";
    public static final String PLAYER_STREAK_DECAY = "%player_streak_decay%";
    public static final String PLAYER_KILLS        = "%player_kills%";
    public static final String PLAYER_SCORE        = "%player_score%";
    public static final String PLAYER_IS_READY     = "%player_is_ready%";
    public static final String PLAYER_KIT_NAME     = "%player_kit_name%";

    @NotNull
    public static PlaceholderMap forArenaAll(@NotNull ArenaConfig arenaConfig) {
        return PlaceholderMap.fusion(forArena(arenaConfig), forArenaEditor(arenaConfig));
    }

    @NotNull
    public static PlaceholderMap forArena(@NotNull ArenaConfig arenaConfig) {
        return new PlaceholderMap()
            .add(Placeholders.ARENA_ID, arenaConfig::getId)
            .add(Placeholders.ARENA_ACTIVE, () -> LangManager.getBoolean(arenaConfig.isActive()))
            .add(Placeholders.ARENA_NAME, arenaConfig::getName)
            .add(Placeholders.ARENA_PERMISSION_REQUIREMENT, () -> LangManager.getBoolean(arenaConfig.isPermissionRequired()))
            ;
    }

    @NotNull
    public static PlaceholderMap forArena(@NotNull Arena arena) {
        return new PlaceholderMap(arena.getConfig().getPlaceholders())
            .add(Placeholders.ARENA_STATE, () -> ArenaAPI.PLUGIN.getLangManager().getEnum(arena.getState()))
            .add(Placeholders.ARENA_PLAYERS, () -> String.valueOf(arena.getPlayers().select(PlayerType.REAL).size()))
            .add(Placeholders.ARENA_REAL_PLAYERS, () -> String.valueOf(arena.getPlayers().select(PlayerType.REAL).size()))
            .add(Placeholders.ARENA_GHOST_PLAYERS, () -> String.valueOf(arena.getPlayers().select(PlayerType.GHOST).size()))
            .add(Placeholders.ARENA_DEAD_PLAYERS, () -> String.valueOf(arena.getPlayers().getDead().size()))
            .add(Placeholders.ARENA_ALIVE_PLAYERS, () -> String.valueOf(arena.getPlayers().getAlive().size()))
            .add(Placeholders.ARENA_PLAYERS_MAX, () -> String.valueOf(arena.getConfig().getGameplaySettings().getPlayerMaxAmount()))
            .add(Placeholders.ARENA_MOBS_ALIVE, () -> String.valueOf(arena.getMobs().getEnemies().size()))
            .add(Placeholders.ARENA_MOBS_LEFT, () -> String.valueOf(arena.getMobsAwaitingSpawn()))
            .add(Placeholders.ARENA_MOBS_TOTAL, () -> String.valueOf(arena.getRoundTotalMobsAmount()))
            .add(Placeholders.ARENA_WAVE_NUMBER, () -> String.valueOf(arena.getRoundNumber()))
            .add(Placeholders.ARENA_WAVE_NEXT_IN, () -> String.valueOf(arena.getNextRoundCountdown()))
            .add(Placeholders.ARENA_END_COUNTDOWN, () -> String.valueOf(arena.getEndCountdown()))
            .add(Placeholders.ARENA_TIMELEFT, () -> {
                if (arena.getConfig().getGameplaySettings().hasTimeleft()) {
                    return TimeUtil.getLocalTimeOf(arena.getGameTimeleft()).format(Arena.FORMAT_TIMELEFT);
                }
                else {
                    return LangManager.getPlain(Lang.OTHER_INFINITY);
                }
            })
            .add(Placeholders.ARENA_SCORE, () -> NumberUtil.format(arena.getGameScore()));
    }

    @NotNull
    public static PlaceholderMap forArenaEditor(@NotNull ArenaConfig arenaConfig) {
        return new PlaceholderMap()
            .add(Placeholders.ARENA_PERMISSION, arenaConfig::getPermission)
            .add(Placeholders.ARENA_PERMISSION_REQUIREMENT, () -> LangManager.getBoolean(arenaConfig.isPermissionRequired()))
            .add(Placeholders.ARENA_REPORT, () -> String.join("\n", arenaConfig.getReport().getFullReport()))
            .add(Placeholders.ARENA_REPORT_GAMEPLAY, () -> String.join("\n", arenaConfig.getGameplaySettings().getReport().getFullReport()))
            .add(Placeholders.ARENA_REPORT_REGIONS, () -> String.join("\n", arenaConfig.getRegionManager().getReport().getFullReport()))
            .add(Placeholders.ARENA_REPORT_WAVES, () -> String.join("\n", arenaConfig.getWaveManager().getReport().getFullReport()))
            .add(Placeholders.ARENA_REPORT_REWARDS, () -> String.join("\n", arenaConfig.getRewardManager().getReport().getFullReport()))
            .add(Placeholders.ARENA_REPORT_SHOP, () -> String.join("\n", arenaConfig.getShopManager().getReport().getFullReport()))
            .add(Placeholders.ARENA_REPORT_SPOTS, () -> String.join("\n", arenaConfig.getSpotManager().getReport().getFullReport()))
            .add(Placeholders.ARENA_REPORT_SCRIPTS, () -> String.join("\n", arenaConfig.getScriptManager().getReport().getFullReport()))
            .add(Placeholders.ARENA_JOIN_COOLDOWN, () -> TimeUtil.formatTime(arenaConfig.getJoinCooldown() * 1000L))
            .add(Placeholders.ARENA_OPEN_TIMES, () -> {
                return arenaConfig.getAutoOpenTimes().entrySet().stream().map(entry -> {
                    String day = StringUtil.capitalizeUnderscored(entry.getKey().name().toLowerCase());
                    String times = entry.getValue().stream().map(time -> time.format(ArenaUtils.TIME_FORMATTER)).collect(Collectors.joining(", "));

                    return Report.good(day + ": " + times);
                }).collect(Collectors.joining("\n"));
            })
            .add(Placeholders.ARENA_CLOSE_TIMES, () -> {
                return arenaConfig.getAutoCloseTimes().entrySet().stream().map(entry -> {
                    String day = StringUtil.capitalizeUnderscored(entry.getKey().name().toLowerCase());
                    String times = entry.getValue().stream().map(time -> time.format(ArenaUtils.TIME_FORMATTER)).collect(Collectors.joining(", "));

                    return Report.good(day + ": " + times);
                }).collect(Collectors.joining("\n"));
            })
            .add(Placeholders.ARENA_PAYMENT_REQUIREMENT, () -> {
                if (arenaConfig.getPaymentRequirements().isEmpty()) {
                    return Report.good("No payments required!");
                }

                return arenaConfig.getPaymentRequirements().keySet().stream().map(currency -> {
                    return Report.good(currency.format(arenaConfig.getPaymentRequirements().getOrDefault(currency, 0D)));
                }).collect(Collectors.joining(", "));
            })
            .add(Placeholders.ARENA_LEVEL_REQUIREMENT, () -> {
                if (arenaConfig.getLevelRequirements().isEmpty()) {
                    return Report.good("No level required!");
                }

                return arenaConfig.getLevelRequirements().entrySet().stream().map(levelProvider -> {
                    return Report.good(levelProvider.getKey().getName() + ": " + levelProvider.getValue());
                }).collect(Collectors.joining(", "));
            });
    }

    @NotNull
    public static PlaceholderMap forKitAll(@NotNull Kit kit) {
        return PlaceholderMap.fusion(forKit(kit), forKitEditor(kit));
    }

    @NotNull
    public static PlaceholderMap forKitEditor(@NotNull Kit kit) {
        return new PlaceholderMap()
            .add(Placeholders.KIT_PERMISSION, kit::getPermission)
            .add(Placeholders.KIT_IS_DEFAULT, () -> LangManager.getBoolean(kit.isDefault()))
            .add(Placeholders.KIT_IS_PERMISSION, () -> LangManager.getBoolean(kit.isPermissionRequired()))
            .add(Placeholders.KIT_COMMANDS, () -> {
                return kit.getCommands().stream().map(Report::good).collect(Collectors.joining("\n"));
            })
            .add(Placeholders.KIT_POTION_EFFECTS, () -> kit.getPotionEffects().stream()
                .map(effect -> Report.good(LangManager.getPotionType(effect.getType()) + " " + NumberUtil.toRoman(effect.getAmplifier() + 1)))
                .collect(Collectors.joining("\n")))
            ;
    }

    @NotNull
    public static PlaceholderMap forKit(@NotNull Kit kit) {
        return new PlaceholderMap()
            .add(Placeholders.KIT_ID, kit::getId)
            .add(KIT_ICON_MATERIAL, () -> kit.getIcon().getType().name())
            .add(Placeholders.KIT_NAME, kit::getName)
            .add(Placeholders.KIT_DESCRIPTION, () -> String.join("\n", kit.getDescription()))
            .add(Placeholders.KIT_COST, () -> kit.getCurrency().format(kit.getCost()))
            .add(Placeholders.KIT_CURRENCY, () -> kit.getCurrency().getName());
    }
}
