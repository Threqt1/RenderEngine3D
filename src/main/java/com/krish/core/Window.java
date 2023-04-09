package com.krish.core;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;

import java.util.concurrent.Callable;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    //The OpenGL window handle ID
    private final long windowHandle;
    private int width;
    private int height;
    private final MouseInput mouseInput;
    private final Callable<Void> resizeFunction;

    /**
     * Create a new Window
     * @param title The title of the window
     * @param opts Any options associated with the window
     * @param resizeFunction The function to handle window resizing
     */
    public Window(String title, WindowOptions opts, Callable<Void> resizeFunction) {
        //Set error callback
        GLFWErrorCallback.createPrint(System.err).set();

        this.resizeFunction = resizeFunction;

        //Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Set default window hints
        glfwDefaultWindowHints();
        //Visibility
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        //Resizability
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        //Context minor/major
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

        //Compatibility profile
        if (opts.compatibleProfile) {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
        } else {
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        }

        //Set width/height
        if (opts.width > 0 && opts.height > 0) {
            this.width = opts.width;
            this.height = opts.height;
        } else {
            //Maximize window
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            //Set width/height to max monitor size
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (vidMode != null) {
                width = vidMode.width();
                height = vidMode.height();
            } else {
                width = 0;
                height = 0;
            }
        }

        //Create window
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        //Handle resizing
        glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> handleResizing(w, h));

        //Handle key callback
        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> handleKeyPressed(key, action));

        //Make the current GLFW context the window
        glfwMakeContextCurrent(windowHandle);

        //If FPS is limited, turn off V-SYNC
        if (opts.fps > 0) {
            glfwSwapInterval(0);
        } else {
            glfwSwapInterval(1);
        }

        //Show the window
        glfwShowWindow(windowHandle);

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetFramebufferSize(windowHandle, arrWidth, arrHeight);
        width = arrWidth[0];
        height = arrHeight[0];

        mouseInput = new MouseInput(windowHandle);
    }

    /**
     * Update the window data
     */
    public void update() {
        glfwSwapBuffers(windowHandle);
    }

    /**
     * Poll events
     */
    public void pollEvents() {
        glfwPollEvents();
    }

    /**
     * Check if a key is pressed
     * @param key The keycode of the key
     * @return If the key is pressed or not
     */
    public boolean isKeyPressed(int key) {
        return glfwGetKey(windowHandle, key) == GLFW_PRESS;
    }

    /**
     * Handle the key pressed (for closing the window)
     * @param key The key to check
     * @param action The action to check
     */
    public void handleKeyPressed(int key, int action) {
        //If escape key is released
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(windowHandle, true);
        }
    }

    /**
     * Handle resizing the window
     * @param newWidth The new width
     * @param newHeight The new height
     */
    protected void handleResizing(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
        try {
            this.resizeFunction.call();
        } catch (Exception e) {
            System.out.println("Exception in resize callback: ");
            e.printStackTrace();
        }
    }

    /**
     * @return If the window should close or not
     */
    public boolean shouldWindowClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    /**
     * Cleanup the Window after closing
     */
    public void cleanup() {
        //Free any callbacks
        glfwFreeCallbacks(windowHandle);
        //Destroy the window
        glfwDestroyWindow(windowHandle);
        //Terminate GLFW
        glfwTerminate();
        //Free error callback
        GLFWErrorCallback cb = glfwSetErrorCallback(null);
        if (cb != null) {
            cb.free();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public MouseInput getMouseInput() {
        return mouseInput;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public static class WindowOptions {
        public boolean compatibleProfile;
        public int width;
        public int height;
        public int fps;
        public int ups = Engine.TARGET_UPS;
    }
}
