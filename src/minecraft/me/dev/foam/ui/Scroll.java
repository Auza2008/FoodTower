/*
Author:SuMuGod
Date:2022/7/10 5:20
Project:foam Reborn
*/
package me.dev.foam.ui;

import me.dev.foam.utils.client.Animation;
import me.dev.foam.utils.client.Direction;
import me.dev.foam.utils.client.SmoothStepAnimation;
import org.lwjgl.input.Mouse;

public class Scroll {

    private float maxScroll = Float.MAX_VALUE, minScroll = 0, rawScroll;
    private float scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);

    public void onScroll(int ms) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        rawScroll += Mouse.getDWheel() / 4f;
        rawScroll = Math.max(Math.min(minScroll, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }

    public boolean isScrollAnimationDone() {
        return scrollAnimation.isDone();
    }

    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
    }

}

