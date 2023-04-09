package com.krish.core.graphics.scene;

import com.krish.core.scene.Entity;

import java.util.*;

public class Model {
    private final String id;
    private final List<Entity> entitiesList;
    private final List<Material> materialList;

    /**
     * Create a new model
     * @param id The model's ID
     * @param materialList The materials associated with the model
     */
    public Model(String id, List<Material> materialList) {
        this.id = id;
        this.materialList = materialList;
        entitiesList = new ArrayList<>();
    }

    /**
     * Cleanup the model
     */
    public void cleanup() {
        materialList.forEach(Material::cleanup);
    }

    public String getId() {
        return id;
    }

    public List<Entity> getEntitiesList() {
        return entitiesList;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }
}
