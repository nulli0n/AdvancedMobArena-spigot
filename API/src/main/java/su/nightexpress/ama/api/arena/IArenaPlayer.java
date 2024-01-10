package su.nightexpress.ama.api.arena;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.api.type.PlayerType;

public interface IArenaPlayer {

    boolean leaveArena();

    void tick();

    void onDeath();

    void revive();

    boolean isAutoRevive();

    boolean isOutOfLifes();

    @NotNull IArena getArena();

    @NotNull GameState getState();

    void setState(@NotNull GameState state);

    @NotNull PlayerType getType();

    void setType(@NotNull PlayerType type);

    default boolean isGhost() {
        return this.getType() == PlayerType.GHOST;
    }

    default boolean isReal() {
        return this.getType() == PlayerType.REAL;
    }

    @NotNull Player getPlayer();

    int getReviveTime();

    void setReviveTime(int reviveTime);

    int getLifes();

    void setLifes(int lifes);

    void takeLive();

    boolean isDead();

    void setDead(boolean dead);

    boolean isSpectator();

    void setSpectator(boolean spectator);

    //boolean isGhost();

    //boolean isReal();

    //void setGhost();

    //void setReal();

    boolean isTransfer();

    void setTransfer(boolean transfer);

    boolean isReady();

    boolean isInGame();

    void addBoard();

    void removeBoard();

    int getScore();

    void setScore(int score);

    void gainScore(int amount);

    void addScore(int amount);

    void removeScore(int amount);

    int getKillStreak();

    void setKillStreak(int killStreak);

    long getKillStreakDecay();

    void setKillStreakDecay(long killStrakDecay);




    void saveStats();
}
