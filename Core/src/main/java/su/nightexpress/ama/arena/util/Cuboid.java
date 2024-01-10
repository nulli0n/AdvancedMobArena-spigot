package su.nightexpress.ama.arena.util;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class Cuboid {

    private final BlockPos min;
    private final BlockPos max;
    private final BlockPos center;

    public Cuboid(@NotNull BlockPos min, @NotNull BlockPos max) {
        int minX = (int) Math.min(min.getX(), max.getX());
        int minY = (int) Math.min(min.getY(), max.getY());
        int minZ = (int) Math.min(min.getZ(), max.getZ());

        int maxX = (int) Math.max(min.getX(), max.getX());
        int maxY = (int) Math.max(min.getY(), max.getY());
        int maxZ = (int) Math.max(min.getZ(), max.getZ());

        this.min = new BlockPos(minX, minY, minZ);
        this.max = new BlockPos(maxX, maxY, maxZ);

        double cx = minX + (maxX - minX) / 2D;
        double cy = minY + (maxY - minY) / 2D;
        double cz = minZ + (maxZ - minZ) / 2D;

        this.center = new BlockPos(cx, cy, cz);
    }

    public boolean isEmpty() {
        return this.getMin().isEmpty() && this.getMax().isEmpty();
    }

    public boolean contains(@NotNull Location location) {
        return this.contains(BlockPos.from(location));
    }

    public boolean contains(@NotNull BlockPos pos) {
        int x = (int) pos.getX();
        int y = (int) pos.getY();
        int z = (int) pos.getZ();

        return x >= (int) min.getX() && x <= (int) max.getX() &&
            y >= (int) min.getY() && y <= (int) max.getY() &&
            z >= (int) min.getZ() && z <= (int) max.getZ();
    }

    @NotNull
    public BlockPos getMin() {
        return this.min;
    }

    @NotNull
    public BlockPos getMax() {
        return this.max;
    }

    @NotNull
    public BlockPos getCenter() {
        return this.center;
    }
}
