package su.nightexpress.ama.currency.external;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.integration.VaultHook;
import su.nightexpress.ama.api.currency.ICurrencyConfig;
import su.nightexpress.ama.currency.AbstractCurrency;

public class VaultEcoCurrency extends AbstractCurrency {

    public VaultEcoCurrency(@NotNull ICurrencyConfig config) {
        super(config);
    }

    @Override
    public double getBalance(@NotNull Player player) {
        return VaultHook.getBalance(player);
    }

    @Override
    public void give(@NotNull Player player, double amount) {
        VaultHook.addMoney(player, amount);
    }

    @Override
    public void take(@NotNull Player player, double amount) {
        VaultHook.takeMoney(player, amount);
    }
}
