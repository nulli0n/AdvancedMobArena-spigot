package su.nightexpress.ama.mob.kill;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JWriter;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.Placeholders;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

public class MobKillStreak implements JWriter {

    private final int                 amount;
    private final LangMessage         message;
    private final String              bonusPaymentRaw;
    private final String              bonusScoreRaw;
    private final DoubleUnaryOperator bonusPayment;
    private final DoubleUnaryOperator bonusScore;
    private final List<String>        commands;

    public MobKillStreak(
        int amount,
        @NotNull LangMessage message,
        @NotNull String bonusPaymentRaw,
        @NotNull String bonusScoreRaw,
        @NotNull List<String> commands
    ) {
        this.amount = amount;
        this.message = message.replace(Placeholders.GENERIC_AMOUNT, this.getAmount());
        this.bonusPaymentRaw = bonusPaymentRaw;
        this.bonusScoreRaw = bonusScoreRaw;

        boolean isPaymentMod = bonusPaymentRaw.endsWith("%");
        boolean isScoreMod = bonusScoreRaw.endsWith("%");

        double amountPayment = StringUtil.getDouble(bonusPaymentRaw.substring(0, bonusPaymentRaw.length() - 1), 0);
        double amountScore = StringUtil.getDouble(bonusScoreRaw.substring(0, bonusScoreRaw.length() - 1), 0);

        this.bonusPayment = (money) -> isPaymentMod ? (money * (1D + amountPayment / 100D)) : (money + amountPayment);
        this.bonusScore = (score) -> isScoreMod ? (score * (1D + amountScore / 100D)) : (score + amountScore);

        this.commands = commands;
    }

    public int getAmount() {
        return amount;
    }

    @NotNull
    public LangMessage getMessage() {
        return this.message;
    }

    @NotNull
    public DoubleUnaryOperator getBonusPayment() {
        return this.bonusPayment;
    }

    @NotNull
    public DoubleUnaryOperator getBonusScore() {
        return this.bonusScore;
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void executeCommands(@NotNull Player player) {
        this.getCommands().forEach(cmd -> {
            PlayerUtil.dispatchCommand(player, cmd);
        });
    }

    @Override
    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Message", this.getMessage().getRaw());
        cfg.set(path + ".Bonus.Payment", this.bonusPaymentRaw);
        cfg.set(path + ".Bonus.Score", this.bonusScoreRaw);
        cfg.set(path + ".Commands", this.getCommands());
    }
}
