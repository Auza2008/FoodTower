package cn.foodtower.module.modules.render;

import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;

public class Animations extends Module {
    public static Mode mode = new Mode("Mode", renderMode.values(), renderMode.Old);
    public static Option NoFire = new Option("NoFire", false);
    public static Option EveryThingBlock = new Option("EveryThingBlock", false);
    public static Numbers<Double> x = new Numbers<>("x", 0.0, -1.0, 1.0, 0.1);
    public static Numbers<Double> y = new Numbers<>("y", 0.0, -1.0, 1.0, 0.1);
    public static Numbers<Double> z = new Numbers<>("z", 0.0, -1.0, 1.0, 0.1);
    public static Option Eliminates = new Option("Eliminates", false);
    public static Numbers<Double> HurtTime = new Numbers<>("HurtTime", 6.0, 0.0, 10.0, 1.0);
    public static Numbers<Double> SpinSpeed = new Numbers<>("SpinSpeed", 10.0, 0.0, 100.0, 1.0);
    public static Numbers<Double> SwingSpeed = new Numbers<>("SwingSpeed", 1.0, -10.0, 10.0, 1.0);

    public Animations() {
        super("Animations", new String[]{"BlockHitanimations"}, ModuleType.Render);
        this.addValues(mode, NoFire, x, y, z, EveryThingBlock, Eliminates, HurtTime, SpinSpeed, SwingSpeed);
        this.setEnabled(true);
    }

    public enum renderMode {
        Sigma, Old, Leaked, None, ETB, Random, Spin, Nivia, Swong, Push, Avatar, Move
    }
}
