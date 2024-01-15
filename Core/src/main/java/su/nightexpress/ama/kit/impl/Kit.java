package su.nightexpress.ama.kit.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.Version;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.*;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Keys;
import su.nightexpress.ama.Perms;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.currency.Currency;
import su.nightexpress.ama.api.hologram.HologramHolder;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.hologram.HologramManager;
import su.nightexpress.ama.kit.editor.KitMainEditor;

import java.util.*;
import java.util.stream.Stream;

import static org.bukkit.attribute.AttributeModifier.Operation;

public class Kit extends AbstractConfigHolder<AMA> implements HologramHolder, Placeholder {

    public static final int EFFECT_DURATION = 60 * 60 * 20;
    public static final int INVENTORY_SIZE = 36;
    public static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] {
        EquipmentSlot.FEET,
        EquipmentSlot.LEGS,
        EquipmentSlot.CHEST,
        EquipmentSlot.HEAD,
        EquipmentSlot.OFF_HAND
    };
    public static final Attribute[] ATTRIBUTES = new Attribute[] {
        Attribute.GENERIC_ATTACK_KNOCKBACK,
        Attribute.GENERIC_KNOCKBACK_RESISTANCE,
        Attribute.GENERIC_ARMOR_TOUGHNESS,
        Attribute.GENERIC_ARMOR,
        Attribute.GENERIC_MOVEMENT_SPEED,
        Attribute.GENERIC_MAX_ABSORPTION,
        Attribute.GENERIC_ATTACK_DAMAGE,
        Attribute.GENERIC_ATTACK_SPEED,
        Attribute.GENERIC_MAX_HEALTH
    };

    private final Map<Attribute, Pair<Double, Operation>> attributeMap;
    private final Map<EquipmentSlot, ItemStack> equipment;
    private final ItemStack[]       items;
    private final Set<UUID>     hologramIds;
    private final Set<Location> hologramLocations;
    private final PlaceholderMap placeholderMap;

    private boolean      isDefault;
    private String       name;
    private List<String> description;
    private ItemStack    icon;
    private Currency     currency;
    private double  cost;
    private boolean permissionRequired;

    private List<String>      commands;
    private Set<PotionEffect> potionEffects;

    private KitMainEditor  editor;

    public Kit(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);
        this.attributeMap = new HashMap<>();
        this.commands = new ArrayList<>();
        this.potionEffects = new HashSet<>();
        this.equipment = new HashMap<>();
        this.items = new ItemStack[INVENTORY_SIZE];

        this.hologramIds = new HashSet<>();
        this.hologramLocations = new HashSet<>();
        this.placeholderMap = Placeholders.forKit(this);
    }

    @Override
    public boolean load() {
        this.setDefault(cfg.getBoolean("Default"));
        this.setName(cfg.getString("Name", this.getId()));
        this.setDescription(cfg.getStringList("Description"));
        this.setCost(cfg.getDouble("Cost"));
        this.setPermissionRequired(cfg.getBoolean("Permission_Required"));

        Currency currency = plugin.getCurrencyManager().getCurrency(cfg.getString("Currency", ""));
        if (currency == null) currency = plugin.getCurrencyManager().getAny();
        this.setCurrency(currency);

        ItemStack icon = cfg.getItem("Icon");
        if (icon.getType().isAir()) icon = new ItemStack(Material.DIAMOND_SWORD);
        this.setIcon(icon);

        this.setCommands(cfg.getStringList("Content.Commands"));

        for (String sId : cfg.getSection("Content.Attributes")) {
            Attribute attribute = StringUtil.getEnum(sId, Attribute.class).orElse(null);
            if (attribute == null) continue;

            String path = "Content.Attributes." + sId;
            Operation operation = StringUtil.getEnum(cfg.getString(path + ".Operation", ""), Operation.class).orElse(Operation.ADD_NUMBER);
            double value = cfg.getDouble(path + ".Value");

            this.setAttribute(attribute, operation, value);
        }

        this.setPotionEffects(new HashSet<>());
        for (String sId : cfg.getSection("Content.Potion_Effects")) {
            PotionEffectType effectType;
            if (Version.isAtLeast(Version.V1_20_R3)) {
                effectType = Registry.EFFECT.get(NamespacedKey.minecraft(sId.toLowerCase()));
            }
            else effectType = PotionEffectType.getByName(sId.toUpperCase());
            if (effectType == null) continue;

            int amplifier = cfg.getInt("Content.Potion_Effects." + sId);
            if (amplifier == 0) continue;

            PotionEffect effect = new PotionEffect(effectType, EFFECT_DURATION, amplifier - 1);
            this.getPotionEffects().add(effect);
        }

        // ---------- 7.15+ UPDATES START ----------
        if (cfg.contains("Content.Armor")) {
            ItemStack[] armor = cfg.getItemsEncoded("Content.Armor");
            ItemStack[] extras = cfg.getItemsEncoded("Content.Extras");

            int armorIndex = 0;
            for (EquipmentSlot slot : EQUIPMENT_SLOTS) {
                if (armorIndex >= armor.length) break;

                cfg.setItemEncoded("Content.Equipment." + slot.name(), armor[armorIndex++]);
            }
            if (extras.length > 0) {
                cfg.setItemEncoded("Content.Equipment." + EquipmentSlot.OFF_HAND.name(), extras[0]);
            }
            cfg.remove("Content.Armor");
            cfg.remove("Content.Extras");
            cfg.saveChanges();
        }
        // ---------- 7.15+ UPDATES END ----------

        this.setItems(cfg.getItemsEncoded("Content.Inventory"));

        for (String sId : cfg.getSection("Content.Equipment")) {
            EquipmentSlot slot = StringUtil.getEnum(sId, EquipmentSlot.class).orElse(null);
            if (slot == null) continue;

            ItemStack item = cfg.getItemEncoded("Content.Equipment." + slot.name());
            if (item == null) item = new ItemStack(Material.AIR);

            this.setEquipment(slot, item);
        }

        this.hologramLocations.addAll(cfg.getStringSet("Hologram.Locations").stream().map(LocationUtil::deserialize)
            .filter(Objects::nonNull).toList());

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

        cfg.remove("Content.Attributes");
        this.getAttributeMap().forEach((att, pair) -> {
            cfg.set("Content.Attributes." + att.name() + ".Value", pair.getFirst());
            cfg.set("Content.Attributes." + att.name() + ".Operation", pair.getSecond().name());
        });

        cfg.remove("Content.Potion_Effects");
        this.getPotionEffects().forEach(potion -> {
            cfg.set("Content.Potion_Effects." + potion.getType().getName(), potion.getAmplifier() + 1);
        });

        cfg.remove("Content.Equipment");
        this.getEquipment().forEach((slot, item) -> {
            cfg.setItemEncoded("Content.Equipment." + slot.name(), item);
        });

        cfg.setItemsEncoded("Content.Inventory", Arrays.asList(this.getItems()));
        cfg.set("Hologram.Locations", this.getHologramLocations().stream().map(LocationUtil::serialize).toList());
    }

    public void clear() {
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

    public boolean canAfford(@NotNull Player player) {
        double cost = this.getCost(player);
        double balance = this.getCurrency().getHandler().getBalance(player);
        return cost <= 0D || balance >= this.getCost();
    }

    public boolean isPlayerLimitReached(@NotNull Arena arena) {
        int limit = arena.getConfig().getGameplaySettings().getKitLimit(this);
        if (limit < 0) return false;

        int players = arena.countKits(this);
        return players >= limit;
    }

    public boolean isAvailable(@NotNull ArenaPlayer arenaPlayer, boolean isMsg) {
        Arena arena = arenaPlayer.getArena();
        Player player = arenaPlayer.getPlayer();

        if (!arena.getConfig().getGameplaySettings().isKitAllowed(this)) {
            if (isMsg) plugin.getMessage(Lang.KIT_SELECT_ERROR_DISABLED).send(player);
            return false;
        }

        if (this.isPlayerLimitReached(arena)) {
            if (isMsg) plugin.getMessage(Lang.KIT_SELECT_ERROR_LIMIT).send(player);
            return false;
        }

        return true;
    }

    public void applyPotionEffects(@NotNull Player player) {
        this.getPotionEffects().forEach(effect -> {
            if (!player.hasPotionEffect(effect.getType())) {
                player.addPotionEffect(effect);
            }
        });
    }

    public void resetPotionEffects(@NotNull Player player) {
        this.getPotionEffects().forEach(effect -> {
            player.removePotionEffect(effect.getType());
        });
    }

    public void applyAttributeModifiers(@NotNull Player player) {
        this.getAttributeMap().forEach((attribute, pair) -> {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) return;

            AttributeModifier modifier = new AttributeModifier(this.getId(), pair.getFirst(), pair.getSecond());
            instance.addModifier(modifier);
        });
    }

    public void resetAttributeModifiers(@NotNull Player player) {
        this.getAttributeMap().forEach((attribute, pair) -> {
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) return;

            instance.getModifiers().forEach(modifier -> {
                if (modifier.getName().equalsIgnoreCase(this.getId())) {
                    instance.removeModifier(modifier);
                }
            });
        });
    }

    public void give(@NotNull Player player) {
        PlayerInventory inventory = player.getInventory();

        ItemStack[] items = this.getItems();
        for (int index = 0; index < items.length; index++) {
            if (index >= INVENTORY_SIZE) continue;

            ItemStack item = new ItemStack(items[index]);
            inventory.setItem(index, this.unshareableItem(item));
        }

        this.getEquipment().forEach((slot, armor) -> {
            ItemStack item = new ItemStack(armor);
            inventory.setItem(slot, this.unshareableItem(item));
        });

        //inventory.setContents(this.getItems());
        //this.getEquipment().forEach(inventory::setItem);
        this.getCommands().forEach(command -> PlayerUtil.dispatchCommand(player, command));
    }

    @NotNull
    private ItemStack unshareableItem(@NotNull ItemStack item) {
        if (Config.KITS_PREVENT_ITEM_SHARE.get()) {
            PDCUtil.set(item, Keys.ITEM_KIT_NAME, this.getId());
        }
        return item;
    }

    public void openPreview(@NotNull Player player) {
        this.plugin.getKitManager().getPreviewMenu().open(player, this, 1);
    }

    @NotNull
    public KitMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new KitMainEditor(this);
        }
        return this.editor;
    }

    public boolean isFree() {
        return this.getCost() <= 0D;
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
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull Currency currency) {
        this.currency = currency;
    }

    public double getCost() {
        return cost;
    }

    public double getCost(@NotNull Player player) {
        return player.hasPermission(Perms.BYPASS_KIT_COST) ? 0D : this.getCost();
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        this.permissionRequired = permissionRequired;
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
    public Map<Attribute, Pair<Double, AttributeModifier.Operation>> getAttributeMap() {
        return attributeMap;
    }

    public void setAttribute(@NotNull Attribute attribute, @NotNull AttributeModifier.Operation operation, double value) {
        this.getAttributeMap().put(attribute, Pair.of(value, operation));
    }

    @Nullable
    public Pair<Double, AttributeModifier.Operation> getAttribute(@NotNull Attribute attribute) {
        return this.getAttributeMap().get(attribute);
    }

    @NotNull
    public Set<PotionEffect> getPotionEffects() {
        return this.potionEffects;
    }

    public void setPotionEffects(@NotNull Set<PotionEffect> potionEffects) {
        this.potionEffects = potionEffects;
    }

    @NotNull
    public Map<EquipmentSlot, ItemStack> getEquipment() {
        return equipment;
    }

    @NotNull
    public ItemStack getEquipment(@NotNull EquipmentSlot slot) {
        return this.getEquipment().computeIfAbsent(slot, k -> new ItemStack(Material.AIR));
    }

    public void setEquipment(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        this.getEquipment().put(slot, item == null ? new ItemStack(Material.AIR) : item);
    }

    @NotNull
    public ItemStack[] getItems() {
        return this.items;
    }

    public void setItems(@NotNull List<ItemStack> items) {
        this.setItems(items.toArray(new ItemStack[0]));
    }

    public void setItems(@NotNull ItemStack[] items) {
        for (int index = 0; index < INVENTORY_SIZE; index++) {
            ItemStack item = index >= items.length ? new ItemStack(Material.AIR) : items[index];
            if (item == null) item = new ItemStack(Material.AIR);

            this.items[index] = item;
        }
    }

    @Override
    public void updateHolograms() {
        HologramHolder.super.updateHolograms();
        this.setHologramClick((player -> {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
            if (arenaPlayer == null) return;

            if (player.isSneaking()) {
                this.openPreview(player);
            }
            else {
                this.plugin.getKitManager().selectOrPurchase(player, this);
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
}
