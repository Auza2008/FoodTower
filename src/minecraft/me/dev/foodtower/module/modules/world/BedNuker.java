/*
Author:SuMuGod
Date:2022/7/10 5:10
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.value.Mode;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BedNuker
        extends Module {

    private static final int radius = 5;
    private final Mode<Enum> mode = new Mode("Mode", "Mode", Break.values(), Break.Bed);
    private int xPos;
    private int yPos;
    private int zPos;

    public BedNuker() {
        super("BedFucker", "破非其恶者", new String[]{"rayaura", "fuck"}, ModuleType.World);
    }

    @NMSL
    private void onUpdate(EventPreUpdate event) {
        this.setSuffix(this.mode.getValue());
        int x = -radius;
        while (x < radius) {
            int y = radius;
            while (y > -radius) {
                int z = -radius;
                while (z < radius) {
                    this.xPos = (int) mc.thePlayer.posX + x;
                    this.yPos = (int) mc.thePlayer.posY + y;
                    this.zPos = (int) mc.thePlayer.posZ + z;
                    BlockPos blockPos = new BlockPos(this.xPos, this.yPos, this.zPos);
                    Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                    if (block.getBlockState().getBlock() == Block.getBlockById(92) && this.mode.getValue() == Break.Cake) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                    } else if (block.getBlockState().getBlock() == Block.getBlockById(122) && this.mode.getValue() == Break.Egg) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                    } else if (block.getBlockState().getBlock() == Block.getBlockById(26) && this.mode.getValue() == Break.Bed) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                    }
                    ++z;
                }
                --y;
            }
            ++x;
        }
    }

    enum Break {
        Cake,
        Egg,
        Bed
    }
}
