package com.sketch.papertracingart.Utils;

import android.view.MotionEvent;

public class Tracing {

    public static float getDistance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

}
