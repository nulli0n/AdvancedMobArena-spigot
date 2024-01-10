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
    private static final SQLColumn COLUMN_ARENA_COOLDOWN = SQLColumn.of("arenaCooldown", ColumnType.STRING);

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
                Map<String, Long> arenaCooldownMap = this.gson.fromJson(resultSet.getString(COLUMN_ARENA_COOLDOWN.getName()), new TypeToken<Map<String, Long>>(){}.getType());
                if (arenaCooldownMap == null) arenaCooldownMap = new HashMap<>();

                return new ArenaUser(plugin, uuid, name, dateCreated, lastOnline, coins, kits, stats, arenaCooldownMap);
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
        this.plugin.getUserManager().getUsersLoaded().forEach(user -> {
            ArenaUser fetch = this.getUser(user.getId());
            if (fetch == null) return;

            fetch.getArenaCooldownMap().clear();
            fetch.getArenaCooldownMap().putAll(user.getArenaCooldownMap());
        });
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        this.addColumn(this.tableUsers, COLUMN_ARENA_COOLDOWN.toValue("{}"));
    }

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Arrays.asList(COLUMN_COINS, COLUMN_KITS, COLUMN_STATS, COLUMN_ARENA_COOLDOWN);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull ArenaUser user) {
        return Arrays.asList(
            COLUMN_COINS.toValue(user.getCoins()),
            COLUMN_KITS.toValue(this.gson.toJson(user.getKits())),
            COLUMN_STATS.toValue(this.gson.toJson(user.getStats())),
            COLUMN_ARENA_COOLDOWN.toValue(this.gson.toJson(user.getArenaCooldownMap()))
        );
    }

    @Override
    @NotNull
    protected Function<ResultSet, ArenaUser> getFunctionToUser() {
        return this.userFunction;
    }
}
