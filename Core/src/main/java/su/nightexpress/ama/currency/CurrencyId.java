package su.nightexpress.ama.currency;

import org.jetbrains.annotations.NotNull;

public class CurrencyId {

    public static final String COINS         = "coins";
    public static final String VAULT         = "vault";
    public static final String PLAYER_POINTS = "player_points";
    public static final String GAME_POINTS   = "game_points";

    @NotNull
    public static String[] values() {
        return new String[]{COINS, VAULT, PLAYER_POINTS, GAME_POINTS};
    }
}
