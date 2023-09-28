package su.nightexpress.ama.arena.util;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.values.UniParticle;

import java.util.ArrayList;
import java.util.List;

public class ArenaCuboid {

    private final Location min;
    private final Location max;
    private final Location   center;
    private final Visualizer visualizer;

    public ArenaCuboid(@NotNull Location loc1, @NotNull Location loc2) {
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        this.min = new Location(loc1.getWorld(), minX, minY, minZ);
        this.max = new Location(loc1.getWorld(), maxX, maxY, maxZ);

        double cx = minX + (maxX - minX) / 2D;
        double cy = minY + (maxY - minY) / 2D;
        double cz = minZ + (maxZ - minZ) / 2D;

        this.center = new Location(loc1.getWorld(), cx, cy, cz);
        this.visualizer = new Visualizer(this.min, this.max);
    }

    @NotNull
    public Visualizer getVisualizer() {
        return visualizer;
    }

    public boolean contains(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null || !world.equals(this.min.getWorld())) return false;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return x >= min.getBlockX() && x <= max.getBlockX() &&
            y >= min.getBlockY() && y <= max.getBlockY() &&
            z >= min.getBlockZ() && z <= max.getBlockZ();
    }

    @NotNull
    public List<Block> getBlocks() {
        List<Block> list = new ArrayList<>(this.getSize());
        World world = this.center.getWorld();
        if (world == null) return list;

        for (int x = this.min.getBlockX(); x <= this.max.getBlockX(); ++x) {
            for (int y = this.min.getBlockY(); y <= this.max.getBlockY(); ++y) {
                for (int z = this.min.getBlockZ(); z <= this.max.getBlockZ(); ++z) {
                    Block blockAt = world.getBlockAt(x, y, z);
                    list.add(blockAt);
                }
            }
        }

        return list;
    }

    public int getSize() {
        int dx = max.getBlockX() - min.getBlockX() + 1;
        int dy = max.getBlockY() - min.getBlockY() + 1;
        int dz = max.getBlockZ() - min.getBlockZ() + 1;
        return dx * dy * dz;
    }

    @NotNull
    public Location getMin() {
        return this.min;
    }

    @NotNull
    public Location getMax() {
        return this.max;
    }

    @NotNull
    public Location getCenter() {
        return this.center;
    }

    public static class Visualizer {

        private final ArrayList<Vector[]> sides;
        private final Location            min;

        public Visualizer(@NotNull Location min, @NotNull Location max) {
            this.sides = new ArrayList<>();
            this.min = min;

            double length = max.getBlockX() - min.getBlockX() + 1D;
            double height = max.getBlockY() - min.getBlockY() + 1D;
            double width = max.getBlockZ() - min.getBlockZ() + 1D;

            Vector A = new Vector(0, 0, 0);
            Vector B = new Vector(length, 0, 0);
            Vector C = new Vector(0, 0, width);
            Vector D = new Vector(0, height, 0);
            Vector E = new Vector(0, height, width);
            Vector F = new Vector(length, height, 0);
            Vector G = new Vector(length, 0, width);
            sides.add(new Vector[]{A, B});
            sides.add(new Vector[]{A, C});
            sides.add(new Vector[]{A, D});
            sides.add(new Vector[]{C, D});
            sides.add(new Vector[]{B, D});
            sides.add(new Vector[]{C, B});
            sides.add(new Vector[]{B, C});
            sides.add(new Vector[]{D, C});
            sides.add(new Vector[]{D, B});
            sides.add(new Vector[]{E, B});
            sides.add(new Vector[]{F, C});
            sides.add(new Vector[]{G, D});
        }

        @NotNull
        public Vector getPostion(double blocksAway, @NotNull Vector origin, @NotNull Vector direction) {
            return origin.clone().add(direction.clone().normalize().multiply(blocksAway));
        }

        @NotNull
        public ArrayList<Vector> traverse(@NotNull Vector origin, @NotNull Vector direction) {
            ArrayList<Vector> positions = new ArrayList<>();
            for (double add = 0; add <= direction.length(); add += 0.1) {
                positions.add(this.getPostion(add, origin, direction));
            }
            return positions;
        }

        public void draw(@NotNull Player player) {
            World world = this.min.getWorld();
            if (world == null) return;

            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1f);
            UniParticle particle = UniParticle.of(Particle.REDSTONE, dustOptions);

            for (Vector[] point : this.sides) {
                for (Vector position : this.traverse(point[0], point[1])) {
                    position = this.min.toVector().clone().add(position);
                    Location location = position.toLocation(world);
                    particle.play(player, location, 0.15, 0);
                }
            }
        }
    }
}
