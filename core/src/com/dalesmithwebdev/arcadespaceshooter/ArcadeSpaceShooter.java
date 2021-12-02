package com.dalesmithwebdev.arcadespaceshooter;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.*;
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

	public static TextureRegion playerShield;
	public static TextureRegion playerLivesGraphic;

	public static ArrayList<TextureRegion> backgroundElements;

	public static TextureRegion blank;

	public static ArrayList<TextureRegion> shipTextures;

	public static ArrayList<TextureRegion> smallMeteors;
	public static ArrayList<TextureRegion> bigMeteors;

	public static TextureRegion missile;
	public static TextureRegion bomb;

	public static TextureRegion fireEffect;

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

	public static TextureAtlas textures;

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
		backgroundElements = new ArrayList<>();
		screens = new ArrayList<>();

		engine = new Engine();
		engine.addSystem(new RenderSystem());
		engine.addSystem(new MovementSystem());
		engine.addSystem(new DamageSystem());
		engine.addSystem(new EnemyLogicSystem());
		engine.addSystem(new ExplosionSystem());

		//Spritefont for scores & notifications
		spriteBatch = new SpriteBatch();

		textures = new TextureAtlas(Gdx.files.internal("ArcadeShooter.atlas"));

		blank = textures.findRegion("blank");

		backgroundElements.add(textures.findRegion("speedLine"));
		backgroundElements.add(textures.findRegion("starBig"));
		backgroundElements.add(textures.findRegion("starSmall"));

		backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/loop-transit.mp3"));

		//Ship textures
		shipTextures = new ArrayList<>();
		shipTextures.add(textures.findRegion("player"));
		shipTextures.add(textures.findRegion("playerLeft"));
		shipTextures.add(textures.findRegion("playerRight"));

		playerLivesGraphic = textures.findRegion("life");
		playerShield = textures.findRegion("shield");

		missile = textures.findRegion("spaceMissiles", 9);
		bomb = textures.findRegion("spaceMissiles", 12);

		//Meteors
		bigMeteors = new ArrayList<>();
		bigMeteors.add(textures.findRegion("meteorBig"));
		bigMeteors.add(textures.findRegion("meteorBrown_big1"));
		bigMeteors.add(textures.findRegion("meteorBrown_big2"));
		bigMeteors.add(textures.findRegion("meteorBrown_big3"));
		bigMeteors.add(textures.findRegion("meteorBrown_big4"));
		bigMeteors.add(textures.findRegion("meteorBrown_med1"));
		bigMeteors.add(textures.findRegion("meteorBrown_med2"));
		bigMeteors.add(textures.findRegion("meteorGrey_big1"));
		bigMeteors.add(textures.findRegion("meteorGrey_big2"));
		bigMeteors.add(textures.findRegion("meteorGrey_big3"));
		bigMeteors.add(textures.findRegion("meteorGrey_big4"));
		bigMeteors.add(textures.findRegion("meteorGrey_med1"));
		bigMeteors.add(textures.findRegion("meteorGrey_med2"));

		smallMeteors = new ArrayList<>();
		smallMeteors.add(textures.findRegion("meteorBrown_small1"));
		smallMeteors.add(textures.findRegion("meteorBrown_small2"));
		smallMeteors.add(textures.findRegion("meteorBrown_tiny1"));
		smallMeteors.add(textures.findRegion("meteorBrown_tiny2"));
		smallMeteors.add(textures.findRegion("meteorGrey_small1"));
		smallMeteors.add(textures.findRegion("meteorGrey_small2"));
		smallMeteors.add(textures.findRegion("meteorGrey_tiny1"));
		smallMeteors.add(textures.findRegion("meteorGrey_tiny2"));

		fireEffect = textures.findRegion("fire03");

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
		if(memoryLastReported > 60000) {
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
