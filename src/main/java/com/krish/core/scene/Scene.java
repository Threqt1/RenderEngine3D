package com.krish.core.scene;

import com.krish.core.IGUIInstance;
import com.krish.core.graphics.scene.Model;
import com.krish.core.graphics.TextureCache;

import java.util.HashMap;
import java.util.Map;

public class Scene {
    private final Map<String, Model> modelMap;
    private final Projection projection;
    private final TextureCache textureCache;
    private final Camera camera;
    private IGUIInstance GUIInstance;

    /**
     * Make a Scene
     *
     * @param width  The width of the scene
     * @param height The height of the scene
     */
    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        camera = new Camera();
    }

    //Add an entity to the scene
    public void addEntity(Entity entity) {
        //Add the entity to the model's entity list
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }
        model.getEntitiesList().add(entity);
    }

    /**
     * Add a model to the Scene
     *
     * @param model The model to add
     */
    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    /**
     * Cleanup the Scene
     */
    public void cleanup() {
        //Delete all the models
        modelMap.values().forEach(Model::cleanup);
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }

    public Projection getProjection() {
        return projection;
    }

    public Camera getCamera() {
        return camera;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    /**
     * Handle resizing the Scene
     *
     * @param width  The new width
     * @param height The new height
     */
    public void resize(int width, int height) {
        //Update the projection matrix
        projection.updateProjectionMatrix(width, height);
    }

    public IGUIInstance getGUIInstance() {
        return GUIInstance;
    }

    public void setGUIInstance(IGUIInstance GUIInstance) {
        this.GUIInstance = GUIInstance;
    }
}
