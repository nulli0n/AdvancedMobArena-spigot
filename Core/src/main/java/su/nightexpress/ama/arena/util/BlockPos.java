package su.nightexpress.ama.arena.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.StringUtil;

public class BlockPos {

    private final double x,y,z;
    private final float pitch, yaw;

    public BlockPos(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public BlockPos(double x, double y, double z, float pitch, float yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    @NotNull
    public static BlockPos empty() {
        return new BlockPos(0, 0, 0);
    }

    @NotNull
    public static BlockPos from(@NotNull Location location) {
        return new BlockPos(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    @NotNull
    public static BlockPos read(@NotNull JYML cfg, @NotNull String path) {
        String str = cfg.getString(path, "");
        return deserialize(str);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path, this.serialize());
    }

    @NotNull
    public static BlockPos deserialize(@NotNull String str) {
        String[] split = str.split(",");
        if (split.length < 5) return new BlockPos(0, 0, 0);

        double x = StringUtil.getDouble(split[0], 0D, true);
        double y = StringUtil.getDouble(split[1], 0D, true);
        double z = StringUtil.getDouble(split[2], 0D, true);
        float pitch = (float) StringUtil.getDouble(split[3], 0D, true);
        float yaw = (float) StringUtil.getDouble(split[4], 0D, true);

        return new BlockPos(x, y, z, pitch, yaw);
    }

    @NotNull
    public String serialize() {
        return this.getX() + "," + this.getY() + "," + this.getZ() + "," + this.getPitch() + "," + this.getYaw();
    }

    @NotNull
    public Location toLocation(@NotNull World world) {
        return new Location(world, this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
    }

    @NotNull
    public BlockPos copy() {
        return new BlockPos(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
    }

    public boolean isEmpty() {
        return this.getX() == 0D && this.getY() == 0D && this.getZ() == 0D;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof BlockPos other)) return false;

        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
            return false;
        }
        if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)) {
            return false;
        }
        return Float.floatToIntBits(this.yaw) == Float.floatToIntBits(other.yaw);
    }

    public int hashCode() {
        int hash = 3;
        hash = 19 * hash;
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash = 19 * hash + (int)(Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        hash = 19 * hash + Float.floatToIntBits(this.pitch);
        hash = 19 * hash + Float.floatToIntBits(this.yaw);
        return hash;
    }

    @Override
    public String toString() {
        return "BlockPos{" +
            "x=" + x +
            ", y=" + y +
            ", z=" + z +
            ", pitch=" + pitch +
            ", yaw=" + yaw +
            '}';
    }
}
