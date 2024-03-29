package cn.foodtower.ui.cloudmusic.utils;

import cn.foodtower.api.EventBus;
import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.util.anim.AnimationUtil;
import net.minecraft.client.Minecraft;

public class SpectrumUtil {

    float[] spectrum;

    public SpectrumUtil() {
        EventBus.getInstance().register(this);
    }

    public void updateSpectrum(float[] spectrum) {
        if (this.spectrum != null) {
            for (int i = 0; i < spectrum.length; i++) {
                // 如果少于给定值就更新
                if (this.spectrum[i] < spectrum[i])
                    this.spectrum[i] = AnimationUtil.moveUD(this.spectrum[i], spectrum[i], 30f / Minecraft.getDebugFPS(), 28f / Minecraft.getDebugFPS());
            }
        } else {
            this.spectrum = spectrum;
        }
    }

    // 获取频谱
    public float[] getSpectrum() {
        if (spectrum != null) {
            return spectrum;
        } else {
            return new float[]{0f};
        }
    }

    @EventHandler
    public void onRender(EventRender2D e) {
        if (spectrum != null) {
            for (int i = 0; i < spectrum.length; i++) {
                // 慢慢的把频谱降到0
                spectrum[i] = AnimationUtil.moveUD(spectrum[i], 0, 2f / Minecraft.getDebugFPS(), 1f / Minecraft.getDebugFPS());
            }
        }
    }
}
