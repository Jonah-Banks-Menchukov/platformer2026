package platformer.code.gamelogic.level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import platformer.code.gameengine.PhysicsObject;
import platformer.code.gameengine.graphics.Camera;
import platformer.code.gameengine.loaders.Mapdata;
import platformer.code.gameengine.loaders.Tileset;
import platformer.code.gamelogic.GameResources;
import platformer.code.gamelogic.Main;
import platformer.code.gamelogic.enemies.Enemy;
import platformer.code.gamelogic.player.Player;
import platformer.code.gamelogic.tiledMap.Map;
import platformer.code.gamelogic.tiles.Flag;
import platformer.code.gamelogic.tiles.Flower;
import platformer.code.gamelogic.tiles.Gas;
import platformer.code.gamelogic.tiles.SolidTile;
import platformer.code.gamelogic.tiles.Spikes;
import platformer.code.gamelogic.tiles.Tile;
import platformer.code.gamelogic.tiles.Water;

public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData(){
		return leveldata;
	}

	public void restartLevel() {
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition*tileSize, yPosition*tileSize, this)); // TODO: objects vs tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
				else if (values[x][y] == 19)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
				else if (values[x][y] == 20)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
				else if (values[x][y] == 21)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
				else if(values[x][y]==22)
					tiles[x][y]=new Tile(xPosition, yPosition, tileSize, null, false, this);
				else if(values[x][y]==23)
					tiles[x][y]=new Tile(xPosition, yPosition, tileSize, null, true, this);
			}

		}
		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if(flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					else
						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}

			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
				}
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}
	
	
	//#############################################################################################################
	//Your code goes here! 
	//Please make sure you read the rubric/directions carefully and implement the solution recursively!
	private void water(int col, int row, Map map, int fullness) {
		Water solid=new Water(col, row, tileSize, tileset.getImage("Full_water"), this, fullness);
		Water half =new Water(col,row, tileSize,tileset.getImage("Half_water"),this,fullness);
		Water quarter=new Water (col,row, tileSize,tileset.getImage("Quarter_water"),this,fullness);
		Water rain = new Water(col,row, tileSize,tileset.getImage("Falling_water"),this,fullness);
		//Make the water block I want become added to the map as I build it
		if(fullness==3){
			map.addTile(col, row,solid);
			fullness=2;
		}else if(fullness==2){
			map.addTile(col, row, half);
			fullness=1;
		}else if(fullness==1){
			map.addTile(col,row,quarter);
		}else if(fullness==0){
			map.addTile(col,row,rain);
		}
		if(row+1<map.getTiles()[0].length&&!map.getTiles()[col][row+1].isSolid()){
			fullness=0;
			//when water hits the floor
			if(row+2<map.getTiles()[0].length&&map.getTiles()[col][row+2].isSolid()){
				fullness=3;
			}
			water(col,row+1,map,fullness);
		}
		else{
		//Examine all the boundaries for left and right water falling:
		//right
		if(col+1 < map.getTiles().length && !(map.getTiles()[col+1][row] instanceof Water)&&!map.getTiles()[col+1][row].isSolid()&&fullness!=0) {
			water(col+1, row, map, fullness);
		}
		//left
		if(col-1 >= 0 && !(map.getTiles()[col-1][row] instanceof Water)&&!map.getTiles()[col-1][row].isSolid()&&fullness!=0) {
			water(col-1, row, map, fullness);
		}
		}
}
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
		while(numSquaresToFill>0){
			map.addTile(col, row, new Gas(col,row,tileSize,tileset.getImage("GasOne"),this,0));
			numSquaresToFill-=1;
			//update the values for this iteration and each subsequent one
			boolean canGoLeft=col-1>=0;
			boolean canGoRight=col+1<map.getTiles().length;
			boolean canGoUp=row-1>=0;
			boolean canGoDown=row+1<map.getTiles()[0].length;
			Tile t=canGoUp? map.getTiles()[row-1][col]:null;
			Tile tr=canGoUp&&canGoRight? map.getTiles()[col+1][row-1]:null;
			Tile tl= canGoUp&&canGoLeft? map.getTiles()[col-1][row-1]:null;
			Tile r=canGoRight? map.getTiles()[col+1][row]:null;
			Tile l=canGoLeft? map.getTiles()[col-1][row]:null;
			Tile d=canGoDown? map.getTiles()[col][row-1]:null;
			Tile dr=canGoDown&&canGoRight? map.getTiles()[col+1][row+1]:null;
			Tile dl=canGoDown&&canGoLeft? map.getTiles()[col-1][row+1]:null;
			//every if statement within the loop starts with this condition and an && 
			//so that the loop auto stops when you meet that condition
			//Do an upwards check
			if(numSquaresToFill>0 && t!=null){
				//No need to check tr,tl,dr, dl for null bc booleans have done that
				if(!(t.isSolid()&&t instanceof Gas)){
					Gas g=new Gas(t.getCol(),t.getRow(),tileSize,tileset.getImage("GasOne"),this,0);
					placedThisRound.add(g);
					map.addTile(t.getCol(),t.getRow(),placedThisRound.get(placedThisRound.size()-1));
					numSquaresToFill-=1;
				}
				if(numSquaresToFill>0&&canGoRight&&!(tr.isSolid() && tr instanceof Gas)){
					Gas g=new Gas(tr.getCol(),tr.getRow(),tileSize,tileset.getImage("GasOne"),this,0);
					placedThisRound.add(g);
					map.addTile(tr.getCol(),tr.getRow(),g);
					numSquaresToFill-=1;
				}
				if(numSquaresToFill>0&&canGoLeft&&!(tl.isSolid()&& tl instanceof Gas)){
					Gas g=new Gas(tl.getCol(),tl.getRow(),tileSize,tileset.getImage("GasOne"),this,0);
					placedThisRound.add(g);
					map.addTile(tl.getCol(),tl.getRow(),g);
					numSquaresToFill-=1;
				}
			}
			//Do a purely horizontal check
			if(numSquaresToFill>0&&canGoRight&&!(r.isSolid()&& r instanceof Gas)){
				Gas g=new Gas(r.getRow(),r.getCol(),tileSize,tileset.getImage("GasOne"),this,0);
				placedThisRound.add(g);
				map.addTile(r.getCol(),r.getRow(),g);
				numSquaresToFill-=1;	
			}
			if(numSquaresToFill>0&&canGoLeft&&!(l.isSolid()&& l instanceof Gas)){
				Gas g=new Gas(l.getCol(),l.getRow(),tileSize,tileset.getImage("GasOne"),this,0);
				placedThisRound.add(g);
				map.addTile(l.getCol(),l.getRow(),g);
				numSquaresToFill-=1;
			}
			//Do the negative of the upwards check
			if(numSquaresToFill>0&&canGoDown&&d!=null){
				if(!(d.isSolid()&& d instanceof Gas)) {
					Gas g=new Gas(d.getCol(),d.getRow(),tileSize,tileset.getImage("GasOne"),this,0);
					placedThisRound.add(g);
					map.addTile(d.getCol(),d.getRow(),placedThisRound.get(placedThisRound.size()-1));
					numSquaresToFill-=1;
				}
				if(numSquaresToFill>0&&canGoRight&&!(dr.isSolid()&& dr instanceof Gas)){
					Gas g=new Gas(d.getCol(),dr.getRow(),tileSize,tileset.getImage("GasOne"),this,0);
					placedThisRound.add(g);
					map.addTile(col+1,row+1,g);
					numSquaresToFill-=1;
				}
				if(numSquaresToFill>0&&canGoLeft&&!(dl.isSolid()&& dl instanceof Gas)){
					Gas g=new Gas(dl.getCol(),dl.getRow(),tileSize,tileset.getImage("GasOne"),this,0);
					placedThisRound.add(g);
					map.addTile(dl.getCol(),dl.getRow(),g);
					numSquaresToFill-=1;
				}
			}
		}
		}	


