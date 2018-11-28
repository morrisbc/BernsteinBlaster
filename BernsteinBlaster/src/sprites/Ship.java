package sprites;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class Ship extends AbstractSprite implements KeyListener
{
  private TransformableContent content;
  private double minX, maxX, x, y;
  private int health;
  
  public Ship(TransformableContent content, double minX, double maxX)
  {
    super();
    this.content = content;
    this.minX = minX;
    this.maxX = maxX;
    health = 3;
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
  
  public void setHealth(int health)
  {
    this.health = health;
  }
  
  public int getHealth()
  {
    return health;
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
    
    if (key == ' ')
    {
      System.out.println("Pew");
    }
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
