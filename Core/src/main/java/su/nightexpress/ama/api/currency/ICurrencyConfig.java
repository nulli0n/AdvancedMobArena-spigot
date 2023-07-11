package su.nightexpress.ama.api.currency;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public interface ICurrencyConfig {

    boolean isEnabled();

    boolean load();

    @NotNull
    String getId();

    @NotNull
    String getName();

    @NotNull
    String getFormat();

    @NotNull
    DecimalFormat getNumberFormat();

    @NotNull
    ItemStack getIcon();
}
