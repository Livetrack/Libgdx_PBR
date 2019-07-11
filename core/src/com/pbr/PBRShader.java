package com.pbr;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class PBRShader implements Shader {
	private ShaderProgram program;
	private Camera camera;
	private RenderContext context;
	
	//Parameters of the camera
	int projViewTrans;
    int worldTrans;
    int camPos;
    
    //Default values if we don't use textures
    int albedo;
    int metallic;
    int roughness;
    int ao;
    
    
    int useTexture;
    
    int albedoMap;
    int metallicMap;
    int roughnessMap;
    int normalMap;
    
    Texture albedoTexture;
    Texture metallicTexture;
    Texture roughnessTexture;
    Texture normalTexture;
	
	@Override
	public void dispose() {
		program.dispose();
	}

	@Override
	public void init() {
		// Setting up the shader program
		String vert = Gdx.files.internal("PBRVShader.glsl").readString();
        String frag = Gdx.files.internal("PBRFShader.glsl").readString();
        program = new ShaderProgram(vert, frag);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        
        projViewTrans = program.getUniformLocation("projViewTrans");
        worldTrans = program.getUniformLocation("worldTrans");
        camPos = program.getUniformLocation("camPos");
        
        useTexture = program.getUniformLocation("useTexture");
        
        albedo = program.getUniformLocation("u_albedo");
        metallic = program.getUniformLocation("u_metallic");
        ao = program.getUniformLocation("u_ao");
        roughness = program.getUniformLocation("u_roughness");
        
        albedoMap = program.getUniformLocation("albedoMap");
        roughnessMap = program.getUniformLocation("roughnessMap");
        normalMap = program.getUniformLocation("normalMap");
        metallicMap = program.getUniformLocation("metallicMap");
        
        metallicTexture = new Texture(Gdx.files.internal("rustediron2_metallic.png"), Pixmap.Format.LuminanceAlpha, true);
        albedoTexture = new Texture(Gdx.files.internal("rustediron2_basecolor.png"), true);
        roughnessTexture = new Texture(Gdx.files.internal("rustediron2_roughness.png"), Pixmap.Format.LuminanceAlpha, true);
        normalTexture = new Texture(Gdx.files.internal("rustediron2_normal.png"), true);
	}

	@Override
	public int compareTo(Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		return true;
	}

	@Override
	public void begin(Camera camera, RenderContext context) {
		program.begin();
		this.camera = camera;
		this.context = context;
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
        program.setUniformMatrix(projViewTrans, camera.combined);
        program.setUniformf(camPos, camera.position);
        
	}
	
	//Set the uniforms we need
	public void setUniforms(Vector3 u_albedo, float u_metallic, float u_roughness, float u_ao, PointLight[] pointLights, int u_useTexture)
	{
		
		program.begin();
        program.setUniformf(albedo, u_albedo);
        program.setUniformf(metallic, u_metallic);
        program.setUniformf(roughness, u_roughness);
        program.setUniformf(ao, u_ao);
        program.setUniformi(useTexture, u_useTexture);
       
    	int location = program.getUniformLocation("pointLights["+Integer.toString(0)+"].color");
    	program.setUniformf(location, pointLights[0].color);
    	location = program.getUniformLocation("pointLights["+Integer.toString(0)+"].position");
    	program.setUniformf(location, pointLights[0].position);
        program.end();
    }	

	@Override
	public void render(Renderable renderable) {
        program.setUniformi(metallicMap, context.textureBinder.bind(metallicTexture));
        program.setUniformi(roughnessMap, context.textureBinder.bind(roughnessTexture));
        program.setUniformi(albedoMap, context.textureBinder.bind(albedoTexture));

		program.setUniformMatrix(worldTrans, renderable.worldTransform);
		renderable.meshPart.render(program);
	}

	@Override
	public void end() {
		program.end();
	}

}
