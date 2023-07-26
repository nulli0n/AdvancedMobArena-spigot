package su.nightexpress.ama.mob.kill;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.ArenaAPI;

import java.util.List;

public class MobKillStreak {

    private final int          amount;
    private final LangMessage  message;
    private final List<String> commands;

    public MobKillStreak(int amount, @NotNull LangMessage message, @NotNull List<String> commands) {
        this.amount = amount;
        this.message = message.replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(this.getAmount()));
        this.commands = commands;
    }

    @NotNull
    public static MobKillStreak read(@NotNull JYML cfg, @NotNull String path, int amount) {
        LangMessage message = new LangMessage(ArenaAPI.PLUGIN, cfg.getString(path + ".Message", ""));
        List<String> commands = cfg.getStringList(path + ".Commands");
        return new MobKillStreak(amount, message, commands);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Message", this.getMessage().getRaw());
        cfg.set(path + ".Commands", this.getCommands());
    }

    public int getAmount() {
        return amount;
    }

    @NotNull
    public LangMessage getMessage() {
        return this.message;
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void executeCommands(@NotNull Player player) {
        this.getCommands().forEach(command -> {
            PlayerUtil.dispatchCommand(player, command);
        });
    }

    public void execute(@NotNull Player player) {
        this.getMessage().send(player);
        this.executeCommands(player);
    }
}
