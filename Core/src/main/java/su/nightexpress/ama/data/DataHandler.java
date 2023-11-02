package su.nightexpress.ama.data;

import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserDataHandler;
import su.nexmedia.engine.api.data.sql.SQLColumn;
import su.nexmedia.engine.api.data.sql.SQLValue;
import su.nexmedia.engine.api.data.sql.column.ColumnType;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.stats.object.StatType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataHandler<AMA, ArenaUser> {

    private static final SQLColumn COLUMN_COINS = SQLColumn.of("coins", ColumnType.INTEGER);
    private static final SQLColumn COLUMN_KITS  = SQLColumn.of("kits", ColumnType.STRING);
    private static final SQLColumn COLUMN_STATS = SQLColumn.of("stats", ColumnType.STRING);

    private static DataHandler instance;

    private final Function<ResultSet, ArenaUser> userFunction;

    protected DataHandler(@NotNull AMA plugin) {
        super(plugin, plugin);

        this.userFunction = (resultSet) -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                int coins = resultSet.getInt(COLUMN_COINS.getName());
                Set<String> kits = gson.fromJson(resultSet.getString(COLUMN_KITS.getName()), new TypeToken<Set<String>>() {}.getType());
                Map<String, Map<StatType, Integer>> stats = gson.fromJson(resultSet.getString(COLUMN_STATS.getName()), new TypeToken<Map<String, Map<StatType, Integer>>>() {}.getType());

                return new ArenaUser(plugin, uuid, name, dateCreated, lastOnline, coins, kits, stats);
            }
            catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        };
    }

    @NotNull
    public static synchronized DataHandler getInstance(@NotNull AMA plugin) {
        if (instance == null) {
            instance = new DataHandler(plugin);
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
    protected List<SQLColumn> getExtraColumns() {
        return Arrays.asList(COLUMN_COINS, COLUMN_KITS, COLUMN_STATS);
    }

    @Override
    protected @NotNull List<SQLValue> getSaveColumns(@NotNull ArenaUser user) {
        return Arrays.asList(
            COLUMN_COINS.toValue(user.getCoins()),
            COLUMN_KITS.toValue(this.gson.toJson(user.getKits())),
            COLUMN_STATS.toValue(this.gson.toJson(user.getStats()))
        );
    }

    /*@Override
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
    }*/

    @Override
    @NotNull
    protected Function<ResultSet, ArenaUser> getFunctionToUser() {
        return this.userFunction;
    }
}
