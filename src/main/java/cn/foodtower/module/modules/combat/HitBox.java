package cn.foodtower.module.modules.combat;

import cn.foodtower.api.value.Numbers;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;

import java.awt.*;


public class HitBox
        extends Module {
    public static Numbers<Double> Size = new Numbers<>("Size", "Size", 0.0, 0.0, 5.0, 0.1);

    public HitBox() {
        super("HitBox", new String[]{"HitBox"}, ModuleType.Combat);
        this.setColor(new Color(208, 30, 142).getRGB());
        super.addValues(Size);
    }
}


