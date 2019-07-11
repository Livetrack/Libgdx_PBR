#version 330

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
attribute vec3 a_tangent;
attribute vec3 a_binormal;

uniform mat4 worldTrans;
uniform mat4 projViewTrans;

varying vec2 TexCoords;
varying vec3 WorldPos;
varying vec3 Normal;
varying vec3 Tangent;
varying mat3 TBN;


void main() {
	TexCoords = a_texCoord0;
	WorldPos = (worldTrans * vec4(a_position, 1.0)).xyz;
	Normal = mat3(transpose(inverse(worldTrans))) * a_normal;
	Tangent  = mat3(transpose(inverse(worldTrans))) * a_tangent;
	vec3 BiNormal = mat3(transpose(inverse(worldTrans))) * a_binormal;
	TBN = transpose(mat3(Tangent, BiNormal, Normal));
	gl_Position = projViewTrans*worldTrans*vec4(a_position,1.0);
}
