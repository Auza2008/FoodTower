package cn.foodtower.module.modules.world;

import cn.foodtower.api.value.Numbers;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.ui.cloudmusic.ui.GuiCloudMusic;

public class MusicPlayer extends Module {
    public static final Numbers<Double> musicPosYlyr = new Numbers<>("MusicPlayerLyricY", 120d, 0d, 200d, 1d);

    public MusicPlayer() {
        super("MusicPlayer", new String[]{"neteasemusicplayer", "music"}, ModuleType.World);
        addValues(musicPosYlyr);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new GuiCloudMusic());
        this.setEnabled(false);
    }
}
