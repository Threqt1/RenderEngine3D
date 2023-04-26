package com.krish.test;

import com.krish.core.*;
import com.krish.core.graphics.Model;
import com.krish.core.graphics.Renderer;
import com.krish.core.scene.Camera;
import com.krish.core.scene.Entity;
import com.krish.core.scene.ModelLoader;
import com.krish.core.scene.scene.Scene;
import com.krish.core.scene.lights.PointLight;
import com.krish.core.scene.lights.SceneLights;
import com.krish.core.scene.lights.SpotLight;
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
    private static final int NUM_CHUNKS = 4;
    private Entity[][] terrainEntities;

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
        Model quadModel = ModelLoader.loadModel("quad-model", Paths.get(Objects.requireNonNull(getClass().getResource("/models/quad/quad.obj")).toURI()).toAbsolutePath().toString(),
                scene.getTextureCache());
        scene.addModel(quadModel);

        int numberOfRows = NUM_CHUNKS * 2 + 1;
        terrainEntities = new Entity[numberOfRows][numberOfRows];
        for (int row = 0; row < numberOfRows; row++) {
            for (int column = 0; column < numberOfRows; column++) {
                Entity entity = new Entity("TERRAIN_" + row + "_" + column, quadModel.getId());
                terrainEntities[row][column] = entity;
                scene.addEntity(entity);
            }
        }

        SceneLights sceneLights = new SceneLights();

        sceneLights.getAmbientLight().setIntensity(0.3f);
        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 0, 0),
                new Vector3f(0, 0, -1.4f), 1.0f));

        Vector3f coneDir = new Vector3f(0, 0, -1);
        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(0, 1, 1),
                new Vector3f(0, 0, -1.4f), 1.0f), coneDir, 140.0f));

        scene.setSceneLights(sceneLights);

        lightsControls = new Lights(scene);
        scene.setGUIInstance(lightsControls);

        Skybox skybox = new Skybox(Paths.get(Objects.requireNonNull(getClass().getResource("/models/skybox/skybox.obj")).toURI()).toAbsolutePath().toString(), scene.getTextureCache());
        skybox.getSkyboxEntity().setScale(50);
        scene.setSkybox(skybox);

        scene.getCamera().moveUp(0.1f);

        updateTerrain(scene);
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
        updateTerrain(scene);
    }

    public void updateTerrain(Scene scene) {
        int cellSize = 10;
        Camera camera = scene.getCamera();
        Vector3f cameraPosition = camera.getPosition();
        int cellCol = (int) (cameraPosition.x() / cellSize);
        int cellRow = (int) (cameraPosition.z() / cellSize);

        int numberOfRows = NUM_CHUNKS * 2 + 1;
        int zOffset = -NUM_CHUNKS;
        float scale = cellSize / 2.0f;
        for (int row = 0; row < numberOfRows; row++) {
            int xOffset = -NUM_CHUNKS;
            for (int column = 0; column < numberOfRows; column++) {
                Entity entity = terrainEntities[row][column];
                entity.setScale(scale);
                entity.setPosition((cellCol + xOffset) * 2.0f, 0, (cellRow + zOffset) * 2.0f);
                entity.getModelMatrix().identity().scale(scale).translate(entity.getPosition());
                xOffset++;
            }
            zOffset++;
        }
    }
}
