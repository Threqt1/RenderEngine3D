package com.krish.core.scene.lights;

import org.joml.Vector3f;

public class SpotLight {
    private Vector3f coneDirection;
    private float cutoff;
    private float cutoffAngle;
    private PointLight pointLight;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutoffAngle) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        setCutoffAngle(cutoffAngle);
    }

    public float getCutoffAngle() {
        return cutoffAngle;
    }

    public final void setCutoffAngle(float cutoffAngle) {
        this.cutoffAngle = cutoffAngle;
        cutoff = (float) Math.cos(Math.toRadians(cutoffAngle));
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(float x, float y, float z) {
        this.coneDirection = new Vector3f(x, y, z);
    }

    public float getCutoff() {
        return cutoff;
    }

    public void setCutoff(float cutoff) {
        this.cutoff = cutoff;
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }
}
