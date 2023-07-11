package su.nightexpress.ama.kit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.*;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.currency.ICurrency;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.arena.type.PlayerType;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.hologram.HologramManager;
import su.nightexpress.ama.kit.editor.KitMainEditor;
import su.nightexpress.ama.kit.menu.KitPreviewMenu;

import java.util.*;
import java.util.stream.Stream;

public class Kit extends AbstractConfigHolder<AMA> implements ConfigHolder, HologramHolder, Placeholder {

    private boolean      isDefault;
    private String       name;
    private List<String> description;
    private ItemStack    icon;
    private ICurrency    currency;
    private double       cost;
    private boolean      isPermissionRequired;

    private List<String>      commands;
    private Set<PotionEffect> potionEffects;
    private ItemStack[]       armor;
    private ItemStack[]       items;
    private ItemStack[]       extras;

    private final Set<UUID>     hologramIds;
    private final Set<Location> hologramLocations;
    private final PlaceholderMap placeholderMap;

    private KitPreviewMenu preview;
    private KitMainEditor  editor;

    public Kit(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
        this.hologramIds = new HashSet<>();
        this.hologramLocations = new HashSet<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.KIT_ID, this::getId)
            .add(Placeholders.KIT_NAME, this::getName)
            .add(Placeholders.KIT_DESCRIPTION, () -> String.join("\n", this.getDescription()))
            .add(Placeholders.KIT_PERMISSION, this::getPermission)
            .add(Placeholders.KIT_IS_DEFAULT, () -> LangManager.getBoolean(this.isDefault()))
            .add(Placeholders.KIT_IS_PERMISSION, () -> LangManager.getBoolean(this.isPermissionRequired()))
            .add(Placeholders.KIT_COMMANDS, () -> String.join("\n", this.getCommands()))
            .add(Placeholders.KIT_POTION_EFFECTS, () -> String.join("\n", this.getPotionEffects()
                .stream().map(effect -> effect.getType().getName() + " " + NumberUtil.toRoman(effect.getAmplifier() + 1)).toList()))
            .add(Placeholders.KIT_COST, () -> this.getCurrency().format(this.getCost()))
            .add(Placeholders.KIT_ICON_LORE, () -> String.join("\n", ItemUtil.getLore(this.getIcon())))
            .add(Placeholders.KIT_ICON_MATERIAL, () -> this.getIcon().getType().name())
            .add(Placeholders.KIT_CURRENCY, () -> this.getCurrency().getConfig().getName())
        ;
    }

    @Override
    public boolean load() {
        this.setDefault(cfg.getBoolean("Default"));
        this.setName(cfg.getString("Name", this.getId()));
        this.setDescription(cfg.getStringList("Description"));
        this.setCost(cfg.getDouble("Cost"));
        this.setPermissionRequired(cfg.getBoolean("Permission_Required"));

        ICurrency currency = plugin.getCurrencyManager().getCurrency(cfg.getString("Currency", ""));
        if (currency == null) currency = plugin.getCurrencyManager().getCurrencyFirst();
        this.setCurrency(currency);

        ItemStack icon = cfg.getItem("Icon");
        if (icon.getType().isAir()) icon = new ItemStack(Material.DIAMOND_SWORD);
        this.setIcon(icon);

        this.setCommands(cfg.getStringList("Content.Commands"));
        this.setPotionEffects(new HashSet<>());
        for (String sId : cfg.getSection("Content.Potion_Effects")) {
            PotionEffectType pet = PotionEffectType.getByName(sId.toUpperCase());
            if (pet == null) {
                plugin.error("Invalid potion effect '" + sId + "' in '" + getId() + "' kit!");
                continue;
            }
            int level = cfg.getInt("Content.Potion_Effects." + sId);
            if (level == 0) continue;

            PotionEffect effect = new PotionEffect(pet, 60 * 60 * 20, level - 1);
            this.getPotionEffects().add(effect);
        }

        this.setArmor(cfg.getItemsEncoded("Content.Armor"));
        this.setItems(cfg.getItemsEncoded("Content.Inventory"));
        this.setExtras(cfg.getItemsEncoded("Content.Extras"));

        this.hologramLocations.addAll(cfg.getStringSet("Hologram.Locations").stream().map(LocationUtil::deserialize)
            .filter(Objects::nonNull).toList());

        ItemUtil.replace(this.icon, this.replacePlaceholders());
        return true;
    }

    @Override
    public void onSave() {
        this.updateHolograms();

        cfg.set("Name", this.getName());
        cfg.set("Description", this.getDescription());
        cfg.set("Default", this.isDefault());
        cfg.set("Cost", this.getCost());
        cfg.set("Currency", this.getCurrency().getId());
        cfg.set("Permission_Required", this.isPermissionRequired());
        cfg.setItem("Icon", this.getIcon());
        cfg.set("Content.Commands", this.getCommands());
        cfg.set("Content.Potion_Effects", null);
        this.getPotionEffects().forEach(potion -> {
            cfg.set("Content.Potion_Effects." + potion.getType().getName(), potion.getAmplifier() + 1);
        });
        cfg.setItemsEncoded("Content.Armor", Arrays.asList(this.getArmor()));
        cfg.setItemsEncoded("Content.Inventory", Arrays.asList(this.getItems()));
        cfg.setItemsEncoded("Content.Extras", Arrays.asList(this.getExtras()));
        cfg.set("Hologram.Locations", this.getHologramLocations().stream().map(LocationUtil::serialize).toList());
    }

    public void clear() {
        if (this.preview != null) {
            this.preview.clear();
            this.preview = null;
        }
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.plugin.getPluginManager().removePermission(this.getPermission());
        this.removeHolograms();
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public boolean hasPermission(@NotNull Player player) {
        return !this.isPermissionRequired() || player.hasPermission(this.getPermission());
    }

    public void applyPotionEffects(@NotNull Player player) {
        this.getPotionEffects().forEach(effect -> {
            if (!player.hasPotionEffect(effect.getType())) {
                player.addPotionEffect(effect);
            }
        });
    }

    public void give(@NotNull ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        player.getInventory().setContents(this.getItems());
        player.getInventory().setArmorContents(this.getArmor());
        player.getInventory().setExtraContents(this.getExtras());

        this.getCommands().forEach(cmd -> PlayerUtil.dispatchCommand(player, cmd));

        arenaPlayer.setKit(this);
    }

    @NotNull
    public KitMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new KitMainEditor(this);
        }
        return this.editor;
    }

    @NotNull
    public KitPreviewMenu getPreview() {
        if (this.preview == null) {
            this.preview = new KitPreviewMenu(this);
        }
        return preview;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = Colorizer.apply(description);
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
    }

    @NotNull
    public ICurrency getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull ICurrency currency) {
        this.currency = currency;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isPermissionRequired() {
        return isPermissionRequired;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        isPermissionRequired = permissionRequired;
    }

    @NotNull
    public String getPermission() {
        return Perms.PREFIX_KIT + this.getId();
    }

    @NotNull
    public List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = commands;
    }

    @NotNull
    public Set<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }

    public void setPotionEffects(@NotNull Set<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    @NotNull
    public ItemStack[] getArmor() {
        return this.armor;
    }

    public void setArmor(@NotNull ItemStack[] armor) {
        this.armor = Arrays.copyOf(this.fineItems(armor), 4);
    }

    @NotNull
    public ItemStack[] getItems() {
        return this.items;
    }

    public void setItems(@NotNull ItemStack[] items) {
        this.items = Arrays.copyOf(this.fineItems(items), 27);
    }

    public ItemStack[] getExtras() {
        return extras;
    }

    public void setExtras(@NotNull  ItemStack[] extras) {
        this.extras = Arrays.copyOf(this.fineItems(extras), 1);
    }

    private ItemStack[] fineItems(@NotNull ItemStack[] array) {
        for (int index = 0; index < array.length; index++) {
            if (array[index] == null) {
                array[index] = new ItemStack(Material.AIR);
            }
        }
        return array;
    }

    @Override
    public void updateHolograms() {
        HologramHolder.super.updateHolograms();
        this.setHologramClick((player -> {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) return;

            if (player.isSneaking()) {
                this.getPreview().open(player, 1);
            }
            else {
                this.buy(arenaPlayer);
            }
        }));
    }

    @NotNull
    @Override
    public HologramType getHologramType() {
        return HologramType.KIT;
    }

    @NotNull
    @Override
    public List<String> getHologramFormat() {
        List<String> text = HologramManager.getFormat(this.getHologramType());
        text.replaceAll(this.replacePlaceholders());
        return text.stream().map(str -> str.split("\n")).flatMap(Stream::of).toList();
    }

    @NotNull
    @Override
    public Set<UUID> getHologramIds() {
        return hologramIds;
    }

    @NotNull
    @Override
    public Set<Location> getHologramLocations() {
        return hologramLocations;
    }

    public boolean buy(@NotNull ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        if (!this.hasPermission(player)) {
            plugin.getMessage(Lang.Kit_Buy_Error_NoPermission).send(player);
            return false;
        }

        ArenaUser user = plugin.getUserManager().getUserData(player);

        // Check if player already have this kit
        // Only for permanent kits
        boolean accountKits = plugin.getKitManager().isSavePurchasedKits();
        if (accountKits && user.hasKit(this)) {
            arenaPlayer.setKit(this);
            return true;
        }

        double balance = this.getCurrency().getBalance(player);
        double cost = player.hasPermission(Perms.BYPASS_KIT_COST) ? 0 : this.getCost();

        if (cost > 0D) {
            if (balance < cost) {
                plugin.getMessage(Lang.Kit_Buy_Error_NoMoney).replace(this.replacePlaceholders()).send(player);
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1.0f, 1.0f); // TODO In msg
                return false;
            }
            this.getCurrency().take(player, cost);
        }

        plugin.getMessage(Lang.Kit_Buy_Success).replace(this.replacePlaceholders()).send(player);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f); // TODO In msg

        if (accountKits) {
            user.addKit(this);
        }
        else {
            arenaPlayer.setKit(this);
        }
        return true;
    }

    public boolean isAvailable(@NotNull ArenaPlayer arenaPlayer, boolean isMsg) {
        Arena arena = arenaPlayer.getArena();
        Player player = arenaPlayer.getPlayer();

        // Check if kits are disabled on the arena
        if (!arena.getConfig().getGameplayManager().isKitsEnabled()) {
            if (isMsg) plugin.getMessage(Lang.Arena_Game_Restrict_Kits).send(player);
            return false;
        }

        // Check if kit is banned on the arena
        if (!arena.getConfig().getGameplayManager().isKitAllowed(this.getId())) {
            if (isMsg) plugin.getMessage(Lang.Kit_Select_Error_Disabled).send(player);
            return false;
        }

        // Check if player has permission.
        if (!this.hasPermission(player)) {
            if (isMsg) plugin.getMessage(Lang.Kit_Select_Error_NoPermission).send(player);
            return false;
        }

        // Check for limit
        int limitMax = arena.getConfig().getGameplayManager().getKitLimit(this.getId());
        if (limitMax >= 0) {
            int limitHas = (int) arena.getPlayers(PlayerType.REAL).stream()
                .filter(arenaPlayer1 -> arenaPlayer1.getKit() != null && arenaPlayer1.getKit().equals(this)).count();
            if (limitHas >= limitMax) {
                if (isMsg) plugin.getMessage(Lang.Kit_Select_Error_Limit).send(player);
                return false;
            }
        }

        return true;
    }
}
