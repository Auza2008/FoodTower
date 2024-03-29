package cn.foodtower.ui.buttons;

import cn.foodtower.fastuni.FastUniFontRenderer;
import cn.foodtower.fastuni.FontLoader;
import cn.foodtower.util.anim.AnimationUtil;
import cn.foodtower.util.render.RenderUtil;
import cn.foodtower.util.time.TimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class UIFlatButton extends GuiButton {
    private final FastUniFontRenderer font;
    private final TimeHelper time = new TimeHelper();
    private final int color;
    public String displayString;
    public int id;
    public boolean enabled;
    public boolean visible;
    protected boolean hovered;
    private float opacity;

    public UIFlatButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, int color) {
        super(buttonId, x, y, 10, 12, buttonText);
        this.width = widthIn;
        this.height = heightIn;
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.displayString = buttonText;
        this.color = color;
        this.font = FontLoader.msFont15;
    }

    public UIFlatButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, int color, FastUniFontRenderer font) {
        super(buttonId, x, y, 10, 12, buttonText);
        this.width = widthIn;
        this.height = heightIn;
        this.enabled = true;
        this.visible = true;
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.displayString = buttonText;
        this.color = color;
        this.font = font;
    }

    protected int getHoverState(boolean mouseOver) {
        int i = 1;
        if (!this.enabled) {
            i = 0;
        } else if (mouseOver) {
            i = 2;
        }

        return i;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            if (!this.hovered) {
                this.opacity = AnimationUtil.moveUD(opacity, 0f, Minecraft.getDebugFPS() / 500f, Minecraft.getDebugFPS() / 400f);
            }

            if (this.hovered) {
                this.opacity = AnimationUtil.moveUD(opacity, 0.3f, Minecraft.getDebugFPS() / 500f, Minecraft.getDebugFPS() / 400f);
            }
        }

        float radius = (float) this.height / 2.0F;
        RenderUtil.drawRoundedRect(this.xPosition, (float) this.yPosition, (this.xPosition + this.width), (float) this.yPosition + radius * 2.0F, 3f, this.color);
        RenderUtil.drawFastRoundedRect(this.xPosition, (float) this.yPosition, (this.xPosition + this.width), (float) this.yPosition + radius * 2.0F, 3f, new Color(1f, 1f, 1f, opacity).getRGB());
        FontLoader.msFont15.drawCenteredString(this.displayString, (float) (this.xPosition + this.width / 2), (float) this.yPosition + (float) (this.height - this.font.FONT_HEIGHT) / 2.0F + 2, new Color(255, 255, 255).getRGB());

        this.mouseDragged(mc, mouseX, mouseY);
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
