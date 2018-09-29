package com.screens;

import static com.game.MainGame.PPM;

import java.util.Random;

import com.actors.Enemy;
import com.actors.Player2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.collisions.MyContactListener;
import com.collisions.userdata.UserData;
import com.game.MainGame;

import constants.Constants;

public class GameScreen implements Screen {

	private OrthographicCamera gamecam;
	private Viewport gameport;
	private MainGame game;

	// Tiled Map
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;

	// Box2D
	private World world;
	private Box2DDebugRenderer box2dRender;
	private MyContactListener contactListener;

	// Scene2D
	private Stage stage;

	private Player2 player;

	private Enemy enemy;

	private Array<Enemy> enemies;

	Body body;

	public GameScreen(MainGame game) {
		this.game = game;

		gamecam = new OrthographicCamera();

		gameport = new FitViewport(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM, gamecam);

		// Tiled Map
		mapLoader = new TmxMapLoader();
		map = mapLoader.load("Mapa de prueba.tmx");
		renderer = new OrthogonalTiledMapRenderer(map, 1 / PPM);

		gamecam.position.set(gameport.getWorldWidth() / 2, gameport.getWorldHeight() / 2, 0);

		// Box2D
		world = new World(new Vector2(0, 0), true);
		box2dRender = new Box2DDebugRenderer();

		contactListener = new MyContactListener();
		world.setContactListener(contactListener);

		// Body Definitions
		createMapObjects();

		player = new Player2(world, gameport);

		// Scene2D
		// stage = new Stage();

	}

	public void update(float delta) {

		world.step(1 / 60f, 6, 2);

		player.update(delta);
		if (contactListener.playerCanAttack()) {
			Enemy enemy = enemies.get(contactListener.getEnemyToAttack());
			player.attack(enemy);
		}

		for (Enemy enemy : enemies) {
			enemy.update(delta);
			System.out.println("Enemy " + enemy.getEnemyIndex() + " health " + enemy.health);
		}

		gamecam.position.x = player.body.getPosition().x;
		gamecam.position.y = player.body.getPosition().y;

		gamecam.update();

		renderer.setView(gamecam);
	}

	@Override
	public void render(float delta) {

		update(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// stage.act();

		renderer.render(); // Tiled Map renderer.

		box2dRender.render(world, gamecam.combined); // Box2D render.

		game.batch.setProjectionMatrix(gamecam.combined);

		game.batch.begin();

		player.draw(game.batch);

		for (Enemy enemy : enemies) {
			enemy.draw(game.batch);
		}

		game.batch.end();

	}

	private void createMapObjects() {
		BodyDef bdef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fdef = new FixtureDef();

		for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {

			Rectangle rect = ((RectangleMapObject) object).getRectangle();

			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set((rect.getX() + rect.getWidth() / 2) / PPM, (rect.getY() + rect.getHeight() / 2) / PPM);

			body = world.createBody(bdef);

			shape.setAsBox((rect.getWidth() / 2) / PPM, (rect.getHeight() / 2) / PPM);
			fdef.shape = shape;
			fdef.filter.categoryBits = Constants.BIT_COLLISION;
			fdef.filter.maskBits = Constants.BIT_PLAYER;
			body.createFixture(fdef).setUserData(new UserData("Map", 0));
			;

		}

		// CREATE ENEMY

		createEnemies();
	}

	public void createEnemies() {
		Random random = new Random();
		int maxEnemies = 4;

		enemies = new Array<Enemy>();
		for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();

			for (int i = 0; i < maxEnemies; i++) {
				float posX = random.nextInt((int) ((rect.getWidth() + rect.getX()) - rect.getX())) + rect.getX();
				float posY = random.nextInt((int) ((rect.getHeight() + rect.getY()) - rect.getY())) + rect.getY();

				enemies.add(new Enemy(world, posX / PPM, posY / PPM, i));
			}

		}
	}

	@Override
	public void resize(int width, int height) {
		gameport.update(width, height);
	}

	@Override
	public void dispose() {
		player.dispose();
		map.dispose();
		world.destroyBody(body);
		renderer.dispose();
		world.dispose();
		box2dRender.dispose();
		stage.dispose();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

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
	public void hide() {
		// TODO Auto-generated method stub

	}

}
