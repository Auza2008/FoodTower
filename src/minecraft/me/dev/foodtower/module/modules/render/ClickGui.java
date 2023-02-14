/*
Author:SuMuGod
Date:2022/7/10 5:03
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.ui.clickGui.CSGOClickUI;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGui", "击视图", new String[]{"gui"}, ModuleType.Render);
        this.setColor(new Color(159, 190, 192).getRGB());
        this.setKey(Keyboard.KEY_RSHIFT);
        this.noToggle = true;
    }

    @Override
    public void onEnable() {
        this.setEnabled(false);
        mc.displayGuiScreen(new CSGOClickUI());
    }
}
