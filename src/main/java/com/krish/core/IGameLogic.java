package com.krish.core;

import com.krish.core.graphics.Renderer;
import com.krish.core.scene.Scene;

public interface IGameLogic {
    void cleanup();

    void init(Window window, Scene scene, Renderer renderer);

    void input(Window window, Scene scene, long diffTimeMillis);

    void update(Window window, Scene scene, long diffTimeMillis);
}