public void draw(Graphics g) {
	   	 g.translate((int) -camera.getX(), (int) -camera.getY());
	   	 // Draw the map
	   	 for (int x = 0; x < map.getWidth(); x++) {
	   		 for (int y = 0; y < map.getHeight(); y++) {
	   			 Tile tile = map.getTiles()[x][y];
	   			 if (tile == null)
	   				 continue;
	   			 if(tile instanceof Gas) {
	   				
	   				 int adjacencyCount =0;
	   				 for(int i=-1; i<2; i++) {
	   					 for(int j =-1; j<2; j++) {
	   						 if(j!=0 || i!=0) {
	   							 if((x+i)>=0 && (x+i)<map.getTiles().length && (y+j)>=0 && (y+j)<map.getTiles()[x].length) {
	   								 if(map.getTiles()[x+i][y+j] instanceof Gas) {
	   									 adjacencyCount++;
	   								 }
	   							 }
	   						 }
	   					 }
	   				 }
	   				 if(adjacencyCount == 8) {
	   					 ((Gas)(tile)).setIntensity(2);
	   					 tile.setImage(tileset.getImage("GasThree"));
	   				 }
	   				 else if(adjacencyCount >5) {
	   					 ((Gas)(tile)).setIntensity(1);
	   					tile.setImage(tileset.getImage("GasTwo"));
	   				 }
	   				 else {
	   					 ((Gas)(tile)).setIntensity(0);
	   					tile.setImage(tileset.getImage("GasOne"));
	   				 }
	   			 }
	   			 if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
	   				 tile.draw(g);
	   		 }
	   	 }


	   	 // Draw the enemies
	   	 for (int i = 0; i < enemies.length; i++) {
	   		 enemies[i].draw(g);
	   	 }


	   	 // Draw the player
	   	 player.draw(g);




	   	 // used for debugging
	   	 if (Camera.SHOW_CAMERA)
	   		 camera.draw(g);
	   	 	g.translate((int) +camera.getX(), (int) +camera.getY());
	}


	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}

}