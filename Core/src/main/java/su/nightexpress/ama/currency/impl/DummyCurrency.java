package su.nightexpress.ama.currency.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.api.currency.CurrencyHandler;

public class DummyCurrency implements Currency, CurrencyHandler {

    public static final String ID = "dummy";

    public static final DummyCurrency INSTANCE = new DummyCurrency();

    @NotNull
    @Override
    public CurrencyHandler getHandler() {
        return this;
    }

    @NotNull
    @Override
    public String getId() {
        return ID;
    }

    @NotNull
    @Override
    public String getName() {
        return "Dummy";
    }

    @NotNull
    @Override
    public String getFormat() {
        return Placeholders.GENERIC_AMOUNT;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return new PlaceholderMap();
    }

    @Override
    public double getBalance(@NotNull Player player) {
        return 0;
    }

    @Override
    public void give(@NotNull Player player, double amount) {

    }

    @Override
    public void take(@NotNull Player player, double amount) {

    }

    @Override
    public void set(@NotNull Player player, double amount) {

    }
}
