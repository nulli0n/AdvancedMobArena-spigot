package su.nightexpress.ama.currency.external;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.currency.ICurrencyConfig;
import su.nightexpress.ama.currency.AbstractCurrency;

public class GamePointsCurrency extends AbstractCurrency {

    public GamePointsCurrency(@NotNull ICurrencyConfig config) {
        super(config);
    }

    @Override
    public double getBalance(@NotNull Player player) {
        //PointUser user = GamePointsAPI.getUserData(player.getUniqueId());
        return 0;//user == null ? 0 : user.getBalance();
    }

    @Override
    public void give(@NotNull Player player, double amount) {
        //PointUser user = GamePointsAPI.getUserData(player.getUniqueId());
        //if (user != null) user.addPoints((int) amount);
    }

    @Override
    public void take(@NotNull Player player, double amount) {
        //PointUser user = GamePointsAPI.getUserData(player.getUniqueId());
        //if (user != null) user.takePoints((int) amount);
    }
}
