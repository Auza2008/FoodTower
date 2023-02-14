package net.minecraft.client.gui;

import me.dev.foodtower.Client;
import me.dev.foodtower.ui.font.FontManager;
import me.dev.foodtower.ui.login.GuiAltManager;
import me.dev.foodtower.utils.client.MainMenuButton;
import me.dev.foodtower.utils.math.GaussianBlur;
import me.dev.foodtower.utils.normal.RenderUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @author ZhangTieNan
 * 2022/6/26
 */

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback
{


    public GuiMainMenu()
    {

    }



    @Override
    public void initGui() {
        ScaledResolution res = new ScaledResolution(mc);
        buttonList.add(new MainMenuButton(1, res.getScaledWidth()/2-270,res.getScaledHeight()/2+53, 31, 35,"C","Singleplayer"));
        buttonList.add(new MainMenuButton(2, res.getScaledWidth()/2-270 + 125,res.getScaledHeight()/2+53, 31, 35,"B","Multiplayer"));
        buttonList.add(new MainMenuButton(3, res.getScaledWidth()/2-270 + (125 * 2),res.getScaledHeight()/2+53, 31, 35,"A","AltManager"));
        buttonList.add(new MainMenuButton(4, res.getScaledWidth()/2-270 + (125 * 3),res.getScaledHeight()/2+53, 31, 35,"G","Setting"));
        buttonList.add(new MainMenuButton(5, res.getScaledWidth()/2-270 + (125 * 4),res.getScaledHeight()/2+53, 31, 35,"D","Exit"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //显示背景
        ScaledResolution res = new ScaledResolution(mc);

        RenderUtil.drawImage(new ResourceLocation("foodtower/background.png"), 0, 0, res.getScaledWidth(), res.getScaledHeight());
        if (!Client.cracked)
            GaussianBlur.renderBlur(10);
        RenderUtil.drawRect(res.getScaledWidth() / 2 - 300, res.getScaledHeight() / 2 - 110, res.getScaledWidth() / 2 + 300, res.getScaledHeight() / 2 + 110, new Color(81, 74, 69, 180).getRGB());
        RenderUtil.drawRect(res.getScaledWidth() / 2 - 300, res.getScaledHeight() / 2 + 50, res.getScaledWidth() / 2 + 300, res.getScaledHeight() / 2 + 110, new Color(87, 77, 68, 180).getRGB());
        FontManager.F22.drawString("FoodTower", res.getScaledWidth() / 2 - 250, res.getScaledHeight() / 2 - 70, -1);
        FontManager.F22.drawString("Build " + Client.instance.version, res.getScaledWidth() / 2 - 250, res.getScaledHeight() / 2 - 27, -1);
        FontManager.F22.drawString("Logged in as " + Client.user, res.getScaledWidth() / 2 + 135, res.getScaledHeight() / 2 + 30, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) mc.displayGuiScreen(new GuiSelectWorld(this));
        if (button.id == 2) mc.displayGuiScreen(new GuiMultiplayer(this));
        if (button.id == 3) mc.displayGuiScreen(new GuiAltManager());
        if (button.id == 4) mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        if (button.id == 5) System.exit(0);
    }
}