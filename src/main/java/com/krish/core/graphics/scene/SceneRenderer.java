package com.krish.core.graphics.scene;

import com.krish.core.graphics.*;
import com.krish.core.scene.Entity;
import com.krish.core.scene.Scene;
import com.krish.core.scene.lights.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class SceneRenderer {
    private static final int MAX_POINT_LIGHTS = 5;
    private static final int MAX_SPOT_LIGHTS = 5;

    private final ShaderManager shaderManager;
    private Uniforms uniforms;

    /**
     * Create a new scene renderer
     */
    public SceneRenderer() {
        //Initialize the shader file names and types
        List<ShaderManager.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderManager.ShaderModuleData("/shaders/scene/scene.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderManager.ShaderModuleData("/shaders/scene/scene.frag", GL_FRAGMENT_SHADER));
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

        uniforms.createUniform("material.ambient");
        uniforms.createUniform("material.diffuse");
        uniforms.createUniform("material.specular");
        uniforms.createUniform("material.reflectance");

        uniforms.createUniform("ambientLight.factor");
        uniforms.createUniform("ambientLight.color");

        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            String name = "pointLights[" + i + "]";
            uniforms.createUniform(name + ".position");
            uniforms.createUniform(name + ".color");
            uniforms.createUniform(name + ".intensity");
            uniforms.createUniform(name + ".att.constant");
            uniforms.createUniform(name + ".att.linear");
            uniforms.createUniform(name + ".att.exponent");
        }

        for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
            String name = "spotLights[" + i + "]";
            uniforms.createUniform(name + ".pl.position");
            uniforms.createUniform(name + ".pl.color");
            uniforms.createUniform(name + ".pl.intensity");
            uniforms.createUniform(name + ".pl.att.constant");
            uniforms.createUniform(name + ".pl.att.linear");
            uniforms.createUniform(name + ".pl.att.exponent");
            uniforms.createUniform(name + ".conedirection");
            uniforms.createUniform(name + ".cutoff");
        }

        uniforms.createUniform("directionLight.color");
        uniforms.createUniform("directionLight.direction");
        uniforms.createUniform("directionLight.intensity");
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

        updateLights(scene);

        //Get all the models and go through their meshes
        Collection<Model> models = scene.getModelMap().values();
        TextureCache textureCache = scene.getTextureCache();
        for (Model model : models) {
            //Get all entities
            List<Entity> entities = model.getEntitiesList();

            for (Material material : model.getMaterialList()) {
                uniforms.setUniform("material.ambient", material.getAmbientColor());
                uniforms.setUniform("material.diffuse", material.getDiffuseColor());
                uniforms.setUniform("material.specular", material.getSpecularColor());
                uniforms.setUniform("material.reflectance", material.getReflectance());
                Texture texture = textureCache.getTexture(material.getTexturePath());
                //Select the texture
                glActiveTexture(GL_TEXTURE0);
                texture.bind();

                //Use texture with all meshes (and entities associated)
                for (SceneMesh sceneMesh : material.getMeshList()) {
                    glBindVertexArray(sceneMesh.getVaoId());
                    for (Entity entity : entities) {
                        uniforms.setUniform("modelMatrix", entity.getModelMatrix());
                        glDrawElements(GL_TRIANGLES, sceneMesh.getVertices(), GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }

        //Deselect VAO
        glBindVertexArray(0);

        //Unbind shaders
        shaderManager.unbind();
    }

    private void updateLights(Scene scene) {
        Matrix4f viewMatrix = scene.getCamera().getViewMatrix();

        SceneLights sceneLights = scene.getSceneLights();
        AmbientLight ambientLight = sceneLights.getAmbientLight();
        uniforms.setUniform("ambientLight.factor", ambientLight.getIntensity());
        uniforms.setUniform("ambientLight.color", ambientLight.getColor());

        DirectionLight directionLight = sceneLights.getDirectionLight();
        Vector4f temporary = new Vector4f(directionLight.getDirection(), 0).mul(viewMatrix);
        uniforms.setUniform("directionLight.color", directionLight.getColor());
        uniforms.setUniform("directionLight.direction", new Vector3f(temporary.x, temporary.y, temporary.z));
        uniforms.setUniform("directionLight.intensity", directionLight.getIntensity());

        List<PointLight> pointLights = sceneLights.getPointLights();
        PointLight pointLight;
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            if (i < pointLights.size()) {
                pointLight = pointLights.get(i);
            } else {
                pointLight = null;
            }
            updatePointLight(pointLight, "pointLights[" + i + "]", viewMatrix);
        }

        List<SpotLight> spotLights = sceneLights.getSpotLights();
        SpotLight spotLight;
        for (int i = 0; i < MAX_SPOT_LIGHTS; i++) {
            if (i < spotLights.size()) {
                spotLight = spotLights.get(i);
            } else {
                spotLight = null;
            }
            updateSpotLight(spotLight, "spotLights[" + i + "]", viewMatrix);
        }
    }

    private void updatePointLight(PointLight pointLight, String prefix, Matrix4f viewMatrix) {
        Vector3f lightPosition = new Vector3f();
        Vector3f color = new Vector3f();
        float intensity = 0.0f;

        float constant = 0.0f;
        float linear = 0.0f;
        float exponent = 0.0f;

        if (pointLight != null) {
            Vector4f temporary = new Vector4f(pointLight.getPosition(), 1).mul(viewMatrix);
            lightPosition.set(temporary.x, temporary.y, temporary.z);
            color.set(pointLight.getColor());
            intensity = pointLight.getIntensity();

            PointLight.Attenuation attenuation = pointLight.getAttenuation();
            constant = attenuation.getConstant();
            linear = attenuation.getLinear();
            exponent = attenuation.getExponent();
        }

        uniforms.setUniform(prefix + ".position", lightPosition);
        uniforms.setUniform(prefix + ".color", color);
        uniforms.setUniform(prefix + ".intensity", intensity);
        uniforms.setUniform(prefix + ".att.constant", constant);
        uniforms.setUniform(prefix + ".att.linear", linear);
        uniforms.setUniform(prefix + ".att.exponent", exponent);
    }

    private void updateSpotLight(SpotLight spotLight, String prefix, Matrix4f viewMatrix) {
        PointLight pointLight = null;
        Vector3f coneDirection = new Vector3f();
        float cutoff = 0.0f;
        if (spotLight != null) {
            coneDirection = spotLight.getConeDirection();
            cutoff = spotLight.getCutoff();
            pointLight = spotLight.getPointLight();
        }

        uniforms.setUniform(prefix + ".conedirection", coneDirection);
        uniforms.setUniform(prefix + ".cutoff", cutoff);
        updatePointLight(pointLight, prefix + ".pl", viewMatrix);
    }
}
