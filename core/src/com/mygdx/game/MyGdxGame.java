package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Date;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer, shapeRendererCel;
	MyCircle circle;
	MyCircle celGry;

	Date startTime;
	Date inCircle;
	boolean inCircleFlag;

	boolean jestCelGry;
	boolean dzialaOpor;
	boolean koniec;
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


	private Stage stage;
	private TextButton buttonGravity, buttonOpor, buttonCel, buttonGra;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRendererCel = new ShapeRenderer();
		circle = new MyCircle(20, 20, 15);
		r = 20;
		y=0;
		x = 0;
		vx = 0;
		vy = 0;
		dt = 1;
		g = (float) 0.955;
		p = 8;

		startTime = new Date();
		isGravity = false;
		dzialaOpor = false;
		jestCelGry = false;
		inCircleFlag = false;
		inCircle = new Date();
		koniec = false;



		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage();

		buttonGravity = new TextButton("Gravity off", skin);
		stage.addActor(buttonGravity);
		buttonGravity.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(isGravity) {
					buttonGravity.setText("Gravity off");
					isGravity = false;
				}
				else {
					buttonGravity.setText("Gravity on");
					isGravity = true;
				}
				System.out.println("TESTS");
			}
		});

		buttonOpor = new TextButton("Opor off", skin);
		stage.addActor(buttonOpor);
		buttonOpor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(dzialaOpor) {
					buttonOpor.setText("Opor off");
					dzialaOpor = false;
				}
				else {
					buttonOpor.setText("Opór on");
					dzialaOpor = true;
				}
				System.out.println("TESTS");
			}
		});

		buttonCel = new TextButton("Cel off", skin);
		stage.addActor(buttonCel);
		buttonCel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(jestCelGry) {
					buttonCel.setText("Cel off");
					jestCelGry = false;
				}
				else {
					buttonCel.setText("Cel on");

					jestCelGry = true;
					if(jestCelGry) {
						Random random = new Random();
						float celX, celY;
						celX = random.nextInt(Gdx.graphics.getWidth() - 80 - 50 + 1) + 50;
						celY = random.nextInt(450 - 80);

						celGry = new MyCircle(celX, celY ,30);
					}
				}
				System.out.println("TESTS");
			}
		});

		//TODO: Dodać reset gry

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
		buttonGravity.setPosition(10, 450);
		buttonOpor.setPosition(buttonGravity.getX() + buttonGravity.getWidth() + 35,450);
		buttonCel.setPosition(buttonOpor.getX() + buttonOpor.getWidth() +35,450);

		if(!koniec) {
			if(jestCelGry) {

				shapeRendererCel.begin(ShapeRenderer.ShapeType.Filled);
				shapeRendererCel.setColor(Color.BLUE);
				celGry.render(shapeRendererCel);
				shapeRendererCel.end();
			}


			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			circle.render(shapeRenderer);
			shapeRenderer.end();


			long t = (new Date().getTime() - startTime.getTime())/1000;

			control();

			if(dzialaOpor) {
				vx = vx*b();
				vy = vy*b();
			}


			gravity();
			System.out.println(vy);
			x += vx*dt;
			y += vy*dt;
			//Kolizja z oknem
			windowCollision();

			//TODO: Promien celu w zmiennej


			circle.setPos(x, y);
			if(jestCelGry) {
				float osX = celGry.getX() - circle.getX();
				float osY = celGry.getY() - circle.getY();
				long timeInCircle = 0;
				if (Math.abs(osX) <= 15 && Math.abs(osY) <= 15) {
					if (vx <= 0.001 && vy <= 0.001) {
						if (!inCircleFlag) {
							inCircleFlag = true;
							inCircle = new Date();
						} else {
							timeInCircle = new Date().getTime() - inCircle.getTime();
							if (timeInCircle >= 2000) {
								System.out.println("Wygrałeś!!!!\nCzas który potrzebowałeś to: " + t);
								shapeRendererCel.begin(ShapeRenderer.ShapeType.Filled);
								koniec = true;
								shapeRendererCel.setColor(Color.RED);
								celGry.render(shapeRendererCel);
								shapeRendererCel.end();
							}
						}

					} else {
						inCircleFlag = false;
					}

				} else {
					inCircleFlag = false;
				}
			}
		}
	}

	private void control() {
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
	}

	private void gravity() {
		if(isGravity) {
			vy -= g*dt;
		}
	}

	private void windowCollision() {
		if(y < 15) {
			vy = 0;
			y = 15;
		}

		if(y > 435) {
			vy = 0;
			y = 435;
		}

		if(x > 625)  {
			vx = 0;
			x = 625;
		}

		if(x < 15) {
			vx = 0;
			x = 15;
		}
	}

	private float b() {
		float wynik = (vx*vx + vy*vy);
		//System.out.println(wynik + " <----" );
		if(wynik >= 1)
			wynik = (float) 0.9;

		if(wynik < 0)
			wynik = 0;

		//System.out.println(wynik + "" );
		return wynik;
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
