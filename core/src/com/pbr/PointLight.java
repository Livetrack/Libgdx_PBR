package com.pbr;
import com.badlogic.gdx.math.Vector3;

public class PointLight {
	public Vector3 color;
	public Vector3 position;
	
	PointLight(Vector3 color, Vector3 position)
	{
		this.color = color;
		this.position = position;
	}
}
