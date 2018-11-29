package sprites;

import visual.dynamic.described.AbstractSprite;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;

public class Bullet extends AbstractSprite
{
  private TransformableContent content;
  private double x, y;
  private Stage stage;
  
  public Bullet (TransformableContent content, double x, double y, Stage stage)
  {
    super();
    this.content = content;
    this.x = x;
    this.y = y;
    this.stage = stage;
    setLocation(x, y);
    setRotation(Math.PI / 2);
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
    }
    setLocation(x, y);
  }

}
