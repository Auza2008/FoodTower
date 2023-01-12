/*
Author:SuMuGod
Date:2022/7/10 4:44
Project:foam Reborn
*/
package me.dev.foam.module.modules.render;

import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;

import java.awt.*;

public class ItemPhysic extends Module {
    public ItemPhysic() {
        super("ItemPhysic", "物理堕物", new String[]{"ip"}, ModuleType.Render);
        this.setColor(new Color(241, 255, 12).getRGB());
    }
}
