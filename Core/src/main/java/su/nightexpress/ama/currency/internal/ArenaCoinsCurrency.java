package su.nightexpress.ama.currency.internal;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.currency.AbstractCurrency;
import su.nightexpress.ama.api.currency.ICurrencyConfig;
import su.nightexpress.ama.data.ArenaUser;

public class ArenaCoinsCurrency extends AbstractCurrency {

    public ArenaCoinsCurrency(@NotNull ICurrencyConfig config) {
        super(config);
    }

    @Override
    public double getBalance(@NotNull Player player) {
        ArenaUser user = ArenaAPI.getUserManager().getUserData(player);
        return user.getCoins();
    }

    @Override
    public void give(@NotNull Player player, double amount) {
        ArenaUser user = ArenaAPI.getUserManager().getUserData(player);
        user.setCoins((int) (user.getCoins() + amount));
    }

    @Override
    public void take(@NotNull Player player, double amount) {
        ArenaUser user = ArenaAPI.getUserManager().getUserData(player);
        user.setCoins((int) (user.getCoins() - amount));
    }
}
