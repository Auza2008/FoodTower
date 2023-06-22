package cn.foodtower.util;

import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.FileManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.misc.Helper;

import java.util.List;

/**
 * ClientSetting
 * 客户端自定义设置
 * 相当于个性化，设置壁纸和颜色用的
 */
public class ClientSetting extends Module {
    public static final Mode backGround = new Mode("BackGround", backgrounds.values(), backgrounds.BG27);
    public static final Mode betterFPS = new Mode("BetterFPS", betterfps.values(), betterfps.Distance);
    public static final Option resetBF = new Option("Reset BetterFPS", false);
    public static final Option backPackAnimation = new Option("BackPack Animation", true);
    public static final Option soundFx = new Option("Module SoundFx", true);
    public static final Option clMode = new Option("AutoCL(Netease Login)", false);
    public static final Option keepWorld = new Option("Keep world on disconnect", true);
    public static final Option enableBlur = new Option("Enable Blur", true);
    public static int id = 1;

    public ClientSetting() {
        super("ClientSetting", new String[]{"ClientSetting"}, ModuleType.World);
        addValues(backGround, backPackAnimation, soundFx, betterFPS, resetBF, clMode, keepWorld, enableBlur);
        enableBlur.setValue(isBlurConfig());
        setRemoved(true);
    }

    @Override
    public void onEnable() {
        Helper.sendMessage("这个模块不需要被开启");
        setEnabled(false);
    }

    private boolean isBlurConfig() {
        List<String> names = FileManager.read("NeedBlur.txt");
        if (names.isEmpty()) {
            return false;
        }
        for (String v : names) {
            if (v.contains("true")) {
                return true;
            } else if (v.contains("false")) {
                return false;
            }
        }
        return false;
    }

    public enum betterfps {
        Rivens("rivens"), LibGDX("libgdx"), RivensFull("rivens-full"), RivensHalf("rivens-half"), JavaMath("java"), Random("random"), Distance("distance"), Vanilla("vanilla");
        final String fileName;

        betterfps(String name) {
            this.fileName = name;
        }

        public String get() {
            return fileName;
        }

    }

    public enum backgrounds {
        BG1("bg.jpg"), BG2("bg2.jpg"), BG3("bg3.jpg"), BG4("bg4.jpg"), BG5("bg5.jpg"), BG6("bg6.jpg"), BG7("bg7.jpg"), BG8("bg8.jpg"), BG9("bg9.jpg"), BG10("bg10.jpg"), BG11("bg11.jpg"), BG12("bg12.jpg"), BG13("bg13.jpg"), BG14("bg14.png"), BG15("bg15.png"), BG16("bg16.png"), BG17("bg17.png"), BG18("bg18.png"), BG19("bg19.png"), BG20("bg20.png"), BG21("bg21.png"), BG22("bg22.png"), BG23("bg23.png"), BG24("bg24.png"), BG25("bg25.png"), BG26("bg26.jpg"), BG27("bg27.png"), BG28("bg28.jpg"), BG29("bg29.png"), BG30("bg30.png"), BG31("bg31.png"), BG32("bg32.png"), BG33("bg33.png"), BG34("bg34.png");


        final String fileName;

        backgrounds(String name) {
            this.fileName = name;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
