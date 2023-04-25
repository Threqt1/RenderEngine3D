package com.krish.core.scene.skybox;

import com.krish.core.graphics.TextureCache;
import com.krish.core.graphics.Model;
import com.krish.core.scene.Entity;
import com.krish.core.scene.ModelLoader;

public class Skybox {
    private final Entity skyboxEntity;
    private final Model skyboxModel;

    public Skybox(String skyboxModelPath, TextureCache textureCache) {
        skyboxModel = ModelLoader.loadModel("skybox-model", skyboxModelPath, textureCache);
        skyboxEntity = new Entity("skyboxEntity-entity", skyboxModel.getId());
    }

    public Entity getSkyboxEntity() {
        return skyboxEntity;
    }

    public Model getSkyboxModel() {
        return skyboxModel;
    }
}
