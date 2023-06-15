package su.nightexpress.ama.arena.editor.wave;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.wave.ArenaWave;
import su.nightexpress.ama.arena.wave.ArenaWaveMob;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.hook.mob.MobProvider;
import su.nightexpress.ama.hook.mob.PluginMobProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class WaveMobsEditor extends EditorMenu<AMA, ArenaWave> implements AutoPaged<ArenaWaveMob> {

    public WaveMobsEditor(@NotNull ArenaWave wave) {
        super(wave.getArena().plugin(), wave, EditorHub.TITLE_WAVE_EDITOR, 45);

        this.addReturn(39).setClick((viewer, event) -> {
            wave.getArenaConfig().getWaveManager().getEditor().getListEditor().openNextTick(viewer, 1);
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.WAVES_WAVE_MOB_CREATE, 41).setClick((viewer, event) -> {
            MobProvider provider = PluginMobProvider.getProviders().stream().findFirst().orElseThrow();
            ArenaWaveMob mob = new ArenaWaveMob(wave, provider, "null", 1, 1, 100D);
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
    public List<ArenaWaveMob> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getMobs());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull ArenaWaveMob waveMob) {
        ItemStack item = new ItemStack(Material.ZOMBIE_HEAD);
        ItemUtil.mapMeta(item, meta -> {
            meta.setDisplayName(EditorLocales.WAVES_WAVE_MOB_OBJECT.getLocalizedName());
            meta.setLore(EditorLocales.WAVES_WAVE_MOB_OBJECT.getLocalizedLore());
            meta.addItemFlags(ItemFlag.values());
            ItemUtil.replace(meta, waveMob.replacePlaceholders());
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull ArenaWaveMob waveMob) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();

            if (event.isShiftClick() && event.isRightClick()) {
                this.object.getMobs().remove(waveMob);
                this.object.getArenaConfig().getWaveManager().save();
                this.openNextTick(player, viewer.getPage());
                return;
            }

            if (event.isShiftClick()) {
                if (event.isLeftClick()) {
                    this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, wrapper -> {
                        waveMob.setLevel(wrapper.asInt(1));
                        this.object.getArenaConfig().getWaveManager().save();
                        return true;
                    });
                }
            }
            else {
                if (event.isLeftClick()) {
                    EditorManager.suggestValues(player, waveMob.getProvider().getMobNames(), true);
                    this.handleInput(viewer, Lang.EDITOR_ARENA_WAVES_ENTER_MOB_ID, wrapper -> {
                        if (!waveMob.getProvider().getMobNames().contains(wrapper.getTextRaw())) {
                            EditorManager.error(player, plugin.getMessage(Lang.EDITOR_ARENA_WAVES_ERROR_MOB_INVALID).getLocalized());
                            return false;
                        }
                        waveMob.setMobId(wrapper.getTextRaw());
                        this.object.getArenaConfig().getWaveManager().save();
                        return true;
                    });
                }
                else if (event.isRightClick()) {
                    this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_NUMBER, wrapper -> {
                        waveMob.setAmount(wrapper.asInt(1));
                        this.object.getArenaConfig().getWaveManager().save();
                        return true;
                    });
                }
                else if (event.getClick() == ClickType.DROP) {
                    this.handleInput(viewer, Lang.EDITOR_GENERIC_ENTER_PERCENT, wrapper -> {
                        waveMob.setChance(wrapper.asAnyDouble(0D));
                        this.object.getArenaConfig().getWaveManager().save();
                        return true;
                    });
                }
                else if (event.getClick() == ClickType.SWAP_OFFHAND) {
                    List<MobProvider> providers = new ArrayList<>(PluginMobProvider.getProviders());
                    waveMob.setProvider(CollectionsUtil.shifted(providers, providers.indexOf(waveMob.getProvider()), 1));
                    this.object.getArenaConfig().getWaveManager().save();
                    this.openNextTick(viewer, viewer.getPage());
                    return;
                }
                else return;
            }
            player.closeInventory();
        };
    }
}
