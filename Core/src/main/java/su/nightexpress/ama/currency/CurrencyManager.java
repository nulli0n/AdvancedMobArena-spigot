package su.nightexpress.ama.currency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.integration.VaultHook;
import su.nexmedia.engine.utils.EngineUtils;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.api.currency.CurrencyHandler;
import su.nightexpress.ama.command.coins.BalanceCommand;
import su.nightexpress.ama.command.coins.CoinsCommand;
import su.nightexpress.ama.currency.handler.ArenaCoinsHandler;
import su.nightexpress.ama.currency.handler.VaultEconomyHandler;
import su.nightexpress.ama.currency.impl.CoinsEngineCurrency;
import su.nightexpress.ama.currency.impl.ConfigCurrency;
import su.nightexpress.ama.currency.impl.DummyCurrency;
import su.nightexpress.ama.hook.HookId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class CurrencyManager extends AbstractManager<AMA> {

    public static final String EXP   = "exp";
    public static final String VAULT = "vault";
    public static final String COINS = "coins";

    private final Map<String, Currency> currencyMap;

    public CurrencyManager(@NotNull AMA plugin) {
        super(plugin);
        this.currencyMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {

        //this.registerCurrency(EXP, ExpPointsHandler::new);
        Currency coins = this.registerCurrency(COINS, ArenaCoinsHandler::new);
        if (coins != null) {
            this.plugin.getMainCommand().addChildren(new BalanceCommand(this.plugin, coins));
            this.plugin.getMainCommand().addChildren(new CoinsCommand(this.plugin, coins));
        }

        if (EngineUtils.hasVault() && VaultHook.hasEconomy()) {
            this.registerCurrency(VAULT, VaultEconomyHandler::new);
        }
        if (EngineUtils.hasPlugin(HookId.COINS_ENGINE)) {
            CoinsEngineCurrency.getCurrencies().forEach(this::registerCurrency);
        }
    }

    @Override
    protected void onShutdown() {
        this.currencyMap.clear();
    }

    @Nullable
    public Currency registerCurrency(@NotNull String id, @NotNull Supplier<CurrencyHandler> supplier) {
        ConfigCurrency currency = new ConfigCurrency(this.plugin, id, supplier.get());
        if (!currency.load()) return null;

        return this.registerCurrency(currency);
    }

    @NotNull
    public Currency registerCurrency(@NotNull Currency currency) {
        this.currencyMap.put(currency.getId(), currency);
        this.plugin.info("Registered currency: " + currency.getId());
        return currency;
    }

    public boolean hasCurrency() {
        return !this.currencyMap.isEmpty();
    }

    @NotNull
    public Collection<Currency> getCurrencies() {
        return currencyMap.values();
    }

    @NotNull
    public Set<String> getCurrencyIds() {
        return this.currencyMap.keySet();
    }

    @Nullable
    public Currency getCurrency(@NotNull String id) {
        return this.currencyMap.get(id.toLowerCase());
    }

    @NotNull
    public Currency getOrAny(@NotNull String id) {
        Currency currency = this.getCurrency(id);
        return currency == null ? this.getAny() : currency;
    }

    @NotNull
    public Currency getAny() {
        return this.getCurrencies().stream().findFirst().orElse(DummyCurrency.INSTANCE);
    }
}
