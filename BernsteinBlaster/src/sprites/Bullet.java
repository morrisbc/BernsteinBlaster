package sprites;

import java.util.ArrayList;

import visual.dynamic.described.AbstractSprite;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;

/**
 * A Bullet sprite within the game. This entity is shot from the protagonist.
 * 
 * @author Bryce Morris <morrisbc@dukes.jmu.edu>, Dylan Parsons <parsondm@dukes.jmu.edu>
 * @version V1 12/3/18
 */
public class Bullet extends AbstractSprite
{
  private TransformableContent content;
  private double x, y;
  private Stage stage;
  private ArrayList<Enemy> antagonists;
  
  /**
   * Constructor for a Bullet sprite.
   * 
   * @param content The piece of content to render
   * @param x The x position of the Bullet
   * @param y The y position of the Bullet
   * @param stage The Stage the Bullet is contained in
   * @param antagonists The list of enemy Sprites
   */
  public Bullet (TransformableContent content, double x, double y, Stage stage, 
      ArrayList<Enemy> antagonists)
  {
    super();
    this.content = content;
    this.x = x;
    this.y = y;
    this.stage = stage;
    this.antagonists = antagonists;
    setLocation(x, y);
    setVisible(true);
  }
  
  @Override
  protected TransformableContent getContent()
  {
    return content;
  }

  @Override
  public void handleTick(int tick)
  {
    y -= 15;
    if (y <= -10)
    {
      stage.remove(this);
      return;
    }
    setLocation(x, y);
    
    for (Enemy e : antagonists)
    {
      if (intersects(e) && e.getBounds2D().getY() > 0)
      {
        e.takeDamage();
        if (e.getHealth() <= 0)
        {
          antagonists.remove(e);
          stage.remove(e);
        }
        stage.remove(this);
        break;
      }
    }
  }

}
