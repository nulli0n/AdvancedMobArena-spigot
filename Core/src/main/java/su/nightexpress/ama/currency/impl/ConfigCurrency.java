package su.nightexpress.ama.currency.impl;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.api.currency.CurrencyHandler;

public class ConfigCurrency implements Currency {

    private String    name;
    private String    format;

    private final AMA plugin;
    private final String id;
    private final CurrencyHandler handler;
    private final PlaceholderMap placeholderMap;

    public ConfigCurrency(@NotNull AMA plugin, @NotNull String id, @NotNull CurrencyHandler handler) {
        this.plugin = plugin;
        this.id = StringUtil.lowerCaseUnderscore(id);
        this.handler = handler;

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.CURRENCY_ID, this::getId)
            .add(Placeholders.CURRENCY_NAME, this::getName);
    }

    public boolean load() {
        JYML cfg = this.plugin.getConfig();
        String path = "Currency." + this.getId();

        cfg.addMissing(path + ".Enabled", true);
        cfg.addMissing(path + ".Name", StringUtil.capitalizeUnderscored(this.getId()));
        cfg.addMissing(path + ".Format", Placeholders.GENERIC_AMOUNT + " " + Placeholders.CURRENCY_NAME);
        cfg.saveChanges();

        if (!cfg.getBoolean(path + ".Enabled")) return false;

        this.name = Colorizer.apply(cfg.getString(path + ".Name", StringUtil.capitalizeUnderscored(this.getId())));
        this.format = Colorizer.apply(cfg.getString(path + ".Format", Placeholders.GENERIC_AMOUNT + " " + Placeholders.CURRENCY_NAME));
        return true;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    @Override
    public CurrencyHandler getHandler() {
        return handler;
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public String getFormat() {
        return format;
    }
}
