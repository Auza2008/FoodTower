package cn.foodtower.module.modules.world.dis.disablers;

import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.world.dis.DisablerModule;
import net.minecraft.client.settings.GameSettings;

public class DCJFastStop implements DisablerModule {
    GameSettings key = mc.gameSettings;

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onPacket(EventPacketSend event) {

    }

    @Override
    public void onPacket(EventPacketReceive event) {

    }

    @Override
    public void onPacket(EventPacket event) {

    }

    @Override
    public void onUpdate(EventPreUpdate event) {

    }

    @Override
    public void onWorldChange(EventWorldChanged event) {

    }

    @Override
    public void onRender2d(EventRender2D event) {

    }

    @Override
    public void onRender3d(EventRender3D event) {

    }

    @Override
    public void onMotionUpdate(EventMotionUpdate event) {
        if (key.keyBindForward.isKeyDown() || key.keyBindLeft.isKeyDown() || key.keyBindRight.isKeyDown() || key.keyBindBack.isKeyDown())
            return;
        mc.thePlayer.motionX *= 0;
        mc.thePlayer.motionZ *= 0;
    }

    @Override
    public void onTick(EventTick event) {

    }
}
