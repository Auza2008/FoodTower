package cn.foodtower.module.modules.world;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventTick;
import cn.foodtower.api.value.Mode;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;

import java.awt.*;

public class FastPlace extends Module {

    private final Mode mode = new Mode("EventMode", EventE.values(), EventE.Render3D);

    public FastPlace() {
        super("FastPlace", new String[]{"fplace", "fc"}, ModuleType.World);
        this.setColor(new Color(226, 197, 78).getRGB());
    }

    @EventHandler
    //Sb EventTick 耽误我hvh上分
    private void onTick(EventTick e) {
        if (mode.get().equals(EventE.Tick)) mc.rightClickDelayTimer = 0;
    }

    @EventHandler
    private void onUpdate(EventMotionUpdate e) {
        if (mode.get().equals(EventE.Update)) mc.rightClickDelayTimer = 0;
    }


    @EventHandler
    private void onAnyTime(EventRender3D e) {
        mc.rightClickDelayTimer = 0;
    }

    enum EventE {
        Update, Tick, Render3D
    }
}
