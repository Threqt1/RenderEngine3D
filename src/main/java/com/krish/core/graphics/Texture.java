package com.krish.core.graphics;

import org.lwjgl.system.MemoryStack;

import java.nio.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private int textureId;
    private final String texturePath;

    /**
     * Create a new texture
     * @param width Texture width
     * @param height Texture height
     * @param buffer Texture buffer
     */
    public Texture(int width, int height, ByteBuffer buffer) {
        this.texturePath = "";
        generateTexture(width, height, buffer);
    }

    /**
     * Create a new texture
     * @param texturePath Path to the texture
     */
    public Texture(String texturePath) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            this.texturePath = texturePath;

            //Make buffers for image data
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            //Create a buffer to store image data (using STB)
            ByteBuffer buffer = stbi_load(texturePath, widthBuffer, heightBuffer, channelsBuffer, 4);
            if (buffer == null) {
                throw new RuntimeException("Image file [" + texturePath + "] not loaded: " + stbi_failure_reason());
            }

            int width = widthBuffer.get();
            int height = heightBuffer.get();

            generateTexture(width, height, buffer);

            stbi_image_free(buffer);
        }
    }

    /**
     * Select the texture
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, this.textureId);
    }

    /**
     * Cleanup the texture
     */
    public void cleanup() {
        glDeleteTextures(this.textureId);
    }

    /**
     * Generate a new texture
     * @param width The width of the texture
     * @param height The height of the texture
     * @param buffer The image buffer
     */
    public void generateTexture(int width, int height, ByteBuffer buffer) {
        this.textureId = glGenTextures();

        //Select texture
        glBindTexture(GL_TEXTURE_2D, this.textureId);
        //Set unpack alignment
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        //Set min and mag filters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        //Set 2D texture information
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0,
                GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        //Generate mipmap
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public String getTexturePath() {
        return texturePath;
    }
}
