package applications;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import app.JApplication;
import io.ResourceFinder;
import visual.Visualization;
import visual.VisualizationView;
import visual.statik.TransformableContent;
import visual.statik.sampled.ContentFactory;

public class BernsteinBlaster extends JApplication implements KeyListener
{ 
  Visualization viz;
  visual.statik.sampled.TransformableContent ship;
  TransformableContent stars;
  int currX, currY;

  public BernsteinBlaster(String[] args, int width, int height)
  {
    super(args, width, height);
  }
  
  public BernsteinBlaster(int width, int height)
  {
    super(width, height);
  }

  @Override
  public void init()
  {
    JPanel contentPane;
    VisualizationView view;
    JLabel textField;
    
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(null);
    contentPane.setBackground(Color.WHITE);
    
    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    ContentFactory factory = new ContentFactory(finder);
    
    textField = new JLabel("Put some text here");
    textField.setBounds(0, 320, 320, 400);
    textField.setOpaque(true);
    textField.setBackground(Color.BLACK);
    textField.setBorder(new MatteBorder(5, 5, 5, 5, new Color(96, 198, 45)));
    textField.setForeground(Color.GREEN);
    contentPane.add(textField);
    
    viz = new Visualization();
    viz.addKeyListener(this);
    
    ship = factory.createContent("spaceship.png");
    ship.setScale(0.1, 0.1);
    currX = 600;
    currY = 655;
    ship.setLocation(currX, currY);
    
    stars = factory.createContent("stars.png");
    stars.setScale(0.8, 0.8);
    stars.setLocation(0, 0);
    
    viz.add(stars);
    viz.add(ship);
    
    view = viz.getView();
    view.setBounds(320, 0, width - 300, height);
    view.setBackground(Color.WHITE);
    
    contentPane.add(view);
  }
  
  public static void main(String[] args) throws InvocationTargetException, InterruptedException
  {
    SwingUtilities.invokeAndWait(new BernsteinBlaster(1280, 720));
  }

  @Override
  public void keyPressed(KeyEvent arg0)
  {
    char key;
    
    key = arg0.getKeyChar();
    
    ship.setScale(0.1, 0.1);
    ship.setLocation(currX, currY);
    
    if (key == 'a' && currX > 0)
    {
      ship.setLocation(currX -= 10, currY);
    }
    
    if (key == 'd' && currX < 900)
    {
      ship.setLocation(currX += 10, currY);
    }
    
    viz.repaint();
  }

  @Override
  public void keyReleased(KeyEvent arg0)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void keyTyped(KeyEvent arg0)
  {
    // TODO Auto-generated method stub
    
  }

}
