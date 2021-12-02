package com.dalesmithwebdev.arcadespaceshooter;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.dalesmithwebdev.arcadespaceshooter.screens.BackgroundScreen;
import com.dalesmithwebdev.arcadespaceshooter.screens.BaseScreen;
import com.dalesmithwebdev.arcadespaceshooter.screens.StartScreen;
import com.dalesmithwebdev.arcadespaceshooter.screens.tests.ShaderTestScreen;
import com.dalesmithwebdev.arcadespaceshooter.systems.*;
import com.dalesmithwebdev.arcadespaceshooter.utility.GameTestCase;

import java.text.NumberFormat;
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

	public static ShaderProgram empShader;
	public static ShaderProgram outlineShader;
	public static ShaderProgram vignetteShader;

	public static boolean empActive = false;
	public static int empElapsedTime = 0;
	public static int totalTime = 0;
	public static GameTestCase testCase;
	public int memoryLastReported = 0;

	public static Texture laserStrengthUpgradeGreen;
	public static Texture laserStrengthUpgradeBlue;
	public static Texture dualLaserUpgrade;
	public static Texture diagonalLaserUpgrade;
	public static Texture missileUpgrade;
	public static Texture bombUpgrade;
	public static Texture shieldUpgrade;
	public static Texture empUpgrade;

	public ArcadeSpaceShooter(GameTestCase testCase) {
		super();
		ShaderProgram.pedantic = false;
		ArcadeSpaceShooter.testCase = testCase;
	}
	
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
		engine.addSystem(new ExplosionSystem());

		//Spritefont for scores & notifications
		spriteBatch = new SpriteBatch();

		laserStrengthUpgradeGreen = new Texture(Gdx.files.internal("power-ups/powerupGreen_bolt.png"));
		laserStrengthUpgradeBlue = new Texture(Gdx.files.internal("power-ups/powerupBlue_bolt.png"));
		diagonalLaserUpgrade = new Texture(Gdx.files.internal("power-ups/pill_green.png"));
		bombUpgrade = new Texture(Gdx.files.internal("power-ups/powerupRed_star.png"));
		dualLaserUpgrade = new Texture(Gdx.files.internal("power-ups/pill_red.png"));
		empUpgrade = new Texture(Gdx.files.internal("power-ups/powerupBlue_star.png"));
		missileUpgrade = new Texture(Gdx.files.internal("power-ups/powerupYellow_star.png"));
		shieldUpgrade = new Texture(Gdx.files.internal("power-ups/powerupBlue_shield.png"));

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

		empShader = new ShaderProgram(
				Gdx.files.internal("shaders/emp/vertex.glsl").readString(),
				Gdx.files.internal("shaders/emp/fragment.glsl").readString()
		);

		outlineShader = new ShaderProgram(
				Gdx.files.internal("shaders/outline/vertex.glsl").readString(),
				Gdx.files.internal("shaders/outline/fragment.glsl").readString()
		);

		vignetteShader = new ShaderProgram(
				Gdx.files.internal("shaders/vignette/vertex.glsl").readString(),
				Gdx.files.internal("shaders/vignette/fragment.glsl").readString()
		);

		//Explosions
		explosionTexture = new Texture(Gdx.files.internal("lasers/laserRedShot.png"));
		explosionTextureGreen = new Texture(Gdx.files.internal("lasers/laserGreenShot.png"));
		explosionTextureBlue = new Texture(Gdx.files.internal("lasers/laserBlue08.png"));

		if(testCase == null) {
			PushScreen(new BackgroundScreen());
			PushScreen(new StartScreen());
		} else {
			switch(testCase) {
				case SHADER:
					PushScreen(new ShaderTestScreen());
					break;
				default:
					break;
			}
		}
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

		for(int i = screens.size() - 1; i >= 0; i--)
		{
			BaseScreen screen = screens.get(i);
			screen.update(dt);
			if(screen.pausesBelow)
			{
				break;
			}
		}

		spriteBatch.begin();
		for(int i = 0; i < screens.size(); i++)
		{
			BaseScreen screen = screens.get(i);
			screen.draw(dt);
		}
		spriteBatch.end();


		memoryLastReported += dt;
		if(memoryLastReported > 10000) {
			memoryLastReported = 0;
			Runtime runtime = Runtime.getRuntime();
			NumberFormat format = NumberFormat.getInstance();

			long maxMemory = runtime.maxMemory();
			long allocatedMemory = runtime.totalMemory();
			long freeMemory = runtime.freeMemory();

			System.out.println("free memory: " + format.format(freeMemory / 1024));
			System.out.println("allocated memory: " + format.format(allocatedMemory / 1024));
			System.out.println("max memory: " + format.format(maxMemory / 1024));
			System.out.println("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
		}
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
