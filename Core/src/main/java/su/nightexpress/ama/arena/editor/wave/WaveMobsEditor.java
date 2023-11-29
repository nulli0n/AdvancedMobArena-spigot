package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemReplacer;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.impl.Wave;
import su.nightexpress.ama.arena.wave.impl.WaveMob;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.hook.mob.MobProvider;
import su.nightexpress.ama.hook.mob.PluginMobProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class WaveMobsEditor extends EditorMenu<AMA, Wave> implements AutoPaged<WaveMob> {

    public WaveMobsEditor(@NotNull AMA plugin, @NotNull Wave wave) {
        super(plugin, wave, "Wave Mobs [" + wave.getId() +"]", 45);

        this.addReturn(39).setClick((viewer, event) -> {
            wave.getArenaConfig().getWaveManager().getEditor().getListEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.WAVES_WAVE_MOB_CREATE, 41).setClick((viewer, event) -> {
            MobProvider provider = PluginMobProvider.getProviders().stream().findFirst().orElseThrow();
            WaveMob mob = new WaveMob(wave, provider, "null", 1, 1, 100D, new ItemStack(Material.ZOMBIE_HEAD));
            wave.getMobs().add(mob);
            wave.getArenaConfig().getWaveManager().save();
            this.openNextTick(viewer, viewer.getPage());
        });
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    @NotNull
    public List<WaveMob> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getMobs());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull WaveMob mob) {
        ItemStack item = mob.getIcon();
        ItemReplacer.create(item).readLocale(EditorLocales.WAVES_WAVE_MOB_OBJECT).trimmed().hideFlags()
            .replace(mob.getPlaceholders())
            .writeMeta();
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull WaveMob mob) {
        return (viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                mob.setIcon(cursor);
                event.getView().setCursor(null);
                this.object.getArenaConfig().getWaveManager().save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }

            Player player = viewer.getPlayer();

            if (event.isShiftClick() && event.isRightClick()) {
                this.object.getMobs().remove(mob);
                this.object.getArenaConfig().getWaveManager().save();
                this.openNextTick(viewer, viewer.getPage());
                return;
            }

            if (event.isShiftClick()) {
                if (event.isLeftClick()) {
                    this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, wrapper -> {
                        mob.setLevel(wrapper.asInt(1));
                        this.object.getArenaConfig().getWaveManager().save();
                        return true;
                    });
                }
            }
            else {
                if (event.isLeftClick()) {
                    EditorManager.suggestValues(player, mob.getProvider().getMobNames(), true);
                    this.handleInput(viewer, Lang.EDITOR_ARENA_WAVES_ENTER_MOB_ID, wrapper -> {
                        if (!mob.getProvider().getMobNames().contains(wrapper.getTextRaw())) {
                            EditorManager.error(player, plugin.getMessage(Lang.EDITOR_ARENA_WAVES_ERROR_MOB_INVALID).getLocalized());
                            return false;
                        }
                        mob.setMobId(wrapper.getTextRaw());
                        this.object.getArenaConfig().getWaveManager().save();
                        return true;
                    });
                }
                else if (event.isRightClick()) {
                    this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, wrapper -> {
                        mob.setAmount(wrapper.asInt(1));
                        this.object.getArenaConfig().getWaveManager().save();
                        return true;
                    });
                }
                else if (event.getClick() == ClickType.DROP) {
                    this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_PERCENT, wrapper -> {
                        mob.setChance(wrapper.asAnyDouble(0D));
                        this.object.getArenaConfig().getWaveManager().save();
                        return true;
                    });
                }
                else if (event.getClick() == ClickType.SWAP_OFFHAND) {
                    List<MobProvider> providers = new ArrayList<>(PluginMobProvider.getProviders());
                    mob.setProvider(CollectionsUtil.shifted(providers, providers.indexOf(mob.getProvider()), 1));
                    this.object.getArenaConfig().getWaveManager().save();
                    this.openNextTick(viewer, viewer.getPage());
                    return;
                }
                else return;
            }
            player.closeInventory();
        };
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);

        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}
