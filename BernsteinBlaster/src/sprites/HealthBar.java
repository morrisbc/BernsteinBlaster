package sprites;

import visual.dynamic.described.AbstractSprite;
import visual.statik.TransformableContent;

public class HealthBar extends AbstractSprite
{
  private TransformableContent[] contents;
  private Ship protagonist;
  private int x, y;
  private int state;
  
  public HealthBar(int x, int y, TransformableContent[] contents, Ship protagonist)
  {
    this.x = x;
    this.y = y;
    setLocation(this.x, this.y);
    this.contents = contents;
    this.protagonist = protagonist;
    state = 0;
    setVisible(true);
  }

  @Override
  protected TransformableContent getContent()
  {
    return contents[state];
  }

  @Override
  public void handleTick(int tick)
  {
    if (protagonist.getHealth() > 0)
    {
      state = protagonist.getHealth() - 1;
    }
    
  }
}
