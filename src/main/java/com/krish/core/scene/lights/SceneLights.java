package com.krish.core.scene.lights;

import org.joml.Vector3f;

import java.util.ArrayList;

public class SceneLights {
    private final AmbientLight ambientLight;
    private final DirectionLight directionLight;
    private final ArrayList<PointLight> pointLights;

    public SceneLights() {
        this.ambientLight = new AmbientLight();
        this.directionLight = new DirectionLight(new Vector3f(1, 1, 1), new Vector3f(0, 1, 0), 1.0f);
        this.pointLights = new ArrayList<>();
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public DirectionLight getDirectionLight() {
        return directionLight;
    }

    public ArrayList<PointLight> getPointLights() {
        return pointLights;
    }

}
