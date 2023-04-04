package com.krish.core;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    private final Vector2f currentPosition;
    private final Vector2f displayVector;
    private boolean inWindow;
    private boolean leftButtonPressed;
    private final Vector2f previousPosition;
    private boolean rightButtonPressed;

    public MouseInput(long windowHandle) {
        previousPosition = new Vector2f(-1, -1);
        currentPosition = new Vector2f();
        displayVector = new Vector2f();
        leftButtonPressed = false;
        rightButtonPressed = false;
        inWindow = false;

        glfwSetCursorPosCallback(windowHandle, (handle, xpos, ypos) -> {
            currentPosition.x = (float) xpos;
            currentPosition.y = (float) ypos;
        });

        glfwSetCursorEnterCallback(windowHandle, (handle, entered) -> {
            inWindow = entered;
        });
        glfwSetMouseButtonCallback(windowHandle, (handle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
        });
    }

    public void input() {
        displayVector.zero();
        if (previousPosition.x > 0 && previousPosition.y > 0 && inWindow) {
            double deltaX = currentPosition.x - previousPosition.x;
            double deltaY = currentPosition.y - previousPosition.y;
            boolean rotateX = deltaX != 0;
            boolean rotateY = deltaY != 0;
            if (rotateX) {
                displayVector.y = (float) deltaX;
            }
            if (rotateY) {
                displayVector.x = (float) deltaY;
            }
        }
        previousPosition.x = currentPosition.x;
        previousPosition.y = currentPosition.y;
    }

    public Vector2f getCurrentPosition() {
        return currentPosition;
    }

    public Vector2f getDisplayVector() {
        return displayVector;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }
}
