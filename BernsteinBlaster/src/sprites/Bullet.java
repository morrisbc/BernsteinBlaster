package sprites;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class Bullet extends AbstractSprite
{
  private TransformableContent content;
  private double x, y;
  
  public Bullet (TransformableContent content, double x, double y)
  {
    super();
    this.content = content;
    this.x = x;
    this.y = y;
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
    y -= 5;
  }

}
