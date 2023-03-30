package com.krish.core.graphics;

import com.krish.core.scene.Entity;

import java.util.*;

public class Model {
    private final String id;
    private final List<Entity> entitiesList;
    private final List<Mesh> meshList;

    /**
     * Create a new model
     * @param id The model's ID
     * @param meshList The meshes associated with the model
     */
    public Model(String id, List<Mesh> meshList) {
        this.id = id;
        this.meshList = meshList;
        entitiesList = new ArrayList<>();
    }

    /**
     * Cleanup the model
     */
    public void cleanup() {
        meshList.forEach(Mesh::cleanup);
    }

    public String getId() {
        return id;
    }

    public List<Entity> getEntitiesList() {
        return entitiesList;
    }

    public List<Mesh> getMeshList() {
        return meshList;
    }
}
