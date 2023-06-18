package cn.foodtower.module.modules.render;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.misc.liquidbounce.LiquidRender;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class OreESP extends Module {
    private final Numbers<Double> Range = new Numbers<>("Range", 40d, 5d, 120d, 1d);
    private final Option Iron = new Option("Iron", false);
    private final Option Gold = new Option("Gold", false);
    private final Option Diamond = new Option("Diamond", false);
    private final Option Coal = new Option("Coal", false);
    private final Option Redstone = new Option("Redstone", false);
    private final Option Lapis = new Option("Lapis", false);
    private final Option Emerald = new Option("Emerald", false);
    private final Option OutLine = new Option("Outline", false);
    private final cn.foodtower.util.time.TimerUtil TimerUtil = new TimerUtil();
    private final CopyOnWriteArrayList<BlockPos> BlockList = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<BlockPos> RenderBlockList = new CopyOnWriteArrayList<>();
    private Thread BlockFinderThread = null;

    public OreESP() {
        super("OreESP", new String[]{"oreesp"}, ModuleType.Render);
        addValues(Range, Iron, Gold, Diamond, Coal, Redstone, Lapis, Emerald, OutLine);
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        if (TimerUtil.hasReached(1000L) && !(BlockFinderThread != null && BlockFinderThread.isAlive())) {
            BlockFinderThread = new Thread(() -> {
                BlockList.clear();
                for (int x = -Range.get().intValue(); x < Range.get(); x++) {
                    for (int y = Range.get().intValue(); y > -Range.get(); y--) {
                        for (int z = -Range.get().intValue(); z < Range.get(); z++) {
                            BlockPos pos = new BlockPos(mc.thePlayer.posX + (double) x, mc.thePlayer.posY + (double) y, mc.thePlayer.posZ + (double) z);
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.diamond_ore) && Diamond.get()) {
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.iron_ore) && Iron.get()) {
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.gold_ore) && Gold.get()) {
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.coal_ore) && Coal.get()) {
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.redstone_ore) && Redstone.get()) {
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.lapis_ore) && Lapis.get()) {
                                BlockList.add(pos);
                            }
                            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.emerald_ore) && Emerald.get()) {
                                BlockList.add(pos);
                            }
                        }
                    }
                }
                TimerUtil.reset();
                synchronized (RenderBlockList) {
                    RenderBlockList.clear();
                    RenderBlockList.addAll(BlockList);
                }
            }, "OreESP-FinderThread");
            BlockFinderThread.start();
        }
    }

    @EventHandler
    public void onRender3D(EventRender3D event) {
        for (final BlockPos pos : RenderBlockList) {
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.diamond_ore) && Diamond.get()) {
                LiquidRender.drawBlockBox(pos, new Color(54, 194, 255, 50), OutLine.get());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.iron_ore) && Iron.get()) {
                LiquidRender.drawBlockBox(pos, new Color(255, 192, 115, 50), OutLine.get());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.gold_ore) && Gold.get()) {
                LiquidRender.drawBlockBox(pos, new Color(255, 221, 0, 50), OutLine.get());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.coal_ore) && Coal.get()) {
                LiquidRender.drawBlockBox(pos, new Color(50, 50, 50, 50), OutLine.get());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.redstone_ore) && Redstone.get()) {
                LiquidRender.drawBlockBox(pos, new Color(255, 73, 73, 50), OutLine.get());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.lapis_ore) && Lapis.get()) {
                LiquidRender.drawBlockBox(pos, new Color(0, 42, 255, 50), OutLine.get());
            }
            if (mc.theWorld.getBlockState(pos).getBlock().equals(Blocks.emerald_ore) && Emerald.get()) {
                LiquidRender.drawBlockBox(pos, new Color(103, 255, 48, 50), OutLine.get());
            }
        }
    }
}
