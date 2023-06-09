package cn.foodtower.ui.gui.clikguis.ClickUi;

import cn.foodtower.Client;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.fastuni.FastUniFontRenderer;
import cn.foodtower.util.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ValueButton {
    public Value value;
    public String name;
    public boolean custom;
    public boolean change;
    public int x;
    public int y;
    public double opacity;
    FastUniFontRenderer mfont = Client.FontLoaders.Chinese18;

    public ValueButton(final Value value, final int x, final int y) {
        this.custom = false;
        this.opacity = 0.0;
        this.value = value;
        this.x = x;
        this.y = y;
        this.name = "";
        if (this.value instanceof Option) {
            this.change = ((Option) this.value).get();
        } else if (this.value instanceof Mode) {
            this.name = String.valueOf(((Mode) this.value).get());
        } else if (value instanceof Numbers) {
            final Numbers v = (Numbers) value;
            this.name = this.name
                    + (v.isInteger() ? ((Number) v.get()).intValue() : ((Number) v.get()).doubleValue());
        }
        this.opacity = 0.0;
    }

    public void render(final int mouseX, final int mouseY) {
        if (!this.custom) {
            if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
                    && mouseY < this.y + Client.FontLoaders.Chinese18.FONT_HEIGHT + 5) {
                if (this.opacity + 10.0 < 200.0) {
                    this.opacity += 10.0;
                } else {
                    this.opacity = 200.0;
                }
            } else if (this.opacity - 6.0 > 0.0) {
                this.opacity -= 6.0;
            } else {
                this.opacity = 0.0;
            }
            if (this.value instanceof Option) {
                this.change = ((Option) this.value).get();
            } else if (this.value instanceof Mode) {
                this.name = String.valueOf(((Mode) this.value).get());
            } else if (this.value instanceof Numbers) {
                final Numbers v = (Numbers) this.value;
                this.name = String.valueOf(v.isInteger() ? ((Number) v.get()).intValue() : ((Number) v.get()).doubleValue());
                if (mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
                        && mouseY < this.y + mfont.FONT_HEIGHT + 5 && Mouse.isButtonDown(0)) {
                    final double min = v.getMinimum().doubleValue();
                    final double max = v.getMaximum().doubleValue();
                    final double inc = v.getIncrement().doubleValue();
                    final double valAbs = mouseX - (this.x + 1.0);
                    double perc = valAbs / 68.0;
                    perc = Math.min(Math.max(0.0, perc), 1.0);
                    final double valRel = (max - min) * perc;
                    double val = min + valRel;
                    val = Math.round(val * (1.0 / inc)) / (1.0 / inc);
                    v.setValue(val);
                }
            }
            Gui.drawRect(0.0, 0.0, 0.0, 0.0, 0);
            RenderUtil.drawRect(this.x - 10, this.y - 4, this.x + 80, this.y + 11,
                    new Color(220, 220, 220).getRGB());
            mfont.drawString(this.value.getName(), this.x - 5, this.y + 1, new Color(108, 108, 108).getRGB());
            mfont.drawString(this.name, this.x + 76 - mfont.getStringWidth(this.name), this.y + 2,
                    new Color(108, 108, 108).getRGB());
            if (this.value instanceof Numbers) {
                final Numbers v = (Numbers) this.value;
                final double render = 68.0f * (((Number) v.get()).floatValue() - v.getMinimum().floatValue())
                        / (v.getMaximum().floatValue() - v.getMinimum().floatValue());
                RenderUtil.drawRect(this.x, this.y + mfont.FONT_HEIGHT + 3,
                        (float) (this.x + render + 1), this.y + mfont.FONT_HEIGHT + 4,
                        new Color(68, 68, 68).getRGB());
            }
            if (this.change) {
                Gui.drawRect(this.x + 70, this.y, this.x + 77, this.y + 7, new Color(108, 108, 108).getRGB());
            }
        }
    }

    public void key(final char typedChar, final int keyCode) {
    }

    public void click(final int mouseX, final int mouseY, final int button) {
        if (!this.custom && mouseX > this.x - 7 && mouseX < this.x + 85 && mouseY > this.y - 6
                && mouseY < this.y + Client.FontLoaders.Chinese18.FONT_HEIGHT + 5) {
            if (this.value instanceof Option) {
                final Option v = (Option) this.value;
                v.setValue(!(boolean) v.get());
                return;
            }
            if (this.value instanceof Mode) {
                final Mode m = (Mode) this.value;
                final Enum current = m.get();
                final int next = (current.ordinal() + 1 >= m.getModes().length) ? 0 : (current.ordinal() + 1);
                this.value.setValue(m.getModes()[next]);
            }
        }
    }
}
