package cn.foodtower.util.time;

public final class WaitTimer {
    public long time;

    public WaitTimer() {
        this.time = (System.nanoTime() / 1000000L);
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (getTime() >= time) {
            if (reset) {
                reset();
            }
            return true;
        }
        return false;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L - this.time;
    }

    public void reset() {
        this.time = (System.nanoTime() / 1000000L);
    }
}
