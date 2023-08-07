package su.nightexpress.ama.mob.config;

import org.bukkit.entity.EntityType;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.lang.LangColors;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.mob.kill.MobKillReward;
import su.nightexpress.ama.mob.kill.MobKillStreak;

import java.util.*;

public class MobsConfig {

    public static final JOption<String> NAME_FORMAT = JOption.create("Mobs.DisplayName_Format",
        LangColors.LIGHT_YELLOW + Placeholders.MOB_NAME + " " + LangColors.GRAY + "Lv. " + LangColors.RED + Placeholders.MOB_LEVEL,
        "Sets entity display name format for internal AMA mobs.",
        "Placeholders: " + Placeholders.MOB_NAME + ", " + Placeholders.MOB_LEVEL
    ).mapReader(Colorizer::apply);

    public static final JOption<String> BOSS_BAR_FORMAT = JOption.create("Mobs.BossBar_Format",
        Placeholders.MOB_NAME + " " + LangColors.RED + Placeholders.MOB_HEALTH + "❤",
        "Sets BossBar title format for internal AMA mobs.",
        "Placeholders: " + Placeholders.MOB_NAME + ", " + Placeholders.MOB_LEVEL + ", " + Placeholders.MOB_HEALTH + ", " + Placeholders.MOB_HEALTH_MAX
    ).mapReader(Colorizer::apply);

    public static final JOption<Boolean> IGNORE_ARMOR_STANDS = JOption.create("Mobs.Ignore_Armor_Stands", false,
        "When enabled, completely ignores Armor Stands in entity spawn events, so they not restricted not counted as a part of arenas.");

    public static final JOption<Set<EntityType>> ALLY_FROM_EGGS = JOption.forSet("Mobs.Ally_From_Eggs",
        (raw) -> StringUtil.getEnum(raw, EntityType.class).orElse(null),
        Set.of(EntityType.SNOWMAN, EntityType.IRON_GOLEM, EntityType.WOLF),
        "A list of mobs that will be marked as 'ally' mobs when spawned by Spawn Eggs on arenas.",
        "These mobs will fight together with players against the arena mobs."
    ).setWriter((cfg, path, set) -> cfg.set(path, set.stream().map(Enum::name).toList()));

    public static final JOption<Set<String>> ALLY_MYTHIC_MOBS = JOption.create("Mobs.Ally_Mythic_Mobs",
        Set.of("SummonedMinion"),
        "List of MythicMobs mob internal names that should not be counted as arena mobs.",
        "This option might be useful when you have plugins to summon MythicMobs that are allied with players.");

    public static final JOption<Boolean> KILL_REWARD_ENABLED = JOption.create("Mobs.Kill_Rewards.Enabled",
        true,
        "Enables/Disables the Mob Kill Rewards feature."
    );

    public static final JOption<Boolean> KILL_REWARD_HOLOGRAM_ENABLED = JOption.create("Mobs.Kill_Rewards.Hologram.Enabled",
        true,
        "When enabled, creates a hologram displaying amount of given reward(s) at mob death location.",
        "Supported Plugins: " + HookId.HOLOGRAPHIC_DISPLAYS + ", " + HookId.DECENT_HOLOGRAMS
    );

    public static final JOption<Integer> KILL_REWARD_HOLOGRAM_LIFETIME = JOption.create("Mobs.Kill_Rewards.Hologram.Lifetime",
        3,
        "Sets how long (in seconds) reward hologram will stay there before disappear."
    );

    public static final JOption<String> KILL_REWARD_HOLOGRAM_FORMAT_SCORE = JOption.create("Mobs.Kill_Rewards.Hologram.Format.Score",
        "+" + Placeholders.GENERIC_AMOUNT + " Score",
        "Sets the hologram line format for score amount.",
        "Placeholders:",
        "- " + Placeholders.GENERIC_AMOUNT + " - Formatted score amount."
    );

    public static final JOption<String> KILL_REWARD_HOLOGRAM_FORMAT_PAYMENT = JOption.create("Mobs.Kill_Rewards.Hologram.Format.Currency",
        "+" + Placeholders.GENERIC_AMOUNT,
        "Sets the hologram line format for currencies.",
        "Placeholders:",
        "- " + Placeholders.GENERIC_AMOUNT + " - Formatted currency amount."
    );

