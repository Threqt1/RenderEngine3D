package com.krish.core;

import com.krish.core.entities.Model;
import com.krish.core.utils.Utils;
import com.krish.test.Launcher;

import static org.lwjgl.opengl.GL30.*;

public class RenderManager {
    private final WindowManager windowManager;
    private ShaderManager shader;

    public RenderManager() {
        this.windowManager = Launcher.getWindow();
    }

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vert"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.frag"));
        shader.link();
        //shader.createUniform("textureSampler");
    }

    public void render(Model model) {
        clear();
        shader.bind();
        //shader.setUniform("textureSampler", 0);
        glBindVertexArray(model.getId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, model.getTexture().getId());
        glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.unbind();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        shader.cleanup();
    }
}
