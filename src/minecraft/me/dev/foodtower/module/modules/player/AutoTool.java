/*
Author:SuMuGod
Date:2022/7/10 5:10
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.normal.BlockUtils;
import net.minecraft.util.BlockPos;

public class AutoTool extends Module {
    public AutoTool() {
        super("AutoTool", "自器", new String[]{"at"}, ModuleType.Player);
    }

    public Class type() {
        return EventPacketSend.class;
    }

    @NMSL
    public void onEvent(EventPreUpdate event) {
        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
            return;
        }
        if (mc.objectMouseOver == null) {
            return;
        }
        BlockPos pos = mc.objectMouseOver.getBlockPos();
        if (pos == null) {
            return;
        }
        BlockUtils.updateTool(pos);
    }
}
