package su.nightexpress.ama.hook.external;

import com.elmakers.mine.bukkit.api.block.MaterialAndData;
import com.elmakers.mine.bukkit.api.economy.Currency;
import com.elmakers.mine.bukkit.api.event.PreLoadEvent;
import com.elmakers.mine.bukkit.api.magic.CasterProperties;
import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.magic.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.ama.api.ArenaAPI;
import su.nightexpress.ama.arena.ArenaPlayer;

@Deprecated
public class MagicHK {

    public boolean setup() {
        // TODO this.registerListeners();

        return true;
    }

    public void shutdown() {
        // TODO this.unregisterListeners();
    }

    @EventHandler
    public void onMagicPreLoad(PreLoadEvent e) {
        e.registerCurrency(new ArenaCurrency());
        e.registerCurrency(new ArenaCurrencyScore());
    }

    class ArenaCurrency implements Currency {

        public ArenaCurrency() {

        }

        @Override
        public void deduct(Mage m, CasterProperties caster, double amount) {
            ArenaAPI.getUserManager().getUserData(m.getPlayer()).takeCoins((int) amount);
        }

        @Override
        public String formatAmount(double amount, Messages messages) {
            return NumberUtil.format(amount);
        }

        @Override
        public double getBalance(Mage mage, CasterProperties caster) {
            Player p = mage.getPlayer();
            return ArenaAPI.getUserManager().getUserData(p).getCoins();
        }

        @Override
        public double getDefaultValue() {
            return 0;
        }

        @Override
        public MaterialAndData getIcon() {
            return null;
        }

        @Override
        public String getKey() {
            return "ama_coins";
        }

        @Override
        public double getMaxValue() {
            return 0;
        }

        @Override
        public String getName(Messages messages) {
            return "Coins";
        }

        @Override
        public double getWorth() {
            return 1;
        }

        @Override
        public boolean give(Mage mage, CasterProperties caster, double amount) {
            ArenaAPI.getUserManager().getUserData(mage.getPlayer()).addCoins((int) amount);
            return true;
        }

        @Override
        public boolean has(Mage mage, CasterProperties caster, double amount) {
            if (!ArenaPlayer.isPlaying(mage.getPlayer())) {
                return false;
            }
            return ArenaAPI.getUserManager().getUserData(mage.getPlayer()).getCoins() >= (int) amount;
        }

        @Override
        public boolean hasMaxValue() {
            return false;
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }

    class ArenaCurrencyScore implements Currency {

        public ArenaCurrencyScore() {

        }

        @Override
        public void deduct(Mage m, CasterProperties caster, double amount) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(m.getPlayer());
            if (arenaPlayer == null) return;

            arenaPlayer.addScore((int) -amount);
        }

        @Override
        public String formatAmount(double amount, Messages messages) {
            return NumberUtil.format(amount);
        }

        @Override
        public double getBalance(Mage mage, CasterProperties caster) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(mage.getPlayer());
            if (arenaPlayer == null) return 0;

            return arenaPlayer.getScore();
        }

        @Override
        public double getDefaultValue() {
            return 0;
        }

        @Override
        public MaterialAndData getIcon() {
            return null;
        }

        @Override
        public String getKey() {
            return "ama_score";
        }

        @Override
        public double getMaxValue() {
            return 0;
        }

        @Override
        public String getName(Messages messages) {
            return "Score";
        }

        @Override
        public double getWorth() {
            return 1;
        }

        @Override
        public boolean give(Mage mage, CasterProperties caster, double amount) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(mage.getPlayer());
            if (arenaPlayer == null) return false;

            arenaPlayer.addScore((int) amount);
            return true;
        }

        @Override
        public boolean has(Mage mage, CasterProperties caster, double amount) {
            ArenaPlayer arenaPlayer = ArenaPlayer.getPlayer(mage.getPlayer());
            if (arenaPlayer == null) return false;

            return arenaPlayer.getScore() >= (int) amount;
        }

        @Override
        public boolean hasMaxValue() {
            return false;
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }
}
