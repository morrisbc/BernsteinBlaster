package applications;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import app.JApplication;
import io.ResourceFinder;
import sprites.Asteroid;
import sprites.Jaw;
import visual.Visualization;
import visual.VisualizationView;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;
import visual.statik.sampled.ContentFactory;

public class BernsteinBlaster extends JApplication implements KeyListener, ActionListener
{ 
  private static final Color BORDER_COLOR = new Color(168, 13, 142);
  private static final MatteBorder BORDER = new MatteBorder(4, 4, 4, 4, BORDER_COLOR);
  
  private JPanel contentPane;
  private Stage menuStage;
  private TransformableContent ship;
  private int currX, currY;
  private Clip menuMusic, laserSound;
  private boolean menuPlaying;

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
    JButton start, highScores, credits;
    VisualizationView view;
    TransformableContent logo, stars, asteroidContent;
    Asteroid asteroid;
    
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
    start.setBorder(BORDER);
    contentPane.add(start);
    
    // Setup the highscores button and add it to the panel
    highScores = new JButton("HIGHSCORES");
    highScores.setBounds(730, 550, 150, 50);
    highScores.setOpaque(true);
    highScores.setBackground(Color.BLACK);
    highScores.setBorder(BORDER);
    contentPane.add(highScores);
    
    // Setup the credits and acknowledgments button
    credits = new JButton("CREDITS & ACKNOWLEDGMENTS");
    credits.setBounds(width - 275, height - 50, 275, 50);
    credits.setOpaque(true);
    credits.setBackground(Color.BLACK);
    credits.setBorder(BORDER);
    contentPane.add(credits);
    
    // Visualization for the main menu containing the stars background as well
    // as the game logo
    menuStage = new Stage(50);
    
    // Create the background content and add it to the Visualization
    stars = factory.createContent("stars.png");
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    menuStage.add(stars);
    
    asteroidContent = factory.createContent("Asteroid.png", 4);
    asteroid = new Asteroid(asteroidContent, width, height);
    asteroid.setScale(0.08, 0.08);
    menuStage.add(asteroid);
    menuStage.start();
    
    // Create the logo content and add it to the Visualization
    logo = factory.createContent("Bernstein.png", 4);
    logo.setLocation((width/2) - 239, (height/4));
    menuStage.add(logo);
    
    // Create the VisualizationView and set the bounds to match the window
    view = menuStage.getView();
    view.setBounds(0, 0, width, height);
    view.setBackground(Color.BLACK);
    contentPane.add(view);
    
    // Update the content pane for the changes to be visible
    contentPane.repaint();
    
    // Add the application as a listener of both the main menu buttons
    start.addActionListener(this);
    highScores.addActionListener(this);
    credits.addActionListener(this);
    
    // Attempt to start the menu background music
    try
    {
      if (!menuPlaying) {
        menuMusic = AudioSystem.getClip();
        menuMusic.open(AudioSystem.getAudioInputStream(new File("MenuMusic.wav")));
        menuMusic.start();
        menuPlaying = true;
      }
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
    Stage gameStage, bernStage;
    VisualizationView gameView, bernView;
    JLabel textField;
    TransformableContent stars, bernNPC, blur, jawContent;
    Jaw jaw;
    
    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    ContentFactory factory = new ContentFactory(finder);
    
    // Clear the components from the main menu
    contentPane.removeAll();
    
    // Setup the text field where NPC communications will appear
    textField = new JLabel("Put some text here");
    textField.setBounds(0, 320, 320, 400);
    textField.setOpaque(true);
    textField.setBackground(Color.BLACK);
    textField.setBorder(BORDER);
    textField.setForeground(BORDER_COLOR);
    contentPane.add(textField);
    
    // Create the stage for the main portion of the game containing the ship
    // sprites and the player model
    gameStage = new Stage(50);
    gameStage.addKeyListener(this);
    
    // Construct and add the star background
    stars = factory.createContent("stars.png");
    stars.setScale(0.8, 0.8);
    stars.setLocation(0, 0);
    gameStage.add(stars);
    
    // Construct and add the ship player model
    ship = factory.createContent("spaceship.png", 4);
    ship.setScale(0.1, 0.1);
    currX = 450;
    currY = 655;
    ship.setLocation(currX, currY);
    gameStage.add(ship);
    gameStage.start();
    
    // Add the VisualizationView of the main game section to the content pane
    gameView = gameStage.getView();
    gameView.setBounds(320, 0, width - 300, height);
    gameView.setBackground(Color.WHITE);
    gameView.setBorder(BORDER);
    contentPane.add(gameView);
    
    // Create the Stage for the NPC
    bernStage = new Stage(200);
    
    // Create and add the background of the NPC
    blur = factory.createContent("blur.png", 4);
    blur.setLocation(0, 0);
    blur.setScale(0.5, 0.5);
    bernStage.add(blur);
    
    // Create and add the NPC without the jaw 
    bernNPC = factory.createContent("no_jaw.png", 4);
    bernNPC.setLocation(0, 45);
    bernNPC.setScale(0.9, 0.9);
    bernStage.add(bernNPC);
    
    // Create and add the NPC's jaw
    jawContent = factory.createContent("jaw.png", 4);
    jaw = new Jaw(jawContent, 155, 190);
    bernStage.add(jaw);
    bernStage.start();
    
    // Create and setup the VisualizationView of the NPC portion of the panel
    bernView = bernStage.getView();
    bernView.setBounds(0, 0, 320, 320);
    bernView.setBackground(Color.BLACK);
    bernView.setBorder(BORDER);
    contentPane.add(bernView);
    
    // Stop the music from the main menu
    menuMusic.stop();
    menuPlaying = false;
    
    // Refresh the content pane for the changes to be visible
    contentPane.repaint();
  }
  
