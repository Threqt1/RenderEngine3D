package com.krish.core.scene.lights;

import org.joml.Vector3f;

public class DirectionLight {
    private Vector3f color;

    private Vector3f direction;

    private float intensity;

    public DirectionLight(Vector3f color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(float r, float g, float b) {
        this.color.set(r, g, b);
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(float x, float y, float z) {
        this.direction.set(x, y, z);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
