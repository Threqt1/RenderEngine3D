package com.krish.test;

import com.krish.core.EngineManager;
import com.krish.core.WindowManager;

public class Launcher {
    public static WindowManager window;
    public static EngineManager engine;

    public static void main(String[] args) {
        window = new WindowManager("TEST", 1600, 900, false);
        engine = new EngineManager();

        try {
            engine.start();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static WindowManager getWindow() {
        return window;
    }
}
