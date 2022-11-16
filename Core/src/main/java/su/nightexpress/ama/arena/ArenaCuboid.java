package su.nightexpress.ama.arena;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.EffectUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArenaCuboid {

    private int xMin;
    private int xMax;

    private int yMin;
    private int yMax;

    private int zMin;
    private int zMax;

    private Location locMin;
    private Location locMax;

    private Location   center;
    private Visualizer visualizer;

    @NotNull
    public static ArenaCuboid empty() {
        return new ArenaCuboid();
    }

    private ArenaCuboid() {
        this.xMin = 0;
        this.xMax = 0;
        this.yMin = 0;
        this.yMax = 0;
        this.zMin = 0;
        this.zMax = 0;
    }

    public ArenaCuboid(@NotNull Location from, @NotNull Location to) {
        this.redefine(from, to);
    }

    public void redefine(@NotNull Location from, @NotNull Location to) {
        this.xMin = Math.min(from.getBlockX(), to.getBlockX());
        this.yMin = Math.min(from.getBlockY(), to.getBlockY());
        this.zMin = Math.min(from.getBlockZ(), to.getBlockZ());

        this.xMax = Math.max(from.getBlockX(), to.getBlockX());
        this.yMax = Math.max(from.getBlockY(), to.getBlockY());
        this.zMax = Math.max(from.getBlockZ(), to.getBlockZ());

        this.locMin = new Location(from.getWorld(), this.xMin, this.yMin, this.zMin);
        this.locMax = new Location(from.getWorld(), this.xMax, this.yMax, this.zMax);

        double cx = xMin + (xMax - xMin) / 2D;
        double cy = yMin + (yMax - yMin) / 2D;
        double cz = zMin + (zMax - zMin) / 2D;

        this.center = new Location(from.getWorld(), cx, cy, cz);
        this.visualizer = new Visualizer(this.locMin, this.locMax);
    }

    @NotNull
    public Visualizer getVisualizer() {
        return visualizer;
    }

    public boolean isEmpty() {
        return this.xMin == 0 && this.xMax == 0 && this.yMin == 0 && this.yMax == 0
            && this.zMin == 0 && this.zMax == 0;
    }

    public boolean contains(@NotNull Location location) {
        if (this.isEmpty()) return false;

        World world = location.getWorld();
        if (world == null || !world.equals(this.locMin.getWorld())) return false;

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        return x >= this.xMin && x <= this.xMax
            && y >= this.yMin && y <= this.yMax
            && z >= this.zMin && z <= this.zMax;
    }

    @NotNull
    public List<Block> getBlocks() {
        if (this.isEmpty()) return Collections.emptyList();

        List<Block> list = new ArrayList<>(this.getSize());
        World world = this.center.getWorld();
        if (world == null) return list;

        for (int x = this.xMin; x <= this.xMax; ++x) {
            for (int y = this.yMin; y <= this.yMax; ++y) {
                for (int z = this.zMin; z <= this.zMax; ++z) {
                    Block blockAt = world.getBlockAt(x, y, z);
                    list.add(blockAt);
                }
            }
        }

        return list;
    }

    public int getSize() {
        return (this.xMax - this.xMin + 1) * (this.yMax - this.yMin + 1) * (this.zMax - this.zMin + 1);
    }

    @NotNull
    public Location getLocationMin() {
        return this.locMin;
    }

    @NotNull
    public Location getLocationMax() {
        return this.locMax;
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

            Particle particle = Particle.REDSTONE;
            String data = "255,0,0";

            for (Vector[] point : this.sides) {
                for (Vector position : this.traverse(point[0], point[1])) {
                    position = this.min.toVector().clone().add(position);
                    Location location = position.toLocation(world);
                    EffectUtil.playEffect(player, location, particle, data, 0, 0, 0, 0.1, 0);
                }
            }
        }
    }
}
