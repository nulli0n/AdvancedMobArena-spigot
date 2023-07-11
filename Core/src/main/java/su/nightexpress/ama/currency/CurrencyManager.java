package su.nightexpress.ama.currency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.integration.VaultHook;
import su.nexmedia.engine.utils.EngineUtils;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.command.currency.BalanceCommand;
import su.nightexpress.ama.currency.config.CurrencyConfig;
import su.nightexpress.ama.currency.external.GamePointsCurrency;
import su.nightexpress.ama.currency.external.PlayerPointsCurrency;
import su.nightexpress.ama.currency.external.VaultEcoCurrency;
import su.nightexpress.ama.currency.internal.ArenaCoinsCurrency;
import su.nightexpress.ama.hook.HookId;

import java.util.*;
import java.util.stream.Stream;

public class CurrencyManager extends AbstractManager<AMA> {

    public static final String DIR_CURRENCY = "/currency/";

    private Map<String, ICurrency> currencyMap;

    public CurrencyManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        this.currencyMap = new HashMap<>();
        this.plugin.getConfigManager().extractResources(DIR_CURRENCY);

        this.loadDefault();
    }

    private void loadDefault() {
        Stream.of(CurrencyId.values()).forEach(currencyId -> {
            CurrencyConfig config = this.loadConfigDefault(currencyId);
            config.load();
            config.save();

            ICurrency currency = switch (currencyId) {
                case CurrencyId.COINS -> new ArenaCoinsCurrency(config);
                case CurrencyId.VAULT -> !EngineUtils.hasVault() || !VaultHook.hasEconomy() ? null : new VaultEcoCurrency(config);
                case CurrencyId.GAME_POINTS -> !EngineUtils.hasPlugin(HookId.GAME_POINTS) ? null : new GamePointsCurrency(config);
                case CurrencyId.PLAYER_POINTS -> !EngineUtils.hasPlugin(HookId.PLAYER_POINTS) ? null : new PlayerPointsCurrency(config);
                default -> null;
            };
            if (currency == null) return;

            this.registerCurrency(currency);
        });
    }

    @NotNull
    private CurrencyConfig loadConfigDefault(@NotNull String id) {
        JYML cfg = JYML.loadOrExtract(plugin, DIR_CURRENCY + id + ".yml");
        return new CurrencyConfig(this.plugin, cfg);
    }

    @Override
    public void onShutdown() {
        if (this.currencyMap != null) {
            this.currencyMap.clear();
            this.currencyMap = null;
        }
    }

    public boolean registerCurrency(@NotNull ICurrency currency) {
        /*if (currency instanceof ICurrencyConfig currencyConfig) {
            if (!currencyConfig.load()) {
                this.plugin.warn("Currency not loaded: " + currency.getId());
                return false;
            }
        }*/
        if (currency.getConfig().isEnabled()) {
            if (currency instanceof ArenaCoinsCurrency coinsCurrency) {
                this.plugin.getCommandManager().getMainCommand().addChildren(new BalanceCommand(this.plugin, coinsCurrency));
            }
            this.currencyMap.put(currency.getId(), currency);
            this.plugin.info("Registered currency: " + currency.getId());
            return true;
        }
        return false;
    }

    public boolean hasCurrency() {
        return !this.currencyMap.isEmpty();
    }

    @NotNull
    public Collection<ICurrency> getCurrencies() {
        return currencyMap.values();
    }

    @NotNull
    public Set<String> getCurrencyIds() {
        return this.currencyMap.keySet();
    }

    @Nullable
    public ICurrency getCurrency(@NotNull String id) {
        return this.currencyMap.get(id.toLowerCase());
    }

    @NotNull
    public ICurrency getCurrencyFirst() {
        return this.getCurrencies().stream().filter(Objects::nonNull).findFirst()
            .orElseThrow(() -> new IllegalStateException("No currencies are available!"));
    }
}