    public static final JOption<Map<String, MobKillReward>> KILL_REWARD_VALUES = JOption.forMap("Mobs.Kill_Rewards.Table",
        (cfg, path, key) -> MobKillReward.read(cfg, path + "." + key, key),
        () -> {
            Map<String, MobKillReward> map = new HashMap<>();
            Map<Currency, Double> payment = new HashMap<>();
            ArenaAPI.getCurrencyManager().getCurrencies().forEach(currency -> payment.put(currency, 1D));
            map.put(Placeholders.DEFAULT, new MobKillReward(Placeholders.DEFAULT, payment, 1));
            map.put("ama:" + EntityType.ZOMBIE.name(), new MobKillReward("ama:zombie", payment, 3));
            return map;
        },
        "Here you can create custom rewards for mob kills on the arena.",
        "===== Naming Info =====",
        "Format for mob names: '<Provider:MobId>'.",
        "* AMA Example: 'ama:zombie' - will give reward(s) for AMA Zombie mob (the one from /mobs/ folder).",
        "* MythicMobs Example: 'mythicmobs:skeletonking' - will give reward(s) for MythicMobs SkeletonKing mob.",
        "Also, you can use the '" + Placeholders.DEFAULT + "' keyword for all other mobs not listed here.",
        "===== =====",
        "All available currency names you can see in plugin startup log or here: " + Placeholders.WIKI_AMA
    ).setWriter((cfg, path, map) -> map.forEach((id, reward) -> reward.write(cfg, path + "." + id)));

    public static final JOption<Boolean> KILL_STREAK_ENABLED = JOption.create("Mobs.Kill_Streaks.Enabled",
        true,
        "Enables the Mob Kill Streak feature.");

    public static final JOption<Integer> KILL_STREAK_DECAY = JOption.create("Mobs.Kill_Streaks.Streak_Decay",
        5,
        "Sets for how long (in seconds) kill streak will retain before reset to zero.");

    public static final JOption<Map<Integer, MobKillStreak>> KILL_STREAK_TABLE = JOption.forMap("Mobs.Kill_Streaks.Table",
        (raw) -> StringUtil.getInteger(raw, 0),
        (cfg, path, key) -> MobKillStreak.read(cfg, path + "." + key, StringUtil.getInteger(key, 0)),
        () -> {
            Map<Integer, MobKillStreak> map = new TreeMap<>();
            map.put(2, new MobKillStreak(2, new LangMessage(ArenaAPI.PLUGIN, "<! type:\"titles:10:50:10\" !>" + LangColors.RED + "&lDouble Kill!"), new ArrayList<>()));
            map.put(3, new MobKillStreak(3, new LangMessage(ArenaAPI.PLUGIN, "<! type:\"titles:10:50:10\" !>" + LangColors.RED + "&lTriple Kill!"), new ArrayList<>()));
            map.put(4, new MobKillStreak(4, new LangMessage(ArenaAPI.PLUGIN, "<! type:\"titles:10:50:10\" !>" + LangColors.CYAN + "&lQuadra Kill!"), new ArrayList<>()));
            map.put(5, new MobKillStreak(5, new LangMessage(ArenaAPI.PLUGIN, "<! type:\"titles:10:50:10\" !>" + LangColors.CYAN + "&lPenta Kill! \\n &d(+10 Coins)"), Collections.singletonList("ama coins add " + Placeholders.PLAYER_NAME + " 10")));
            map.put(10, new MobKillStreak(10, new LangMessage(ArenaAPI.PLUGIN, "<! type:\"titles:10:50:10\" !>" + LangColors.YELLOW + "&lx" + Placeholders.GENERIC_AMOUNT + " Kill! \\n " + LangColors.GREEN + "(Heal)"), Collections.singletonList("heal " + Placeholders.PLAYER_NAME)));
            return map;
        },
        "Here you can create and customize mob kill streaks.",
        "For 'Message' you can use " + Placeholders.GENERIC_AMOUNT + " placeholder for a kills amount. Please check: " + Placeholders.WIKI_LANG_URL,
        "For 'Commands', please check https://github.com/nulli0n/NexEngine-spigot/wiki/Configuration-Tips#command-sections"
    ).setWriter((cfg, path, map) -> map.forEach((key, streak) -> streak.write(cfg, path + "." + key)));
}
