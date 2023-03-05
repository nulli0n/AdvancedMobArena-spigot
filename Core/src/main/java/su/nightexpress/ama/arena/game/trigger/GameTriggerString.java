package su.nightexpress.ama.arena.game.trigger;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.arena.game.ArenaGameEventTrigger;
import su.nightexpress.ama.api.arena.game.ArenaGameTriggerValue;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.event.*;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.game.trigger.value.TriggerValueString;

public class GameTriggerString extends ArenaGameEventTrigger<String> {

    public GameTriggerString(@NotNull ArenaGameEventType eventType, @NotNull String inputs) {
        super(eventType, inputs);
    }

    @NotNull
    @Override
    protected ArenaGameTriggerValue<String> loadValue(@NotNull String input) {
        return new TriggerValueString(input);
    }

    /*@Override
    @NotNull
    public String formatValue(@NotNull ArenaGameTriggerValue<String> triggerValue) {
        AMA plugin = this.arenaConfig.plugin();
        String value = triggerValue.getValue();
        if (value.equalsIgnoreCase(Placeholders.MASK_ANY)) {
            return plugin.getMessage(Lang.OTHER_ANY).getLocalized();
        }

        return switch (this.getType()) {
            case MOB_KILLED -> {
                ArenaCustomMob customMob = plugin.getMobManager().getMobById(value);
                if (customMob != null) yield customMob.getName();
                if (Hooks.hasMythicMobs()) yield MythicMobsHook.getMobDisplayName(value);

                yield value;
            }
            case SPOT_CHANGED -> {
                ArenaSpot spot = arenaConfig.getSpotManager().getSpot(value);
                yield spot != null ? spot.getName() : value;
            }
            case REGION_LOCKED, REGION_UNLOCKED -> {
                ArenaRegion region = arenaConfig.getRegionManager().getRegion(value);
                yield region != null ? region.getName() : value;
            }
            case SHOP_ITEM_LOCKED, SHOP_ITEM_UNLOCKED -> {
                ArenaShopProduct shopItem = arenaConfig.getShopManager().getCategories().stream()
                    .filter(category -> category.getProduct(value) != null).findFirst()
                    .map(category -> category.getProduct(value)).orElse(null);
                yield shopItem != null ? ItemUtil.getItemName(shopItem.getPreview()) : value;
            }
            case SHOP_CATEGORY_LOCKED, SHOP_CATEGORY_UNLOCKED -> {
                ArenaShopCategory category = arenaConfig.getShopManager().getCategory(value);
                yield category != null ? category.getName() : value;
            }
            default -> value;
        };
    }*/

    @Override
    @NotNull
    public String getEventRawValue(@NotNull ArenaGameGenericEvent event) {
        Arena arena = event.getArena();
        return switch (this.getType()) {
            case MOB_KILLED -> {
                ArenaMobDeathEvent deathEvent = (ArenaMobDeathEvent) event;
                yield deathEvent.getMobId();
            }
            case PLAYER_DEATH, PLAYER_LEAVE -> {
                ArenaPlayerGameEvent playerGameEvent = (ArenaPlayerGameEvent) event;
                yield playerGameEvent.getArenaPlayer().getPlayer().getName();
            }
            case PLAYER_JOIN -> {
                ArenaPlayerJoinEvent playerJoinEvent = (ArenaPlayerJoinEvent) event;
                yield playerJoinEvent.getPlayer().getName();
            }
            case SPOT_CHANGED -> {
                ArenaSpotStateChangeEvent spotEvent = (ArenaSpotStateChangeEvent) event;
                yield spotEvent.getSpot().getId();
            }
            case REGION_LOCKED, REGION_UNLOCKED -> {
                ArenaRegionEvent regionEvent = (ArenaRegionEvent) event;
                yield regionEvent.getArenaRegion().getId();
            }
            case SHOP_CATEGORY_LOCKED, SHOP_CATEGORY_UNLOCKED -> {
                ArenaShopCategoryEvent categoryEvent = (ArenaShopCategoryEvent) event;
                yield categoryEvent.getShopCategory().getId();
            }
            case SHOP_ITEM_LOCKED, SHOP_ITEM_UNLOCKED -> {
                ArenaShopProductEvent shopEvent = (ArenaShopProductEvent) event;
                yield shopEvent.getShopProduct().getId();
            }
            default -> "";
        };
    }
}
