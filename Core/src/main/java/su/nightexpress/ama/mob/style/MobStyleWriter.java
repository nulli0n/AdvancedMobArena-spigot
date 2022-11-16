package su.nightexpress.ama.mob.style;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.utils.CollectionsUtil;

public interface MobStyleWriter<E, T> {

    @Nullable T parse(@NotNull String value);

    @NotNull T[] values();

    void apply(@NotNull E entity, @NotNull T value);

    boolean alreadyHas(@NotNull E entity, @NotNull T value);

    interface WriterBoolean<E> extends MobStyleWriter<E, Boolean> {

        @Nullable
        @Override
        default Boolean parse(@NotNull String value) {
            return Boolean.parseBoolean(value);
        }

        @NotNull
        @Override
        default Boolean[] values() {
            return new Boolean[]{true,false};
        }
    }

    interface WriterEnum<E, T extends Enum<T>> extends MobStyleWriter<E, T> {

        @NotNull Class<T> getEnumClass();

        @Nullable
        @Override
        default T parse(@NotNull String value) {
            return CollectionsUtil.getEnum(value, this.getEnumClass());
        }

        @NotNull
        @Override
        default T[] values() {
            return this.getEnumClass().getEnumConstants();
        }
    }
}
