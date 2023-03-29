package com.krish.core.scene;

import org.joml.Matrix4f;

public class Projection {
    // The field of view
    private static final float FOV = (float) Math.toRadians(60.0f);
    //The farthest Z value at which to clip at
    private static final float Z_FAR = 1000.0f;
    //The closest Z value at which to clip at
    private static final float Z_NEAR = 0.01f;

    //The projection matrix
    private Matrix4f projectionMatrix;

    /**
     * Create a new projection matrix
     * @param width The width
     * @param height The height
     */
    public Projection(int width, int height) {
        projectionMatrix = new Matrix4f();
        updateProjectionMatrix(width, height);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    /**
     * Update the projection matrix based on a new width or height
     * @param width The new width
     * @param height The new height
     */
    public void updateProjectionMatrix(int width, int height) {
        projectionMatrix.setPerspective(FOV, (float) width / height, Z_NEAR, Z_FAR);
    }
}
