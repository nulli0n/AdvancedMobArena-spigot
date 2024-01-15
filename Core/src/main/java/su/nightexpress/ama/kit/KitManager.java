package su.nightexpress.ama.kit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.api.menu.impl.Menu;
import su.nexmedia.engine.utils.FileUtil;
import su.nexmedia.engine.utils.PDCUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.Keys;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.currency.CurrencyManager;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.kit.impl.Kit;
import su.nightexpress.ama.kit.menu.KitConfirmMenu;
import su.nightexpress.ama.kit.menu.KitPreviewMenu;
import su.nightexpress.ama.kit.menu.KitSelectMenu;
import su.nightexpress.ama.kit.menu.KitShopMenu;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

public class KitManager extends AbstractManager<AMA> {

    private final Map<String, Kit> kitMap;

    private KitPreviewMenu previewMenu;
    private KitSelectMenu selectMenu;
    private KitShopMenu   shopMenu;
    private KitConfirmMenu confirmMenu;

    public KitManager(@NotNull AMA plugin) {
        super(plugin);
        this.kitMap = new HashMap<>();
    }

    @Override
    public void onLoad() {
        // ---------- 7.15+ UPDATES START ----------
        this.moveConfig("gui.preview.yml", KitPreviewMenu.FILE_NAME);
        this.moveConfig("gui.selector.yml", KitSelectMenu.FILE_NAME);
        this.moveConfig("gui.shop.yml", KitShopMenu.FILE_NAME);

        File configFile = new File(this.plugin.getDataFolder() + Config.DIR_KITS, "settings.yml");
        if (configFile.exists()) {
            JYML cfg = new JYML(configFile);
            boolean saveKits = cfg.getBoolean("General.Save_Purchased_Kits", true);
            Config.KITS_PERMANENT_PURCHASES.set(saveKits);
            Config.KITS_PERMANENT_PURCHASES.write(plugin.getConfig());
            plugin.getConfig().saveChanges();
            configFile.delete();
        }

        File folder = new File(this.plugin.getDataFolder() + "/kits/kits");
        if (folder.exists()) {
            for (File file : FileUtil.getFiles(folder.getAbsolutePath(), false)) {
                file.renameTo(new File(this.plugin.getDataFolder() + Config.DIR_KITS, file.getName()));
            }
            folder.delete();
        }
        // ---------- 7.15+ UPDATES END ----------

        File kits = new File(this.plugin.getDataFolder() + Config.DIR_KITS);
        if (!kits.exists() && kits.mkdirs()) {
            this.createDefaultKits();
        }
        else {
            for (JYML config : JYML.loadAll(kits.getAbsolutePath(), false)) {
                Kit kit = new Kit(this.plugin, config);
                if (kit.load()) {
                    this.getKitMap().put(kit.getId(), kit);
                    kit.updateHolograms();
                }
                else this.plugin.error("Kit not loaded: '" + config.getFile().getName() + "'.");
            }
        }
        this.plugin.info("Kits Loaded: " + this.getKitMap().size());

        this.previewMenu = new KitPreviewMenu(plugin, JYML.loadOrExtract(plugin, Config.DIR_MENU, KitPreviewMenu.FILE_NAME));
        this.selectMenu = new KitSelectMenu(plugin, JYML.loadOrExtract(plugin, Config.DIR_MENU, KitSelectMenu.FILE_NAME));
        this.shopMenu = new KitShopMenu(plugin, JYML.loadOrExtract(plugin, Config.DIR_MENU, KitShopMenu.FILE_NAME));
        this.confirmMenu = new KitConfirmMenu(plugin, JYML.loadOrExtract(plugin, Config.DIR_MENU, KitConfirmMenu.FILE_NAME));

        this.addListener(new KitListener(this.plugin, this));
    }

    private boolean moveConfig(@NotNull String from, @NotNull String to) {
        File old = new File(plugin.getDataFolder() + Config.DIR_KITS, from);
        return old.exists() && old.renameTo(new File(this.plugin.getDataFolder() + Config.DIR_MENU, to));
    }

    @Override
    public void onShutdown() {
        this.getKits().forEach(Kit::clear);
        this.getKitMap().clear();

        if (this.confirmMenu != null) this.confirmMenu.clear();
        if (this.previewMenu != null) this.previewMenu.clear();
        if (this.selectMenu != null) this.selectMenu.clear();
        if (this.shopMenu != null) this.shopMenu.clear();
    }

    public boolean createKit(@NotNull String id) {
        return this.createKit(id, kit -> {
            kit.setDescription(new ArrayList<>());
            kit.setIcon(new ItemStack(Material.GOLDEN_CHESTPLATE));
            kit.setCost(100);

            List<ItemStack> items = new ArrayList<>();
            items.add(new ItemStack(Material.IRON_SWORD));
            items.add(new ItemStack(Material.COOKED_BEEF, 16));
            items.add(new ItemStack(Material.GOLDEN_APPLE, 4));
            kit.setItems(items);
        });
    }

