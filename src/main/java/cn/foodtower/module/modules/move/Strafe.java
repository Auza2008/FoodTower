package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.entity.MoveUtils;

import java.awt.*;


public class Strafe
        extends Module {

    public Strafe() {
        super("Strafe", new String[]{"Strafe"}, ModuleType.Movement);
        this.setColor(new Color(208, 30, 142).getRGB());
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if (MoveUtils.isMoving()) {
            MoveUtils.strafe();
        }
    }
}


