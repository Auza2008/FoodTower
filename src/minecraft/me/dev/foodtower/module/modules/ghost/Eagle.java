package me.dev.foodtower.module.modules.ghost;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class Eagle extends Module {
    public Eagle() {
        super("Eagle", "边界行者", new String[]{"Eagle"}, ModuleType.Movement);
    }

    public Block getBlock(BlockPos pos) {
        return this.mc.theWorld.getBlockState(pos).getBlock();
    }

    public Block getBlockUnderPlayer(EntityPlayer player) {
        return this.getBlock(new BlockPos(player.posX, player.posY - 1.0, player.posZ));
    }
    @NMSL
    public void onUpdate(EventPreUpdate event) {
        if (this.getBlockUnderPlayer((EntityPlayer)mc.thePlayer) instanceof BlockAir) {
            if (mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState((int)mc.gameSettings.keyBindSneak.getKeyCode(), (boolean)true);
            }
        } else if (mc.thePlayer.onGround) {
            KeyBinding.setKeyBindState((int)mc.gameSettings.keyBindSneak.getKeyCode(), (boolean)false);
        }
    }
    public void onEnable() {
        mc.thePlayer.setSneaking(false);
        super.onEnable();
    }

    public void onDisable() {
        KeyBinding.setKeyBindState((int)mc.gameSettings.keyBindSneak.getKeyCode(), (boolean)false);
        super.onDisable();
    }
}
