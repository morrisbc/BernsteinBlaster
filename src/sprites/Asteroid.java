package sprites;

import java.util.Random;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

/**
 * An Asteroid that flies around on screen from left to right in the background for 
 * visual aesthetics.
 * 
 * @author Bryce Morris <morrisbc@dukes.jmu.edu>, Dylan Parsons <parsondm@dukes.jmu.edu>
 * @version V1 12/3/18
 */
public class Asteroid extends AbstractSprite
{
  private TransformableContent content;
  private double maxX, maxY, x, y;
  private Random rng;
  
  /**
   * The constructor for the Asteroid.
   * 
   * @param content The Content to render
   * @param width The width of the Asteroid's container
   * @param height The height of the Asteroid's container
   */
  public Asteroid(TransformableContent content, int width, int height)
  {
    super();
    this.content = content;
    this.maxX = width;
    this.maxY = height;
    rng = new Random();
    x = -50.0;
    y = rng.nextDouble() * maxY;
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
    x += 15;
    if (x > maxX + 50.0)
    {
      x = 0.0;
      y = rng.nextDouble() * maxY;
    }
    setLocation(x, y);
    angle += 0.5;
    setRotation(angle);
  }

}
