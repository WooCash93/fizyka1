package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Date;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer shapeRenderer, shapeRendererCel;

	MyCircle player;
	MyCircle circleGoal;

	Date startTime;
	Date timeInCircle;
	boolean inCircleFlag;

	boolean isGoal = false;
	boolean isResistance = false;
	boolean isEndGame = false;

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
	boolean isGravity = false;

	//o ile powiekszamy predkosc
	float p;

	private Stage stage;
	private TextButton buttonGravity, buttonResistance, buttonGoal, buttonReset;
	private TextField winMessage, speedIncreaseTextField, vxTF, vyTF;

	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		shapeRendererCel = new ShapeRenderer();
		player = new MyCircle(20, 20, 15);
		r = 20;
		y = 0;
		x = 0;
		vx = 0;
		vy = 0;
		dt = 1;
		g = (float) 0.955;
		p = 2;

		startTime = new Date();
		//isGravity = false;
		//dzialaOpor = false;
		//jestCelGry = false;
		inCircleFlag = false;
		timeInCircle = new Date();
		isEndGame = false;

		if(isGoal) {
			Random random = new Random();
			float celX, celY;
			celX = random.nextInt(Gdx.graphics.getWidth() - 80 - 50 + 1) + 50;
			celY = random.nextInt(450 - 80);

			circleGoal = new MyCircle(celX, celY ,30);
		}

		setupButton();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();
		buttonGravity.setPosition(10, 450);
		buttonResistance.setPosition(buttonGravity.getX() + buttonGravity.getWidth() + 10,450);
		buttonGoal.setPosition(buttonResistance.getX() + buttonResistance.getWidth() + 10,450);
		buttonReset.setPosition(buttonGoal.getX() + buttonGoal.getWidth() + 10,450);
		speedIncreaseTextField.setPosition(buttonReset.getX() + buttonReset.getWidth() + 10,450);
		vxTF.setPosition(speedIncreaseTextField.getX() + speedIncreaseTextField.getWidth() + 10,465);
		vyTF.setPosition(speedIncreaseTextField.getX() + speedIncreaseTextField.getWidth() + 10,450);
		if(!isEndGame) {
			game();
		}
	}

	private void game() {
		drawGoalCircle();
		drawPlayerCircle();
		long t = (new Date().getTime() - startTime.getTime())/1000;
		control();
		resistance();
		gravity();
		vxTF.setText("VX: " + Math.abs(vx));
		if(player.getY() <= 15) {
			vyTF.setText("VY: " + 0.0);
		} else {
			vyTF.setText("VY: " + Math.abs(vy));
		}

		calculatePlayerPosition();
		goalEngine(t);
	}

	private void goalEngine(long t) {
		if(isGoal) {
			buttonGoal.setText("Game ON " + t);
			float osX = circleGoal.getX() - player.getX();
			float osY = circleGoal.getY() - player.getY();
			long timeInCircle = 0;
			if (Math.abs(osX) <= 15 && Math.abs(osY) <= 15) {
				if (vx <= 0.001 && vy <= 0.001) {
					if (!inCircleFlag) {
						inCircleFlag = true;
						this.timeInCircle = new Date();
					} else {
						timeInCircle = new Date().getTime() - this.timeInCircle.getTime();
						if (timeInCircle >= 2000) {
							//TODO: Pokazac komunikat w okienku gry
							System.out.println("Wygrałeś!!!!\nCzas który potrzebowałeś to: " + t);
							String message = "WIN! Time: "+ t;
							winMessage.setText(message);
							shapeRendererCel.begin(ShapeRenderer.ShapeType.Filled);
							isEndGame = true;
							shapeRendererCel.setColor(Color.RED);
							circleGoal.render(shapeRendererCel);
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

	private void calculatePlayerPosition() {
		x += vx*dt;
		y += vy*dt;
		windowCollision();
		player.setPos(x, y);
	}

	private void resistance() {
		if(isResistance) {
			vx = vx*b();
			vy = vy*b();
		}
	}

	private float b() {
		float wynik = (vx*vx + vy*vy);

		if(wynik >= 1)
			wynik = (float) 0.9;

		if(wynik < 0)
			wynik = 0;

		return wynik;
	}

	private void drawPlayerCircle() {
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		player.render(shapeRenderer);
		shapeRenderer.end();
	}

	private void drawGoalCircle() {
		if(isGoal) {
			shapeRendererCel.begin(ShapeRenderer.ShapeType.Filled);
			shapeRendererCel.setColor(Color.BLUE);
			circleGoal.render(shapeRendererCel);
			shapeRendererCel.end();
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



	@Override
	public void dispose () {
		batch.dispose();
	}

	private void setupButton() {
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		stage = new Stage();


		if(isGravity) {
			buttonGravity = new TextButton("Gravity ON", skin);
		}
		else {
			buttonGravity= new TextButton("Gravity OFF", skin);
		}

		stage.addActor(buttonGravity);
		buttonGravity.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(isGravity) {
					buttonGravity.setText("Gravity OFF");
					isGravity = false;
				}
				else {
					buttonGravity.setText("Gravity ON");
					isGravity = true;
				}
			}
		});

		if(isResistance) {
			buttonResistance = new TextButton("Resistance ON", skin);
		}
		else {
			buttonResistance = new TextButton("Resistance OFF", skin);

		}

		stage.addActor(buttonResistance);
		buttonResistance.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(isResistance) {
					buttonResistance.setText("Resistance OFF");
					isResistance = false;
				}
				else {
					buttonResistance.setText("Resistance ON");
					isResistance = true;
				}
			}
		});

		if(isGoal) {
			buttonGoal = new TextButton("Goal ON", skin);
		}
		else {
			buttonGoal = new TextButton("Goal OFF", skin);
		}

		stage.addActor(buttonGoal);
		buttonGoal.setWidth(buttonGoal.getWidth()+32);
		buttonGoal.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(isGoal) {
					buttonGoal.setText("Goal OFF");
					isGoal = false;
				}
				else {
					buttonGoal.setText("Goal ON");
					startTime = new Date();
					isGoal = true;
					if(isGoal) {
						Random random = new Random();
						float celX, celY;
						celX = random.nextInt(Gdx.graphics.getWidth() - 80 - 50 + 1) + 50;
						celY = random.nextInt(450 - 80);

						circleGoal = new MyCircle(celX, celY ,30);
					}
				}

			}
		});

		buttonReset = new TextButton("RESET", skin);
		stage.addActor(buttonReset);
		buttonReset.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
					create();
			}
		});



		speedIncreaseTextField = new TextField(p + "", skin);
		speedIncreaseTextField.setSize(64, buttonGoal.getHeight());
		speedIncreaseTextField.setTextFieldListener((textField, c) -> {
			try {
				p = Float.parseFloat(textField.getText());
			} catch (Exception ex) {
				System.out.println("Nie da sie");
				p = 0;
			}

		});
		stage.addActor(speedIncreaseTextField);


		TextField.TextFieldStyle style = new TextField.TextFieldStyle();
		style.fontColor = Color.GREEN;
		style.font = new BitmapFont();
		float f = (float) 1.3;
		style.font.getData().setScale(18 * style.font.getScaleY() / style.font.getLineHeight());
		winMessage = new TextField("", style);
		winMessage.setPosition(640/2 - winMessage.getWidth()/2 , 230);
		stage.addActor(winMessage);

		style = new TextField.TextFieldStyle();
		style.fontColor = Color.WHITE;
		style.font = new BitmapFont();
		style.font.getData().setScale(16 * style.font.getScaleY() / style.font.getLineHeight());
		vxTF = new TextField("VX: ", style);
		vxTF.setPosition(640/2 - vxTF.getWidth()/2 , 230);
		stage.addActor(vxTF);

		style = new TextField.TextFieldStyle();
		style.fontColor = Color.WHITE;
		style.font = new BitmapFont();
		style.font.getData().setScale(16 * style.font.getScaleY() / style.font.getLineHeight());
		vyTF = new TextField("VY: ", style);
		stage.addActor(vyTF);

		Gdx.input.setInputProcessor(stage);
	}
}
