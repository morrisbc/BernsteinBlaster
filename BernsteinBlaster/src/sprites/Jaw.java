package sprites;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class Jaw extends AbstractSprite
{
  private TransformableContent content;
  private double x, y;
  private boolean jawUp;
  
  public Jaw(TransformableContent content, double x, double y)
  {
    this.content = content;
    this.x = x;
    this.y = y;
    jawUp = true;
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
    if (jawUp)
    {
      y += 10;
      setLocation(x, y);
      jawUp = false;
    }
    else
    {
      y -= 10;
      setLocation(x, y);
      jawUp = true;
    }
  }

}
