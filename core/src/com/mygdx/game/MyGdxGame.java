package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	MyCircle circle;

	//pozycja koła
	float y;
	float x;

	//promień koła
	float r;

	//predkosc wzgledem osi
	float vx;
	float vy;

	//czas
	float dt;

	//wartosc grawitacji
	float g;
	boolean isGravity;

	//o ile powiekszamy predkosc
	float p;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		circle = new MyCircle(20, 20, 20);
		r = 20;
		y=20;
		x = r;
		vx = 0;
		vy = 0;
		dt = 1;
		g = (float) 0.255;
		p = 4;

		isGravity = false;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		circle.render(shapeRenderer);
		shapeRenderer.end();

		if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
			vx-=p;
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
			vy+=p;
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
			vx+=p;
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
			vy-=p;
		}

		if(isGravity)
			vy -=g*dt;

		x += vx*dt;
		y += vy*dt;

		//Kolizja z oknem
		if(y < 21) {
			vy = 0;
			y = 20;
		}

		if(y > 460) {
			vy = 0;
			y = 460;
		}

		if(x > 620)  {
			vx = 0;
			x = 620;
		}

		if(x < 20) {
			vx = 0;
			x = 20;
		}

		circle.setPos(x, y);
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
