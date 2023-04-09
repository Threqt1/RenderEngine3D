#version 330

in vec2 outTextureCoordinate;

out vec4 fragmentColor;

struct Material
{
    vec4 diffuse;
};

uniform sampler2D textureSampler;
uniform Material material;

void main()
{
    fragmentColor = texture(textureSampler, outTextureCoordinate) + material.diffuse;
}