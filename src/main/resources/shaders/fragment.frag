#version 400 core

in vec2 inFragTexture;

out vec4 fragColor;

uniform sampler2D texture_manager;

void main() {
    fragColor = texture(texture_manager, inFragTexture);
}