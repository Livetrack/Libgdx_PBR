package com.pbr;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pbr.PBRShader;
import com.pbr.PointLight;

public class PBRTest extends ApplicationAdapter {
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public PBRShader shader;
	public RenderContext renderContext;
	public Array<ModelInstance> instances =  new Array<ModelInstance>();
	public Model model;
	public Renderable renderable;
	public ModelBatch batch;
	
	public PointLight[] pointLights;
	
	public float ao; //Default AO value for the spheres
	public Vector3 albedo; //Default albedo value for the spheres
	
	//Toggle textures
	public int useTexture = 0;
	
	@Override
	public void create() {
		//Setting up the camera
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f,10f);
		cam.lookAt(0,0,0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		camController = new CameraInputController(cam);
	    Gdx.input.setInputProcessor(camController);
	    
	    //Create the spheres
	    ModelBuilder modelBuilder = new ModelBuilder();
	    model = modelBuilder.createSphere(2f, 2f, 2f, 20, 20, new Material(), Usage.Position | 
	    		Usage.Normal | Usage.TextureCoordinates | Usage.Tangent | Usage.BiNormal);
	    for(int x = - 5; x <= 5; x+=2)
	    {
	    	for(int z = -5; z <= 5; z+=2)
	    	{
	    		for(int y = -5; y <= 5; y+=2)
	    		{
	    			//Ajouter un data ,à chaque instance
		    		ModelInstance instance = new ModelInstance(model, x, y, z);
		    		instances.add(instance);
	    		}
	    		
	    	}
	    }
	    ao = 0.1f;
	    albedo = new Vector3(1.0f,1.0f,0.0f);
	    
	    //Setting up the shader
	    shader = new PBRShader();
	    shader.init();
		
	    batch = new ModelBatch();
	    
	    
	    //Setting the lights
	    pointLights = new PointLight[1];
	    pointLights[0] = new PointLight(new Vector3(300.0f, 300.0f, 300.0f), new Vector3(-10.0f,10.0f,10.0f));

	    

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		//Moving the cameras
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
			pointLights[0].position.x += 5;
		}
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
        	pointLights[0].position.x -= 5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
			pointLights[0].position.y += 5;
		}
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
        	pointLights[0].position.y -= 5;
        }
        //Toggle textures
        if(Gdx.input.isKeyPressed(Input.Keys.U)){
        	useTexture = 1;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.V)){
        	useTexture = 0;
        }
        
        
		camController.update();

	    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	    
	    int i = 0;
	    int j = 0;
	    float step = 10/ ( (float)instances.size);
	    
	    for(ModelInstance instance : instances)
	    {
	    	//Extremely inefficient.
	    	//Should be able to send everything once to the GPU before rendering.
	    	batch.begin(cam);
	    	shader.setUniforms( albedo, (float)(j*step), (float)(i*step), ao, pointLights, useTexture);
	    	batch.render(instance, shader);
	    	i+=1;
	    	if(i % 10 == 0) {
	    		j+=1;
	    		i=0;
	    	}
	    	batch.end();	
	    }
	    	
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
	   shader.dispose();
	   model.dispose();	
	   batch.dispose();
	}
}
