package sprites;

import java.util.Random;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class Enemy extends AbstractSprite
{
  private TransformableContent content;
  double maxX, maxY, x, y;
  Random rng;
  
  public Enemy(TransformableContent content, int width, int height)
  {
    super();
    this.content = content;
    this.maxX = width;
    this.maxY = height;
    rng = new Random();
    x = rng.nextDouble() * maxX;
    y = rng.nextDouble() * -250.0;
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
    y += 3;
    if (y > maxY)
    {
      x = rng.nextDouble() * maxX;
      y = rng.nextDouble() * -250.0;
    }
    setLocation(x, y);
    setRotation(angle += 0.1);
  }
  
}
