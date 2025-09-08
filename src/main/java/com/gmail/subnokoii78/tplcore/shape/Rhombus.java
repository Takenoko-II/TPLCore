package com.gmail.subnokoii78.tplcore.shape;

import com.gmail.subnokoii78.tplcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;

public class Rhombus extends ShapeBase {
    @Override
    public void draw() {
        final TripleAxisRotationBuilder.ObjectCoordsSystem axes = rotation.getObjectsCoordsSystem();

        var left = axes.getX();
        var right = axes.getX().invert();
        var up = axes.getY();
        var down = axes.getY().invert();

        lineFromTo(down, left);
        lineFromTo(left, up);
        lineFromTo(up,right);
        lineFromTo(right, down);
    }

    protected void lineFromTo(Vector3Builder from, Vector3Builder to) {
        final StraightLine line = new StraightLine();
        line.put(world, from);
        line.onDot(this::dot);
        line.rotate(TripleAxisRotationBuilder.from(from.getDirectionTo(to).getRotation2f()));
        line.setScale((float) from.getDistanceTo(to));
        line.draw();
    }
}
