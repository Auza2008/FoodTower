/*
Author:SuMuGod
Date:2022/7/10 5:09
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.api.events.BlockRenderSideEvent;
import me.dev.foodtower.api.events.EventRender3D;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.normal.RenderUtil;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Xray
        extends Module {
    private static final Numbers<Double> range = new Numbers<>("Range", "Range", 50.0,0.0,500.0,1.0);
    public static final Numbers<Double> alpha = new Numbers<>("Alpha", "Alpha", 150.0,0.0,255.0,1.0);
    public static final LinkedList<Integer> antiXRayBlocks = new LinkedList<>();
    private static final Numbers<Double> extremeRange = new Numbers<>("extremeRange", "extremeRange", 4.0,0.0,6.0,1.0);
    public static Option<Boolean> coal = new Option<>("Coal", "coal", true);
    public static Option<Boolean> iron = new Option<>("iron", "iron", true);
    public static Option<Boolean> gold = new Option<>("gold", "gold", true);
    public static Option<Boolean> lapisLazuli = new Option<>("lapisLazuli", "lapisLazuli", true);
    public static Option<Boolean> diamond = new Option<>("diamond", "diamond", true);
    public static Option<Boolean> redStone = new Option<>("redStone", "redStone", true);
    public static Option<Boolean> emerald = new Option<>("emerald", "emerald", false);
    public static Option<Boolean> quartz = new Option<>("quartz", "quartz", false);
    public static Option<Boolean> water = new Option<>("water", "water", false);
    public static Option<Boolean> lava = new Option<>("lava", "lava", false);
    public static Option<Boolean> tracer = new Option<>("tracer", "tracer", false);
    public static Option<Boolean> block = new Option<>("block", "block", false);
    public static Option<Boolean> cave = new Option<>("cave", "cave", true);
    public static Option<Boolean> esp = new Option<>("esp", "esp", true);
    public static Option<Boolean> extreme = new Option<>("extreme", "extreme", true);
    public static boolean isEnable = false;
    private final CopyOnWriteArrayList<XRayBlock> xRayBlocks = new CopyOnWriteArrayList<>();
    Block[] _extreme_var0 = {Blocks.obsidian, Blocks.clay, Blocks.mossy_cobblestone, Blocks.diamond_ore, Blocks.redstone_ore, Blocks.iron_ore, Blocks.coal_ore, Blocks.lapis_ore, Blocks.gold_ore, Blocks.emerald_ore, Blocks.quartz_ore};

    public Xray() {
        super("Xray", "矿石皆透", new String[]{"dis"}, ModuleType.Render);
    }

    @Override
    public void onEnable() {
        if (extreme.getValue()) {
            if (mc.thePlayer.posY <= 25.0) {
                doExtreme();
            }
        }
        mc.renderGlobal.loadRenderers();
        int posX = (int)mc.thePlayer.posX;
        int posY = (int)mc.thePlayer.posY;
        int posZ = (int)mc.thePlayer.posZ;
        mc.renderGlobal.markBlockRangeForRenderUpdate(posX - 900,posY - 900,posZ - 900,posX + 900,posY + 900,posZ + 900);
        addAntiXRayBlocks();
        isEnable = true;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.renderGlobal.loadRenderers();
        antiXRayBlocks.clear();
        xRayBlocks.clear();
        isEnable = false;
        super.onDisable();
    }

    @NMSL
    public void onEventBlockRenderSide(BlockRenderSideEvent e) {
        for (int id : antiXRayBlocks) {
            if (Block.getIdFromBlock(e.getBlock()) == id) {
                if (cave.getValue()) {
                    if (!f1(e.getBlockAccess(),e.getPos(),e.getMinY(),e.getMaxY(),e.getMinZ(),e.getMaxZ(),e.getMinX(),e.getMaxX())) {
                        continue;
                    }
                }
                e.shouldRender = true;
                if (!esp.getValue() && !tracer.getValue()) return;
                float xDiff = (float) (mc.thePlayer.posX - e.getPos().getX());
                float yDiff = 0;
                float zDiff = (float) (mc.thePlayer.posZ - e.getPos().getZ());
                float dis = MathHelper.sqrt_float(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
                if (dis > range.getValue().floatValue()) {
                    continue;
                }
                XRayBlock x = new XRayBlock(Math.round(e.getPos().offset(e.getSide(),-1).getX()),Math.round(e.getPos().offset(e.getSide(),-1).getY()),Math.round(e.getPos().offset(e.getSide(), -1).getZ()),e.getBlock());
                if (!xRayBlocks.contains(x)) {
                    xRayBlocks.add(x);
                }
            }
        }
    }

    private boolean f1(IBlockAccess iBlockAccess, BlockPos blockPos, double minY, double maxY, double minZ, double maxZ, double minX, double maxX) {
        for (EnumFacing enumFacing2 : EnumFacing.VALUES) {
            if (!f0(iBlockAccess, blockPos.offset(enumFacing2), enumFacing2,minY,maxY,minZ,maxZ,minX,maxX)) continue;
            return true;
        }
        return false;
    }

    private boolean f0(IBlockAccess iBlockAccess, BlockPos blockPos, EnumFacing enumFacing,double minY,double maxY,double minZ,double maxZ,double minX,double maxX) {
        return enumFacing == EnumFacing.DOWN && minY > 0.0D || (enumFacing == EnumFacing.UP && maxY < 1.0D || (enumFacing == EnumFacing.NORTH && minZ > 0.0D || (enumFacing == EnumFacing.SOUTH && maxZ < 1.0D || (enumFacing == EnumFacing.WEST && minX > 0.0D || (enumFacing == EnumFacing.EAST && maxX < 1.0D || !iBlockAccess.getBlockState(blockPos).getBlock().isOpaqueCube())))));
    }

    @NMSL
    public void on3D(EventRender3D e) {
        for (XRayBlock block : xRayBlocks) {
            BlockPos currentPos = new BlockPos(block.getX(),block.getY(),block.getZ());
            if (!(mc.theWorld.getBlock(currentPos) instanceof BlockOre)) {
                xRayBlocks.remove(block);
                continue;
            }
            Color color = new Color(12, 12, 12);

            switch (block.getBlock().getUnlocalizedName()) {
                case "tile.blockEmerald":
                case "tile.oreEmerald":
                    color = new Color(0,255,0);
                    break;
                case "tile.blockGold":
                case "tile.oreGold":
                    color = new Color(0xFFFF00);
                    break;
                case "tile.blockIron":
                case "tile.oreIron":
                    color = new Color(210, 210, 210);
                    break;
                case "tile.blockLapis":
                case "tile.oreLapis":
                    color = new Color(0x0000FF);
                    break;
                case "tile.blockRedstone":
                case "tile.oreRedstone":
                    color = new Color(0xFF0000);
                    break;
                case "tile.blockDiamond":
                case "tile.oreDiamond":
                    color = new Color(0, 255,255);
                    break;
                case "tile.netherquartz":
                    color = new Color(255, 255, 255);
                    break;
            }
            if (esp.getValue()) {
                double x = (block.x - mc.getRenderManager().renderPosX);
                double y = (block.y - mc.getRenderManager().renderPosY);
                double z = (block.z - mc.getRenderManager().renderPosZ);
                double minX = (block.getBlock() instanceof BlockStairs || Block.getIdFromBlock(block.getBlock()) == 134) ? 0.0 : block.getBlock().getBlockBoundsMinX();
                double minY = (block.getBlock() instanceof BlockStairs || Block.getIdFromBlock(block.getBlock()) == 134) ? 0.0 : block.getBlock().getBlockBoundsMinY();
                double minZ = (block.getBlock() instanceof BlockStairs || Block.getIdFromBlock(block.getBlock()) == 134) ? 0.0 : block.getBlock().getBlockBoundsMinZ();
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(3553);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                GL11.glLineWidth(1.0F);
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 160 / 255.0f);
                RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x + minX, y + minY, z + minZ, x + block.getBlock().getBlockBoundsMaxX(), y + block.getBlock().getBlockBoundsMaxY(), z + block.getBlock().getBlockBoundsMaxZ()));
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }

            if (tracer.getValue()) {
                double posX = block.x - mc.getRenderManager().renderPosX;
                double posY = block.y - mc.getRenderManager().renderPosY;
                double posZ = block.z - mc.getRenderManager().renderPosZ;
                boolean oldBobbing = mc.gameSettings.viewBobbing;
                mc.gameSettings.viewBobbing = false;
                mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
                mc.gameSettings.viewBobbing = oldBobbing;
                GL11.glPushMatrix();
                GL11.glColor4f(1.0f,1.0f,1.0f, 1.0f);
                GL11.glEnable(2848);
                GL11.glDisable(3553);
                GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
                GL11.glLineWidth(0.5f);
                GL11.glBegin(1);
                GL11.glVertex3d(0.0, mc.thePlayer.getEyeHeight(), 0.0);
                GL11.glVertex3d(posX, posY,posZ);
                GL11.glEnd();
                GL11.glDisable(2848);
                GL11.glEnable(3553);
                GL11.glColor4f(1.0f,1.0f,1.0f, 1.0f);
                GL11.glPopMatrix();
            }
        }
    }

    public void doExtreme() {
        int var1 = extremeRange.getValue().intValue();
        for(int var2 = -var1; var2 < var1; ++var2) {
            for(int var3 = var1; var3 > -var1; --var3) {
                for(int var4 = -var1; var4 < var1; ++var4) {
                    int var5 = (int)Math.floor(mc.thePlayer.posX) + var2;
                    int var6 = (int)Math.floor(mc.thePlayer.posY) + var3;
                    int var7 = (int)Math.floor(mc.thePlayer.posZ) + var4;
                    if (mc.thePlayer.getDistanceSq(mc.thePlayer.posX + (double)var2, mc.thePlayer.posY + (double)var3, mc.thePlayer.posZ + (double)var4) <= 16.0D) {
                        Block var8 = mc.theWorld.getBlockState(new BlockPos(var5, var6, var7)).getBlock();
                        boolean var9 = false;
                        Block[] var10 = _extreme_var0;

                        for (Block var13 : var10) {
                            if (var8.equals(var13)) {
                                var9 = true;
                                break;
                            }
                        }
                        var9 = var9 && (var8.getBlockHardness(mc.theWorld, BlockPos.ORIGIN) != -1.0F || mc.playerController.isInCreativeMode());
                        boolean dont = false;
                        for (XRayBlock xRayBlock : xRayBlocks) {
                            if (xRayBlock.samePos(new BlockPos(var5, var6, var7))) {
                                dont = true;
                                break;
                            }
                        }
                        if (var9 && !dont) {
                            BlockPos pos = new BlockPos(var5, var6, var7);
                            mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,pos,EnumFacing.UP));
                            xRayBlocks.add(new XRayBlock(var5,var6,var7,mc.theWorld.getBlock(pos)));
                        }
                    }
                }
            }
        }
    }

    private void addAntiXRayBlocks() {
        if (coal.getValue()) {
            antiXRayBlocks.add(16);
            if (block.getValue()) antiXRayBlocks.add(173);
        }
        if (iron.getValue()) {
            antiXRayBlocks.add(15);
            if (block.getValue()) antiXRayBlocks.add(42);
        }
        if (gold.getValue()) {
            antiXRayBlocks.add(14);
            if (block.getValue()) antiXRayBlocks.add(41);
        }
        if (lapisLazuli.getValue()) {
            antiXRayBlocks.add(21);
            if (block.getValue()) antiXRayBlocks.add(22);
        }
        if (diamond.getValue()) {
            antiXRayBlocks.add(56);
            if (block.getValue()) antiXRayBlocks.add(57);
        }
        if (redStone.getValue()) {
            antiXRayBlocks.add(73);
            antiXRayBlocks.add(74);
            if (block.getValue()) antiXRayBlocks.add(152);
        }
        if (emerald.getValue()) {
            antiXRayBlocks.add(129);
            if (block.getValue()) antiXRayBlocks.add(133);
        }
        if (quartz.getValue()) {
            antiXRayBlocks.add(153);
        }
        if (water.getValue()) {
            antiXRayBlocks.add(8);
            antiXRayBlocks.add(9);
        }
        if (lava.getValue()) {
            antiXRayBlocks.add(10);
            antiXRayBlocks.add(11);
        }
    }

    private enum modeEnums {
        Normal, FoodByte
    }

    private static class XRayBlock {
        private final double x;
        private final double y;
        private final double z;
        private final Block block;

        public XRayBlock(double x, double y, double z, Block block) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.block = block;
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

        public Block getBlock() {
            return block;
        }

        public boolean samePos(BlockPos blockPos) {
            return (int) x == blockPos.getX() && (int) y == blockPos.getY() && z == blockPos.getZ();
        }
    }
}
