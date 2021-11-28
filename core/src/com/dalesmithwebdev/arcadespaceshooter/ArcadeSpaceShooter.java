package com.dalesmithwebdev.arcadespaceshooter;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.dalesmithwebdev.arcadespaceshooter.screens.BackgroundScreen;
import com.dalesmithwebdev.arcadespaceshooter.screens.BaseScreen;
import com.dalesmithwebdev.arcadespaceshooter.screens.StartScreen;
import com.dalesmithwebdev.arcadespaceshooter.systems.*;

import java.util.ArrayList;

public class ArcadeSpaceShooter extends ApplicationAdapter {
	public static Rectangle screenRect;
	public static Engine engine;
	public static SpriteBatch spriteBatch;
	public static BitmapFont bitmapFont;
	public static GlyphLayout glyphLayout;
	public static int kills = 0;
	public static double playerScore = 0;

	public static Texture playerShield;
	public static Texture playerLivesGraphic;

	public static Texture background;
	public static ArrayList<Texture> backgroundElements;

	public static Texture blank;

	public static Texture ufoBlue;
	public static Texture ufoGreen;
	public static Texture ufoRed;
	public static Texture ufoYellow;
	public static Texture enemyShip;
	public static Texture bossTexture;

	public static Texture laserRed;
	public static Texture laserGreen;
	public static Texture laserBlue;

	public static ArrayList<Texture> shipTextures;

	public static ArrayList<Texture> smallMeteors;
	public static ArrayList<Texture> bigMeteors;

	//Explosions for laser-meteor collisions
	public static Texture explosionTexture;
	public static Texture explosionTextureGreen;
	public static Texture explosionTextureBlue;

	public static Texture missile;
	public static Texture bomb;

	public static Texture fireEffect;

	public static Music backgroundMusic;

