package cn.foodtower.api;

public abstract class Event {
    public static double y;
    public static double x;
    public static double z;
    public byte type;
    private boolean cancelled;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public byte getType() {
        return this.type;
    }

    public void setType(byte type) {
        this.type = type;
    }
}
