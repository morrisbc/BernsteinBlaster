package sprites;

import java.util.ArrayList;

import visual.dynamic.described.AbstractSprite;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;

public class Bullet extends AbstractSprite
{
  private TransformableContent content;
  private double x, y;
  private Stage stage;
  private ArrayList<Enemy> antagonists;
  
  public Bullet (TransformableContent content, double x, double y, Stage stage, 
      ArrayList<Enemy> antagonists)
  {
    super();
    this.content = content;
    this.x = x;
    this.y = y;
    this.stage = stage;
    this.antagonists = antagonists;
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
    y -= 15;
    if (y <= -10)
    {
      stage.remove(this);
      return;
    }
    setLocation(x, y);
    
    for (Enemy e : antagonists)
    {
      if (intersects(e) && e.getBounds2D().getY() > 0)
      {
        e.takeDamage();
        if (e.getHealth() <= 0)
        {
          antagonists.remove(e);
          stage.remove(e);
        }
        stage.remove(this);
        break;
      }
    }
  }

}
