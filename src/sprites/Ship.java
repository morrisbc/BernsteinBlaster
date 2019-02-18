package sprites;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.sound.sampled.Clip;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

/**
 * A Ship sprite within the game. This entity is the main character or protagonist.
 * 
 * @author Bryce Morris <morrisbc@dukes.jmu.edu>, Dylan Parsons <parsondm@dukes.jmu.edu>
 * @version V1 12/3/18
 */
public class Ship extends AbstractSprite implements KeyListener
{
  private TransformableContent content;
  private double minX, maxX, x, y;
  private int health;
  private Clip damageSound;
  
  /**
   * Constructor for the Ship Sprite.
   * 
   * @param content The Content to render
   * @param minX The minimum x value the Ship can move to
   * @param maxX The maximum x value the Ship can move to
   * @param damageSound The audio that plays when the Ship takes damage
   */
  public Ship(TransformableContent content, double minX, double maxX, Clip damageSound)
  {
    super();
    this.content = content;
    this.minX = minX;
    this.maxX = maxX;
    health = 3;
    this.damageSound = damageSound;
    x = 450;
    y = 655;
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
    // Unused as the Ship only needs to respond to key presses
  }
  
  /**
   * Sets the Ship's current health.
   * 
   * @param health The Ship's new health value.
   */
  private void setHealth(int health)
  {
    this.health = health;
  }
  
  /**
   * Returns the Ship's current health.
   * 
   * @return The Ship's current health
   */
  public int getHealth()
  {
    return health;
  }
  
  /**
   * 
   */
  public void takeDamage()
  {
    setHealth(getHealth() - 1);
    damageSound.setMicrosecondPosition(0);
    damageSound.start();
  }

  @Override
  public void keyPressed(KeyEvent stroke)
  {
    char key;
    
    key = stroke.getKeyChar();
    
    if (key == 'a' && x > minX)
    {
      x -= 10;
      setLocation(x, y);
    }
    
    if (key == 'd' && x < maxX)
    {
      x += 10;
      setLocation(x, y);
    }
  }
  
  /**
   * Returns the Ship's x coordinate.
   * 
   * @return The Ship's x coordinate
   */
  public double getX()
  {
    return x;
  }
  
  /**
   * Returns the Ship's y coordinate.
   * 
   * @return The Ship's y coordinate.
   */
  public double getY()
  {
    return y;
  }

  @Override
  public void keyReleased(KeyEvent key)
  {
    // UNUSED  
  }

  @Override
  public void keyTyped(KeyEvent key)
  {
    // UNUSED  
  }

}
