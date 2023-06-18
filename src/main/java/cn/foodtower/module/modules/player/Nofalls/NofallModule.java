package cn.foodtower.module.modules.player.Nofalls;

import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.api.events.World.EventPreUpdate;
import net.minecraft.client.Minecraft;

public interface NofallModule {
    Minecraft mc = Minecraft.getMinecraft();

    void onEnable();

    void onUpdate(EventPreUpdate e);

    void onPacketSend(EventPacketSend e);

    void onUpdateMotion(EventMotionUpdate e);
}
