package cn.foodtower.module.modules.player;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;


public class Eagle extends Module {
    public Eagle() {
        super("Eagle", new String[]{"Eagle"}, ModuleType.Movement);
    }

    public Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    public Block getBlockUnderPlayer(EntityPlayer player) {
        return this.getBlock(new BlockPos(player.posX, player.posY - 1.0, player.posZ));
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if (this.getBlockUnderPlayer(mc.thePlayer) instanceof BlockAir && !mc.gameSettings.keyBindJump.pressed) {
            if (mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            }
        } else if (mc.thePlayer.onGround) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
    }

    public void onEnable() {
        mc.thePlayer.setSneaking(false);
        super.onEnable();
    }

    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        super.onDisable();
    }
}


