#version 330

struct PointLight {    
    vec3 position;
	vec3 color;
};  
#define NR_POINT_LIGHTS 1
uniform PointLight pointLights[NR_POINT_LIGHTS];

const float PI = 3.1415;

varying vec2 TexCoords;
varying vec3 WorldPos;
varying vec3 Normal;
varying vec3 Tangent;
varying mat3 TBN;

uniform vec3 camPos;

uniform int useTexture;

uniform vec3 u_albedo;
uniform float u_metallic;
uniform float u_roughness;
uniform float u_ao;

uniform sampler2D metallicMap;
uniform sampler2D albedoMap;
uniform sampler2D normalMap;
uniform sampler2D roughnessMap;

vec3 Fresnel(vec3 H, vec3 V, vec3 F0)
{
	return F0 + (1.0-F0)*pow(1.0-max(dot(H,V),0.0), 5.0);
}

float DistributionGGX(vec3 N, vec3 H, float roughness)
{
	float a2 = roughness*roughness;
	float NdotH = max(dot(N,H),0.0);
	float NdotH2 = NdotH*NdotH;

	float num = a2;
	float denum = (NdotH2*(a2-1.0) + 1.0);
	denum = (NdotH * a2 - NdotH)*NdotH +  1.0;
	denum = PI* denum *denum;
	return num/max(denum,0.001);
}

float GeometrySchlickGGX(vec3 N, vec3 A, float k)
{
	return max(dot(N,A), 0.0) / max (  ( max(dot(N,A),0.0) *( 1-k) + k ), 0.001);
}

void main()
{
	vec3 albedo = vec3(0.0);
	float metallic = 0.0;
	float roughness = 0.0;

	if(useTexture>0){
		vec3 tex = texture(albedoMap, TexCoords).rgb;
		albedo = pow(tex, vec3(2.2));
		metallic = texture(metallicMap, TexCoords).r;
		roughness  = texture(roughnessMap, TexCoords).r;
	}else{
		albedo = u_albedo;
		metallic = u_metallic;
		roughness = u_roughness;
	}

	//Compute Normal and View Vectors
	vec3 N = normalize(Normal);
	//N = inverse(TBN)*normalize(texture(normalMap, TexCoords).rgb * vec3(2.0) - vec3(1.0));
	vec3 V = normalize(camPos - WorldPos);

	//Compute Direct Lighting
	vec3 Lo = vec3(0.0);
	for(int i = 0; i < NR_POINT_LIGHTS; i++)
	{
		//Compute the light vector
		vec3 L = normalize(pointLights[i].position - WorldPos);
		vec3 H = normalize(V+L);

		//Compute the light radiance
		float d = length(pointLights[i].position - WorldPos);
		float attenuation = 1.0/(1.0f + 0.002*d + 0.004*d*d);
		vec3 radiance = pointLights[i].color*attenuation;
		radiance = pointLights[i].color/(d*d);

		//Microfacet Model
		float k = (roughness + 1)*(roughness + 1) / 8;
		vec3 F0 = vec3(0.04);
		vec3 ks = Fresnel(H,V,F0);
		float NDF = DistributionGGX(N,H,roughness);
		float G  = GeometrySchlickGGX(N,V,k)*GeometrySchlickGGX(N,L,k);
		vec3 cookTorrance = ks*vec3(NDF)*G;
		float denominator = 4.0 * max( dot(V,N), 0.0) * max( dot(L,N) , 0.0);
		cookTorrance /= max(denominator, 0.001);

		//Lambert Model
		vec3 kd = 1 - ks;
		kd *= (1.0-metallic); // = 0 if the surface is metallic
		vec3 lambert = kd * albedo / PI;

		Lo += ( lambert + cookTorrance)*radiance* max(dot(N, L), 0.0);
	}



	vec3 color = pow(Lo, vec3(1.0/2.2)); //Gamma Correction

	gl_FragColor = vec4(color,1.0);
}
