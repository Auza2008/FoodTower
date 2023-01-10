/*
Author:SuMuGod
Date:2022/7/10 5:10
Project:foam Reborn
*/
package me.dev.foam.module.modules.player;

import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventPacketSend;
import me.dev.foam.api.events.EventPreUpdate;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.utils.normal.BlockUtils;
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
