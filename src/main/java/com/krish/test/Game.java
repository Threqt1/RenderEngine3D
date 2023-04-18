package com.krish.test;

import com.krish.core.*;
import com.krish.core.graphics.scene.Model;
import com.krish.core.graphics.Renderer;
import com.krish.core.scene.Camera;
import com.krish.core.scene.Entity;
import com.krish.core.scene.ModelLoader;
import com.krish.core.scene.Scene;
import com.krish.core.scene.lights.PointLight;
import com.krish.core.scene.lights.SceneLights;
import com.krish.core.scene.lights.SpotLight;
import org.joml.Vector2f;

import imgui.*;
import imgui.flag.ImGuiCond;
import org.joml.Vector3f;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements IGameLogic, IGUIInstance {
    private static final float MOUSE_SENSITIVITY = .1f;
    private static final float SCROLL_SENSITIVITY = 2;
    private static final float MOVEMENT_SPEED = 0.005f;
    private Entity cube;
    private float rotation;

    public static void main(String[] args) throws URISyntaxException {
        Game game = new Game();
        Engine engine = new Engine("Krish Engine", new Window.WindowOptions(), game);
        engine.start();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void drawGUI() {
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.showDemoWindow();
        ImGui.endFrame();
        ImGui.render();
    }

    @Override
    public boolean isGUIInput(Scene scene, Window window) {
        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f mousePos = mouseInput.getCurrentPosition();
        imGuiIO.setMousePos(mousePos.x, mousePos.y);
        imGuiIO.setMouseDown(0, mouseInput.isLeftButtonPressed());
        imGuiIO.setMouseDown(1, mouseInput.isRightButtonPressed());

        return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
    }

    @Override
    public void init(Window window, Scene scene, Renderer renderer) throws URISyntaxException {
        Model cubeModel = ModelLoader.loadModel("cube-model", Paths.get(Objects.requireNonNull(getClass().getResource("/models/cube/cube.obj")).toURI()).toAbsolutePath().toString(),
                scene.getTextureCache());
        scene.addModel(cubeModel);

        cube = new Entity("cube-entity", cubeModel.getId());
        cube.setPosition(0, 0, -2);
        scene.addEntity(cube);

        SceneLights sceneLights = new SceneLights();
        scene.setSceneLights(sceneLights);

        sceneLights.getAmbientLight().setIntensity(0.3f);
        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 0, 0),
                new Vector3f(0, 0, -1.4f), 1.0f));

        Vector3f coneDir = new Vector3f(0, 0, -1);
        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(0, 1, 1),
                new Vector3f(0, 0, -1.4f), 1.0f), coneDir, 140.0f));

        scene.setGUIInstance(this);
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
        rotation += 1.5;
        if (rotation > 360) {
            rotation = 0;
        }
        this.cube.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        this.cube.updateModelMatrix();
    }


}
