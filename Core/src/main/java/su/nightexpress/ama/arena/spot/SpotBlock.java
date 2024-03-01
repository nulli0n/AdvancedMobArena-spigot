package su.nightexpress.ama.arena.spot;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.util.BlockPos;

public class SpotBlock {

    private final BlockPos  blockPos;
    private final BlockData blockData;
    private final Object    blockStateTag;

    public SpotBlock(@NotNull BlockPos blockPos, @NotNull BlockData blockData, @Nullable Object blockStateTag) {
        this.blockPos = blockPos;
        this.blockData = blockData;
        this.blockStateTag = blockStateTag;
    }

    @NotNull
    public static SpotBlock fromBlock(@NotNull AMA plugin, @NotNull Block block) {
        BlockPos blockPos = BlockPos.from(block.getLocation());
        BlockData blockData = block.getBlockData();
        Object blockStateTag = plugin.getArenaNMS().getBlockStateTag(block);

        return new SpotBlock(blockPos, blockData, blockStateTag);
    }

    @Nullable
    public static SpotBlock fromString(@NotNull AMA plugin, @NotNull String rawData) {
        String[] blockSplit = rawData.split("~");
        if (blockSplit.length < 2) return null;

        BlockPos blockPos = BlockPos.deserialize(blockSplit[0]);
        if (blockPos.isEmpty()) return null;

        BlockData blockData;
        try {
            blockData = plugin.getServer().createBlockData(blockSplit[1]);
        }
        catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            return null;
        }

        Object blockStateTag = null;
        if (blockSplit.length >= 3) {
            try {
                blockStateTag = plugin.getArenaNMS().decompressBlockState(blockSplit[2]);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        return new SpotBlock(blockPos, blockData, blockStateTag);
    }

    @NotNull
    public String toRawString(@NotNull AMA plugin) {
        String pos = this.getBlockPos().serialize();
        String data = this.getBlockData().getAsString();
        String tag = this.blockStateTag == null ? null : plugin.getArenaNMS().tagToNBTString(this.blockStateTag);

        String str = pos + "~" + data;
        if (tag != null) str += "~" + tag;

        return str;
    }

    public void setBlock(@NotNull AMA plugin, @NotNull World world) {
        Location location = this.getBlockPos().toLocation(world);
        Block block = location.getBlock();
        if (!block.getBlockData().matches(this.blockData)) {
            block.setBlockData(this.blockData);
        }

        if (this.blockStateTag != null) {
            plugin.getArenaNMS().setBlockStateFromTag(block, this.blockStateTag);
        }
    }

    @NotNull
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @NotNull
    public BlockData getBlockData() {
        return blockData;
    }

    @Nullable
    public Object getBlockStateTag() {
        return blockStateTag;
    }
}
