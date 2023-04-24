package com.krish.core.scene.lights;

import org.joml.Vector3f;

public class PointLight {
    private final Attenuation attenuation;
    private Vector3f color;
    private float intensity;
    private Vector3f position;

    public PointLight(Vector3f color, Vector3f position, float intensity) {
        this.attenuation = new Attenuation(0, 0, 1);
        this.color = color;
        this.intensity = intensity;
        this.position = position;
    }

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public Vector3f getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setColor(float r, float g, float b) {
        this.color = new Vector3f(r, g, b);
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }

    public static class Attenuation {
        private final float constant;
        private final float exponent;
        private final float linear;

        public Attenuation(float constant, float exponent, float linear) {
            this.constant = constant;
            this.exponent = exponent;
            this.linear = linear;
        }

        public float getConstant() {
            return constant;
        }

        public float getExponent() {
            return exponent;
        }

        public float getLinear() {
            return linear;
        }
    }
}
