package sprites;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

/**
 * The jaw of the in game NPC (Lord Bernstein). This moves up and down to simulate speech.
 * 
 * @author Bryce Morris <morrisbc@dukes.jmu.edu>, Dylan Parsons <parsondm@dukes.jmu.edu>
 * @version V1 12/3/18
 */
public class Jaw extends AbstractSprite
{
  private TransformableContent content;
  private double x, y;
  private boolean jawUp;
  
  /**
   * The constructor for the Jaw Sprite.
   * 
   * @param content The content to render
   * @param x The x location of the Jaw
   * @param y The y location of the Jaw
   */
  public Jaw(TransformableContent content, double x, double y)
  {
    this.content = content;
    this.x = x;
    this.y = y;
    jawUp = true;
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
    if (jawUp)
    {
      y += 10;
      setLocation(x, y);
      jawUp = false;
    }
    else
    {
      y -= 10;
      setLocation(x, y);
      jawUp = true;
    }
  }

}
