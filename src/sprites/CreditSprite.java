package sprites;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

/**
 * An element of the in game credit sequence. These move up the screen as the Metronome
 * they listen to ticks.
 * 
 * @author Bryce Morris <morrisbc@dukes.jmu.edu>, Dylan Parsons <parsondm@dukes.jmu.edu>
 * @version V1 12/3/18
 */
public class CreditSprite extends AbstractSprite
{
  private TransformableContent content;
  private double x, y;
  private int tickDelay;

  /**
   * The constructor for a CreditSprite.
   * 
   * @param content The content to render
   * @param x The x location of the CreditSprite
   * @param y The y location of the CreditSprite
   */
  public CreditSprite(TransformableContent content, double x, double y)
  {
    super();
    this.content = content;
    this.x = x;
    this.y = y;
    tickDelay = 0;
    setLocation(x, y);
    setVisible(true);
  }
  
  @Override
  protected TransformableContent getContent()
  {
    return content;
  }
  
  /**
   * Returns this CreditSprite's x value.
   * 
   * @return This CreditSprite's x value
   */
  public double getX()
  {
    return x;
  }
  
  /**
   * Returns this CreditSprite's y value.
   * 
   * @return This CreditSprite's y value
   */
  public double getY()
  {
    return y;
  }

  @Override
  public void handleTick(int tick)
  {
    tickDelay++;
    if (tickDelay >= 15)
    {
      y -= 3;
      setLocation(x, y);
    }
  }

}
