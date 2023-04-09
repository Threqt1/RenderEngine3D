package com.krish.core.graphics;

import com.krish.core.Window;
import com.krish.core.graphics.gui.GUIRenderer;
import com.krish.core.graphics.scene.SceneRenderer;
import com.krish.core.scene.Scene;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private final SceneRenderer sceneRenderer;
    private final GUIRenderer guiRenderer;

    /**
     * Create a new renderer
     */
    public Renderer(Window window) {
        GL.createCapabilities();
        //Enable depth testing
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        this.sceneRenderer = new SceneRenderer();
        this.guiRenderer = new GUIRenderer(window);
    }

    /**
     * Cleanup the renderer
     */
    public void cleanup() {
        guiRenderer.cleanup();
        sceneRenderer.cleanup();
    }

    public void render(Window window, Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());

        sceneRenderer.render(scene);
        guiRenderer.render(scene);
    }

    public void resize(int width, int height) {
        guiRenderer.resize(width, height);
    }
}
