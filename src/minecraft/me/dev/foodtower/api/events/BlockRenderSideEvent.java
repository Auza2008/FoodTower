/*
Author:SuMuGod
Date:2022/7/10 3:22
Project:foodtower Reborn
*/
package me.dev.foodtower.api.events;

import me.dev.foodtower.api.Event;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockRenderSideEvent extends Event {
    private final Block block;
    private final IBlockAccess blockAccess;
    private final BlockPos pos;
    private final EnumFacing side;
    public boolean shouldRender = false;
    private double maxX;
    private double maxY;
    private double maxZ;
    private double minX;
    private double minY;
    private double minZ;

    public BlockRenderSideEvent(Block block,IBlockAccess blockAccess, BlockPos pos, EnumFacing side, double maxX, double maxY, double maxZ, double minX, double minY, double minZ) {
        this.block = block;
        this.blockAccess = blockAccess;
        this.pos = pos;
        this.side = side;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
    }

    public Block getBlock() {
        return block;
    }

    public IBlockAccess getBlockAccess() {
        return blockAccess;
    }

    public BlockPos getPos() {
        return pos;
    }

    public EnumFacing getSide() {
        return side;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }
}
