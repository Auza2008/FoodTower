/*
 * Decompiled with CFR 0.136.
 */
package me.dev.foodtower.ui.clickGui;

import me.dev.foodtower.Client;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleManager;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.ui.font.FontManager;
import me.dev.foodtower.ui.hudeditor.HUDEditor;
import me.dev.foodtower.utils.normal.RenderUtil;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import me.dev.foodtower.value.Value;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class CSGOClickUI extends GuiScreen implements GuiYesNoCallback {
    public static ModuleType currentModuleType = ModuleType.Combat;
    public static Module currentModule = ModuleManager.getModulesInType(currentModuleType).size() != 0
            ? ModuleManager.getModulesInType(currentModuleType).get(0)
            : null;
    public static float startX = 40, startY = 40;
    public int moduleStart = 0;
    public int valueStart = 0;
    public Opacity opacity = new Opacity(255);
    public int opacityx = 255;
    public float moveX = 0, moveY = 0;
    boolean previousmouse = true;
    boolean mouse;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		RenderUtil.drawImage(new ResourceLocation("foodtower/hudedit.png"), 4, height - 36, 32, 32);
		if (isHovered(4, height - 36, 4 + 32, height - 36 + 32, mouseX, mouseY) && Mouse.isButtonDown(0)) {
			mc.displayGuiScreen(new HUDEditor());
		}

		//	RenderUtil.drawImage(new ResourceLocation("NovAssets/wifu.png"), 500, 20, 270, 495);
        if (isHovered(startX, startY - 25, startX + 400, startY + 25, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            if (moveX == 0 && moveY == 0) {
                moveX = mouseX - startX;
                moveY = mouseY - startY;
            } else {
                startX = mouseX - moveX;
                startY = mouseY - moveY;
            }
            this.previousmouse = true;
        } else if (moveX != 0 || moveY != 0) {
            moveX = 0;
            moveY = 0;
        }
        this.opacity.interpolate((float) opacityx);
        Gui.drawRect(startX, startY, startX + 60, startY + 320,
                new Color(40, 40, 40, (int) opacity.getOpacity()).getRGB());
        Gui.drawRect(startX + 60, startY, startX + 200, startY + 320,
                new Color(31, 31, 31, (int) opacity.getOpacity()).getRGB());
        Gui.drawRect(startX + 200, startY, startX + 420, startY + 320,
                new Color(40, 40, 40, (int) opacity.getOpacity()).getRGB());
		FontManager.F18.drawString("    " + "FoodTower" + " " + Client.instance.version, startX + 4, startY + 4, -1);
		for (int i = 0; i < ModuleType.values().length; i++) {
            ModuleType[] iterator = ModuleType.values();
            if (iterator[i] != currentModuleType) {
                RenderUtil.drawFilledCircle(startX + 30, startY + 30 + i * 40, 15,
                        new Color(56, 56, 56, (int) opacity.getOpacity()).getRGB(), 5);
            } else {
                RenderUtil.drawFilledCircle(startX + 30, startY + 30 + i * 40, 15,
                        new Color(101, 81, 255, (int) opacity.getOpacity()).getRGB(), 5);
            }
			RenderUtil.drawImage(new ResourceLocation("foodtower/clickicon/" + iterator[i].name().toLowerCase() + ".png"), (int) (startX + 20), (int) (startY + 20 + i * 40), 20, 20);
			try {
                if (this.isCategoryHovered(startX + 15, startY + 15 + i * 40, startX + 45, startY + 45 + i * 40, mouseX,
                        mouseY) && Mouse.isButtonDown(0)) {
                    currentModuleType = iterator[i];
                    currentModule = ModuleManager.getModulesInType(currentModuleType).size() != 0
                            ? ModuleManager.getModulesInType(currentModuleType).get(0)
                            : null;
                    moduleStart = 0;
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        int m = Mouse.getDWheel();
        if (this.isCategoryHovered(startX + 60, startY, startX + 200, startY + 320, mouseX, mouseY)) {
            if (m < 0 && moduleStart < ModuleManager.getModulesInType(currentModuleType).size() - 1) {
                moduleStart++;
            }
            if (m > 0 && moduleStart > 0) {
                moduleStart--;
            }
        }
        if (this.isCategoryHovered(startX + 200, startY, startX + 420, startY + 320, mouseX, mouseY)) {
            if (m < 0 && valueStart < currentModule.getValues().size() - 1) {
                valueStart++;
            }
            if (m > 0 && valueStart > 0) {
                valueStart--;
            }
        }
        mc.fontRendererObj.drawStringWithShadow(
                currentModule == null ? currentModuleType.toString()
                        : currentModuleType.toString() + "/" + currentModule.getName(),
                startX + 70, startY + 15, new Color(248, 248, 248).getRGB());
        if (currentModule != null) {
            float mY = startY + 30;
            for (int i = 0; i < ModuleManager.getModulesInType(currentModuleType).size(); i++) {
                Module module = ModuleManager.getModulesInType(currentModuleType).get(i);
                if (mY > startY + 300)
                    break;
                if (i < moduleStart) {
                    continue;
                }

                RenderUtil.drawRect2(startX + 75, mY, startX + 185, mY + 2,
                        new Color(40, 40, 40, (int) opacity.getOpacity()).getRGB());
                mc.fontRendererObj.drawStringWithShadow(module.getName(), startX + 90, mY + 9,
                        new Color(248, 248, 248, (int) opacity.getOpacity()).getRGB());
                if (!module.isEnabled()) {
                    RenderUtil.drawFilledCircle(startX + 75, mY + 13, 2,
                            new Color(255, 0, 0, (int) opacity.getOpacity()).getRGB(), 5);
                } else {

                    RenderUtil.drawFilledCircle(startX + 75, mY + 13, 2,
                            new Color(0, 255, 0, (int) opacity.getOpacity()).getRGB(), 5);
                }
                if (isSettingsButtonHovered(startX + 90, mY,
                        startX + 100 + (mc.fontRendererObj.getStringWidth(module.getName())),
                        mY + 8 + mc.fontRendererObj.FONT_HEIGHT, mouseX, mouseY)) {
                    if (!this.previousmouse && Mouse.isButtonDown(0)) {
						module.setEnabled(!module.isEnabled());
                        previousmouse = true;
                    }
                    if (!this.previousmouse && Mouse.isButtonDown(1)) {
                        previousmouse = true;
                    }
                }

                if (!Mouse.isButtonDown(0)) {
                    this.previousmouse = false;
                }
                if (isSettingsButtonHovered(startX + 90, mY,
                        startX + 100 + (mc.fontRendererObj.getStringWidth(module.getName())),
                        mY + 8 + mc.fontRendererObj.FONT_HEIGHT, mouseX, mouseY) && Mouse.isButtonDown(1)) {
                    currentModule = module;
                    valueStart = 0;
                }
                mY += 25;
            }
            mY = startY + 30;
            for (int i = 0; i < currentModule.getValues().size(); i++) {
                if (mY > startY + 300)
                    break;
                if (i < valueStart) {
                    continue;
                }
                Value value = currentModule.getValues().get(i);
                if (value instanceof Numbers) {
                    float x = startX + 300;
                    double render = 68.0F
                            * (((Number) value.getValue()).floatValue() - ((Numbers) value).getMinimum().floatValue())
                            / (((Numbers) value).getMaximum().floatValue()
                            - ((Numbers) value).getMinimum().floatValue());
                    RenderUtil.drawRect2(x - 6, mY + 2, (float) ((double) x + 75), mY + 3,
                            (new Color(50, 50, 50, (int) opacity.getOpacity())).getRGB());
                    RenderUtil.drawRect2(x - 6, mY + 2, (float) ((double) x + render + 6.5D), mY + 3,
                            (new Color(61, 141, 255, (int) opacity.getOpacity())).getRGB());
                    RenderUtil.drawRect2((float) ((double) x + render + 2D), mY, (float) ((double) x + render + 7D),
                            mY + 5, (new Color(61, 141, 255, (int) opacity.getOpacity())).getRGB());
                    mc.fontRendererObj.drawStringWithShadow(value.getName() + ": " + value.getValue(), startX + 210, mY, -1);
                    if (!Mouse.isButtonDown(0)) {
                        this.previousmouse = false;
                    }
                    if (this.isButtonHovered(x, mY - 2, x + 100, mY + 7, mouseX, mouseY)
                            && Mouse.isButtonDown(0)) {
                        if (!this.previousmouse && Mouse.isButtonDown(0)) {
                            render = ((Numbers) value).getMinimum().doubleValue();
                            double max = ((Numbers) value).getMaximum().doubleValue();
                            double inc = ((Numbers) value).getIncrement().doubleValue();
                            double valAbs = (double) mouseX - ((double) x + 1.0D);
                            double perc = valAbs / 68.0D;
                            perc = Math.min(Math.max(0.0D, perc), 1.0D);
                            double valRel = (max - render) * perc;
                            double val = render + valRel;
                            val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
                            value.setValue(Double.valueOf(val));
                        }
                        if (!Mouse.isButtonDown(0)) {
                            this.previousmouse = false;
                        }
                    }
                    mY += 20;
                }
                if (value instanceof Option) {
                    float x = startX + 300;
                    mc.fontRendererObj.drawStringWithShadow(value.getName(), startX + 210, mY, -1);
                    RenderUtil.drawRect2(x + 56, mY, x + 76, mY + 1,
                            new Color(255, 255, 255, (int) opacity.getOpacity()).getRGB());
                    RenderUtil.drawRect2(x + 56, mY + 8, x + 76, mY + 9,
                            new Color(255, 255, 255, (int) opacity.getOpacity()).getRGB());
                    RenderUtil.drawRect2(x + 56, mY, x + 57, mY + 9,
                            new Color(255, 255, 255, (int) opacity.getOpacity()).getRGB());
                    RenderUtil.drawRect2(x + 77, mY, x + 76, mY + 9,
                            new Color(255, 255, 255, (int) opacity.getOpacity()).getRGB());
                    if ((boolean) value.getValue()) {
                        RenderUtil.drawRect2(x + 67, mY + 2, x + 75, mY + 7,
                                new Color(61, 141, 255, (int) opacity.getOpacity()).getRGB());
                    } else {
                        RenderUtil.drawRect2(x + 58, mY + 2, x + 65, mY + 7,
                                new Color(150, 150, 150, (int) opacity.getOpacity()).getRGB());
                    }
                    mc.fontRendererObj.drawStringWithShadow(value.getName(), startX + 210, mY, -1);
                    Gui.drawRect(x + 56, mY, x + 76, mY + 1, new Color(255, 255, 255).getRGB());
                    Gui.drawRect(x + 56, mY + 8, x + 76, mY + 9, new Color(255, 255, 255).getRGB());
                    Gui.drawRect(x + 56, mY, x + 57, mY + 9, new Color(255, 255, 255).getRGB());
                    Gui.drawRect(x + 77, mY, x + 76, mY + 9, new Color(255, 255, 255).getRGB());
                    if ((boolean) value.getValue()) {
                        Gui.drawRect(x + 67, mY + 2, x + 75, mY + 7,
                                new Color(61, 141, 255).getRGB());
                    } else {
                        Gui.drawRect(x + 58, mY + 2, x + 65, mY + 7,
                                new Color(150, 150, 150).getRGB());
                    }
                    if (this.isCheckBoxHovered(x + 56, mY, x + 76, mY + 9, mouseX, mouseY)) {
                        if (!this.previousmouse && Mouse.isButtonDown(0)) {
                            this.previousmouse = true;
                            this.mouse = true;
                        }

                        if (this.mouse) {
                            value.setValue(!(boolean) value.getValue());
                            this.mouse = false;
                        }
                    }
                    if (!Mouse.isButtonDown(0)) {
                        this.previousmouse = false;
                    }
                    mY += 20;
                }
                if (value instanceof Mode) {
                    float x = startX + 300;
                    mc.fontRendererObj.drawStringWithShadow(value.getName(), startX + 210, mY, -1);
                    RenderUtil.drawRect2(x - 5, mY - 5, x + 90, mY + 15,
                            new Color(56, 56, 56, (int) opacity.getOpacity()).getRGB());
                    RenderUtil.R2DUtils.drawBorderedRect(x - 5, mY - 5, x + 90, mY + 15,
                            new Color(101, 81, 255, (int) opacity.getOpacity()).getRGB(), 2);
                    mc.fontRendererObj.drawStringWithShadow(((Mode) value).getModeAsString(),
							x + 44 - mc.fontRendererObj.getStringWidth(((Mode) value).getModeAsString()) / 2, mY + 1, -1);
                    if (this.isStringHovered(x, mY - 5, x + 100, mY + 15, mouseX, mouseY)) {
                        if (Mouse.isButtonDown(0) && !this.previousmouse) {
                            Enum current = (Enum) value.getValue();
                            int next = current.ordinal() + 1 >= ((Mode) value).getModes().length ? 0
                                    : current.ordinal() + 1;
                            value.setValue(((Mode) value).getModes()[next]);
                            this.previousmouse = true;
                        }
                        if (!Mouse.isButtonDown(0)) {
                            this.previousmouse = false;
                        }

                    }
                    mY += 25;
                }
            }
        }

    }

    public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		return mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2;
	}

    public boolean isSettingsButtonHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
	}

    public boolean isButtonHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		return mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2;
	}

    public boolean isCheckBoxHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		return mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2;
	}

    public boolean isCategoryHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
	}

    public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
	}

    public void initGui() {
        this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        this.opacity.setOpacity(0);
        this.mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/fxaa.json"));
    }
}
