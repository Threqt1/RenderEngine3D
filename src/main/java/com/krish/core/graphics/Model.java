package com.krish.core.graphics;

import com.krish.core.scene.Entity;

import java.util.*;

public class Model {
    private final String id;
    private List<Entity> entitiesList;
    private List<Mesh> meshList;

    public Model(String id, List<Mesh> meshList) {
        this.id = id;
        this.meshList = meshList;
        entitiesList = new ArrayList<>();
    }

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
