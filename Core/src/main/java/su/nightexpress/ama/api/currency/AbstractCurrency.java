package su.nightexpress.ama.api.currency;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.Placeholders;

import java.util.function.UnaryOperator;

public abstract class AbstractCurrency implements ICurrency {

    protected final ICurrencyConfig config;

    public AbstractCurrency(@NotNull ICurrencyConfig config) {
        this.config = config;
    }

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.CURRENCY_NAME, this.getConfig().getName())
            .replace(Placeholders.CURRENCY_ID, this.getConfig().getId())
            ;
    }

    @NotNull
    @Override
    public ICurrencyConfig getConfig() {
        return config;
    }

    @Override
    @NotNull
    public final String getId() {
        return this.getConfig().getId();
    }

    @Override
    @NotNull
    public final String getFormat() {
        return this.replacePlaceholders().apply(this.getConfig().getFormat());
    }

    @Override
    @NotNull
    public final String format(double price) {
        return this.getFormat().replace(Placeholders.GENERIC_PRICE, this.getConfig().getNumberFormat().format(price));
    }

    @Override
    @NotNull
    public final ItemStack getIcon() {
        ItemStack icon = this.getConfig().getIcon();
        ItemUtil.replace(icon, this.replacePlaceholders());
        return icon;
    }
}
