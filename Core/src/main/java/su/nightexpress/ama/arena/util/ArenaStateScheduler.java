package su.nightexpress.ama.arena.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.config.Lang;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ArenaStateScheduler {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    private final ArenaConfig                    arenaConfig;
    private final Map<DayOfWeek, Set<LocalTime>> times;
    private final boolean                        state;

    private ScheduledFuture<?> updateTask;

    public ArenaStateScheduler(@NotNull ArenaConfig arenaConfig, @NotNull Map<DayOfWeek, Set<LocalTime>> times, boolean state) {
        this.arenaConfig = arenaConfig;
        this.times = times;
        this.state = state;
    }

    @NotNull
    public Map<DayOfWeek, Set<LocalTime>> getTimes() {
        return times;
    }

    public boolean canSchedule() {
        return this.updateTask == null || this.updateTask.isDone();
    }

    public void startScheduler() {
        this.updateTask = this.createScheduler();
    }

    public void stopScheduler() {
        if (this.updateTask != null) {
            this.updateTask.cancel(true);
        }
    }

    @NotNull
    private Runnable getCommand() {
        return () -> {
            this.arenaConfig.setActive(this.state);
            this.arenaConfig.save();
            this.arenaConfig.plugin().getMessage(this.state ? Lang.ARENA_SCHEDULER_OPEN_ANNOUNCE : Lang.ARENA_SCHEDULER_CLOSE_ANNOUNCE)
                .replace(this.arenaConfig.replacePlaceholders())
                .broadcast();
        };
    }

    @Nullable
    private ScheduledFuture<?> createScheduler() {
        LocalDateTime updateTime = this.getClosestDate();
        if (updateTime == null || !this.canSchedule()) return null;

        long delay = LocalDateTime.now().until(updateTime, ChronoUnit.MILLIS);

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = service.schedule(this.getCommand(), delay, TimeUnit.MILLISECONDS);
        service.schedule(this::startScheduler, delay + 1000L, TimeUnit.MILLISECONDS);
        service.shutdown();
        return future;
    }

    @Nullable
    private LocalDateTime getClosestDate() {
        if (this.getTimes().isEmpty()) return null;

        LocalDateTime closest = LocalDateTime.now();
        while (!this.getTimes().containsKey(closest.getDayOfWeek())) {
            closest = closest.plusDays(1);
        }

        Set<LocalTime> times = this.getTimes().get(closest.getDayOfWeek());
        if (times == null || times.isEmpty()) return null;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime closest1 = closest;
        return times.stream()
            .map(timeSch -> LocalDateTime.of(closest1.toLocalDate(), timeSch))
            .filter(now::isBefore).min(LocalDateTime::compareTo).orElse(null);
    }
}
