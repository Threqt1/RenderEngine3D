package com.krish.core.graphics;

import com.krish.core.scene.Entity;
import com.krish.core.scene.Scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class SceneRenderer {
    private ShaderManager shaderManager;
    private Uniforms uniforms;

    public SceneRenderer() {
        List<ShaderManager.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderManager.ShaderModuleData("/shaders/scene.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderManager.ShaderModuleData("/shaders/scene.frag", GL_FRAGMENT_SHADER));
        shaderManager = new ShaderManager(shaderModuleDataList);
        createUniforms();
    }

    private void createUniforms() {
        uniforms = new Uniforms(shaderManager.getProgramID());
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("modelMatrix");
    }

    public void cleanup() {
        shaderManager.cleanup();
    }

    public void render(Scene scene) {
        shaderManager.bind();

        uniforms.setUniform("projectionMatrix", scene.getProjection().getProjectionMatrix());

        Collection<Model> models = scene.getModelMap().values();
        for (Model model : models) {
            model.getMeshList().forEach(mesh -> {
                glBindVertexArray(mesh.getVaoId());
                List<Entity> entities = model.getEntitiesList();
                for (Entity entity : entities) {
                    this.uniforms.setUniform("modelMatrix", entity.getModelMatrix());
                    glDrawElements(GL_TRIANGLES, mesh.getVertices(), GL_UNSIGNED_INT, 0);
                }
            });
        }

        glBindVertexArray(0);

        shaderManager.unbind();
    }
}
