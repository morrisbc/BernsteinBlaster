package sprites;

import java.util.Random;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class Enemy extends AbstractSprite
{
  private TransformableContent content;
  private double maxX, maxY, x, y;
  private Ship protagonist;
  private int health;
  private boolean hitShip;
  private Random rng;
  
  public Enemy(TransformableContent content, int width, int height, Ship protagonist)
  {
    super();
    this.content = content;
    this.maxX = width;
    this.maxY = height;
    this.protagonist = protagonist;
    health = 3;
    hitShip = false;
    rng = new Random(System.currentTimeMillis());
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
  
  public void setHealth(int health)
  {
    this.health = health;
  }
  
  public int getHealth()
  {
    return health;
  }

  @Override
  public void handleTick(int tick)
  {
    y += 5;
    if (y > maxY)
    {
      x = rng.nextDouble() * maxX;
      y = rng.nextDouble() * -250.0;
      hitShip = false;
    }
    setLocation(x, y);
    
    if (intersects(protagonist) && x > 0 && x < maxX && y > 0 && y < maxY && !hitShip) 
    {
      hitShip = true;
      protagonist.setHealth(protagonist.getHealth() - 1);
      System.out.println("Hit, Health: " + protagonist.getHealth());
    }
  }
}
