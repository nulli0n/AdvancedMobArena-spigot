package su.nightexpress.ama.arena.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.editor.EditorLocales;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.api.menu.item.MenuItem;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static su.nexmedia.engine.utils.Colors2.*;
import static su.nightexpress.ama.Placeholders.*;

public class ArenaListMenu extends ConfigMenu<AMA> implements AutoPaged<Arena> {

    public static final String FILE_NAME = "arena_list.yml";

    private static final String PLACEHOLDER_STATUS = "%status%";
    private static final String PLACEHOLDER_PAYMENT = "%payment%";
    private static final String PLACEHOLDER_PLAYERS = "%max_players%";
    private static final String PLACEHOLDER_LOW_FUNDS = "%no_funds%";

    private String arenaName;
    private List<String> arenaMainLore;
    private List<String> arenaMaxPlayersLore;
    private List<String> arenaCantAffordLore;
    private List<String> arenaPaymentLore;
    private List<String> arenaSpectateStatus;
    private List<String> arenaJoinStatus;
    private int[] arenaSlots;

    public ArenaListMenu(@NotNull AMA plugin) {
        super(plugin, JYML.loadOrExtract(plugin, Config.DIR_MENU, FILE_NAME));

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, ClickHandler.forClose(this))
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this));

        this.load();
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public boolean isCodeCreation() {
        return true;
    }

    @Override
    @NotNull
    protected MenuOptions createDefaultOptions() {
        return new MenuOptions("Mob Arenas", 27, InventoryType.CHEST);
    }

    @Override
    @NotNull
    protected List<MenuItem> createDefaultItems() {
        List<MenuItem> list = new ArrayList<>();

        ItemStack blackFillItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        list.add(new MenuItem(blackFillItem).setSlots(0,8,18,26));

        ItemStack whiteFillItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        list.add(new MenuItem(whiteFillItem).setSlots(1,2,3,4,5,6,7,9,17,19,20,21,22,23,24,25));

        ItemStack exitItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==");
        ItemUtil.mapMeta(exitItem, meta -> {
            meta.setDisplayName(LIGHT_RED + BOLD + "Close");
        });
        list.add(new MenuItem(exitItem).setType(MenuItemType.CLOSE).setSlots(22).setPriority(10));

        ItemStack nextPageItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19");
        ItemUtil.mapMeta(nextPageItem, meta -> {
            meta.setDisplayName(EditorLocales.NEXT_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(nextPageItem).setType(MenuItemType.PAGE_NEXT).setSlots(17).setPriority(10));

        ItemStack backPageItem = ItemUtil.createCustomHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0=");
        ItemUtil.mapMeta(backPageItem, meta -> {
            meta.setDisplayName(EditorLocales.PREVIOUS_PAGE.getLocalizedName());
        });
        list.add(new MenuItem(backPageItem).setType(MenuItemType.PAGE_PREVIOUS).setSlots(9).setPriority(10));

        return list;
    }

    @Override
    protected void loadAdditional() {
        this.arenaName = JOption.create("Arena.Name", LIGHT_YELLOW + BOLD + ARENA_NAME).read(cfg);

        this.arenaMainLore = JOption.create("Arena.Lore", Arrays.asList(
            LIGHT_GRAY + "Status: " + LIGHT_YELLOW + ARENA_STATE,
            LIGHT_GRAY + "Players: " + LIGHT_YELLOW + ARENA_REAL_PLAYERS + LIGHT_GRAY + "/" + LIGHT_YELLOW + ARENA_PLAYERS_MAX,
            LIGHT_GRAY + "Round: " + LIGHT_YELLOW + ARENA_WAVE_NUMBER + LIGHT_GRAY + "/" + LIGHT_YELLOW + ARENA_WAVES_FINAL_ROUND,
            "",
            PLACEHOLDER_PAYMENT,
            "",
            PLACEHOLDER_PLAYERS,
            PLACEHOLDER_LOW_FUNDS,
            "",
            PLACEHOLDER_STATUS
        )).read(cfg);

        this.arenaPaymentLore = JOption.create("Arena.Info.Payment", List.of(
            LIGHT_YELLOW + BOLD + "Payment Required:",
            LIGHT_YELLOW + "▪ " + LIGHT_GRAY + CURRENCY_NAME + ": " + LIGHT_YELLOW + GENERIC_AMOUNT
        )).read(cfg);

        this.arenaMaxPlayersLore = JOption.create("Arena.Info.MaxPlayers", List.of(
            LIGHT_RED + "[❗] " + LIGHT_GRAY + "There are already " + LIGHT_RED + "maximum" + LIGHT_GRAY + " players!"
        )).read(cfg);

        this.arenaCantAffordLore = JOption.create("Arena.Info.CantAfford", List.of(
            LIGHT_RED + "[❗] " + LIGHT_GRAY + "You don't have enough " + LIGHT_RED + "funds" + LIGHT_GRAY + "!"
        )).read(cfg);

        this.arenaJoinStatus = JOption.create("Arena.Info.Join", List.of(
            LIGHT_GREEN + "[▶] " + LIGHT_GRAY + "Click to " + LIGHT_GREEN + "join" + LIGHT_GRAY + "."
        )).read(cfg);

        this.arenaSpectateStatus = JOption.create("Arena.Info.Spectate", List.of(
            LIGHT_GREEN + "[▶] " + LIGHT_GRAY + "Click to " + LIGHT_GREEN + "spectate" + LIGHT_GRAY + "."
        )).read(cfg);

        this.arenaSlots = new JOption<>("Arena.Slots",
            JYML::getIntArray,
            IntStream.range(10, 17).toArray()
        ).setWriter(JYML::setIntArray).read(cfg);
    }

    @Override
    public int[] getObjectSlots() {
        return this.arenaSlots;
    }

    @Override
    @NotNull
    public List<Arena> getObjects(@NotNull Player player) {
        return this.plugin.getArenaManager().getArenas().stream()
            .filter(arena -> arena.getConfig().isActive())
            .sorted(Comparator.comparing(Arena::getId)).toList();
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull Arena arena) {
        ItemStack item = arena.getConfig().getIcon();

        List<String> status = new ArrayList<>();
        if (!arena.canJoin(player, false)) {
            status.addAll(this.arenaSpectateStatus);
        }
        else status.addAll(this.arenaJoinStatus);

        List<String> maxPlayers = new ArrayList<>();
        int playerMax = arena.getConfig().getGameplaySettings().getPlayerMaxAmount();
        if (playerMax > 0 && arena.getPlayers().players().size() >= playerMax) {
            maxPlayers.addAll(this.arenaMaxPlayersLore);
        }

        List<String> lowFunds = new ArrayList<>();
        if (!arena.checkPaymentRequirements(player)) {
            lowFunds.addAll(this.arenaCantAffordLore);
        }

        List<String> payment = new ArrayList<>();
        if (!arena.getConfig().getPaymentRequirements().isEmpty()) {
            for (String line : this.arenaPaymentLore) {
                if (line.contains(GENERIC_AMOUNT)) {
                    arena.getConfig().getPaymentRequirements().forEach((currency, amount) -> {
                        payment.add(currency.replacePlaceholders().apply(line
                            .replace(GENERIC_AMOUNT, currency.format(amount))
                        ));
                    });
                }
                else payment.add(line);
            }
        }

        ItemReplacer.create(item).trimmed().hideFlags()
            .setDisplayName(this.arenaName)
            .setLore(this.arenaMainLore)
            .replaceLoreExact(PLACEHOLDER_STATUS, status)
            .replaceLoreExact(PLACEHOLDER_PAYMENT, payment)
            .replaceLoreExact(PLACEHOLDER_PLAYERS, maxPlayers)
            .replaceLoreExact(PLACEHOLDER_LOW_FUNDS, lowFunds)
            .replace(ARENA_WAVES_FINAL_ROUND, () -> {
                int last = arena.getConfig().getWaveManager().getFinalRound();
                return last <= 0 ? LangManager.getPlain(Lang.OTHER_INFINITY) : NumberUtil.format(last);
            })
            .replace(arena.getPlaceholders())
            .replace(Colorizer::apply)
            .writeMeta();

        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull Arena arena) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (arena.canJoin(player, false)) {
                arena.joinLobby(player);
            }
            else arena.joinSpectate(player);
        };
    }
}
