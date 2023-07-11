package su.nightexpress.ama.stats.object;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.ArenaAPI;

import java.util.function.UnaryOperator;

public class StatsScore implements Placeholder {

    public static final String NO_NAME = "<?>";

    private final String   name;
    private final StatType statType;
    private final int      score;
    private final String   arenaId;
    private final PlaceholderMap placeholderMap;

    public StatsScore(@NotNull String name, @NotNull StatType statType, int score, @NotNull String arenaId) {
        this.name = name;
        this.statType = statType;
        this.score = score;
        this.arenaId = arenaId;

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.STATS_SCORE_AMOUNT, () -> String.valueOf(this.getScore()))
            .add(Placeholders.STATS_SCORE_NAME, this::getName)
            .add(Placeholders.STATS_SCORE_TYPE, () -> ArenaAPI.PLUGIN.getLangManager().getEnum(this.getStatType()))
        ;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders(int pos) {
        return str -> this.replacePlaceholders().apply(str
            .replace(Placeholders.STATS_SCORE_POSITION, String.valueOf(pos))
        );
    }

    @NotNull
    public static StatsScore empty(@NotNull StatType statType) {
        return new StatsScore(NO_NAME, statType, 0, "");
    }

    public boolean isEmpty() {
        return this.getName().equalsIgnoreCase(NO_NAME);
    }

    @NotNull
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @NotNull
    public StatType getStatType() {
        return statType;
    }

    @NotNull
    public String getArenaId() {
        return arenaId;
    }

    @Override
    public String toString() {
        return "StatsScore{" +
            "name='" + name + '\'' +
            ", statType=" + statType +
            ", score=" + score +
            ", arenaId='" + arenaId + '\'' +
            '}';
    }
}
