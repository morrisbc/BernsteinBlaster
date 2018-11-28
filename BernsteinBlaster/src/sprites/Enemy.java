package sprites;

import java.util.Random;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class Enemy extends AbstractSprite
{
  private TransformableContent content;
  private double maxX, maxY, x, y;
  private Ship protagonist;
  private boolean hit;
  private Random rng;
  private int id;
  
  private static int idCounter = 0;
  
  public Enemy(TransformableContent content, int width, int height, Ship protagonist)
  {
    super();
    this.content = content;
    this.maxX = width;
    this.maxY = height;
    this.protagonist = protagonist;
    hit = false;
    id = idCounter++;
    rng = new Random(System.currentTimeMillis());
    x = rng.nextDouble() * (maxX - content.getBounds2D(true).getWidth());
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
      hit = false;
    }
    setLocation(x, y);
    
    if (intersects(protagonist) && x > 0 && x < maxX && y > 0 && y < maxY && !hit) 
    {
      hit = true;
      protagonist.setHealth(protagonist.getHealth() - 1);
      System.out.println("Hit by, ID: " + id + " Health: " + protagonist.getHealth());
    }
  }
}
