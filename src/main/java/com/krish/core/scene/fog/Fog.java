package com.krish.core.scene.fog;

import org.joml.Vector3f;

public class Fog {
    private boolean active;
    private Vector3f color;
    private float density;

    public Fog() {
        active = false;
        color = new Vector3f();
    }

    public Fog(boolean active, Vector3f color, float density) {
        this.active = active;
        this.color = color;
        this.density = density;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
