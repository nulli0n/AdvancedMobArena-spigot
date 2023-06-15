package su.nightexpress.ama.mob.config;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractLoadableItem;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.manager.IPlaceholder;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.*;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.arena.util.ArenaUtils;
import su.nightexpress.ama.mob.editor.MobMainEditor;
import su.nightexpress.ama.mob.style.MobStyleType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MobConfig extends AbstractLoadableItem<AMA> implements IPlaceholder, ICleanable {

    private String     name;
    private boolean    nameVisible;
    private EntityType entityType;

    private int levelMin;
    private int levelMax;

    private Map<MobStyleType, String> styles;

    private boolean  barEnabled;
    private String   barTitle;
    private BarStyle barStyle;
    private BarColor barColor;

    private final Map<EquipmentSlot, ItemStack> equipment;
    private final Map<Attribute, double[]>      attributes; // [0] Base, [1] Per Level

    private MobMainEditor editor;

    @Override
    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return str -> str
            .replace(Placeholders.MOB_ID, this.getId())
            .replace(Placeholders.MOB_NAME, this.getName())
            .replace(Placeholders.MOB_NAME_VISIBLE, LangManager.getBoolean(this.isNameVisible()))
            .replace(Placeholders.MOB_ENTITY_TYPE, plugin.getLangManager().getEnum(this.getEntityType()))
            .replace(Placeholders.MOB_LEVEL_MIN, String.valueOf(this.getLevelMin()))
            .replace(Placeholders.MOB_LEVEL_MAX, String.valueOf(this.getLevelMax()))
            .replace(Placeholders.MOB_BOSSBAR_ENABLED, LangManager.getBoolean(this.isBarEnabled()))
            .replace(Placeholders.MOB_BOSSBAR_TITLE, this.getBarTitle())
            .replace(Placeholders.MOB_BOSSBAR_COLOR, this.getBarColor().name())
            .replace(Placeholders.MOB_BOSSBAR_STYLE, this.getBarStyle().name())
            .replace(Placeholders.MOB_ATTRIBUTES_BASE, this.getAttributes().entrySet().stream()
                .map(enrty -> enrty.getKey() + ": " + enrty.getValue()[0]).collect(Collectors.joining("\n")))
            .replace(Placeholders.MOB_ATTRIBUTES_LEVEL, this.getAttributes().entrySet().stream()
                .map(enrty -> enrty.getKey() + ": " + enrty.getValue()[1]).collect(Collectors.joining("\n")))
            ;
    }

    // Creating new config
    public MobConfig(@NotNull AMA plugin, @NotNull String path, @NotNull EntityType entityType) {
        super(plugin, path);

        this.setEntityType(entityType);
        this.setName("&f" + StringUtil.capitalizeFully(getEntityType().name().toLowerCase().replace("_", " ")) + " &cLv. &6" + Placeholders.MOB_LEVEL);
        this.setNameVisible(true);

        this.setLevelMin(1);
        this.setLevelMax(10);

        this.setStyles(new HashMap<>());

        this.setBarEnabled(false);
        this.setBarTitle("&c&l" + Placeholders.MOB_NAME + " &7&l- &f&l" + Placeholders.MOB_HEALTH + "&7/&f&l" + Placeholders.MOB_HEALTH_MAX);
        this.setBarStyle(BarStyle.SEGMENTED_12);
        this.setBarColor(BarColor.RED);

        this.equipment = new HashMap<>();

        this.attributes = new HashMap<>();
        this.attributes.put(Attribute.GENERIC_MAX_HEALTH, new double[]{20D, 1D});
    }

    public MobConfig(@NotNull AMA plugin, @NotNull JYML cfg) {
        super(plugin, cfg);

        this.setName(cfg.getString("Name", this.getId()));
        this.setNameVisible(cfg.getBoolean("Name_Visible"));
        EntityType type = cfg.getEnum("Entity_Type", EntityType.class);
        if (type == null) {
            throw new IllegalStateException("Invalid entity type for '" + getId() + "' mob!");
        }
        this.setEntityType(type);

        this.setLevelMin(cfg.getInt("Level.Minimum", 1));
        this.setLevelMax(cfg.getInt("Level.Maximum", 1));

        this.setStyles(new HashMap<>());
        for (String sId : cfg.getSection("Styles")) {
            MobStyleType styleType = CollectionsUtil.getEnum(sId, MobStyleType.class);
            if (styleType == null) continue;

            String value = cfg.getString("Styles." + sId, "");
            this.addStyle(styleType, value);
        }

        // Boss Bar
        String path = "Boss_Bar.";
        this.setBarEnabled(cfg.getBoolean(path + "Enabled"));
        this.setBarTitle(cfg.getString(path + "Title", Placeholders.MOB_NAME));
        this.setBarColor(cfg.getEnum(path + "Color", BarColor.class, BarColor.RED));
        this.setBarStyle(cfg.getEnum(path + "Style", BarStyle.class, BarStyle.SOLID));

        this.equipment = new HashMap<>();
        Stream.of(EquipmentSlot.values()).forEach(slot -> {
            this.setEquipment(slot, cfg.getItemEncoded("Equipment." + slot.name()));
        });

        // Attributes
        path = "Attributes.";
        this.attributes = new HashMap<>();
        for (Attribute attribute : Attribute.values()) {
            double valueBase = cfg.getDouble(path + "Base." + attribute.name());
            double valueLevel = cfg.getDouble(path + "Per_Level." + attribute.name());
            if (valueBase > 0 || valueLevel > 0) {
                this.attributes.put(attribute, new double[]{valueBase, valueLevel});
            }
        }
    }

    @Override
    public void onSave() {
        cfg.set("Name", this.getName());
        cfg.set("Name_Visible", this.isNameVisible());
        cfg.set("Entity_Type", this.getEntityType().name());

        cfg.set("Level.Minimum", this.getLevelMin());
        cfg.set("Level.Maximum", this.getLevelMax());

        cfg.set("Styles", null);
        this.getStyles().forEach((styleType, value) -> {
            cfg.set("Styles." + styleType.name(), value);
        });

        cfg.set("Equipment", null);
        Stream.of(EquipmentSlot.values()).forEach(slot -> {
            cfg.setItemEncoded("Equipment." + slot.name(), this.getEquipment(slot));
        });

        String path = "Boss_Bar.";
        cfg.set(path + "Enabled", this.isBarEnabled());
        cfg.set(path + "Title", this.getBarTitle());
        cfg.set(path + "Style", this.getBarStyle().name());
        cfg.set(path + "Color", this.getBarColor().name());

        cfg.set("Attributes", null);
        this.getAttributes().forEach((att, values) -> {
            String name = att.name();
            cfg.set("Attributes.Base." + name, values[0]);
            cfg.set("Attributes.Per_Level." + name, values[1]);
        });
    }

    @NotNull
    public MobMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new MobMainEditor(this);
        }
        return editor;
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }
    @NotNull
    public BossBar createOrUpdateBar(@NotNull LivingEntity entity) {
        String title = this.getBarTitle(entity);

        BossBar bossBar = ArenaUtils.getMobBossBar(entity);
        if (bossBar == null) bossBar = this.plugin.getServer().createBossBar(title, this.getBarColor(), this.getBarStyle(), BarFlag.DARKEN_SKY);

        double maxHealth = EntityUtil.getAttribute(entity, Attribute.GENERIC_MAX_HEALTH);
        double percent = Math.max(0D, Math.min(1D, entity.getHealth() / maxHealth));

        bossBar.setTitle(title);
        bossBar.setProgress(percent);
        bossBar.setVisible(true);
        return bossBar;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    public boolean isNameVisible() {
        return this.nameVisible;
    }

    public void setNameVisible(boolean visible) {
        this.nameVisible = visible;
    }

    @NotNull
    public EntityType getEntityType() {
        return this.entityType;
    }

    public void setEntityType(@NotNull EntityType type) {
        this.entityType = type;
    }

    public int getLevelMin() {
        return this.levelMin;
    }

    public void setLevelMin(int levelMin) {
        this.levelMin = levelMin;
    }

    public int getLevelMax() {
        return this.levelMax;
    }

    public void setLevelMax(int levelMax) {
        this.levelMax = levelMax;
    }

    public boolean isBarEnabled() {
        return barEnabled;
    }

    public void setBarEnabled(boolean barEnabled) {
        this.barEnabled = barEnabled;
    }

    public void setBarTitle(@NotNull String barTitle) {
        this.barTitle = Colorizer.apply(barTitle);
    }

    @NotNull
    public String getBarTitle() {
        return this.barTitle;
    }

    @NotNull
    private String getBarTitle(@NotNull LivingEntity entity) {
        double maxHealth = EntityUtil.getAttribute(entity, Attribute.GENERIC_MAX_HEALTH);
        return this.getBarTitle()
            .replace(Placeholders.MOB_HEALTH, NumberUtil.format(entity.getHealth()))
            .replace(Placeholders.MOB_HEALTH_MAX, NumberUtil.format(maxHealth))
            .replace(Placeholders.MOB_NAME, entity.getCustomName() != null ? entity.getCustomName() : entity.getName())
            ;
    }

    @NotNull
    public BarStyle getBarStyle() {
        return this.barStyle;
    }

    public void setBarStyle(@NotNull BarStyle barStyle) {
        this.barStyle = barStyle;
    }

    @NotNull
    public BarColor getBarColor() {
        return this.barColor;
    }

    public void setBarColor(@NotNull BarColor barColor) {
        this.barColor = barColor;
    }

    @NotNull
    public Map<MobStyleType, String> getStyles() {
        return styles;
    }

    public void setStyles(@NotNull Map<MobStyleType, String> styles) {
        this.styles = styles;
    }

    @NotNull
    public String getStyle(@NotNull MobStyleType styleType) {
        return this.getStyles().getOrDefault(styleType, "");
    }

    public void addStyle(@NotNull MobStyleType styleType, @NotNull String value) {
        if (value.isEmpty() || !styleType.isApplicable(this.getEntityType())) return;

        this.getStyles().put(styleType, value);
    }

    public void removeStyle(@NotNull MobStyleType styleType) {
        this.getStyles().remove(styleType);
    }

    @NotNull
    public Map<EquipmentSlot, ItemStack> getEquipment() {
        return this.equipment;
    }

    @NotNull
    public ItemStack getEquipment(@NotNull EquipmentSlot slot) {
        return this.equipment.getOrDefault(slot, new ItemStack(Material.AIR));
    }

    public void setEquipment(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        if (item == null) item = new ItemStack(Material.AIR);
        this.equipment.put(slot, item);
    }

    /**
     * @return Mob attribute values array, where [0] is base value, [1] is per level increase.
     */
    @NotNull
    public Map<Attribute, double[]> getAttributes() {
        return this.attributes;
    }

    public void applySettings(@NotNull LivingEntity entity, int level) {
        entity.setCustomName(this.getName().replace(Placeholders.MOB_LEVEL, String.valueOf(level)));
        entity.setCustomNameVisible(this.isNameVisible());

        EntityEquipment armor = entity.getEquipment();
        if (armor != null) {
            this.getEquipment().forEach(armor::setItem);
        }

        if (entity instanceof Ageable ageable) {
            ageable.setAdult();
        }
        if (entity instanceof PiglinAbstract piglin) {
            piglin.setImmuneToZombification(true);
        }
        else if (entity instanceof Hoglin hoglin) {
            hoglin.setImmuneToZombification(true);
        }
        else if (entity instanceof Zombie zombie) {
            if (zombie instanceof ZombieVillager zombieVillager) {
                zombieVillager.setConversionTime(-1);
            }
            else if (zombie instanceof PigZombie pigZombie) {
                pigZombie.setAngry(true);
            }
            else if (zombie instanceof Husk husk) {
                husk.setConversionTime(-1);
            }
            else {
                zombie.setConversionTime(-1);
            }
        }

        this.getStyles().forEach(((styleType, value) -> {
            styleType.getWrapper().apply(entity, value);
        }));
    }

    public void applyAttributes(@NotNull LivingEntity entity, int level) {
        final int lvl2 = Math.min(this.getLevelMax(), Math.max(this.getLevelMin(), level)) - 1; // -1 to fine value

        this.getAttributes().forEach((attribute, values) -> {
            AttributeInstance aInstance = entity.getAttribute(attribute);
            if (aInstance == null) return;

            // Fix for cases where default value is not present to use the vanilla one.
            if (values[0] <= 0) values[0] = aInstance.getBaseValue();

            double value = values[0] + (values[1] * lvl2);
            aInstance.setBaseValue(value);

            if (attribute == Attribute.GENERIC_MAX_HEALTH) {
                entity.setHealth(value);
            }
        });
    }
}
