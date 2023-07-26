package su.nightexpress.ama.currency.handler;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.api.currency.CurrencyHandler;
import su.nightexpress.ama.data.impl.ArenaUser;

public class ArenaCoinsHandler implements CurrencyHandler {

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

    @Override
    public void set(@NotNull Player player, double amount) {
        ArenaUser user = ArenaAPI.getUserManager().getUserData(player);
        user.setCoins((int) amount);
    }
}
