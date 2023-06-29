package cn.foodtower.ui.notifications.user;

import cn.foodtower.Client;
import cn.foodtower.ui.cloudmusic.MusicManager;
import cn.foodtower.util.anim.Translate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Notification implements INotification {
    private final String header;
    private final String subtext;
    private final long start;
    private final long displayTime;
    private final Notifications.Type type;
    public long id;
    public Translate translate;
    boolean isplayed;
    private float x;
    private float tarX;
    private float y;
    private long last;


    protected Notification(String header, String subtext, long displayTime, Notifications.Type type) {
        this.header = header;
        this.subtext = subtext;
        this.start = System.currentTimeMillis();
        this.displayTime = displayTime;
        this.type = type;
        this.last = System.currentTimeMillis();
        ScaledResolution XD = new ScaledResolution(Minecraft.getMinecraft());
        this.y = (float) (ScaledResolution.getScaledHeight() + 30);
        this.x = (float) ScaledResolution.getScaledWidth();
        float subHeaderWidth = Client.FontLoaders.Chinese16.getStringWidth(subtext);
        float headerWidth = Client.FontLoaders.Chinese20.getStringWidth(getHeader() + (getType().equals(Notifications.Type.MUSIC) ? "( \u2022ω\u2022)\u266A~" : ""));
        this.tarX = (float) (ScaledResolution.getScaledWidth() - 90) - (Math.max(headerWidth, subHeaderWidth));
        this.translate = new Translate(this.x, this.y);
        if (MusicManager.INSTANCE.getCurrentTrack() != null) {
            id = MusicManager.INSTANCE.getCurrentTrack().id;
        } else {
            id = 0;
        }
        isplayed = false;
    }

    public long getLast() {
        return this.last;
    }

    public void setLast(long last) {
        this.last = last;
    }

    public long checkTime() {
        return System.currentTimeMillis() - this.getDisplayTime();
    }

    public String getHeader() {
        return this.header;
    }

    public String getSubtext() {
        return this.subtext;
    }

    public long getStart() {
        return this.start;
    }

    public long getDisplayTime() {
        return this.displayTime;
    }

    public Notifications.Type getType() {
        return this.type;
    }

    public float getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = (float) x;
    }

    public float getTarX() {
        return this.tarX;
    }

    public void setTarX(int x) {
        this.tarX = (float) x;
    }

    public float getTarY() {
        return 0.0F;
    }

    public float getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = (float) y;
    }
}
