package sprites;

import java.util.Random;

import javax.sound.sampled.Clip;

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
  private Clip damageSound;
  
  public Enemy(TransformableContent content, int width, int height, Ship protagonist, Clip damageSound)
  {
    super();
    this.content = content;
    this.maxX = width;
    this.maxY = height;
    this.protagonist = protagonist;
    health = 3;
    hitShip = false;
    this.damageSound = damageSound;
    rng = new Random(System.currentTimeMillis());
    x = rng.nextDouble() * (maxX - 10);
    y = rng.nextDouble() * -250.0;
    setLocation(x, y);
    setVisible(true);
  }
  
  @Override
  protected TransformableContent getContent()
  {
    return content;
  }
  
  private void setHealth(int health)
  {
    this.health = health;
  }
  
  public int getHealth()
  {
    return health;
  }
  
  public void takeDamage()
  {
    setHealth(getHealth() - 1);
    damageSound.setMicrosecondPosition(0);
    damageSound.start();
  }

  @Override
  public void handleTick(int tick)
  {
    y += 5;
    if (y > maxY)
    {
      x = rng.nextDouble() * maxX;
      y = rng.nextDouble() * -200;
      hitShip = false;
    }
    setLocation(x, y);
    
    if (intersects(protagonist) && x > 0 && x < maxX && y > 0 && y < maxY && !hitShip) 
    {
      hitShip = true;
      protagonist.takeDamage();
      System.out.println("Hit, Health: " + protagonist.getHealth());
    }
  }
}
