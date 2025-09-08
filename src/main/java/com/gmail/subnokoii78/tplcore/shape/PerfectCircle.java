package com.gmail.subnokoii78.tplcore.shape;

import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;

public class PerfectCircle extends ShapeBase {
    @Override
    public void draw() {
        for (int i = 0; i < 360; i++) {
            if (Math.random() > getDensity()) continue;
            dot(getPointOnAngle(i));
        }
    }

    protected Vector3Builder getPointOnAngle(float angle) {
        double rad = (angle + rotation.roll()) * Math.PI / 180;
        var axes = rotation.getObjectsCoordsSystem();
        return axes.getX().scale(Math.cos(rad))
            .add(axes.getY().scale(Math.sin(rad)));
    }
}