  private void setupCredits()
  {
    JLabel authors, audio;
    Visualization creditViz;
    VisualizationView creditView;
    TransformableContent stars;
    
    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    ContentFactory factory = new ContentFactory(finder);
    
    contentPane.removeAll();
    
    authors = new JLabel("Authors", SwingConstants.CENTER);
    authors.setBounds(width / 2 - 60, 20, 120, 50);
    authors.setOpaque(true);
    authors.setBackground(Color.BLACK);
    authors.setForeground(BORDER_COLOR);
    authors.setFont(new Font(Font.SERIF, Font.PLAIN, 36));
    contentPane.add(authors);
    
    audio = new JLabel("Audio");
    audio.setBounds(0, 200, 500, 100);
    audio.setForeground(Color.WHITE);
    contentPane.add(audio);
    
    creditViz = new Visualization();
    
    stars = factory.createContent("stars.png", 4);
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    creditViz.add(stars);
    
    creditView = creditViz.getView();
    creditView.setBounds(0, 0, width, height);
    contentPane.add(creditView);
    
    contentPane.repaint();
  }

  private void setupHighscores()
  {
    TransformableContent scoreLogo, stars, asteroidContent, first, second, third;
    Stage scoreStage;
    VisualizationView scoreView;
    Asteroid asteroid;
    
    ResourceFinder finder = ResourceFinder.createInstance(resources.Marker.class);
    ContentFactory factory = new ContentFactory(finder);
    
    contentPane.removeAll();
    
    scoreStage = new Stage(50);
    
    stars = factory.createContent("stars.png");
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    scoreStage.add(stars);
    
    asteroidContent = factory.createContent("Asteroid.png", 4);
    asteroid = new Asteroid(asteroidContent, width, height);
    asteroid.setScale(0.08, 0.08);
    scoreStage.add(asteroid);
    scoreStage.start();
    
    scoreLogo = factory.createContent("Highscores.png", 4);
    scoreLogo.setLocation((width / 2) - 255, 20);
    scoreLogo.setScale(0.85,  0.85);
    scoreStage.add(scoreLogo);
    
    first = factory.createContent("one.png", 4);
    first.setLocation((width / 4), 150);
    first.setScale(0.75, 0.75);
    scoreStage.add(first);
    
    second = factory.createContent("two.png", 4);
    second.setLocation((width / 4), 250);
    second.setScale(0.75, 0.75);
    scoreStage.add(second);
    
    third = factory.createContent("three.png", 4);
    third.setLocation((width / 4), 350);
    third.setScale(0.75, 0.75);
    scoreStage.add(third);
    
    scoreView = scoreStage.getView();
    scoreView.setBounds(0, 0, width, height);
    contentPane.add(scoreView);
    
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
      System.out.println("Pew");
    }
  }

  @Override
  public void keyReleased(KeyEvent arg0)
  {
    
  }

  @Override
  public void keyTyped(KeyEvent arg0)
  {
    
  }

  @Override
  public void actionPerformed(ActionEvent button)
  {
    System.out.println(button.getActionCommand());
    if (button.getActionCommand().equals("START"))
    {
      setupGame();
    }
    
    if (button.getActionCommand().equals("CREDITS & ACKNOWLEDGMENTS"))
    {
      setupCredits();
    }
    
    if (button.getActionCommand().equals("HIGHSCORES"))
    {
      setupHighscores();
    }
  }
  
  @Override
  public void stop()
  {
    menuMusic.stop();
    menuPlaying = false;
  }
  
  @Override
  public void start()
  {
    if (!menuPlaying) {
      menuMusic.start();
      menuPlaying = true;
    }
  }
}
