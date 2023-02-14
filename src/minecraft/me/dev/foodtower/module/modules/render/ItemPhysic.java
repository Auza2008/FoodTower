/*
Author:SuMuGod
Date:2022/7/10 4:44
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;

import java.awt.*;

public class ItemPhysic extends Module {
    public ItemPhysic() {
        super("ItemPhysic", "物理堕物", new String[]{"ip"}, ModuleType.Render);
        this.setColor(new Color(241, 255, 12).getRGB());
    }
}
