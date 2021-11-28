package com.dalesmithwebdev.arcadespaceshooter;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

	public static Texture enemyShip;
	public static Texture bossTexture;

	public static Texture laserRed;
	public static Texture laserGreen;

	public static ArrayList<Texture> shipTextures;

	public static Texture meteorBig;
	public static Texture meteorSmall;

	//Explosions for laser-meteor collisions
	public static Texture explosionTexture;
	public static Texture explosionTextureGreen;

	public static Music backgroundMusic;

	public static ArrayList<BaseScreen> screens;
	
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

		//Meteors
		meteorBig = new Texture(Gdx.files.internal("meteors/meteorBig.png"));
		meteorSmall = new Texture(Gdx.files.internal("meteors/meteorSmall.png"));

		//Explosions
		explosionTexture = new Texture(Gdx.files.internal("lasers/laserRedShot.png"));
		explosionTextureGreen = new Texture(Gdx.files.internal("lasers/laserGreenShot.png"));

		PushScreen(new BackgroundScreen());
		PushScreen(new StartScreen());
	}

	public static float measureText(CharSequence text) {
		glyphLayout.setText(bitmapFont, text);
		return glyphLayout.width;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float dt = Gdx.graphics.getDeltaTime() * 1000;

		spriteBatch.begin();
		for(int i = 0; i < screens.size(); i++)
		{
			BaseScreen screen = screens.get(i);
			screen.draw(dt);
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
