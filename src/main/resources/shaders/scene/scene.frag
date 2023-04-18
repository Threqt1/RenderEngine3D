#version 330

const int MAX_POINT_LIGHTS = 5;
const int MAX_SPOT_LIGHTS = 5;
const float SPECULAR_POWER = 10;

in vec3 outPosition;
in vec3 outNormal;
in vec2 outTextureCoordinate;

out vec4 fragmentColor;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float reflectance;
};

struct AmbientLight {
    vec3 color;
    float factor;
};

struct PointLight {
    vec3 position;
    vec3 color;
    float intensity;
    Attenuation att;
};

struct DirectionLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

uniform sampler2D textureSampler;
uniform Material material;
uniform AmbientLight ambientLight;
uniform DirectionLight directionLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];

vec4 calculateAmbient(AmbientLight ambientLight, vec4 ambient) {
    return vec4(ambientLight.factor * ambientLight.color, 1) * ambient;
}

vec4 calculateLightColor(vec4 diffuse, vec4 specular, vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDirection, vec3 normal) {
    float diffuseFactor = max(dot(normal, toLightDirection), 0.0);
    vec4 diffuseColor = diffuse * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDirection = -toLightDirection;
    vec3 reflectedLight = normalize(reflect(fromLightDirection, normal));

    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    vec4 specularColor = specular * vec4(lightColor, 1.0) * lightIntensity * specularFactor * material.reflectance;

    return (specularColor + diffuseColor);
}

vec4 calculatePointLight(vec4 diffuse, vec4 specular, PointLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec4 lightColor = calculateLightColor(diffuse, specular, light.color, light.intensity, position, toLightDirection, normal);

    float distance = length(lightDirection);
    float attentuationInverse = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
    return lightColor / attentuationInverse;
}

vec4 calculateDirectionLight(vec4 diffuse, vec4 specular, DirectionLight light, vec3 position, vec3 normal) {
    return calculateLightColor(diffuse, specular, light.color, light.intensity, position, normalize(light.direction), normal);
}

void main()
{
    vec4 textureColor = texture(textureSampler, outTextureCoordinate) + material.diffuse;
    vec4 ambientColor = calculateAmbient(ambientLight, textureColor + material.ambient);
    vec4 diffuseColor = textureColor + material.diffuse;
    vec4 specularColor = textureColor + material.specular;

    vec4 diffuseSpecularComposition = calculateDirectionLight(diffuseColor, specularColor, directionLight, outPosition, outNormal);

    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (pointLights[i].intensity > 0) {
            diffuseSpecularComposition += calculatePointLight(diffuseColor, specularColor, pointLights[i], outPosition, outNormal);
        }
    }

    fragmentColor = ambientColor + diffuseSpecularComposition;
}