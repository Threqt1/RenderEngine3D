package com.krish.core.graphics.skybox;

import com.krish.core.graphics.*;
import com.krish.core.scene.Entity;
import com.krish.core.scene.scene.Scene;
import com.krish.core.scene.skybox.Skybox;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class SkyboxRenderer {
    private ShaderManager shaderManager;
    private Uniforms uniforms;
    private Matrix4f viewMatrix;

    public SkyboxRenderer() {
        List<ShaderManager.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderManager.ShaderModuleData("/shaders/skybox/skybox.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderManager.ShaderModuleData("/shaders/skybox/skybox.frag", GL_FRAGMENT_SHADER));
        shaderManager = new ShaderManager(shaderModuleDataList);
        viewMatrix = new Matrix4f();
        createUniforms();
    }

    public void cleanup() {
        shaderManager.cleanup();
    }

    private void createUniforms() {
        uniforms = new Uniforms(shaderManager.getProgramID());
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("viewMatrix");
        uniforms.createUniform("modelMatrix");
        uniforms.createUniform("diffuse");
        uniforms.createUniform("textureSampler");
        uniforms.createUniform("hasTexture");
    }

    public void render(Scene scene) {
        Skybox skybox = scene.getSkybox();
        if (skybox == null) {
            return;
        }
        shaderManager.bind();

        uniforms.setUniform("projectionMatrix", scene.getProjection().getProjectionMatrix());
        viewMatrix.set(scene.getCamera().getViewMatrix());
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);
        uniforms.setUniform("viewMatrix", viewMatrix);
        uniforms.setUniform("textureSampler", 0);

        Model skyboxModel = skybox.getSkyboxModel();
        Entity skyboxEntity = skybox.getSkyboxEntity();
        TextureCache textureCache = scene.getTextureCache();
        for (Material material : skyboxModel.getMaterialList()) {
            Texture texture = textureCache.getTexture(material.getTexturePath());
            glActiveTexture(GL_TEXTURE0);
            texture.bind();

            uniforms.setUniform("diffuse", material.getDiffuseColor());
            uniforms.setUniform("hasTexture", texture.getTexturePath().equals(TextureCache.DEFAULT_TEXTURE) ? 0 : 1);

            for (Mesh mesh : material.getMeshList()) {
                glBindVertexArray(mesh.getVaoId());

                uniforms.setUniform("modelMatrix", skyboxEntity.getModelMatrix());
                glDrawElements(GL_TRIANGLES, mesh.getVertices(), GL_UNSIGNED_INT, 0);
            }
        }

        glBindVertexArray(0);

        shaderManager.unbind();
    }
}
