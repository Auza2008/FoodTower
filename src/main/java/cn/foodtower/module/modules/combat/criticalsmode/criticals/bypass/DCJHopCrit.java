package cn.foodtower.module.modules.combat.criticalsmode.criticals.bypass;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.modules.combat.Criticals;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;
import cn.foodtower.module.modules.world.Disabler;
import cn.foodtower.ui.notifications.user.Notifications;

public class DCJHopCrit extends CriticalsModule {
    boolean b = false;
    double posY;

    @Override
    public void onEnabled() {
        if (mc.thePlayer.onGround) posY = mc.thePlayer.posY + 0.20;
        if (!ModuleManager.getModuleByClass(Disabler.class).isEnabled() || Disabler.mode.get() != Disabler.Modes.DCJ)
            Notifications.getManager().post("DCJ Hop", "请使用DCJ Disabler获得最好体验");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAttack(EventAttack e) {
        if (mc.thePlayer.getHealth() <= 3.0) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.2, mc.thePlayer.posZ);
                b = true;
            }
            if (Criticals.DCJAlways.get()) {
                if (mc.thePlayer.posY <= posY) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.2, mc.thePlayer.posZ);
                    b = true;
                }
            }
        } else {
            if (mc.thePlayer.onGround) mc.thePlayer.motionY = 0.20;
            if (Criticals.DCJAlways.get()) {
                if (mc.thePlayer.posY <= posY) {
                    mc.thePlayer.motionY = 0.20;
                }
            }
        }
    }

    @Override
    public void onPacketSend(EventPacketSend e) {
    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onUpdate(EventMotionUpdate e) {
        if (b) {
            mc.thePlayer.motionY = -0.1;
            if (mc.thePlayer.onGround) {
                b = false;
            }
        }
    }
}
