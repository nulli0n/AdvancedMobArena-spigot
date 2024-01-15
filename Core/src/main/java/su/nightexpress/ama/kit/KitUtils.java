package su.nightexpress.ama.kit;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.kit.impl.Kit;

import java.util.HashSet;

import static su.nexmedia.engine.utils.Colors2.*;

public class KitUtils {

    public static void setWarriorKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newArrayList(
            LIGHT_GRAY + "The warrior kit combines",
            LIGHT_GRAY + "a balance of defense and damage.",
            "",
            LIGHT_GRAY + "Having " + LIGHT_GREEN + "no negative" + LIGHT_GRAY + " effects,",
            LIGHT_GRAY + "it has " + LIGHT_RED + "no positive" + LIGHT_GRAY + " ones either."
        ));
        kit.setDefault(true);
        kit.setIcon(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
        kit.setCost(0);
        kit.setPotionEffects(new HashSet<>());

        ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemStack chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);

        helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack strengthPotion = new ItemStack(Material.POTION);
        ItemUtil.mapMeta(strengthPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 60 * 20, 0), true);
            potionMeta.setColor(Color.fromRGB(170, 10, 40));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Potion of Strength"));
        });

        ItemStack regenPotion = new ItemStack(Material.POTION);
        ItemUtil.mapMeta(regenPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(230, 145, 195));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Potion of Resistance"));
        });

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 27; slot++) {
            items[slot] = new ItemStack(regenPotion);
        }
        for (int slot = 27; slot < 36; slot++) {
            items[slot] = new ItemStack(strengthPotion);
        }
        kit.setItems(items);
    }

    public static void setTankKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newArrayList(
            LIGHT_GRAY + "Tanks are capable of absorbing",
            LIGHT_GRAY + "large amounts of damage for",
            LIGHT_GRAY + "long periods of time.",
            "",
            LIGHT_GRAY + "Despite their " + LIGHT_GREEN + "high defense" + LIGHT_GRAY + ",",
            LIGHT_GRAY + "they are " + LIGHT_RED + "immobile" + LIGHT_GRAY + " and have",
            LIGHT_GRAY + "very " + LIGHT_RED + "low damage" + LIGHT_GRAY + "."
        ));
        kit.setIcon(new ItemStack(Material.DIAMOND_CHESTPLATE));
        kit.setCost(0);
        kit.setPotionEffects(new HashSet<>());

        kit.setAttribute(Attribute.GENERIC_MAX_HEALTH, AttributeModifier.Operation.ADD_NUMBER, 10D);
        kit.setAttribute(Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR, -0.35);
        kit.setAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE, AttributeModifier.Operation.ADD_SCALAR, 0.3);

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);

        for (Enchantment enchantment : new Enchantment[]{
            Enchantment.PROTECTION_ENVIRONMENTAL,
            Enchantment.PROTECTION_EXPLOSIONS,
            Enchantment.PROTECTION_PROJECTILE,
            Enchantment.PROTECTION_FIRE,
            Enchantment.THORNS,
        }) {
            helmet.addUnsafeEnchantment(enchantment, 4);
            chestplate.addUnsafeEnchantment(enchantment, 4);
            leggings.addUnsafeEnchantment(enchantment, 4);
            boots.addUnsafeEnchantment(enchantment, 4);
        }

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);
        kit.setEquipment(EquipmentSlot.OFF_HAND, new ItemStack(Material.SHIELD));

        ItemStack healPotion = new ItemStack(Material.POTION);
        ItemUtil.mapMeta(healPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1), true);
            potionMeta.setColor(Color.fromRGB(240, 65, 100));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Potion of Heal"));
        });

        ItemStack defensePotion = new ItemStack(Material.POTION);
        ItemUtil.mapMeta(defensePotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(90, 80, 130));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Potion of Resistance"));
        });

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = new ItemStack(Material.STONE_SWORD);
        items[1] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 27; slot++) {
            items[slot] = new ItemStack(defensePotion);
        }
        for (int slot = 27; slot < 36; slot++) {
            items[slot] = new ItemStack(healPotion);
        }
        kit.setItems(items);
    }

    public static void setArcherKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newArrayList(
            LIGHT_GRAY + "Archers are " + LIGHT_GREEN + "strong" + LIGHT_GRAY + ", " + LIGHT_GREEN + "fast" + LIGHT_GRAY + " and " + LIGHT_GREEN + "agile" + LIGHT_GRAY + ".",
            LIGHT_GRAY + "But only as long as they",
            LIGHT_GRAY + "keep their " + LIGHT_RED + "distance.",
            "",
            LIGHT_GRAY + "And while they have",
            LIGHT_GRAY + "melee defenses, it's not",
            LIGHT_GRAY + "their strongest suit."
        ));
        kit.setIcon(new ItemStack(Material.BOW));
        kit.setCost(0);
        kit.setPotionEffects(new HashSet<>());

        kit.setAttribute(Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR, 0.3);
        kit.setAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK, AttributeModifier.Operation.ADD_SCALAR, 0.2);

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        ItemUtil.mapMeta(helmet, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(150, 235, 250));
        });
        ItemUtil.mapMeta(chestplate, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(150, 235, 250));
        });
        ItemUtil.mapMeta(leggings, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(150, 235, 250));
        });
        ItemUtil.mapMeta(boots, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(150, 235, 250));
        });

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack speedPotion = new ItemStack(Material.POTION);
        ItemUtil.mapMeta(speedPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(150, 235, 250));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Potion of Swiftness"));
        });

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
        bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);

        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);

        ItemStack arrow = new ItemStack(Material.ARROW, 64);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = bow;
        items[1] = sword;
        items[2] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 27; slot++) {
            items[slot] = new ItemStack(arrow);
        }
        for (int slot = 27; slot < 36; slot++) {
            items[slot] = new ItemStack(speedPotion);
        }
        kit.setItems(items);
    }

    public static void setAssasinKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newArrayList(
            LIGHT_GRAY + "Assassins are " + LIGHT_GREEN + "fast" + LIGHT_GRAY + " and " + LIGHT_GREEN + "strong" + LIGHT_GRAY + " in",
            LIGHT_GRAY + "close combat. Their extra tools are",
            LIGHT_GRAY + "crossbow and splash poison potions.",
            "",
            LIGHT_GRAY + "However, their " + LIGHT_RED + "armor" + LIGHT_GRAY + " and " + LIGHT_RED + "health",
            LIGHT_GRAY + "stats are severely reduced,",
            LIGHT_GRAY + "making them easy prey."
        ));
        kit.setIcon(new ItemStack(Material.FEATHER));
        kit.setCost(8000);
        kit.setPotionEffects(Sets.newHashSet(
            new PotionEffect(PotionEffectType.INVISIBILITY, Kit.EFFECT_DURATION, 1)
        ));

        kit.setAttribute(Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR, 0.5);
        kit.setAttribute(Attribute.GENERIC_MAX_HEALTH, AttributeModifier.Operation.ADD_NUMBER, -5);
        kit.setAttribute(Attribute.GENERIC_ARMOR, AttributeModifier.Operation.ADD_SCALAR, -0.5);

        ItemStack helmet = new ItemStack(Material.OAK_LEAVES);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        ItemUtil.mapMeta(chestplate, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(65, 140, 65));
        });
        ItemUtil.mapMeta(leggings, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(65, 140, 65));
        });
        ItemUtil.mapMeta(boots, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(65, 140, 65));
        });

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack speedPotion = new ItemStack(Material.SPLASH_POTION, 4);
        ItemUtil.mapMeta(speedPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 60 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(65, 140, 65));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Splash Potion of Poison"));
        });

        ItemStack bow = new ItemStack(Material.CROSSBOW);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);

        ItemStack arrow = new ItemStack(Material.ARROW, 64);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = bow;
        items[2] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 21; slot++) {
            items[slot] = new ItemStack(arrow);
        }
        for (int slot = 27; slot < 30; slot++) {
            items[slot] = new ItemStack(speedPotion);
        }
        kit.setItems(items);
    }

    public static void setSupportKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newArrayList(
            LIGHT_GRAY + "Support has a bunch of " + WHITE + "splash",
            WHITE + "potions" + LIGHT_GRAY + " with various " + LIGHT_GREEN + "positive",
            LIGHT_GRAY + "effects to enhance yourself",
            LIGHT_GRAY + "or your team.",
            "",
            LIGHT_GRAY + "However, this is their " + LIGHT_RED + "only" + LIGHT_GRAY + " advantage."
        ));
        kit.setIcon(new ItemStack(Material.POTION));
        kit.setCost(0);
        kit.setPotionEffects(new HashSet<>());

        ItemStack helmet = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemStack chestplate = new ItemStack(Material.GOLDEN_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemStack boots = new ItemStack(Material.GOLDEN_BOOTS);

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack healPotion = new ItemStack(Material.SPLASH_POTION, 8);
        ItemUtil.mapMeta(healPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1), true);
            potionMeta.setColor(Color.fromRGB(240, 65, 100));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Potion of Heal"));
        });

        ItemStack firePotion = new ItemStack(Material.SPLASH_POTION, 8);
        ItemUtil.mapMeta(firePotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 90 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(250, 170, 60));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Potion of Fire Resistance"));
        });

        ItemStack regenPotion = new ItemStack(Material.SPLASH_POTION, 8);
        ItemUtil.mapMeta(regenPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(230, 145, 195));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Potion of Regeneration"));
        });

        ItemStack speedPotion = new ItemStack(Material.SPLASH_POTION, 8);
        ItemUtil.mapMeta(speedPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 90 * 20, 0), true);
            potionMeta.setColor(Color.fromRGB(150, 235, 250));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Potion of Swiftness"));
        });

        ItemStack harmPotion = new ItemStack(Material.SPLASH_POTION, 8);
        ItemUtil.mapMeta(harmPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(250, 240, 130));
            potionMeta.setDisplayName(Colorizer.apply(WHITE + "Potion of Haste"));
        });

        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        sword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 21; slot++) {
            items[slot] = new ItemStack(speedPotion);
        }
        for (int slot = 21; slot < 24; slot++) {
            items[slot] = new ItemStack(harmPotion);
        }
        for (int slot = 27; slot < 30; slot++) {
            items[slot] = new ItemStack(healPotion);
        }
        for (int slot = 30; slot < 33; slot++) {
            items[slot] = new ItemStack(regenPotion);
        }
        for (int slot = 33; slot < 36; slot++) {
            items[slot] = new ItemStack(firePotion);
        }
        kit.setItems(items);
    }

    public static void setPriestKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newArrayList(
            LIGHT_GRAY + "Priests are adept at " + LIGHT_GREEN + "healing" + LIGHT_GRAY + ".",
            LIGHT_GRAY + "Especially powerful against the " + LIGHT_GREEN + "undead" + LIGHT_GRAY + ".",
            LIGHT_GRAY + "In addition, their " + LIGHT_GREEN + "movement speed",
            LIGHT_GRAY + "is slightly increased.",
            "",
            LIGHT_GRAY + "However, they are " + LIGHT_RED + "weak" + LIGHT_GRAY + " against",
            LIGHT_GRAY + "most creatures, " + LIGHT_RED + "weakly defended" + LIGHT_GRAY + ",",
            LIGHT_GRAY + "and have " + LIGHT_RED + "reduced health" + LIGHT_GRAY + "."
        ));
        kit.setIcon(new ItemStack(Material.BLAZE_ROD));
        kit.setCost(7500);
        kit.setPotionEffects(Sets.newHashSet(
            new PotionEffect(PotionEffectType.REGENERATION, Kit.EFFECT_DURATION, 0)
        ));

        kit.setAttribute(Attribute.GENERIC_MAX_HEALTH, AttributeModifier.Operation.ADD_NUMBER, -4);
        kit.setAttribute(Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR, 0.15);

        ItemStack helmet = new ItemStack(Material.GOLDEN_HELMET);
        ItemStack chestplate = new ItemStack(Material.GOLDEN_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.GOLDEN_LEGGINGS);
        ItemStack boots = new ItemStack(Material.GOLDEN_BOOTS);

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack holyPotion = new ItemStack(Material.SPLASH_POTION, 10);
        ItemUtil.mapMeta(holyPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 1, 2), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HARM, 1, 2), true);
            potionMeta.setColor(Color.fromRGB(230, 250, 255));
            potionMeta.setDisplayName(Colorizer.apply(LIGHT_CYAN + "Holy Potion of Smite"));
        });

        ItemStack regenPotion = new ItemStack(Material.SPLASH_POTION, 10);
        ItemUtil.mapMeta(regenPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 20, 1), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(230, 145, 195));
            potionMeta.setDisplayName(Colorizer.apply(LIGHT_CYAN + "Holy Potion of Regeneration"));
        });

        ItemStack healthPotion = new ItemStack(Material.SPLASH_POTION, 10);
        ItemUtil.mapMeta(healthPotion, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 90 * 20, 1), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 90 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(150, 235, 250));
            potionMeta.setDisplayName(Colorizer.apply(LIGHT_CYAN + "Holy Potion of Health"));
        });

        // BOMBS

        ItemStack regenBomb = new ItemStack(Material.LINGERING_POTION, 10);
        ItemUtil.mapMeta(regenBomb, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, 20, 1), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(230, 145, 195));
            potionMeta.setDisplayName(Colorizer.apply(LIGHT_CYAN + "Holy Bomb of Regeneration"));
        });

        ItemStack healthBomb = new ItemStack(Material.LINGERING_POTION, 10);
        ItemUtil.mapMeta(healthBomb, meta -> {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, 90 * 20, 1), true);
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 90 * 20, 1), true);
            potionMeta.setColor(Color.fromRGB(150, 235, 250));
            potionMeta.setDisplayName(Colorizer.apply(LIGHT_CYAN + "Holy Bomb of Health"));
        });

        // OTHER

        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 5);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = new ItemStack(Material.COOKED_BEEF, 64);
        for (int slot = 18; slot < 21; slot++) {
            items[slot] = new ItemStack(healthPotion);
        }
        for (int slot = 21; slot < 24; slot++) {
            items[slot] = new ItemStack(holyPotion);
        }
        for (int slot = 24; slot < 27; slot++) {
            items[slot] = new ItemStack(regenPotion);
        }
        for (int slot = 27; slot < 30; slot++) {
            items[slot] = new ItemStack(healthBomb);
        }
        for (int slot = 33; slot < 36; slot++) {
            items[slot] = new ItemStack(regenBomb);
        }
        kit.setItems(items);
    }

    public static void setPyroKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newArrayList(
            LIGHT_GRAY + "Pyromaniacs love to play with fire.",
            LIGHT_GRAY + "Having a permanent " + LIGHT_GREEN + "immunity to fire" + LIGHT_GRAY + ",",
            LIGHT_GRAY + "they burn everything around them.",
            "",
            LIGHT_GRAY + "However, their supply of fire stuff",
            LIGHT_GRAY + "is " + LIGHT_RED + "limited" + LIGHT_GRAY + " and attack speed is " + LIGHT_RED + "reduced" + LIGHT_GRAY + "."
        ));
        kit.setIcon(new ItemStack(Material.FIRE_CHARGE));
        kit.setCost(15000);
        kit.setPotionEffects(Sets.newHashSet(
            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Kit.EFFECT_DURATION, 1)
        ));

        kit.setAttribute(Attribute.GENERIC_ATTACK_SPEED, AttributeModifier.Operation.ADD_SCALAR, -0.15);

        ItemStack helmet = new ItemStack(Material.ORANGE_STAINED_GLASS);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        ItemUtil.mapMeta(chestplate, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(240, 155, 75));
        });
        ItemUtil.mapMeta(leggings, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(240, 155, 75));
        });
        ItemUtil.mapMeta(boots, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(240, 155, 75));
        });

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 5);

        ItemStack bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 5);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = bow;
        items[2] = new ItemStack(Material.COOKED_BEEF, 64);
        items[8] = new ItemStack(Material.BLAZE_SPAWN_EGG, 16);
        for (int slot = 18; slot < 21; slot++) {
            items[slot] = new ItemStack(new ItemStack(Material.ARROW, 64));
        }
        for (int slot = 21; slot < 24; slot++) {
            items[slot] = new ItemStack(new ItemStack(Material.FIRE_CHARGE, 64));
        }
        kit.setItems(items);
    }

    public static void setBomberKit(@NotNull Kit kit) {
        kit.setDescription(Lists.newArrayList(
            LIGHT_GRAY + "Bombers are experts at blowing things up.",
            LIGHT_GRAY + "Their main strength is in " + LIGHT_GREEN + "explosives" + LIGHT_GRAY + ".",
            LIGHT_GRAY + "They have a slight bonus to " + LIGHT_GREEN + "armor" + LIGHT_GRAY + " and " + LIGHT_GREEN + "speed" + LIGHT_GRAY + ".",
            "",
            LIGHT_GRAY + "On the other hand, they " + LIGHT_RED + "don't",
            LIGHT_GRAY + "have strong weapons if they",
            LIGHT_RED + "run out" + LIGHT_GRAY + " of explosives."
        ));
        kit.setIcon(new ItemStack(Material.TNT));
        kit.setCost(20000);
        kit.setPotionEffects(new HashSet<>());

        kit.setAttribute(Attribute.GENERIC_ARMOR, AttributeModifier.Operation.ADD_SCALAR, 0.3);
        kit.setAttribute(Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR, 0.1);

        ItemStack helmet = new ItemStack(Material.TNT);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5);
        leggings.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 5);

        ItemUtil.mapMeta(chestplate, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(210, 80, 80));
        });
        ItemUtil.mapMeta(leggings, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(210, 80, 80));
        });
        ItemUtil.mapMeta(boots, meta -> {
            ((LeatherArmorMeta)meta).setColor(Color.fromRGB(210, 80, 80));
        });

        kit.setEquipment(EquipmentSlot.HEAD, helmet);
        kit.setEquipment(EquipmentSlot.CHEST, chestplate);
        kit.setEquipment(EquipmentSlot.LEGS, leggings);
        kit.setEquipment(EquipmentSlot.FEET, boots);

        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);

        ItemStack[] items = new ItemStack[Kit.INVENTORY_SIZE];
        items[0] = sword;
        items[1] = new ItemStack(Material.COOKED_BEEF, 64);
        items[8] = new ItemStack(Material.CREEPER_SPAWN_EGG, 16);
        for (int slot = 18; slot < 21; slot++) {
            items[slot] = new ItemStack(new ItemStack(Material.TNT, 64));
        }
        kit.setItems(items);
    }
}
