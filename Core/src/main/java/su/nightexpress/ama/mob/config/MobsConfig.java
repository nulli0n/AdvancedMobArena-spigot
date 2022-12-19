package su.nightexpress.ama.mob.config;

import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.mob.kill.MobKillReward;
import su.nightexpress.ama.mob.kill.MobKillStreak;

import java.util.*;

public class MobsConfig {

    public static final JOption<Boolean>                    KILL_REWARD_ENABLED                 = JOption.create("Mobs.Kill_Rewards.Enabled", true,
        "Enables/Disables the Mob Kill Rewards feature."
    );
    public static final JOption<Boolean>                    KILL_REWARD_HOLOGRAM_ENABLED        = JOption.create("Mobs.Kill_Rewards.Hologram.Enabled", true,
        "When enabled, creates a hologram displaying amount of given reward(s) at mob death location.",
        "Supported Plugins: " + HookId.HOLOGRAPHIC_DISPLAYS + ", " + HookId.DECENT_HOLOGRAMS
    );
    public static final JOption<Integer>                    KILL_REWARD_HOLOGRAM_LIFETIME       = JOption.create("Mobs.Kill_Rewards.Hologram.Lifetime", 3,
        "Sets how long (in seconds) reward hologram will stay there before disappear."
    );
    public static final JOption<String>                     KILL_REWARD_HOLOGRAM_FORMAT_SCORE   = JOption.create("Mobs.Kill_Rewards.Hologram.Format.Score", "+" + Placeholders.GENERIC_AMOUNT + " Score",
        "Sets the hologram line format for score amount.",
        "Placeholders:",
        "- " + Placeholders.GENERIC_AMOUNT + " - Formatted score amount."
    );
    public static final JOption<String>                     KILL_REWARD_HOLOGRAM_FORMAT_PAYMENT = JOption.create("Mobs.Kill_Rewards.Hologram.Format.Currency", "+" + Placeholders.GENERIC_AMOUNT,
        "Sets the hologram line format for currencies.",
        "Placeholders:",
        "- " + Placeholders.GENERIC_AMOUNT + " - Formatted currency amount."
    );
    public static final JOption<Map<String, MobKillReward>> KILL_REWARD_VALUES                  = new JOption<Map<String, MobKillReward>>("Mobs.Kill_Rewards.Table",
        (cfg, path, def) -> {
            Map<String, MobKillReward> map = new HashMap<>();
            for (String mobId : cfg.getSection(path)) {
                String path2 = path + "." + mobId + ".";
                Map<ICurrency, Double> payment = new HashMap<>();
                for (String curId : cfg.getSection(path2 + "Currency")) {
                    ICurrency currency = ArenaAPI.getCurrencyManager().getCurrency(curId);
                    if (currency == null) {
                        ArenaAPI.PLUGIN.error("Invalid currency '" + curId + "' for '" + mobId + "' mob kill reward!");
                        continue;
                    }

                    double amount = cfg.getDouble(path2 + "Currency." + curId);
                    payment.put(currency, amount);
                }

                int score = cfg.getInt(path2 + "Score");
                MobKillReward killReward = new MobKillReward(mobId, payment, score);
                map.put(killReward.mobId(), killReward);
            }
            return map;
        },
        () -> {
        Map<String, MobKillReward> map = new HashMap<>();
        Map<ICurrency, Double> payment = new HashMap<>();
        ArenaAPI.getCurrencyManager().getCurrencies().forEach(currency -> payment.put(currency, 1D));
        map.put(Placeholders.DEFAULT, new MobKillReward(Placeholders.DEFAULT, payment, 1));
        return map;
        },
        "A table with rewards given for killed mobs on arenas.",
        "For Mob names, use mob identifiers from the /mobs/ sub-folder, or MythicMobs internal mob names.",
        "Also, you can use the 'default' keyword for all other mobs not listed here.",
        "For Currency, use currency identifiers from the /currency/ sub-folder."
    );

