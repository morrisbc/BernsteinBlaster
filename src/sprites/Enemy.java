package sprites;

import java.util.Random;

import javax.sound.sampled.Clip;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

/**
 * An Enemy sprite within the game. This does damage to the protagonist on contact.
 * 
 * @author Bryce Morris <morrisbc@dukes.jmu.edu>, Dylan Parsons <parsondm@dukes.jmu.edu>
 * @version V1 12/3/18
 */
public class Enemy extends AbstractSprite
{
  private TransformableContent contents[];
  private double maxX, maxY, x, y;
  private Ship protagonist;
  private int health;
  private int state;
  private boolean hitShip;
  private Random rng;
  private Clip damageSound;
  
  /**
   * Constructor for an Enemy Sprite within the game.
   * 
   * @param contents The Contents to render.
   * @param width The width of the Enemy's container
   * @param height The height of the Enemy's container
   * @param protagonist The Enemy's enemy (the game protagonist)
   * @param damageSound The audio that plays when the Enemy takes damage
   */
  public Enemy(TransformableContent contents[], int width, int height, Ship protagonist, 
               Clip damageSound)
  {
    super();
    this.contents = contents;
    this.maxX = width;
    this.maxY = height;
    this.protagonist = protagonist;
    health = 3;
    state = health - 1;
    hitShip = false;
    this.damageSound = damageSound;
    rng = new Random(System.currentTimeMillis());
    x = rng.nextDouble() * (maxX - 50);
    y = rng.nextDouble() * -250.0;
    setLocation(x, y);
    setVisible(true);
  }
  
  @Override
  protected TransformableContent getContent()
  {
    return contents[state];
  }
  
  /**
   * Sets the Enemy's current health.
   * 
   * @param health The Enemy's new health
   */
  private void setHealth(int health)
  {
    this.health = health;
  }
  
  /**
   * Returns the Enemy's current health.
   * 
   * @return The Enemy's current health
   */
  public int getHealth()
  {
    return health;
  }
  
  /**
   * Damages the Enemy, changes its character model to reflect the damage taken,
   * and plays its damage audio.
   */
  public void takeDamage()
  {
    setHealth(getHealth() - 1);
    if (getHealth() > 0) 
    {
      state = getHealth() - 1;
    }
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
