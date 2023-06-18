package cn.foodtower.module.modules.render;

import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;

public class TabGui extends Module {
    public static Mode modes = new Mode("Mode", tabguimode.values(), tabguimode.Distance);
    //	public static Numbers<Double> x = new Numbers<Double>("X",0d,-10d,100d,1d);
    public static Numbers<Double> y = new Numbers<>("Y", 0d, -25d, 60d, 1d);

    public TabGui() {
        super("TabGui", new String[]{"TabGui"}, ModuleType.Render);
        addValues(modes, y);
    }

    public enum tabguimode {
        Novoline,
        Distance
    }
}
