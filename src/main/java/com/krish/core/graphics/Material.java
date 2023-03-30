package com.krish.core.graphics;

import java.util.ArrayList;
import java.util.List;

public class Material {
    private final List<Mesh> meshList;
    private String texturePath;

    /**
     * Create a new material
     */
    public Material() {
        meshList = new ArrayList<>();
    }

    /**
     * Cleanup the material
     */
    public void cleanup() {
        meshList.forEach(Mesh::cleanup);
    }

    public List<Mesh> getMeshList() {
        return meshList;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }
}
