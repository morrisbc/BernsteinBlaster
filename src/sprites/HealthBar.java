package sprites;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

/**
 * The main health pool for the player in the game. This displays to the player
 * the health of the protagonist at any given time.
 * 
 * @author Bryce Morris <morrisbc@dukes.jmu.edu>, Dylan Parsons <parsondm@dukes.jmu.edu>
 * @version V1 12/3/18
 */
public class HealthBar extends AbstractSprite
{
  private TransformableContent[] contents;
  private Ship protagonist;
  private int x, y;
  private int state;
  
  /**
   * The constructor for the HealthBar.
   * 
   * @param x The x location of the HealthBar
   * @param y The y location of the HealthBar
   * @param contents The multiple contents representing health levels
   * @param protagonist The main character of the game whose health is displayed
   */
  public HealthBar(int x, int y, TransformableContent[] contents, Ship protagonist)
  {
    this.x = x;
    this.y = y;
    setLocation(this.x, this.y);
    this.contents = contents;
    this.protagonist = protagonist;
    state = 0;
    setVisible(true);
  }

  @Override
  protected TransformableContent getContent()
  {
    return contents[state];
  }

  @Override
  public void handleTick(int tick)
  {
    if (protagonist.getHealth() > 0)
    {
      state = protagonist.getHealth() - 1;
    }
    
  }
}
