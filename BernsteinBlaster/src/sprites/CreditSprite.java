package sprites;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class CreditSprite extends AbstractSprite
{
  private TransformableContent content;
  double x, y;
  int tickDelay;

  public CreditSprite(TransformableContent content, double x, double y)
  {
    super();
    this.content = content;
    this.x = x;
    this.y = y;
    tickDelay = 0;
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
    tickDelay++;
    if (tickDelay >= 15)
    {
      y -= 5;
      setLocation(x, y);
    }
  }

}
