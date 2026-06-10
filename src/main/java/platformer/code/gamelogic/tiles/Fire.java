package platformer.code.gamelogic.tiles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import platformer.code.gameengine.GameObject;
import platformer.code.gameengine.hitbox.Hitbox;
import platformer.code.gameengine.hitbox.RectHitbox;
import platformer.code.gameengine.maths.Vector2D;
import platformer.code.gamelogic.level.Level;

//Gives players immunity from enemies for 10 seconds
//Also, water puts out fire, gas is replaced by fire because it makes fire explode
public class Fire extends Tile{
  public Fire(float x, float y, int size, BufferedImage image, Level level) {
    super(x,y,size,image,false,level);
  	this.hitbox = new RectHitbox(x*size , y*size, 0, 10, size, size);
  } 
  public Hitbox geHitbox(){
    return this.hitbox;
  }
}