    public static final JOption<Boolean>                     KILL_STREAK_ENABLED = JOption.create("Mobs.Kill_Streaks.Enabled", true, "Enables the Mob Kill Streak feature.");
    public static final JOption<Integer>                     KILL_STREAK_DECAY   = JOption.create("Mobs.Kill_Streaks.Streak_Decay", 5, "Sets for how long (in seconds) kill streak will retain before reset to zero.");
    public static final JOption<Map<Integer, MobKillStreak>> KILL_STREAK_TABLE   = new JOption<Map<Integer, MobKillStreak>>("Mobs.Kill_Streaks.Table",
        (cfg, path, def) -> {
            Map<Integer, MobKillStreak> map = new TreeMap<>();
            for (String sId : cfg.getSection(path)) {
                int streak = StringUtil.getInteger(sId, -1);
                if (streak <= 0) continue;

                String path2 = path + "." + sId + ".";
                LangMessage streakMessage = new LangMessage(ArenaAPI.PLUGIN, cfg.getString(path2 + "Message", ""));
                String bonusPayment = cfg.getString(path2 + "Bonus.Payment", "0");
                String bonusScore = cfg.getString(path2 + "Bonus.Score", "0");
                List<String> commands = cfg.getStringList(path2 + "Commands");

                MobKillStreak killStreak = new MobKillStreak(streak, streakMessage, bonusPayment, bonusScore, commands);
                map.put(streak, killStreak);
            }
            return map;
        }, () -> {
        Map<Integer, MobKillStreak> map = new TreeMap<>();
        map.put(1, new MobKillStreak(1, new LangMessage(ArenaAPI.PLUGIN, ""), "0", "0", new ArrayList<>()));
        map.put(2, new MobKillStreak(2, new LangMessage(ArenaAPI.PLUGIN, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 50; ~fadeOut: 10;}&c&lDouble Kill!"), "0", "0", new ArrayList<>()));
        map.put(3, new MobKillStreak(3, new LangMessage(ArenaAPI.PLUGIN, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 50; ~fadeOut: 10;}&c&lTriple Kill!"), "0", "0", new ArrayList<>()));
        map.put(4, new MobKillStreak(4, new LangMessage(ArenaAPI.PLUGIN, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 50; ~fadeOut: 10;}&b&lQuadra Kill!"), "0", "0", new ArrayList<>()));
        map.put(5, new MobKillStreak(5, new LangMessage(ArenaAPI.PLUGIN, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 50; ~fadeOut: 10;}&b&lPenta Kill! \\n &d(+10 Coins)"), "0", "10", new ArrayList<>()));
        map.put(10, new MobKillStreak(10, new LangMessage(ArenaAPI.PLUGIN, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 50; ~fadeOut: 10;}&e&lx" + Placeholders.GENERIC_AMOUNT + " Kill! \\n &a(+30 Score, Heal)"), "0", "30", Collections.singletonList("[CONSOLE] heal %player%")));
        map.put(15, new MobKillStreak(15, new LangMessage(ArenaAPI.PLUGIN, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 50; ~fadeOut: 10;}&e&lx" + Placeholders.GENERIC_AMOUNT + " Kill! \\n &a(+30 Payment)"), "30", "0", new ArrayList<>()));
        map.put(20, new MobKillStreak(20, new LangMessage(ArenaAPI.PLUGIN, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 50; ~fadeOut: 10;}&e&lx" + Placeholders.GENERIC_AMOUNT + " Kill! \\n &a(x10 Payment)"), "900%", "0", new ArrayList<>()));
        map.put(30, new MobKillStreak(30, new LangMessage(ArenaAPI.PLUGIN, "{message: ~type: TITLES; ~fadeIn: 10; ~stay: 50; ~fadeOut: 10;}&e&lx" + Placeholders.GENERIC_AMOUNT + " Kill! \\n &a(x10 Score)"), "0", "900%", new ArrayList<>()));
        return map;
        },
        "A table with kill streaks. Each section is a streak kills amount.",
        "For 'Bonus' section you can set percent values like 100%. Then it will be applied as a multiplier to the Mob Kill Reward value(s).",
        "For 'Message' you can use " + Placeholders.GENERIC_AMOUNT + " placeholder for a kills amount. Please check: https://github.com/nulli0n/NexEngine-spigot/wiki/Language-Config#message-options",
        "For 'Commands', please check https://github.com/nulli0n/NexEngine-spigot/wiki/Configuration-Tips#command-sections"
        );
}
