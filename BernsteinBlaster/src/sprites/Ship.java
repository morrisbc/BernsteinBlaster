package sprites;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.sound.sampled.Clip;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class Ship extends AbstractSprite implements KeyListener
{
  private TransformableContent content;
  private double minX, maxX, x, y;
  private int health;
  private Clip damageSound;
  
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
  public void keyPressed(KeyEvent stroke)
  {
    char key;
    
    key = stroke.getKeyChar();
    
    if (key == 'a' && x > minX)
    {
      setLocation(x -= 10, y);
    }
    
    if (key == 'd' && x < maxX)
    {
      setLocation(x += 10, y);
    }
  }
  
  public double getX()
  {
    return x;
  }
  
  public double getY()
  {
    return y;
  }

  @Override
  public void keyReleased(KeyEvent key)
  {
    // TODO Auto-generated method stub  
  }

  @Override
  public void keyTyped(KeyEvent key)
  {
    // TODO Auto-generated method stub  
  }

}
