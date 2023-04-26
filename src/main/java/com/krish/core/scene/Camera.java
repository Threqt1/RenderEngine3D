package com.krish.core.scene;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    //A temporary variable used in the Z-axis movement methods
    private Vector3f direction;
    //The position of the camera
    private Vector3f position;
    //A temporary variable used in the X-axis movement methods
    private Vector3f right;
    //The rotation of the camera
    private Vector2f rotation;
    //A temporary variable used in the Y-axis movement methods
    private Vector3f up;
    //The internal view matrix
    private Matrix4f viewMatrix;

    /**
     * Create a new, default camera
     */
    public Camera() {
        direction = new Vector3f();
        right = new Vector3f();
        up = new Vector3f();
        position = new Vector3f();
        viewMatrix = new Matrix4f();
        rotation = new Vector2f();
    }

    /**
     * Recalculate the view matrix
     */
    public void recalculate() {
        viewMatrix.identity().rotateX(rotation.x()).rotateY(rotation.y()).translate(-position.x(), -position.y(), -position.z());
    }

    public void addRotation(float x, float y) {
        rotation.add(x, y);
        recalculate();
    }

    public void moveForward(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        position.add(direction);
        recalculate();
    }

    public void moveBackward(float inc) {
        viewMatrix.positiveZ(direction).negate().mul(inc);
        position.sub(direction);
        recalculate();
    }

    public void moveUp(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.add(up);
        recalculate();
    }

    public void moveDown(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.sub(up);
        recalculate();
    }

    public void moveRight(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        position.add(right);
        recalculate();
    }

    public void moveLeft(float inc) {
        viewMatrix.positiveX(right).mul(inc);
        position.sub(right);
        recalculate();
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }
}
