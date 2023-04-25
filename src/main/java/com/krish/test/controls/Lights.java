package com.krish.test.controls;

import com.krish.core.IGUIInstance;
import com.krish.core.MouseInput;
import com.krish.core.Window;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import org.joml.*;

import com.krish.core.scene.scene.Scene;
import com.krish.core.scene.lights.*;

public class Lights implements IGUIInstance {
    float[] ambientColor;
    float[] ambientIntensity;

    float[] directionLightColor;
    float[] directionLightIntensity;

    float[] directionLightX;
    float[] directionLightY;
    float[] directionLightZ;

    float[] pointLightColor;
    float[] pointLightIntensity;

    float[] pointLightX;
    float[] pointLightY;
    float[] pointLightZ;

    float[] spotLightColor;
    float[] spotLightIntensity;
    float[] spotLightCutoffAngle;

    float[] spotLightX;
    float[] spotLightY;
    float[] spotLightZ;

    float[] spotLightConeDirectionX;
    float[] spotLightConeDirectionY;
    float[] spotLightConeDirectionZ;

    public Lights(Scene scene) {
        SceneLights sceneLights = scene.getSceneLights();
        AmbientLight ambientLight = sceneLights.getAmbientLight();

        Vector3f colorVector;
        Vector3f positionVector;

        colorVector = ambientLight.getColor();
        ambientIntensity = new float[] {ambientLight.getIntensity()};
        ambientColor = new float[] {colorVector.x, colorVector.y, colorVector.z};

        PointLight pointLight = sceneLights.getPointLights().get(0);
        colorVector = pointLight.getColor();
        positionVector = pointLight.getPosition();

        pointLightColor = new float[] {colorVector.x, colorVector.y, colorVector.z};
        pointLightIntensity = new float[] {pointLight.getIntensity()};
        pointLightX = new float[] {positionVector.x};
        pointLightY = new float[] {positionVector.y};
        pointLightZ = new float[] {positionVector.z};

        SpotLight spotLight = sceneLights.getSpotLights().get(0);
        pointLight = spotLight.getPointLight();
        colorVector = pointLight.getColor();
        positionVector = pointLight.getPosition();

        spotLightColor = new float[] {colorVector.x, colorVector.y, colorVector.z};
        spotLightIntensity = new float[] {pointLight.getIntensity()};
        spotLightX = new float[] {positionVector.x};
        spotLightY = new float[] {positionVector.y};
        spotLightZ = new float[] {positionVector.z};

        spotLightCutoffAngle = new float[] {spotLight.getCutoffAngle()};
        Vector3f coneDirection = spotLight.getConeDirection();
        spotLightConeDirectionX = new float[]{coneDirection.x};
        spotLightConeDirectionY = new float[]{coneDirection.y};
        spotLightConeDirectionZ = new float[]{coneDirection.z};

        DirectionLight directionLight = sceneLights.getDirectionLight();
        colorVector = directionLight.getColor();
        positionVector = directionLight.getDirection();

        directionLightColor = new float[] {colorVector.x, colorVector.y, colorVector.z};
        directionLightIntensity = new float[] {directionLight.getIntensity()};
        directionLightX = new float[] {positionVector.x};
        directionLightY = new float[] {positionVector.y};
        directionLightZ = new float[] {positionVector.z};
    }


