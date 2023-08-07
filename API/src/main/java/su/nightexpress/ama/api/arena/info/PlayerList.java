package su.nightexpress.ama.api.arena.info;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.random.Rnd;
import su.nightexpress.ama.api.arena.IArenaPlayer;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.api.type.PlayerType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerList<P extends IArenaPlayer> {

    private final Set<P> players;

    public PlayerList() {
        this.players = new HashSet<>();
    }

    public boolean hasAlive() {
        return !this.getAlive().isEmpty();
    }

    public boolean add(@NotNull P arenaPlayer) {
        return this.players.add(arenaPlayer);
    }

    public boolean remove(@NotNull P arenaPlayer) {
        return this.players.remove(arenaPlayer);
    }

    @NotNull
    public Set<P> select(@NotNull ArenaTargetType targetType) {
        if (targetType == ArenaTargetType.PLAYER_ALL) return this.select(GameState.INGAME);
        if (targetType == ArenaTargetType.PLAYER_RANDOM && !this.select(GameState.INGAME).isEmpty())
            return Stream.of(Rnd.get(this.select(GameState.INGAME))).collect(Collectors.toSet());
        return Collections.emptySet();
    }

    @NotNull
    public Set<P> select(@NotNull ArenaTargetType targetType, @NotNull PlayerType playerType) {
        if (targetType == ArenaTargetType.PLAYER_ALL) return this.select(GameState.INGAME, playerType);
        if (targetType == ArenaTargetType.PLAYER_RANDOM && !this.select(GameState.INGAME, playerType).isEmpty())
            return Stream.of(Rnd.get(this.select(GameState.INGAME, playerType))).collect(Collectors.toSet());
        return Collections.emptySet();
    }

    @NotNull
    public Set<P> all() {
        return new HashSet<>(this.players);
    }

    @NotNull
    public Set<P> select(@NotNull PlayerType playerType) {
        return players.stream().filter(arenaPlayer -> arenaPlayer.getType() == playerType).collect(Collectors.toSet());
    }

    @NotNull
    public Set<P> select(@NotNull GameState state) {
        return this.all().stream().filter(arenaPlayer -> arenaPlayer.getState() == state).collect(Collectors.toSet());
    }

    @NotNull
    public Set<P> select(@NotNull GameState state, @NotNull PlayerType playerType) {
        return this.select(playerType).stream().filter(arenaPlayer -> arenaPlayer.getState() == state).collect(Collectors.toSet());
    }

    @NotNull
    public Set<P> getDead() {
        return this.select(GameState.INGAME, PlayerType.REAL).stream().filter(P::isDead).collect(Collectors.toSet());
    }

    @NotNull
    public Set<P> getAlive() {
        return this.select(GameState.INGAME, PlayerType.REAL).stream().filter(Predicate.not(P::isDead)).collect(Collectors.toSet());
    }

    @Nullable
    public P getRandom() {
        Set<P> alive = this.getAlive();
        return alive.isEmpty() ? null : Rnd.get(new ArrayList<>(alive));
    }
}
