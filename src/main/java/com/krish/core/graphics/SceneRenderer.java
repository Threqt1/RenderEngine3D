package com.krish.core.graphics;

import com.krish.core.scene.Entity;
import com.krish.core.scene.Scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class SceneRenderer {
    private final ShaderManager shaderManager;
    private Uniforms uniforms;

    /**
     * Create a new scene renderer
     */
    public SceneRenderer() {
        //Initialize the shader file names and types
        List<ShaderManager.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderManager.ShaderModuleData("/shaders/scene.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderManager.ShaderModuleData("/shaders/scene.frag", GL_FRAGMENT_SHADER));
        //Load the shaders
        shaderManager = new ShaderManager(shaderModuleDataList);
        createUniforms();
    }

    /**
     * Initialize all the shader uniforms
     */
    private void createUniforms() {
        uniforms = new Uniforms(shaderManager.getProgramID());
        uniforms.createUniform("projectionMatrix");
        uniforms.createUniform("modelMatrix");
    }

    /**
     * Cleanup the scene renderer
     */
    public void cleanup() {
        shaderManager.cleanup();
    }

    /**
     * Render a scene
     * @param scene The scene to render
     */
    public void render(Scene scene) {
        //Bind the shader manager to the program
        shaderManager.bind();

        //Set the projection matrix uniform
        uniforms.setUniform("projectionMatrix", scene.getProjection().getProjectionMatrix());

        //Get all the models and go through their meshes
        Collection<Model> models = scene.getModelMap().values();
        for (Model model : models) {
            model.getMeshList().forEach(mesh -> {
                //Select the mesh's VAO and get all the entities corresponding to the mesh
                glBindVertexArray(mesh.getVaoId());
                List<Entity> entities = model.getEntitiesList();
                for (Entity entity : entities) {
                    //Set the model matrix to the entity's model matrix
                    this.uniforms.setUniform("modelMatrix", entity.getModelMatrix());
                    //Draw the mesh
                    glDrawElements(GL_TRIANGLES, mesh.getVertices(), GL_UNSIGNED_INT, 0);
                }
            });
        }

        //Deselect VAO
        glBindVertexArray(0);

        //Unbind shaders
        shaderManager.unbind();
    }
}
