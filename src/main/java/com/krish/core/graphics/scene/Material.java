package com.krish.core.graphics.scene;

import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class Material {
    public static final Vector4f DEFAULT_COLOR = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    private final List<SceneMesh> sceneMeshList;
    private String texturePath;
    private Vector4f diffuseColor;

    /**
     * Create a new material
     */
    public Material() {
        diffuseColor = DEFAULT_COLOR;
        sceneMeshList = new ArrayList<>();
    }

    /**
     * Cleanup the material
     */
    public void cleanup() {
        sceneMeshList.forEach(SceneMesh::cleanup);
    }

    public List<SceneMesh> getMeshList() {
        return sceneMeshList;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public void setTexturePath(String texturePath) {
        this.texturePath = texturePath;
    }

    public Vector4f getDiffuseColor() {
        return diffuseColor;
    }

    public void setDiffuseColor(Vector4f diffuseColor) {
        this.diffuseColor = diffuseColor;
    }
}
