package com.krish.core;

import com.krish.core.graphics.Renderer;
import com.krish.core.scene.Scene;

/*
UPS - Updates Per Second
FPS - Frames Per Second
 */

public class Engine {
    public static final int TARGET_UPS = 30;
    private final IGameLogic gameLogic;
    private final Window window;
    private Renderer renderer;
    private boolean isRunning;
    private Scene scene;
    private int targetFPS;
    private int targetUPS;

    public Engine(String windowTitle, Window.WindowOptions opts, IGameLogic gameLogic) {
        this.window = new Window(windowTitle, opts, () -> {
            resize();
            return null;
        });
        this.targetFPS = opts.fps;
        this.targetUPS = opts.ups;
        this.gameLogic = gameLogic;
        this.renderer = new Renderer();
        this.scene = new Scene(window.getWidth(), window.getHeight());

        gameLogic.init(window, scene, renderer);
        this.isRunning = true;
    }

    public void start() {
        isRunning = true;
        run();
    }

    public void stop() {
        isRunning = false;
    }

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

    private void resize() {
        scene.resize(window.getWidth(), window.getHeight());
    }

    private void cleanup() {
        gameLogic.cleanup();
        renderer.cleanup();
        scene.cleanup();
        window.cleanup();
    }
}
