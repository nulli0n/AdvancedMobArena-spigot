package su.nightexpress.ama.data;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserDataHandler;
import su.nexmedia.engine.api.data.DataTypes;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.stats.object.StatType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public class ArenaUserData extends AbstractUserDataHandler<AMA, ArenaUser> {

    private static ArenaUserData instance;

    private final Function<ResultSet, ArenaUser> FUNC_USER;

    protected ArenaUserData(@NotNull AMA plugin) {
        super(plugin, plugin);

        FUNC_USER = (resultSet) -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COL_USER_UUID));
                String name = resultSet.getString(COL_USER_NAME);
                long dateCreated = resultSet.getLong(COL_USER_DATE_CREATED);
                long lastOnline = resultSet.getLong(COL_USER_LAST_ONLINE);

                int coins = resultSet.getInt("coins");
                Set<String> kits = gson.fromJson(resultSet.getString("kits"), new TypeToken<Set<String>>() {
                }.getType());
                Map<String, Map<StatType, Integer>> stats = gson.fromJson(resultSet.getString("stats"), new TypeToken<Map<String, Map<StatType, Integer>>>() {
                }.getType());

                return new ArenaUser(plugin, uuid, name, dateCreated, lastOnline, coins, kits, stats);
            }
            catch (SQLException ex) {
                return null;
            }
        };
    }

    @NotNull
    public static synchronized ArenaUserData getInstance(@NotNull AMA plugin) throws SQLException {
        if (instance == null) {
            instance = new ArenaUserData(plugin);
        }
        return instance;
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
        instance = null;
    }

    @Override
    public void onSynchronize() {

    }

    @Override
    @NotNull
    protected LinkedHashMap<String, String> getColumnsToCreate() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("coins", DataTypes.INTEGER.build(this.getDataType(), 11));
        map.put("kits", DataTypes.STRING.build(this.getDataType()));
        map.put("stats", DataTypes.STRING.build(this.getDataType()));
        return map;
    }

    @Override
    @NotNull
    protected LinkedHashMap<String, String> getColumnsToSave(@NotNull ArenaUser user) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("coins", String.valueOf(user.getCoins()));
        map.put("kits", this.gson.toJson(user.getKits()));
        map.put("stats", this.gson.toJson(user.getStats()));
        return map;
    }

    @Override
    @NotNull
    protected Function<ResultSet, ArenaUser> getFunctionToUser() {
        return this.FUNC_USER;
    }
}
