package su.nightexpress.ama.mob.kill;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.arena.impl.ArenaPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobKillReward {

    private final String mobId;
    private final Map<Currency, Double> payment;
    private final List<String> commands;
    private final int score;

    public MobKillReward(@NotNull String mobId, @NotNull Map<Currency, Double> payment, @NotNull List<String> commands, int score) {
        this.mobId = mobId.toLowerCase();
        this.payment = payment;
        this.commands = commands;
        this.score = score;
    }

    @NotNull
    public static MobKillReward read(@NotNull JYML cfg, @NotNull String path, @NotNull String id) {
        Map<Currency, Double> payment = new HashMap<>();
        for (String curId : cfg.getSection(path + ".Currency")) {
            Currency currency = ArenaAPI.getCurrencyManager().getCurrency(curId);
            if (currency == null) continue;

            double amount = cfg.getDouble(path + ".Currency." + curId);
            payment.put(currency, amount);
        }
        List<String> commands = cfg.getStringList(path + ".Commands");

        int score = cfg.getInt(path + ".Score");
        return new MobKillReward(id, payment, commands, score);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        this.getPayment().forEach((currency, amount) -> {
            cfg.set(path + ".Currency." + currency.getId(), amount);
        });
        cfg.set(path + ".Commands", this.getCommands());
        cfg.set(path + ".Score", this.getScore());
    }

    public void reward(@NotNull ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        this.getPayment().forEach(((currency, amount) -> currency.getHandler().give(player, amount)));
        this.getCommands().forEach(command -> PlayerUtil.dispatchCommand(player, command));
        arenaPlayer.addScore(this.getScore());
    }

    @NotNull
    public String getMobId() {
        return mobId;
    }

    public int getScore() {
        return score;
    }

    @NotNull
    public Map<Currency, Double> getPayment() {
        return payment;
    }

    @NotNull
    public List<String> getCommands() {
        return commands;
    }
}
