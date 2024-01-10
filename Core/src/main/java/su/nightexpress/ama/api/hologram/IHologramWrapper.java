package su.nightexpress.ama.api.hologram;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record IHologramWrapper<E>(IHologramHandler<E> handler, E hologram, long deathTime) {

    public IHologramWrapper(@NotNull IHologramHandler<E> handler, @NotNull E hologram, int lifetime) {
        this(handler, hologram, lifetime <= 0 ? -1 : System.currentTimeMillis() + lifetime * 1000L);
    }

    public IHologramWrapper(@NotNull IHologramHandler<E> handler, @NotNull E hologram, long deathTime) {
        this.handler = handler;
        this.hologram = hologram;
        this.deathTime = deathTime;
    }

    public void delete() {
        this.handler().delete(this);
    }

    public void setText(@NotNull List<String> text) {
        this.handler().setText(this, text);
    }

    public void setClick(@NotNull IHologramClick click) {
        this.handler().setClick(this, click);
    }

    public boolean isDead() {
        return this.deathTime() > 0 && System.currentTimeMillis() >= this.deathTime();
    }
}
