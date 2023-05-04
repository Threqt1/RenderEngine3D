package com.krish.test;

import com.krish.core.Engine;
import com.krish.core.IGameLogic;
import com.krish.core.MouseInput;
import com.krish.core.Window;
import com.krish.core.graphics.Model;
import com.krish.core.graphics.Renderer;
import com.krish.core.scene.Camera;
import com.krish.core.scene.Entity;
import com.krish.core.scene.ModelLoader;
import com.krish.core.scene.fog.Fog;
import com.krish.core.scene.lights.AmbientLight;
import com.krish.core.scene.lights.DirectionLight;
import com.krish.core.scene.lights.SceneLights;
import com.krish.core.scene.scene.Scene;
import com.krish.core.scene.skybox.Skybox;
import com.krish.test.controls.Lights;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements IGameLogic {
    private static final float MOUSE_SENSITIVITY = .1f;
    private static final float SCROLL_SENSITIVITY = 2;
    private static final float MOVEMENT_SPEED = 0.005f;
    private Lights lightsControls;

    public static void main(String[] args) throws URISyntaxException {
        Game game = new Game();
        Engine engine = new Engine("Krish Engine", new Window.WindowOptions(), game);
        engine.start();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void init(Window window, Scene scene, Renderer renderer) throws URISyntaxException {
        Model quadModel = ModelLoader.loadModel("terrain-model", Paths.get(Objects.requireNonNull(getClass().getResource("/models/terrain/terrain.obj")).toURI()).toAbsolutePath().toString(),
                scene.getTextureCache());
        scene.addModel(quadModel);
        Entity terrainEntity = new Entity("terrainEntity", "terrain-model");
        terrainEntity.setScale(100.0f);
        terrainEntity.updateModelMatrix();
        scene.addEntity(terrainEntity);

        SceneLights sceneLights = new SceneLights();
        AmbientLight ambientLight = sceneLights.getAmbientLight();

        ambientLight.setIntensity(0.5f);
        ambientLight.setColor(0.3f, 0.3f, 0.3f);

        DirectionLight directionLight = sceneLights.getDirectionLight();
        directionLight.setDirection(0, 1, 0);
        directionLight.setIntensity(1f);

        scene.setSceneLights(sceneLights);

        Skybox skybox = new Skybox(Paths.get(Objects.requireNonNull(getClass().getResource("/models/skybox/skybox.obj")).toURI()).toAbsolutePath().toString(), scene.getTextureCache());
        skybox.getSkyboxEntity().setScale(50);
        scene.setSkybox(skybox);

        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.95f));

        scene.getCamera().moveUp(0.1f);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.moveForward(move);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.moveBackward(move);
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            camera.moveLeft(move);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            camera.moveRight(move);
        }
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            camera.moveUp(move);
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            camera.moveDown(move);
        }

        MouseInput mouseInput = window.getMouseInput();
        if (mouseInput.isRightButtonPressed()) {
            Vector2f displayVector = mouseInput.getDisplayVector();
            camera.addRotation((float) Math.toRadians(-displayVector.x * MOUSE_SENSITIVITY), (float) Math.toRadians(-displayVector.y * MOUSE_SENSITIVITY));
        }
        double scrollWheelMovement = mouseInput.getScrollWheelOffset();
        if (scrollWheelMovement != 0.0) {
            if (scrollWheelMovement > 0.0) camera.moveForward(move * SCROLL_SENSITIVITY);
            else camera.moveBackward(move * SCROLL_SENSITIVITY);
        }
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
    }
}
