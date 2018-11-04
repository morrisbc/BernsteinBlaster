package applications;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import app.JApplication;
import event.Metronome;
import event.MetronomeListener;
import io.ResourceFinder;
import visual.Visualization;
import visual.VisualizationView;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;
import visual.statik.sampled.ContentFactory;

public class BernsteinBlaster extends JApplication implements KeyListener, ActionListener, MetronomeListener
{ 
  private static final Color BORDER_COLOR = new Color(168, 13, 142);
  
  private JPanel contentPane;
  private Visualization menuViz;
  private Stage gameViz, bernViz; 
  private TransformableContent ship, jaw;
  private int currX, currY;
  private Clip menuMusic, laserSound;
  private Metronome talk;
  private boolean jawUp = true;

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
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(null);
    contentPane.setBackground(Color.BLACK);
    
    talk = new Metronome();
    
    setupMenu();
  }
  
  public static void main(String[] args) throws InvocationTargetException, InterruptedException
  {
    SwingUtilities.invokeAndWait(new BernsteinBlaster(1280, 720));
  }
  
  /**
   * Helper function that updates the content pane with the appropriate components for the main
   * menu of the game.
   */
  private void setupMenu()
  {
    JButton start, highScores;
    VisualizationView view;
    TransformableContent logo, stars;
    
    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    ContentFactory factory = new ContentFactory(finder);
    
    // Clear the content pane. This isn't necessary on startup but will be after the 
    // implementation of an in game quit button
    contentPane.removeAll();
    
    // Setup the start button and add it to the panel
    start = new JButton("START");
    start.setBounds(400, 550, 150, 50);
    start.setOpaque(true);
    start.setBackground(Color.BLACK);
    //startButton.setForeground(Color.GREEN);
    start.setBorder(new MatteBorder(3, 3, 3, 3, BORDER_COLOR));
    contentPane.add(start);
    
    // Setup the highscores button and add it to the panel
    highScores = new JButton("HIGHSCORES");
    highScores.setBounds(730, 550, 150, 50);
    highScores.setOpaque(true);
    highScores.setBackground(Color.BLACK);
    //highScores.setForeground(Color.GREEN);
    highScores.setBorder(new MatteBorder(3, 3, 3, 3, BORDER_COLOR));
    contentPane.add(highScores);
    
    // Visualization for the main menu containing the stars background as well
    // as the game logo
    menuViz = new Visualization();
    
    // Create the background content and add it to the Visualization
    stars = factory.createContent("stars.png");
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    menuViz.add(stars);
    
    // Create the logo content and add it to the Visualization
    logo = factory.createContent("Bernstein.png", 4);
    logo.setLocation((width/2) - 239, (height/4));
    menuViz.add(logo);
    
    // Create the VisualizationView and set the bounds to match the window
    view = menuViz.getView();
    view.setBounds(0, 0, width, height);
    view.setBackground(Color.BLACK);
    contentPane.add(view);
    
    // Update the content pane for the changes to be visible
    contentPane.repaint();
    
    // Add the application as a listener of both the main menu buttons
    start.addActionListener(this);
    highScores.addActionListener(this);
    
    // Attempt the start the menu background music
    try
    {
      menuMusic = AudioSystem.getClip();
      menuMusic.open(AudioSystem.getAudioInputStream(new File("MenuMusic.wav")));
      menuMusic.start();
    }
    catch (LineUnavailableException | IOException | UnsupportedAudioFileException e)
    {
      System.out.println("Exception");
    }
  }
  
  /**
   * Helper function that updates the content pane with the appropriate components for the main
   * section of the game.
   */
  private void setupGame()
  {
    VisualizationView gameView, bernView;
    JLabel textField;
    TransformableContent stars, bernNPC, blur;
    
    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    ContentFactory factory = new ContentFactory(finder);
    
    // Clear the components from the main menu
    contentPane.removeAll();
    
    // Setup the text field where NPC communications will appear
    textField = new JLabel("Put some text here");
    textField.setBounds(0, 320, 320, 400);
    textField.setOpaque(true);
    textField.setBackground(Color.BLACK);
    textField.setBorder(new MatteBorder(5, 5, 5, 5, BORDER_COLOR));
    textField.setForeground(BORDER_COLOR);
    contentPane.add(textField);
    
    // Create the stage for the main portion of the game containing the ship
    // sprites and the player model
    gameViz = new Stage(50);
    gameViz.addKeyListener(this);
    
    // Construct and add the star background
    stars = factory.createContent("stars.png");
    stars.setScale(0.8, 0.8);
    stars.setLocation(0, 0);
    gameViz.add(stars);
    
    // Construct and add the ship player model
    ship = factory.createContent("spaceship.png", 4);
    ship.setScale(0.1, 0.1);
    currX = 450;
    currY = 655;
    ship.setLocation(currX, currY);
    gameViz.add(ship);
    
    // Add the VisualizationView of the main game section to the content pane
    gameView = gameViz.getView();
    gameView.setBounds(320, 0, width - 300, height);
    gameView.setBackground(Color.WHITE);
    gameView.setBorder(new MatteBorder(5, 5, 5, 5, BORDER_COLOR));
    contentPane.add(gameView);
    
    // Create the Stage for the NPC
    bernViz = new Stage(50);
    
    // Create and add the background of the NPC
    blur = factory.createContent("blur.png", 4);
    blur.setLocation(0, 0);
    blur.setScale(0.5, 0.5);
    bernViz.add(blur);
    
    // Create and add the NPC without the jaw 
    bernNPC = factory.createContent("no_jaw.png", 4);
    bernNPC.setLocation(0, 45);
    bernNPC.setScale(0.9, 0.9);
    bernViz.add(bernNPC);
    
    // Create and add the NPC's jaw
    jaw = factory.createContent("jaw.png", 4);
    jaw.setLocation(155, 190);
    bernViz.add(jaw);
    
    // Create and setup the VisualizationView of the NPC portion of the panel
    bernView = bernViz.getView();
    bernView.setBounds(0, 0, 320, 320);
    bernView.setBackground(Color.BLACK);
    bernView.setBorder(new MatteBorder(5, 5, 5, 5, BORDER_COLOR));
    contentPane.add(bernView);
    
    // Stop the music from the main menu
    menuMusic.stop();
    
    // Add the application as a metronome listener to be able to move the NPC/s
    // jaw at each tick and start the metronome
    talk.addListener(this);
    talk.start();
    
    // Refresh the content pane for the changes to be visible
    contentPane.repaint();
  }

  @Override
  public void keyPressed(KeyEvent stroke)
  {
    char key;
    
    key = stroke.getKeyChar();
    
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
    
    if (key == ' ')
    {
      
    }
    
    
    gameViz.repaint();
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

  @Override
  public void actionPerformed(ActionEvent button)
  {
    System.out.println(button.getActionCommand());
    if (button.getActionCommand().equals("START"))
    {
      setupGame();
    }
  }
  
  @Override
  public void stop()
  {
    menuMusic.stop();
    talk.stop();
  }
  
  @Override
  public void start()
  {
    menuMusic.start();
    talk.start();
  }

  @Override
  public void handleTick(int tick)
  {
    if (jawUp) {
      jaw.setLocation(155, 210);
      jawUp = false;
    }
    else {
      jaw.setLocation(155, 190);
      jawUp = true;
    }
    
    bernViz.repaint();
  } 
}
