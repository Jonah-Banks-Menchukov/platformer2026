package platformer.code.gamelogic.tiles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import platformer.code.gameengine.GameObject;
import platformer.code.gameengine.hitbox.Hitbox;
import platformer.code.gameengine.hitbox.RectHitbox;
import platformer.code.gameengine.maths.Vector2D;
import platformer.code.gamelogic.level.Level;
import platformer.code.gameengine.loaders.Tileset;
//Poop is a booby trap piece, only appears when player steps on a tile designated as a booby trap
public class Poop extends Tile{
    private boolean disguised;
    private BufferedImage poopImage;
    public Poop(float x, float y, int size, BufferedImage image, Level level,boolean disguised) {
    super(x,y,size,image,false,level);
   this.disguised=disguised;
  	this.hitbox = new RectHitbox(x*size , y*size, 0, 10, size, size);
    poopImage=new Tileset().getImage("Poop");
  } 
  public Hitbox geHitbox(){
    return this.hitbox;
  }
  @Override
  public void setImage(BufferedImage b){
    if(disguised){
        super.setImage(b);
    }else{
        super.setImage(poopImage);
    }
  }
  public void setDisguised(boolean b){
    disguised=b;
  }
}
