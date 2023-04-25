package com.krish.core;

import com.krish.core.scene.scene.Scene;

public interface IGUIInstance {
    void drawGUI();

    boolean isGUIInput(Scene scene, Window window);
}