    @Override
    public void drawGUI() {
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.setWindowSize(1000, 1000);

        ImGui.begin("Lights Controls");

        if (ImGui.collapsingHeader("Ambient Light")) {
            ImGui.sliderFloat("Ambient Light Intensity", ambientIntensity, 0.0f, 1.0f, "%.2f");
            ImGui.colorEdit3("Ambient Light Color", ambientColor);
        }

        if (ImGui.collapsingHeader("Point Light")) {
            ImGui.sliderFloat("Point Light Intensity", pointLightIntensity, 0.0f, 1.0f, "%.2f");
            ImGui.colorEdit3("Point Light Color", pointLightColor);
            ImGui.sliderFloat("Point Light Position X", pointLightX, -10.0f, 10.0f, "%.2f");
            ImGui.sliderFloat("Point Light Position Y", pointLightY, -10.0f, 10.0f, "%.2f");
            ImGui.sliderFloat("Point Light Position Z", pointLightZ, -10.0f, 10.0f, "%.2f");
        }

        if (ImGui.collapsingHeader("Spot Light")) {
            ImGui.sliderFloat("Spot Light Intensity", spotLightIntensity, 0.0f, 1.0f, "%.2f");
            ImGui.colorEdit3("Spot Light Color", spotLightColor);
            ImGui.sliderFloat("Spot Light Position X", spotLightX, -10.0f, 10.0f, "%.2f");
            ImGui.sliderFloat("Spot Light Position Y", spotLightY, -10.0f, 10.0f, "%.2f");
            ImGui.sliderFloat("Spot Light Position Z", spotLightZ, -10.0f, 10.0f, "%.2f");
            ImGui.separator();
            ImGui.sliderFloat("Spot Light Cutoff Angle", spotLightCutoffAngle, 0.0f, 360.0f, "%.2f");
            ImGui.sliderFloat("Spot Light Direction Cone X", spotLightConeDirectionX, -1.0f, 1.0f, "%.2f");
            ImGui.sliderFloat("Spot Light Direction Cone Y", spotLightConeDirectionY, -1.0f, 1.0f, "%.2f");
            ImGui.sliderFloat("Spot Light Direction Cone Z", spotLightConeDirectionZ, -1.0f, 1.0f, "%.2f");
        }

        if (ImGui.collapsingHeader("Direction Light")) {
            ImGui.sliderFloat("Direction Light Intensity", directionLightIntensity, 0.0f, 1.0f, "%.2f");
            ImGui.colorEdit3("Direction Light Color", directionLightColor);
            ImGui.sliderFloat("Direction Light Position X", directionLightX, -1.0f, 1.0f, "%.2f");
            ImGui.sliderFloat("Direction Light Position Y", directionLightY, -1.0f, 1.0f, "%.2f");
            ImGui.sliderFloat("Direction Light Position Z", directionLightZ, -1.0f, 1.0f, "%.2f");
        }

        ImGui.end();
        ImGui.endFrame();
        ImGui.render();
    }

    @Override
    public boolean isGUIInput(Scene scene, Window window) {
        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f mousePosition = mouseInput.getCurrentPosition();

        imGuiIO.setMousePos(mousePosition.x, mousePosition.y);
        imGuiIO.setMouseDown(0, mouseInput.isLeftButtonPressed());
        imGuiIO.setMouseDown(1, mouseInput.isRightButtonPressed());

        boolean consumed = imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
        if (consumed) {
            SceneLights sceneLights = scene.getSceneLights();

            AmbientLight ambientLight = sceneLights.getAmbientLight();
            ambientLight.setIntensity(ambientIntensity[0]);
            ambientLight.setColor(ambientColor[0], ambientColor[1], ambientColor[2]);

            PointLight pointLight = sceneLights.getPointLights().get(0);
            pointLight.setIntensity(pointLightIntensity[0]);
            pointLight.setColor(pointLightColor[0], pointLightColor[1], pointLightColor[2]);
            pointLight.setPosition(pointLightX[0], pointLightY[0], pointLightZ[0]);

            SpotLight spotLight = sceneLights.getSpotLights().get(0);
            pointLight = spotLight.getPointLight();
            pointLight.setIntensity(spotLightIntensity[0]);
            pointLight.setPosition(spotLightX[0], spotLightY[0], spotLightZ[0]);
            pointLight.setColor(spotLightColor[0], spotLightColor[1], spotLightColor[2]);
            spotLight.setCutoffAngle(spotLightCutoffAngle[0]);
            spotLight.setConeDirection(spotLightConeDirectionX[0], spotLightConeDirectionY[0], spotLightConeDirectionZ[0]);

            DirectionLight directionLight = sceneLights.getDirectionLight();
            directionLight.setIntensity(directionLightIntensity[0]);
            directionLight.setColor(directionLightColor[0], directionLightColor[1], directionLightColor[2]);
            directionLight.setDirection(directionLightX[0], directionLightY[0], directionLightZ[0]);
        }
        return consumed;
    }
}