    public boolean createKit(@NotNull String id, @NotNull Consumer<Kit> consumer) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getKitById(id) != null) return false;

        JYML cfg = new JYML(this.plugin.getDataFolder() + Config.DIR_KITS, id + ".yml");

        Kit kit = new Kit(this.plugin, cfg);
        kit.setName(StringUtil.capitalizeUnderscored(id));
        kit.setCurrency(this.plugin.getCurrencyManager().getOrAny(CurrencyManager.COINS));

        consumer.accept(kit);

        kit.save();
        kit.load();
        this.getKitMap().put(kit.getId(), kit);
        return true;
    }

    public boolean deleteKit(@NotNull Kit kit) {
        if (!kit.getFile().delete()) return false;

        kit.clear();
        this.getKitMap().remove(kit.getId());
        return true;
    }

    private void createDefaultKits() {
        this.createKit("tank", KitUtils::setTankKit);
        this.createKit("warrior", KitUtils::setWarriorKit);
        this.createKit("archer", KitUtils::setArcherKit);
        this.createKit("support", KitUtils::setSupportKit);
        this.createKit("priest", KitUtils::setPriestKit);
        this.createKit("assasin", KitUtils::setAssasinKit);
        this.createKit("bomber", KitUtils::setBomberKit);
        this.createKit("pyro", KitUtils::setPyroKit);
    }

    @NotNull
    public KitConfirmMenu getConfirmMenu() {
        return confirmMenu;
    }

    @NotNull
    public KitPreviewMenu getPreviewMenu() {
        return previewMenu;
    }

    @NotNull
    public KitSelectMenu getSelectMenu() {
        return selectMenu;
    }

    @NotNull
    public KitShopMenu getShopMenu() {
        return shopMenu;
    }

    public boolean isKitExists(@NotNull String id) {
        return this.getKitById(id) != null;
    }

    @NotNull
    public Map<String, Kit> getKitMap() {
        return this.kitMap;
    }

    @NotNull
    public Collection<Kit> getKits() {
        return this.getKitMap().values();
    }

    @NotNull
    public List<String> getKitIds() {
        return new ArrayList<>(this.kitMap.keySet());
    }

    @Nullable
    public Kit getKitById(@NotNull String id) {
        return this.kitMap.get(id.toLowerCase());
    }

    @Nullable
    public Kit getKitByItem(@NotNull ItemStack item) {
        String id = PDCUtil.getString(item, Keys.ITEM_KIT_NAME).orElse(null);
        return id == null ? null : this.getKitById(id);
    }

    @Nullable
    public Kit getDefaultKit() {
        return this.getKits().stream().filter(Kit::isDefault).findFirst().orElse(null);
    }

    public boolean openSelector(@NotNull Player player) {
        return this.openMenu(player, this.getSelectMenu());
    }

    public boolean openShop(@NotNull Player player) {
        return this.openMenu(player, this.getShopMenu());
    }

    private boolean openMenu(@NotNull Player player, @NotNull Menu<?> menu) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return false;
        if (!arenaPlayer.getArena().getConfig().getGameplaySettings().isKitsEnabled()) return false;

        menu.open(player, 1);
        return true;
    }

    @Nullable
    public Kit getAnyAvailable(@NotNull ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();

        ArenaUser user = plugin.getUserManager().getUserData(player);
        return user.getKits().stream().map(this::getKitById)
            .filter(kit -> kit != null && kit.hasPermission(player) && kit.isAvailable(arenaPlayer, false))
            .findAny().orElse(this.getDefaultKit());
    }

    public void selectOrPurchase(@NotNull Player player, @NotNull Kit kit) {
        ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(player);
        if (arenaPlayer == null) return;
        if (!arenaPlayer.getArena().getConfig().getGameplaySettings().isKitsEnabled()) return;

        if (!kit.hasPermission(player)) {
            plugin.getMessage(Lang.KIT_ERROR_NO_PERMISSION).send(player);
            return;
        }

        ArenaUser user = plugin.getUserManager().getUserData(player);
        if (user.hasKit(kit)) {
            if (!kit.isAvailable(arenaPlayer, true)) {
                return;
            }

            arenaPlayer.setKit(kit);
            this.plugin.getMessage(Lang.KIT_SELECT_SUCCESS).replace(kit.replacePlaceholders()).send(player);
            return;
        }

        this.getConfirmMenu().open(player, kit, 1);
    }

    public boolean purchase(@NotNull Player player, @NotNull Kit kit) {
        ArenaUser user = plugin.getUserManager().getUserData(player);
        if (user.hasKit(kit)) {
            return false;
        }

        if (!kit.canAfford(player)) {
            plugin.getMessage(Lang.KIT_BUY_ERROR_NO_MONEY).replace(kit.replacePlaceholders()).send(player);
            return false;
        }

        double cost = kit.getCost(player);
        if (cost > 0D) {
            kit.getCurrency().getHandler().take(player, cost);
        }

        plugin.getMessage(Lang.KIT_BUY_SUCCESS).replace(kit.replacePlaceholders()).send(player);
        user.addKit(kit);
        return true;
    }
}
