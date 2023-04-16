package com.krish.core.scene.lights;

import java.util.ArrayList;

public class SceneLights {
    public AmbientLight ambientLight;
    private final ArrayList<PointLight> pointLights;

    public SceneLights() {
        this.ambientLight = new AmbientLight();
        this.pointLights = new ArrayList<>();
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public ArrayList<PointLight> getPointLights() {
        return pointLights;
    }

}
