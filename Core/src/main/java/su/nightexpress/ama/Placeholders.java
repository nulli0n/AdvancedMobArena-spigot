package su.nightexpress.ama;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Placeholders extends su.nexmedia.engine.utils.Placeholders {

    public static final String GENERIC_TIME     = "%time%";
    public static final String GENERIC_VALUE    = "%value%";
    public static final String GENERIC_PRICE    = "%price%";
    public static final String GENERIC_PROBLEMS = "%problems%";
    public static final String GENERIC_AMOUNT   = "%amount%";
    public static final String GENERIC_MESSAGE  = "%message%";
    public static final String GENERIC_TYPE     = "%type%";

    public static final String CURRENCY_NAME = "%currency_name%";
    public static final String CURRENCY_ID   = "%currency_id%";

    public static final String ARENA_ID                     = "%arena_id%";
    public static final String ARENA_ACTIVE                 = "%arena_active%";
    public static final String ARENA_NAME                   = "%arena_name%";
    public static final String ARENA_AUTO_STATE_OPEN_TIMES = "%arena_auto_state_open_times%";
    public static final String ARENA_AUTO_STATE_CLOSE_TIMES = "%arena_auto_state_close_times%";
    public static final String ARENA_REQUIREMENT_PERMISSION = "%arena_requirement_permission%";
    public static final String ARENA_REQUIREMENT_PAYMENT    = "%arena_requirement_payment%";
    public static final String ARENA_REQUIREMENT_LEVEL      = "%arena_requirement_level%";
    public static final String ARENA_PERMISSION             = "%arena_permission%";
    public static final String ARENA_STATE                  = "%arena_state%";
    public static final String ARENA_PLAYERS                = "%arena_players%";
    public static final String ARENA_PLAYERS_MAX            = "%arena_players_max%";
    public static final String ARENA_MOBS_ALIVE             = "%arena_mobs_alive%";
    public static final String ARENA_MOBS_LEFT              = "%arena_mobs_left%";
    public static final String ARENA_MOBS_TOTAL             = "%arena_mobs_total%";
    public static final String ARENA_WAVE_NUMBER            = "%arena_wave_number%";
    public static final String ARENA_WAVE_NEXT_IN           = "%arena_wave_next_in%";
    public static final String ARENA_TIMELEFT               = "%arena_timeleft%";
    public static final String ARENA_SCORE                  = "%arena_score%";

    public static final String ARENA_WAVE_ID   = "%arena_wave_id%";
    public static final String ARENA_WAVE_MOBS = "%arena_wave_mobs%";

    public static final String ARENA_WAVES_DELAY_FIRST               = "%arena_waves_delay_first%";
    public static final String ARENA_WAVES_DELAY_DEFAULT             = "%arena_waves_delay_default%";
    public static final String ARENA_WAVES_FINAL_WAVE                = "%arena_waves_final_wave%";
    public static final String ARENA_WAVES_GRADUAL_ENABLED           = "%arena_waves_gradual_enabled%";
    public static final String ARENA_WAVES_GRADUAL_FIRST_PERCENT     = "%arena_waves_gradual_first_percent%";
    public static final String ARENA_WAVES_GRADUAL_NEXT_PERCENT      = "%arena_waves_gradual_next_percent%";
    public static final String ARENA_WAVES_GRADUAL_NEXT_INTERVAL     = "%arena_waves_gradual_next_interval%";
    public static final String ARENA_WAVES_GRADUAL_NEXT_KILL_PERCENT = "%arena_waves_gradual_next_kill_percent%";

    public static final String ARENA_WAVE_MOB_ID     = "%arena_wave_mob_id%";
    public static final String ARENA_WAVE_MOB_AMOUNT = "%arena_wave_mob_amount%";
    public static final String ARENA_WAVE_MOB_LEVEL  = "%arena_wave_mob_level%";
    public static final String ARENA_WAVE_MOB_CHANCE = "%arena_wave_mob_chance%";

    public static final String GAME_COMMAND_TARGET   = "%game_command_target%";
    public static final String GAME_COMMAND_COMMANDS = "%game_command_commands%";
    public static final String GAME_COMMAND_TRIGGERS = "%game_command_triggers%";

    public static final String GAMEPLAY_TIMELEFT                  = "%gameplay_timeleft%";
    public static final String GAMEPLAY_LOBBY_TIME                = "%gameplay_lobby_time%";
    public static final String GAMEPLAY_ANNOUNCEMENTS             = "%gameplay_announcements%";
    public static final String GAMEPLAY_SCOREBOARD_ENABLED        = "%gameplay_scoreboard_enabled%";
    public static final String GAMEPLAY_SCOREBOARD_ID             = "%gameplay_scoreboard_id%";
    public static final String GAMEPLAY_HUNGER_ENABLED            = "%gameplay_hunger_enabled%";
    public static final String GAMEPLAY_REGENERATION_ENABLED      = "%gameplay_regeneration_enabled%";
    public static final String GAMEPLAY_ITEM_DROP_ENABLED         = "%gameplay_item_drop_enabled%";
    public static final String GAMEPLAY_ITEM_PICKUP_ENABLED       = "%gameplay_item_pickup_enabled%";
    public static final String GAMEPLAY_ITEM_DURABULITY_ENABLED   = "%gameplay_item_durability_enabled%";
    public static final String GAMEPLAY_MOB_DROP_ITEMS            = "%gameplay_mob_drop_items%";
    public static final String GAMEPLAY_MOB_DROP_EXP              = "%gameplay_mob_drop_exp%";
    public static final String GAMEPLAY_MOB_HIGHLIGHT_ENABLED     = "%gameplay_mob_highlight_enabled%";
    public static final String GAMEPLAY_MOB_HIGHLIGHT_AMOUNT      = "%gameplay_mob_highlight_amount%";
    public static final String GAMEPLAY_MOB_HIGHLIGHT_COLOR       = "%gameplay_mob_highlight_color%";
    public static final String GAMEPLAY_BANNED_ITEMS              = "%gameplay_banned_items%";
    public static final String GAMEPLAY_ALLOWED_SPAWN_REASONS     = "%gameplay_allowed_spawn_reasons%";
    public static final String GAMEPLAY_PLAYERS_AMOUNT_MIN        = "%gameplay_players_amount_min%";
    public static final String GAMEPLAY_PLAYERS_AMOUNT_MAX        = "%gameplay_players_amount_max%";
    public static final String GAMEPLAY_PLAYER_DEATH_DROP_ITEMS   = "%gameplay_player_death_drop_items%";
    public static final String GAMEPLAY_PLAYER_DEATH_LIVES_AMOUNT = "%gameplay_player_death_lives_amount%";
    public static final String GAMEPLAY_SPECTATE_ENABLED          = "%gameplay_spectate_enabled%";
    public static final String GAMEPLAY_SPECTATE_ON_DEATH         = "%gameplay_spectate_on_death%";
    public static final String GAMEPLAY_COMMAND_USAGE_ENABLED     = "%gameplay_command_usage_enabled%";
    public static final String GAMEPLAY_COMMAND_USAGE_WHITELIST   = "%gameplay_command_usage_whitelist%";
    public static final String GAMEPLAY_KITS_ENABLED              = "%gameplay_kits_enabled%";
    public static final String GAMEPLAY_KITS_ALLOWED              = "%gameplay_kits_allowed%";
    public static final String GAMEPLAY_KITS_LIMITS               = "%gameplay_kits_limits%";
    public static final String GAMEPLAY_PETS_ALLOWED              = "%gameplay_pets_allowed%";
    public static final String GAMEPLAY_MCMMO_ALLOWED             = "%gameplay_mcmmo_allowed%";

    public static final String REGION_FILE              = "%region_file%";
    public static final String REGION_ID                = "%region_id%";
    public static final String REGION_NAME              = "%region_name%";
    public static final String REGION_ACTIVE            = "%region_active%";
    public static final String REGION_DEFAULT           = "%region_default%";
    public static final String REGION_STATE             = "%region_state%";
    public static final String REGION_TRIGGERS_LOCKED   = "%region_triggers_locked%";
    public static final String REGION_TRIGGERS_UNLOCKED = "%region_triggers_unlocked%";
    public static final String REGION_WAVE_ID           = "%region_wave_id%";
    public static final String REGION_WAVE_WAVE_IDS     = "%region_wave_wave_ids%";
    public static final String REGION_WAVE_SPAWNERS     = "%region_wave_spawners%";
    public static final String REGION_LINKED_REGIONS    = "%region_linked_regions%";
    public static final String REGION_WAVE_TRIGGERS     = "%region_wave_triggers%";

    public static final String CONTAINER_LOCATION_X       = "%container_location_x%";
    public static final String CONTAINER_LOCATION_Y       = "%container_location_y%";
    public static final String CONTAINER_LOCATION_Z       = "%container_location_z%";
    public static final String CONTAINER_LOCATION_WORLD   = "%container_location_world%";
    public static final String CONTAINER_REFILL_ITEMS_MIN = "%container_refill_items_min%";
    public static final String CONTAINER_REFILL_ITEMS_MAX = "%container_refill_items_max%";
    public static final String CONTAINER_TRIGGERS         = "%container_triggers%";

    public static final String REWARD_MANAGER_RETAIN_ON_DEATH = "%reward_manager_retain_on_death%";
    public static final String REWARD_MANAGER_RETAIN_ON_LEAVE = "%reward_manager_retain_on_leave%";

    public static final String REWARD_NAME        = "%reward_name%";
    public static final String REWARD_IS_LATE     = "%reward_is_late%";
    public static final String REWARD_CHANCE      = "%reward_chance%";
    public static final String REWARD_COMMANDS    = "%reward_commands%";
    public static final String REWARD_TARGET_TYPE = "%reward_target_type%";
    public static final String REWARD_TRIGGERS    = "%reward_triggers%";

    public static final String SHOP_TRIGGERS_LOCKED              = "%shop_triggers_locked%";
    public static final String SHOP_TRIGGERS_UNLOCKED            = "%shop_triggers_unlocked%";
    public static final String SHOP_MANAGER_IS_ACTIVE            = "%shop_manager_is_active%";
    public static final String SHOP_MANAGER_HIDE_OTHER_KIT_ITEMS = "%shop_manager_hide_other_kit_items%";

    public static final String SHOP_CATEGORY_ID                = "%shop_category_id%";
    public static final String SHOP_CATEGORY_NAME              = "%shop_category_name%";
    public static final String SHOP_CATEGORY_DESCRIPTION       = "%shop_category_description%";
    public static final String SHOP_CATEGORY_ICON_TYPE         = "%shop_category_icon_type%";
    //public static final String SHOP_CATEGORY_ICON_NAME         = "%shop_category_icon_name%";
    //public static final String SHOP_CATEGORY_ICON_LORE         = "%shop_category_icon_lore%";
    public static final String SHOP_CATEGORY_TRIGGERS_LOCKED   = "%shop_category_triggers_locked%";
    public static final String SHOP_CATEGORY_TRIGGERS_UNLOCKED = "%shop_category_triggers_unlocked%";
    public static final String SHOP_CATEGORY_ALLOWED_KITS      = "%shop_category_allowed_kits%";

    public static final String SHOP_PRODUCT_ID                = "%shop_product_id%";
    public static final String SHOP_PRODUCT_NAME              = "%shop_product_name%";
    public static final String SHOP_PRODUCT_DESCRIPTION       = "%shop_product_description%";
    public static final String SHOP_PRODUCT_COMMANDS          = "%shop_product_commands%";
    public static final String SHOP_PRODUCT_APPLICABLE_KITS   = "%shop_product_applicable_kits%";
    public static final String SHOP_PRODUCT_PRICE             = "%shop_product_price%";
    //public static final String SHOP_PRODUCT_ITEM_LORE         = "%shop_product_item_lore%";
    public static final String SHOP_PRODUCT_ICON_TYPE         = "%shop_product_icon_type%";
    public static final String SHOP_PRODUCT_TRIGGERS_LOCKED   = "%shop_product_triggers_locked%";
    public static final String SHOP_PRODUCT_TRIGGERS_UNLOCKED = "%shop_product_triggers_unlocked%";
    public static final String SHOP_PRODUCT_CURRENCY          = "%shop_product_currency%";

    public static final String SPOT_ID             = "%spot_id%";
    public static final String SPOT_NAME           = "%spot_name%";
    public static final String SPOT_ACTIVE         = "%spot_active%";
    public static final String SPOT_STATE_ID       = "%spot_state_id%";
    public static final String SPOT_STATE_TRIGGERS = "%spot_state_triggers%";

    public static final String WAVE_AMPLIFICATOR_ID           = "%wave_amplificator_id%";
    public static final String WAVE_AMPLIFICATOR_VALUE_AMOUNT = "%wave_amplificator_value_amount%";
    public static final String WAVE_AMPLIFICATOR_VALUE_LEVEL  = "%wave_amplificator_value_level%";
    public static final String WAVE_AMPLIFICATOR_TRIGGERS     = "%wave_amplificator_triggers%";

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
    public static final String MOB_BOSSBAR_TITLE    = "%mob_bossbar_title%";
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
    public static final String KIT_IS_AVAILABLE   = "%kit_is_available%";
    public static final String KIT_ICON_LORE      = "%kit_icon_lore%";
    public static final String KIT_ICON_MATERIAL  = "%kit_icon_material%";
    public static final String KIT_CURRENCY       = "%kit_currency%";

    public static final String PROBLEM_PREFIX                = StringUtil.color("&c⚠ &e");
    public static final String PROBLEM_REGION_CUBOID_INVALID = "Invalid Region Cuboid!";
    public static final String PROBLEM_REGION_SPAWN_LOCATION = "Invalid Spawn Location!";
    public static final String PROBLEM_REGION_SPAWNERS_EMPTY = "No Mob Spawners Defined!";

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
    @Deprecated
    public static String format(@NotNull Collection<ArenaGameEventTrigger<?>> triggers) {
        return StringUtil.color("&a" + triggers.stream()
            .map(trigger -> trigger.getType() + ": &f" + trigger.getValuesRaw())
            .collect(Collectors.joining("\n"))
        );
    }

    @NotNull
    public static String formatProblems(@NotNull List<String> problems) {
        List<String> problems2 = problems.stream().map(str -> Placeholders.PROBLEM_PREFIX + str).toList();

        return String.join("\n", problems2);
    }
}
