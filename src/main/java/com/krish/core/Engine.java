package com.krish.core;

import com.krish.core.graphics.Renderer;
import com.krish.core.scene.Scene;

import java.net.URISyntaxException;

/*
UPS - Updates Per Second
FPS - Frames Per Second
 */

public class Engine {
    public static final int TARGET_UPS = 30;
    private final IGameLogic gameLogic;
    private final Window window;
    private final Renderer renderer;
    private boolean isRunning;
    private final Scene scene;
    private final int targetFPS;
    private final int targetUPS;

    /**
     * Make a new Engine
     * @param windowTitle The title of the window
     * @param opts The window options
     * @param gameLogic A class implementing the game logic interface
     */
    public Engine(String windowTitle, Window.WindowOptions opts, IGameLogic gameLogic) throws URISyntaxException {
        //Create a new window
        this.window = new Window(windowTitle, opts, () -> {
            resize();
            return null;
        });
        //Translate opts into properties
        this.targetFPS = opts.fps;
        this.targetUPS = opts.ups;
        this.gameLogic = gameLogic;
        //Create new renderer
        this.renderer = new Renderer();
        //Initialize new scene
        this.scene = new Scene(window.getWidth(), window.getHeight());

        //Initialize the game logic
        gameLogic.init(window, scene, renderer);
        this.isRunning = true;
    }

    /**
     * Start the game engine
     */
    public void start() {
        isRunning = true;
        run();
    }

    /**
     * Stop the game engine
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Run the game engine
     */
    private void run() {
        long lastTime = System.currentTimeMillis();
        float millisecondsPerUpdate = 1000.0f / targetUPS;
        //If no set FPS, render as frequently as possible
        float millisecondsPerFrame = targetFPS > 0 ? 1000.0f / targetFPS : 0;

        //Time since last update
        float deltaUpdate = 0;
        //Time since last render
        float deltaFPS = 0;

        //Time of last update
        long lastUpdateTime = lastTime;

        while (isRunning && !window.shouldWindowClose()) {
            window.pollEvents();

            //Get current time, and calculate deltas
            long now = System.currentTimeMillis();
            long diff = now - lastTime;
            deltaUpdate += diff / millisecondsPerUpdate;
            deltaFPS += diff / millisecondsPerFrame;

            //Handle inputs
            if (targetFPS <= 0 || deltaFPS >= 1) {
                window.getMouseInput().input();
                gameLogic.input(window, scene, diff);
            }

            //Update the game state as many times as needed
            if (deltaUpdate >= 1) {
                long diffUpdateTimeMillis = now - lastUpdateTime;
                gameLogic.update(window, scene, diffUpdateTimeMillis);
                lastUpdateTime = now;
                deltaUpdate--;
            }

            //Render as many times as needed
            if (targetFPS <= 0 || deltaFPS >= 1) {
                renderer.render(window, scene);
                deltaFPS--;
                window.update();
            }

            lastTime = now;
        }

        cleanup();
    }

    /**
     * Handle resizing the window
     */
    private void resize() {
        //Resize the scene
        scene.resize(window.getWidth(), window.getHeight());
    }

    /**
     * Cleanup the Engine
     */
    private void cleanup() {
        //Cleanup all components of the engine
        gameLogic.cleanup();
        renderer.cleanup();
        scene.cleanup();
        window.cleanup();
    }
}
