package com.krish.core.graphics;

import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    public static final String DEFAULT_TEXTURE = "src/main/resources/models/default/default_texture.png";
    private final Map<String, Texture> textureMap;

    /**
     * Create a new texture cache
     */
    public TextureCache() {
        this.textureMap = new HashMap<>();
        this.textureMap.put(DEFAULT_TEXTURE, new Texture(DEFAULT_TEXTURE));
    }

    /**
     * Cleanup the texture cache
     */
    public void cleanup() {
        textureMap.values().forEach(Texture::cleanup);
    }

    /**
     * Create a new texture in cache
     * @param texturePath The path to the texture
     * @return The created texture
     */
    public Texture createTexture(String texturePath) {
        return textureMap.computeIfAbsent(texturePath, Texture::new);
    }

    /**
     * Get a texture from cache
     * @param texturePath The path to the texture
     * @return The requested texture or default
     */
    public Texture getTexture(String texturePath) {
        Texture texture = null;
        if (texturePath != null) {
            texture = textureMap.get(texturePath);
        }
        if (texture == null) {
            texture = textureMap.get(DEFAULT_TEXTURE);
        }
        return texture;
    }
}
