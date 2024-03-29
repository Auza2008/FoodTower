package cn.foodtower.api.events.Render;


import cn.foodtower.api.Event;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

/**
 * Created by Keir on 21/04/2017.
 */
public class EventBlockRenderSide extends Event {

    public BlockPos pos;
    public double maxX;
    public double maxY;
    public double maxZ;
    public double minX;
    public double minY;
    public double minZ;
    private IBlockState state;
    private IBlockAccess world;
    private EnumFacing side;
    private boolean toRender;

    public EventBlockRenderSide(IBlockAccess world, BlockPos pos, EnumFacing side, double maxX, double minX,
                                double maxY, double minY, double maxZ, double minZ) {
        if (Minecraft.getMinecraft().theWorld != null) {
            this.state = Minecraft.getMinecraft().theWorld.getBlockState(pos);
            this.world = world;
            this.pos = pos;
            this.side = side;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
        }
    }

    public IBlockState getState() {
        return state;
    }

    public IBlockAccess getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getSide() {
        return side;
    }

    public boolean isToRender() {
        return toRender;
    }

    public void setToRender(boolean toRender) {
        this.toRender = toRender;
    }
}
