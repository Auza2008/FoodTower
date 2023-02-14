/*
Author:SuMuGod
Date:2022/7/10 4:04
Project:foodtower Reborn
*/
package me.dev.foodtower.utils.math;

public class TimerUtil {
    private long lastMS;

    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(double milliseconds) {
        if ((double) (this.getCurrentMS() - this.lastMS) >= milliseconds) {
            return true;
        }
        return false;
    }
    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }
    public void reset() {
        this.lastMS = this.getCurrentMS();
    }
    public boolean hasTimeElapsed(long time, boolean reset) {

        if (lastMS > System.currentTimeMillis()) {
            lastMS = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - lastMS > time) {

            if (reset)
                reset();

            return true;


        }else {
            return false;
        }

    }
    public boolean delay(float milliSec) {
        if ((float) (this.getTime() - this.lastMS) >= milliSec) {
            return true;
        }
        return false;
    }

    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setTime(long time) {
        lastMS = time;
    }
}
