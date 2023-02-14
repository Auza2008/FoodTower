/*
Author:SuMuGod
Date:2022/7/10 5:11
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.TimerUtil;

import java.awt.*;

public class Deathclip
        extends Module {
    private TimerUtil timer = new TimerUtil();

    public Deathclip() {
        super("DeathClip", "死行传", new String[]{"deathc", "dc"}, ModuleType.World);
        this.setColor(new Color(157, 58, 157).getRGB());
    }

    @NMSL
    private void onUpdate(EventPreUpdate e) {
        if (this.mc.thePlayer.getHealth() == 0.0f && this.mc.thePlayer.onGround) {
            this.mc.thePlayer.boundingBox.offsetAndUpdate(this.mc.thePlayer.posX, -10.0, this.mc.thePlayer.posZ);
            if (this.timer.hasReached(500.0)) {
                this.mc.thePlayer.sendChatMessage("/home");
            }
        }
    }
}
