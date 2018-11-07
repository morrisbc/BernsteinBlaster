package sprites;

import java.util.Random;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class Asteroid extends AbstractSprite
{
  private TransformableContent content;
  private double maxX, maxY, x, y;
  private Random rng;
  
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
    setRotation(rng.nextDouble());
  }

}
