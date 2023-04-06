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
        uniforms.createUniform("viewMatrix");
        uniforms.createUniform("textureSampler");
        uniforms.createUniform("material.diffuse");
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
        uniforms.setUniform("viewMatrix", scene.getCamera().getViewMatrix());

        //Set texture sampler uniform
        uniforms.setUniform("textureSampler", 0);

        //Get all the models and go through their meshes
        Collection<Model> models = scene.getModelMap().values();
        TextureCache textureCache = scene.getTextureCache();
        for (Model model : models) {
            //Get all entities
            List<Entity> entities = model.getEntitiesList();

            for (Material material : model.getMaterialList()) {
                uniforms.setUniform("material.diffuse", material.getDiffuseColor());
                Texture texture = textureCache.getTexture(material.getTexturePath());
                //Select the texture
                glActiveTexture(GL_TEXTURE0);
                texture.bind();

                //Use texture with all meshes (and entities associated)
                for (Mesh mesh : material.getMeshList()) {
                    glBindVertexArray(mesh.getVaoId());
                    for (Entity entity : entities) {
                        uniforms.setUniform("modelMatrix", entity.getModelMatrix());
                        glDrawElements(GL_TRIANGLES, mesh.getVertices(), GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }

        //Deselect VAO
        glBindVertexArray(0);

        //Unbind shaders
        shaderManager.unbind();
    }
}
