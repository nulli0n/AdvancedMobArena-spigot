package su.nightexpress.ama.mob.kill;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.arena.impl.ArenaPlayer;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public record MobKillReward(String mobId,
                            Map<ICurrency, Double> payment, int score) implements JOption.Writer {

    public MobKillReward(@NotNull String mobId, @NotNull Map<ICurrency, Double> payment, int score) {
        this.mobId = mobId.toLowerCase();
        this.payment = payment;
        this.score = score;
    }

    @NotNull
    public MobKillReward multiply(@NotNull MobKillStreak killStreak) {
        Map<ICurrency, Double> payment = new HashMap<>();
        this.payment().forEach(((currency, amount) -> {
            payment.put(currency, killStreak.getBonusPayment().applyAsDouble(amount));
        }));

        int score = (int) killStreak.getBonusScore().applyAsDouble(this.score());

        return new MobKillReward(this.mobId(), payment, score);
    }

    public void reward(@NotNull ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        this.payment().forEach(((currency, amount) -> currency.give(player, amount)));
        arenaPlayer.addScore(this.score());
    }

    @Override
    public void write(@NotNull JYML cfg, @NotNull String path) {
        this.payment().forEach((currency, amount) -> {
            cfg.set(path + ".Currency." + currency.getId(), amount);
        });
        cfg.set(path + ".Score", this.score());
    }
}
