package com.screens;

import com.actors.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.game.MainGame;

public class GameScreen extends BaseScreen{

	private Stage stage;
	private Player player;
	private Texture playerTexture;
	
	// Tiled Map
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	public OrthographicCamera camera;
	private Viewport vista;
	private AssetManager manager;
	//
	private TmxMapLoader mapLoader;
	
	//
	
	// Box2D
	private Box2DDebugRenderer box2dRender; 
	private World world;
	
	
	public GameScreen (MainGame game) {
		super(game);
		playerTexture = new Texture("player.png");
		

		stage = new Stage();
		player = new Player(playerTexture);
		
		stage.addActor(player);
		player.setPosition(50, 100);
		
		int vpWidth = Gdx.graphics.getWidth(), vpHeight = Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(vpWidth,vpHeight);
		camera.position.set(1800,1200,0);
		camera.update();
	
		vista = new StretchViewport(840, 620, camera);
		
		manager = new AssetManager();
		manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		manager.load("Mapa de Prueba.tmx", TiledMap.class);
		manager.finishLoading();
		
		map = manager.get("Mapa de Prueba.tmx", TiledMap.class);
		renderer = new OrthogonalTiledMapRenderer(map);
		
		// Box2D
		box2dRender = new Box2DDebugRenderer();
		world = new World(new Vector2(0, -9.8f), true);
		
		BodyDef bodyDef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fixtureDef = new FixtureDef();
		Body body;
		
		
		// Colisiones del mapa.
		for (MapObject object: map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			bodyDef.type = BodyType.StaticBody;
			System.out.println(rect.getX() + " x " + rect.getY());
			bodyDef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
			
			body = world.createBody(bodyDef);
			
			shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
			fixtureDef.shape = shape;
			body.createFixture(fixtureDef);
		}
		
		
		// Box2D Player 
		// No funciona. Player position = 50, 100. Map objects 1800 - 1200
		BodyDef playerBodyDef = new BodyDef();
		PolygonShape playerShape = new PolygonShape();
		FixtureDef playerFixtureDef = new FixtureDef();
		Body playerBody;
		
		System.out.println("Player position: " + player.getX() + " , " + player.getY());
		System.out.println("Player size: " + player.getWidth() + " , " + player.getHeight());
		
		
		playerBodyDef.type = BodyType.StaticBody;
		playerBodyDef.position.set(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);
		
		playerBody = world.createBody(playerBodyDef);
		
		playerShape.setAsBox(player.getWidth() / 2, player.getHeight() / 2);
		playerFixtureDef.shape = playerShape;
		playerBody.createFixture(playerFixtureDef);
		
		
		
		
	}
	
	
	@Override
	public void show() {
		
		
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.5f, 0.7f, 0.9f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		renderer.setView(camera);
		renderer.render();
		
		
		box2dRender.render(world, camera.combined);
	
		
//		NO FUNCIONA 
//		if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_1)){
//			camera.zoom +=10; 
//			}
//		if(Gdx.input.isKeyPressed(Input.Keys.N)){
//			camera.zoom += 10; 
//		}
	
		stage.act();
		
	
		stage.draw();

	}
	
	@Override
	public void hide() {
		stage.dispose();
		playerTexture.dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		vista.update(width, height);

	}
	
	@Override
	public void dispose() {
		manager.dispose();

	}
}
