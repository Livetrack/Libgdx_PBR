# Implementation of Physically Based Rendering with OpenGL and Libgdx

Here is a small implementation of PBR with OpenGL and Libgdx. Libgdx makes it very easy to manage memory and buffers, so we only need to care about the shaders.
This implementation is based on Joey DeVries's tutorial https://learnopengl.com/PBR/Lighting.

Following Xoppa's tutorial https://xoppa.github.io/blog/creating-a-shader-with-libgdx/, the program creates a new Shader class (PBRShader) that implements the methods that a usual shader needs. It manages the vertex shader and the fragment shaders. The vertex shader passes the world position, the normal and the texture coordinate of the vertex. It also passes the tangent and binormal vector, for a future implementation of normal mapping.

All the computation is done in the fragment shader. It can split in two main computations : Lambert model (Diffuse reflection) and GGX model (used for specular reflexion). Though, other model can be used instead of GGX : Cook-Torrance, Beckmann...

