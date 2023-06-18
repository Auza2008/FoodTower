package cn.foodtower.module.modules.render;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.render.RenderWings;

import java.awt.*;

public class Wings
        extends Module {
    //public static Option<Boolean> Rainbow = new Option<Boolean>("Rainbow", "Rainbow", false);
    public Wings() {
        super("Wings", new String[]{"Wings"}, ModuleType.Render);
        this.setColor(new Color(208, 30, 142).getRGB());
        //super.addValues(Rainbow);
    }

    @EventHandler
    public void onRenderPlayer(EventRender3D event) {
        RenderWings renderWings = new RenderWings();
        renderWings.renderWings(event.getPartialTicks());
    }

}


