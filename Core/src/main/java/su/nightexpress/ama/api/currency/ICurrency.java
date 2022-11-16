package su.nightexpress.ama.api.currency;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.IPlaceholder;

public interface ICurrency extends IPlaceholder {

    @NotNull
    ICurrencyConfig getConfig();

    @NotNull
    String getId();

    @NotNull
    String getFormat();

    @NotNull
    String format(double price);

    @NotNull
    ItemStack getIcon();

    double getBalance(@NotNull Player player);

    void give(@NotNull Player player, double amount);

    void take(@NotNull Player player, double amount);
}
