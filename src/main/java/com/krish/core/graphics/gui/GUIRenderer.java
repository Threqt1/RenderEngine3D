package com.krish.core.graphics.gui;

import com.krish.core.IGUIInstance;
import com.krish.core.scene.scene.Scene;
import imgui.*;
import com.krish.core.Window;
import com.krish.core.graphics.ShaderManager;
import com.krish.core.graphics.Texture;
import com.krish.core.graphics.Uniforms;
import imgui.type.ImInt;
import org.joml.Vector2f;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

public class GUIRenderer {
    private GUIMesh mesh;
    private Vector2f scale;
    private final ShaderManager shaderManager;
    private Texture texture;
    private Uniforms uniforms;

    public GUIRenderer(Window window) {
        List<ShaderManager.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderManager.ShaderModuleData("/shaders/gui/gui.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderManager.ShaderModuleData("/shaders/gui/gui.frag", GL_FRAGMENT_SHADER));
        shaderManager = new ShaderManager(shaderModuleDataList);
        createUniforms();
        createUIResources(window);
        setupKeyCallBack(window);
    }

    public void cleanup() {
        shaderManager.cleanup();
        texture.cleanup();
    }

    private void createUIResources(Window window) {
        ImGui.createContext();

        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setIniFilename(null);
        imGuiIO.setDisplaySize(window.getWidth(), window.getHeight());

        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();
        ByteBuffer buf = fontAtlas.getTexDataAsRGBA32(width, height);
        texture = new Texture(width.get(), height.get(), buf);

        mesh = new GUIMesh();
    }

    private void createUniforms() {
        uniforms = new Uniforms(shaderManager.getProgramID());
        uniforms.createUniform("scale");
        scale = new Vector2f();
    }

    public void render(Scene scene) {
        IGUIInstance guiInstance = scene.getGUIInstance();
        if (guiInstance == null) {
            return;
        }
        guiInstance.drawGUI();

        shaderManager.bind();

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glBindVertexArray(mesh.getVaoID());

        glBindBuffer(GL_ARRAY_BUFFER, mesh.getVerticesVBO());
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mesh.getIndicesVBO());

        ImGuiIO io = ImGui.getIO();
        scale.x = 2.0f / io.getDisplaySizeX();
        scale.y = -2.0f / io.getDisplaySizeY();
        uniforms.setUniform("scale", scale);

        ImDrawData drawData = ImGui.getDrawData();
        int numLists = drawData.getCmdListsCount();
        for (int i = 0; i < numLists; i++) {
            glBufferData(GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL_STREAM_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL_STREAM_DRAW);

            int numCmds = drawData.getCmdListCmdBufferSize(i);
            for (int j = 0; j < numCmds; j++) {
                final int elemCount = drawData.getCmdListCmdBufferElemCount(i, j);
                final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
                final int indices = idxBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;

                texture.bind();
                glDrawElements(GL_TRIANGLES, elemCount, GL_UNSIGNED_SHORT, indices);
            }
        }

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glDisable(GL_BLEND);
    }

    public void resize(int width, int height) {
        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setDisplaySize(width, height);
    }

    private void setupKeyCallBack(Window window) {
        glfwSetKeyCallback(window.getWindowHandle(), (handle, key, scancode, action, mods) -> {
                    window.handleKeyPressed(key, action);
                    ImGuiIO io = ImGui.getIO();
                    if (!io.getWantCaptureKeyboard()) {
                        return;
                    }
                    if (action == GLFW_PRESS) {
                        io.setKeysDown(key, true);
                    } else if (action == GLFW_RELEASE) {
                        io.setKeysDown(key, false);
                    }
                    io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
                    io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
                    io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
                    io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
                }
        );

        glfwSetCharCallback(window.getWindowHandle(), (handle, c) -> {
            ImGuiIO io = ImGui.getIO();
            if (!io.getWantCaptureKeyboard()) {
                return;
            }
            io.addInputCharacter(c);
        });
    }
}