	public static ArrayList<BaseScreen> screens;
	public String empVertexShader;
	public String empFragmentShader;
	public static ShaderProgram empShader;
	public static boolean empActive = false;
	public static int empElapsedTime = 0;
	public static int totalTime = 0;
	
	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		bitmapFont = new BitmapFont();
		glyphLayout = new GlyphLayout();
		screenRect = new Rectangle(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		backgroundElements = new ArrayList<Texture>();
		screens = new ArrayList<BaseScreen>();

		engine = new Engine();
		engine.addSystem(new RenderSystem());
		engine.addSystem(new MovementSystem());
		engine.addSystem(new DamageSystem());
		engine.addSystem(new EnemyLogicSystem());
		engine.addSystem(new InputSystem());
		engine.addSystem(new ExplosionSystem());
		engine.addSystem(new NotificationSystem());
		engine.addSystem(new LevelSystem());

		//Spritefont for scores & notifications
		spriteBatch = new SpriteBatch();

		//Purple background
		background = new Texture(Gdx.files.internal("background/backgroundColor.png"));
		backgroundElements.add(new Texture(Gdx.files.internal("background/speedLine.png")));
		backgroundElements.add(new Texture(Gdx.files.internal("background/starBig.png")));
		backgroundElements.add(new Texture(Gdx.files.internal("background/starSmall.png")));
		blank = new Texture(Gdx.files.internal("ui/blank.png"));

		ufoBlue = new Texture(Gdx.files.internal("ships-enemies/ufoBlue.png"));
		ufoGreen = new Texture(Gdx.files.internal("ships-enemies/ufoGreen.png"));
		ufoRed = new Texture(Gdx.files.internal("ships-enemies/ufoRed.png"));
		ufoYellow = new Texture(Gdx.files.internal("ships-enemies/ufoYellow.png"));
		enemyShip = new Texture(Gdx.files.internal("ships-enemies/enemyShip.png"));
		bossTexture = new Texture(Gdx.files.internal("ships-enemies/bossEnemy.png"));

		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/loop-transit.mp3"));

		//Ship textures
		shipTextures = new ArrayList<Texture>();
		shipTextures.add(new Texture(Gdx.files.internal("ships-player/player.png")));
		shipTextures.add(new Texture(Gdx.files.internal("ships-player/playerleft.png")));
		shipTextures.add(new Texture(Gdx.files.internal("ships-player/playerright.png")));
		playerLivesGraphic = new Texture(Gdx.files.internal("ui/life.png"));
		playerShield = new Texture(Gdx.files.internal("power-ups/shield.png"));

		//Lasers
		laserRed = new Texture(Gdx.files.internal("lasers/laserRed.png"));
		laserGreen = new Texture(Gdx.files.internal("lasers/laserGreen.png"));
		laserBlue = new Texture(Gdx.files.internal("lasers/laserBlue12.png"));

		missile = new Texture(Gdx.files.internal("lasers/spaceMissiles_009.png"));
		bomb = new Texture(Gdx.files.internal("lasers/spaceMissiles_012.png"));

		//Meteors
		bigMeteors = new ArrayList<>();
		bigMeteors.add(new Texture("meteors/meteorBig.png"));
		bigMeteors.add(new Texture("meteors/meteorBrown_big1.png"));
		bigMeteors.add(new Texture("meteors/meteorBrown_big2.png"));
		bigMeteors.add(new Texture("meteors/meteorBrown_big3.png"));
		bigMeteors.add(new Texture("meteors/meteorBrown_big4.png"));
		bigMeteors.add(new Texture("meteors/meteorBrown_med1.png"));
		bigMeteors.add(new Texture("meteors/meteorBrown_med2.png"));

		bigMeteors.add(new Texture("meteors/meteorGrey_big1.png"));
		bigMeteors.add(new Texture("meteors/meteorGrey_big2.png"));
		bigMeteors.add(new Texture("meteors/meteorGrey_big3.png"));
		bigMeteors.add(new Texture("meteors/meteorGrey_big4.png"));
		bigMeteors.add(new Texture("meteors/meteorGrey_med1.png"));
		bigMeteors.add(new Texture("meteors/meteorGrey_med2.png"));

		smallMeteors = new ArrayList<>();
		smallMeteors.add(new Texture("meteors/meteorBrown_small1.png"));
		smallMeteors.add(new Texture("meteors/meteorBrown_small2.png"));
		smallMeteors.add(new Texture("meteors/meteorBrown_tiny1.png"));
		smallMeteors.add(new Texture("meteors/meteorBrown_tiny2.png"));

		smallMeteors.add(new Texture("meteors/meteorGrey_small1.png"));
		smallMeteors.add(new Texture("meteors/meteorGrey_small2.png"));
		smallMeteors.add(new Texture("meteors/meteorGrey_tiny1.png"));
		smallMeteors.add(new Texture("meteors/meteorGrey_tiny2.png"));

		fireEffect = new Texture("effects/fire03.png");

		empFragmentShader = Gdx.files.internal("shaders/emp/fragment.glsl").readString();
		empVertexShader = Gdx.files.internal("shaders/emp/vertex.glsl").readString();
		empShader = new ShaderProgram(empVertexShader, empFragmentShader);

		//Explosions
		explosionTexture = new Texture(Gdx.files.internal("lasers/laserRedShot.png"));
		explosionTextureGreen = new Texture(Gdx.files.internal("lasers/laserGreenShot.png"));
		explosionTextureBlue = new Texture(Gdx.files.internal("lasers/laserBlue08.png"));

		PushScreen(new BackgroundScreen());
		PushScreen(new StartScreen());
	}

	public static float measureText(CharSequence text) {
		glyphLayout.setText(bitmapFont, text);
		return glyphLayout.width;
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime() * 1000;
		totalTime += dt;
		if(empActive) {
			empElapsedTime += dt;
			if(empElapsedTime >= 20000) {
				empActive = false;
			}
		}

		spriteBatch.begin();
		spriteBatch.draw(ArcadeSpaceShooter.background, 0, 0, screenRect.width, screenRect.height);
		spriteBatch.end();

		FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, (int)screenRect.width, (int)screenRect.height, false);
		fbo.begin();

		spriteBatch.begin();

		for(int i = 0; i < screens.size(); i++)
		{
			BaseScreen screen = screens.get(i);
			screen.draw(dt);
		}

		spriteBatch.end();

		fbo.end();

		Sprite s = new Sprite(fbo.getColorBufferTexture());
		s.flip(false,true);

		spriteBatch.begin();
		for(int i = screens.size() - 1; i >= 0; i--)
		{
			BaseScreen screen = screens.get(i);
			screen.update(dt);
			if(screen.pausesBelow)
			{
				break;
			}
		}
		spriteBatch.end();

		spriteBatch.begin();
		spriteBatch.draw(s, 0, 0, screenRect.width, screenRect.height);
		spriteBatch.end();

	}

	
	@Override
	public void dispose () {
		spriteBatch.dispose();
		bitmapFont.dispose();
	}

	public static void PushScreen(BaseScreen screen)
	{
		screens.add(screen);
	}

	public static void PopScreen()
	{
		screens.remove(screens.size() - 1);
	}
}
