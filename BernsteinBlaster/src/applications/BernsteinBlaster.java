package applications;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import app.JApplication;
import event.MetronomeListener;
import io.ResourceFinder;
import sprites.Asteroid;
import sprites.Bullet;
import sprites.CreditSprite;
import sprites.Enemy;
import sprites.HealthBar;
import sprites.Jaw;
import sprites.Ship;
import visual.VisualizationView;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;
import visual.statik.sampled.ContentFactory;

/**
 * Bernstein Blaster. A game where you as the player must fend off waves of
 * F-Ships sent by Lord Bernstein.
 * 
 * @author Bryce Morris <morrisbc@dukes.jmu.edu>, Dylan Parsons <parsondm@dukes.jmu.edu>
 * @version V1 12/3/18
 */
public class BernsteinBlaster extends JApplication implements KeyListener, ActionListener, 
                                                              MetronomeListener
{ 
  private static final String           FONT_FAMILY = "Arial Black";
  private static final String           CONTROLS = "CONTROLS";
  private static final String           START = "START";
  private static final String           HIGHSCORES = "HIGHSCORES";
  private static final String           CREDITS = "CREDITS";
  private static final String           MENU = "MENU";
  private static final String           HIGHSCORES_FILE = "Highscores.txt";
  private static final String           PAUSE = "||";
  private static final String           ASTEROID_FILE = "Asteroid.png";
  private static final String           BERNSTEIN_FILE = "Bernstein.png";
  private static final String           MENU_STATE = "menu";
  private static final String           GAME_STATE = "game";
  private static final String           CREDIT_STATE = "credits";
  private static final String           HIGHSCORES_STATE = "highscores";
  private static final String           CONTROLS_STATE = "controls";
  private static final String           SCORE_FORMAT = "%08d";
  private static final Color            BORDER_COLOR = new Color(254, 45, 194);
  private static final MatteBorder      BORDER = new MatteBorder(4, 4, 4, 4, BORDER_COLOR);
  private static final Font             BUTTON_FONT = new Font(FONT_FAMILY, Font.BOLD, 16);
  private static final Font             SCORE_FONT  = new Font(FONT_FAMILY, Font.BOLD, 24);
  private static final Font             HIGHSCORE_FONT = new Font(FONT_FAMILY, Font.BOLD, 36);
  private static final ResourceFinder   FINDER = 
                                          ResourceFinder.createInstance(resources.Marker.class);
  private static final ContentFactory   FACTORY = new ContentFactory(FINDER);
  private static final String           BACKGROUND = "Space.png";
  private static final String[]         MESSAGES = {"Your pilot skills are almost\nas rough as "
                                                     + "my book!", 
                                                    "I've fought sheep scarier than\nyou!",
                                                    "I've got tenure so I've got\nall day to "
                                                    + "watch you lose!",
                                                    "Your mother was a hamster\nand your father "
                                                    + "smelt of\nelderberries!",
                                                    "Your momma's so fat if she\nwas a recursive "
                                                    + "function\nshe would cause a stack"
                                                    + "\noverflow!",
                                                    "I've got a Capri Sun with\nyour name on it!",
                                                    "You're about one bit short\nof a byte!",
                                                    "Don't you need a license to\nbe that ugly?",
                                                    "Nice shot, but I think you're\nsupposed to "
                                                    + "actually\nHIT the ships!",
                                                    "You got lucky you made it this\nfar!"};
  private static final Random           RNG = new Random(System.currentTimeMillis());
  
  private JPanel                       contentPane;
  private Stage                        menuStage, gameStage, creditStage, bernStage, scoreStage,
                                       controlsStage;
  private Clip                         menuMusic, laser, gameMusic, defeat, shipDamage, enemyDamage;
  private boolean                      menuPlaying, creditsPaused;
  private Ship                         ship;
  //Used to return to the main menu after the credits are over;
  private int                          score, messageTimer, creditTimer;
  private JLabel                       scoreLabel;
  private JTextArea                    textArea;
  private ArrayList<Enemy>             enemies;
  //Keeps state information to avoid NullPointerExceptions in listeners
  private String                       state;
  private ArrayList<String>            highscores;
  private HealthBar                    healthBar;

  /**
   * Explicit value constructor.
   * 
   * @param args Command line arguments
   * @param width Window width
   * @param height Window height
   */
  public BernsteinBlaster(String[] args, int width, int height)
  {
    super(args, width, height);
  }
  
  /**
   * Explicit value constructor.
   * 
   * @param width Window width
   * @param height Window height
   */
  public BernsteinBlaster(int width, int height)
  {
    super(width, height);
  }

  /**
   * Initialization before the application becomes visible.
   */
  @Override
  public void init()
  { 
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(null);
    contentPane.setBackground(Color.BLACK);
    
    highscores = new ArrayList<String>();
    // Read in the highscores from the text file into the highscores array
    readHighscores();
    // Setup the main menu
    setupMenu();
  }
  
  /**
   * Main entry point of the application.
   * 
   * @param args Command line arguments
   * @throws InvocationTargetException Thrown exception
   * @throws InterruptedException Thrown exception
   */
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
    JButton                 start, highScores, credits, controls;
    VisualizationView       view;
    TransformableContent    logo, stars, asteroidContent;
    Asteroid                asteroid;
    
    state = MENU_STATE;
    
    // Clear the content pane. This isn't necessary on startup but will be after the 
    // implementation of an in game quit button
    contentPane.removeAll();
    
    // Setup and add the controls button to the content pane
    controls = new JButton(CONTROLS);
    controls.setBounds(145, 550, 150, 50);
    controls.setContentAreaFilled(false);
    controls.setOpaque(true);
    controls.setForeground(Color.WHITE);
    controls.setBackground(Color.BLACK);
    controls.setFont(BUTTON_FONT);
    controls.setBorder(BORDER);
    contentPane.add(controls);
    
    // Setup the start button and add it to the panel
    start = new JButton(START);
    start.setBounds(425, 550, 150, 50);
    start.setContentAreaFilled(false);
    start.setOpaque(true);
    start.setForeground(Color.WHITE);
    start.setBackground(Color.BLACK);
    start.setFont(BUTTON_FONT);
    start.setBorder(BORDER);
    contentPane.add(start);
    
    // Setup the highscores button and add it to the panel
    highScores = new JButton(HIGHSCORES);
    highScores.setBounds(705, 550, 150, 50);
    highScores.setContentAreaFilled(false);
    highScores.setOpaque(true);
    highScores.setForeground(Color.WHITE);
    highScores.setBackground(Color.BLACK);
    highScores.setFont(BUTTON_FONT);
    highScores.setBorder(BORDER);
    contentPane.add(highScores);
    
    // Setup the credits and acknowledgments button
    credits = new JButton(CREDITS);
    credits.setBounds(985, 550, 150, 50);
    credits.setContentAreaFilled(false);
    credits.setOpaque(true);
    credits.setForeground(Color.WHITE);
    credits.setBackground(Color.BLACK);
    credits.setFont(BUTTON_FONT);
    credits.setBorder(BORDER);
    contentPane.add(credits);
    
    // Stage for the main menu containing the stars background as well
    // as the game logo
    menuStage = new Stage(50);
    
    // Create the background content and add it to the Visualization
    stars = FACTORY.createContent(BACKGROUND);
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    menuStage.add(stars);
    
    // Create and add the asteroid to the stage
    asteroidContent = FACTORY.createContent(ASTEROID_FILE, 4);
    asteroid = new Asteroid(asteroidContent, width, height);
    asteroid.setScale(0.08, 0.08);
    menuStage.add(asteroid);
    menuStage.start();
    
    // Create the logo content and add it to the Visualization
    logo = FACTORY.createContent(BERNSTEIN_FILE, 4);
    logo.setLocation((width/2) - 239, (height/4));
    menuStage.add(logo);
    
    // Create the VisualizationView and set the bounds to match the window
    view = menuStage.getView();
    view.setBounds(0, 0, width, height);
    view.setBackground(Color.BLACK);
    contentPane.add(view);
    
    // Update the content pane for the changes to be visible
    contentPane.repaint();
    
    // Add the application as a listener of the main menu buttons as well
    // as the menuStage to allow key presses to work
    start.addActionListener(this);
    highScores.addActionListener(this);
    credits.addActionListener(this);
    controls.addActionListener(this);
    menuStage.addKeyListener(this);
    
    // Attempt to start the menu background music
    try
    {
      if (!menuPlaying) 
      {
        InputStream in = FINDER.findInputStream("MenuMusic.wav");
        BufferedInputStream bis = new BufferedInputStream(in);
        menuMusic = AudioSystem.getClip();
        menuMusic.open(AudioSystem.getAudioInputStream(bis));
        menuMusic.start();
        menuPlaying = true;
      }
    }
    catch (LineUnavailableException | IOException | UnsupportedAudioFileException e)
    {
      System.out.println("Exception");
      System.out.println(e.toString());
    }
    
    creditTimer = 0;
  }
  
  /**
   * Helper function that updates the content pane with the appropriate components for the main
   * section of the game.
   */
  private void setupGame()
  {
    VisualizationView           gameView, bernView;
    JButton                     back;
    TransformableContent        stars, bernNPC, blur, jawContent, shipContent, oneHeart,
                                twoHearts, threeHearts;
    Jaw                         jaw;
    TransformableContent[]      hearts;
    
    state = GAME_STATE;
    
    // Stop the music from the main menu
    menuMusic.stop();
    menuPlaying = false;
    
    // Attempt to open all the necessary audio clips used in the main game
    try
    {
      InputStream in = FINDER.findInputStream("This_is_happening.wav");
      BufferedInputStream bis = new BufferedInputStream(in);
      gameMusic = AudioSystem.getClip();
      gameMusic.open(AudioSystem.getAudioInputStream(bis));
      gameMusic.start();
      gameMusic.loop(Clip.LOOP_CONTINUOUSLY);
      
      in = FINDER.findInputStream("sfx_wpn_laser7.wav");
      bis = new BufferedInputStream(in);
      laser = AudioSystem.getClip();
      laser.open(AudioSystem.getAudioInputStream(bis));
      
      in = FINDER.findInputStream("sfx_exp_short_hard3.wav");
      bis = new BufferedInputStream(in);
      shipDamage = AudioSystem.getClip();
      shipDamage.open(AudioSystem.getAudioInputStream(bis));
      
      in = FINDER.findInputStream("sfx_exp_shortest_hard5.wav");
      bis = new BufferedInputStream(in);
      enemyDamage = AudioSystem.getClip();
      enemyDamage.open(AudioSystem.getAudioInputStream(bis));
      
      in = FINDER.findInputStream("Defeat.wav");
      bis = new BufferedInputStream(in);
      defeat = AudioSystem.getClip();
      defeat.open(AudioSystem.getAudioInputStream(bis));
    }
    catch (IOException | UnsupportedAudioFileException | LineUnavailableException e)
    {
      System.out.println(e.getMessage());
    }
    
    // Clear the components from the main menu
    contentPane.removeAll();
    
    // Setup the return to menu button
    back = new JButton(MENU);
    back.setBounds(0, height - 54, 320, 54);
    back.setContentAreaFilled(false);
    back.setOpaque(true);
    back.setForeground(Color.WHITE);
    back.setBorder(BORDER);
    back.setFont(BUTTON_FONT);
    back.setBackground(Color.BLACK);
    contentPane.add(back);
    back.addActionListener(this);
    
    // Setup the text area where NPC communications will appear
    textArea = new JTextArea();
    textArea.setBounds(0, 320, 320, 350);
    textArea.setOpaque(true);
    textArea.setBackground(Color.BLACK);
    textArea.setBorder(BORDER);
    textArea.setForeground(Color.WHITE);
    textArea.setFont(BUTTON_FONT);
    contentPane.add(textArea);
    
    textArea.setText(MESSAGES[RNG.nextInt(MESSAGES.length)]);
    
    // Initialize the score and add the score label to the panel
    score = 0;
    scoreLabel = new JLabel(String.format(SCORE_FORMAT, score));
    scoreLabel.setFont(SCORE_FONT);
    scoreLabel.setBounds(1135, 5, 150, 30);
    scoreLabel.setForeground(Color.WHITE);
    contentPane.add(scoreLabel);
    
    // Create the stage for the main portion of the game containing the ship
    // sprites and the player model
    gameStage = new Stage(50);
    
    // Construct and add the star background
    stars = FACTORY.createContent(BACKGROUND, 4);
    stars.setScale(0.8, 0.8);
    stars.setLocation(0, 0);
    gameStage.add(stars);
    
    // Construct and add the ship player model
    shipContent = FACTORY.createContent("Spaceship.png", 4);
    ship = new Ship(shipContent, 0, 900, shipDamage);
    ship.setScale(0.13, 0.13);
    
    gameStage.add(ship);
    gameStage.addKeyListener(ship);
    
    // Spawn enemies and add them to the list of enemies
    enemies = new ArrayList<Enemy>();
    for (int i = 0; i < 10; i++) 
    {
      spawnEnemy();
    }
    
    // Create and setup the health pool portion of the HUD
    hearts = new TransformableContent[3];
    oneHeart = FACTORY.createContent("one_heart.png", 4);    
    twoHearts = FACTORY.createContent("two_hearts.png", 4);        
    threeHearts = FACTORY.createContent("full_hearts.png", 4);
    hearts[0] = oneHeart;
    hearts[1] = twoHearts;
    hearts[2] = threeHearts;
    healthBar = new HealthBar(0, -25, hearts, ship);
    healthBar.setScale(0.07, 0.07);
    gameStage.add(healthBar);
    gameStage.toBack(healthBar);
    
    // Start the stage and add the application as both a key and metronome listener
    gameStage.start();
    gameStage.addKeyListener(this);
    gameStage.getMetronome().addListener(this);
    
    // Add the VisualizationView of the main game section to the content pane
    gameView = gameStage.getView();
    gameView.setBounds(320, 0, width - 300, height);
    gameView.setBackground(Color.WHITE);
    gameView.setBorder(BORDER);
    contentPane.add(gameView);
    
    // Create the Stage for the NPC
    bernStage = new Stage(200);
    
    // Create and add the background of the NPC
    blur = FACTORY.createContent("blur.png", 4);
    blur.setLocation(0, 0);
    blur.setScale(0.5, 0.5);
    bernStage.add(blur);
    
    // Create and add the NPC without the jaw 
    bernNPC = FACTORY.createContent("no_jaw.png", 4);
    bernNPC.setLocation(0, 45);
    bernNPC.setScale(0.9, 0.9);
    bernStage.add(bernNPC);
    
    // Create and add the NPC's jaw
    jawContent = FACTORY.createContent("jaw.png", 4);
    jaw = new Jaw(jawContent, 157, 190);
    jaw.setScale(0.92, 0.92);
    bernStage.add(jaw);
    bernStage.start();
    
    // Create and setup the VisualizationView of the NPC portion of the panel
    bernView = bernStage.getView();
    bernView.setBounds(0, 0, 320, 320);
    bernView.setBackground(Color.BLACK);
    bernView.setBorder(BORDER);
    contentPane.add(bernView);
    
    messageTimer = 0;
    
    // Refresh the content pane for the changes to be visible
    contentPane.repaint();
  }
  
  /**
   * Helper function that updates the content pane with the appropriate components for the for
   * the game credits.
   */
  private void setupCredits()
  {
    JButton                   pause, back;
    VisualizationView         creditView;
    TransformableContent      content;
    Asteroid                  asteroid;
    CreditSprite              sprite;
    
    state = CREDIT_STATE;
    
    // Refresh the content pane by clearing elements from the previous menu
    contentPane.removeAll();
    
    // Add the pause button
    pause = new JButton(PAUSE);
    pause.setBounds(width - 60, height - 60, 50, 50);
    pause.setContentAreaFilled(false);
    pause.setOpaque(true);
    pause.setForeground(Color.WHITE);
    pause.setBackground(Color.BLACK);
    pause.setFont(BUTTON_FONT);
    pause.setBorder(BORDER);
    contentPane.add(pause);
    pause.addActionListener(this);
    
    // Add the menu button
    back = new JButton(MENU);
    back.setBounds(width - 220, height - 60, 150, 50);
    back.setContentAreaFilled(false);
    back.setOpaque(true);
    back.setForeground(Color.WHITE);
    back.setBorder(BORDER);
    back.setFont(BUTTON_FONT);
    back.setBackground(Color.BLACK);
    contentPane.add(back);
    back.addActionListener(this);
    
    creditStage = new Stage(50);
    
    // Add the background
    content = FACTORY.createContent(BACKGROUND, 4);
    content.setScale(1.1, 1);
    content.setLocation(0, 0);
    creditStage.add(content);
    
    // Add the asteroid
    content = FACTORY.createContent(ASTEROID_FILE, 4);
    asteroid = new Asteroid(content, width, height);
    asteroid.setScale(0.08, 0.08);
    creditStage.add(asteroid);
    
    // Add the other various pieces of content related to the credit sequence
    content = FACTORY.createContent(BERNSTEIN_FILE, 4);
    sprite = new CreditSprite(content, (width/2) - 239, (height/4));
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Developers.png", 4);
    sprite = new CreditSprite(content, (width/2) - 243.5, (height/4) + 550);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Names.png", 4);
    sprite = new CreditSprite(content, (width/2) - 141, (height/4) + 650);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Characters.png", 4);
    sprite = new CreditSprite(content, (width/2) - 245.5, (height/4) + 800);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Lord-Bernstein.png", 4);
    sprite = new CreditSprite(content, (width/2) - 330.6, (height/4) + 900);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Resources.png", 4);
    sprite = new CreditSprite(content, (width/2) - 231, (height/4) + 1000);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Visuals.png", 4);
    sprite = new CreditSprite(content, (width/2) - 60.6, (height/4) + 1100);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Asteroid-Word.png", 4);
    sprite = new CreditSprite(content, (width/2) - 63.4, (height/4) + 1150);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Asteroid-Link.png", 4);
    sprite = new CreditSprite(content, (width/2) - 515, (height/4) + 1200);
    sprite.setScale(0.2, 0.2);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Courtesy.png", 4);
    sprite = new CreditSprite(content, (width/2) - 236.4, (height/4) + 1300);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("textcraft.png", 4);
    sprite = new CreditSprite(content, (width/2) - 124.5, (height/4) + 1350);
    sprite.setScale(0.6, 0.6);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Audio.png", 4);
    sprite = new CreditSprite(content, (width/2) - 46.8, (height/4) + 1450);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Defeat-Words.png", 4);
    sprite = new CreditSprite(content, (width/2) - 180.4, (height/4) + 1500);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("La-Calahorra-Word.png", 4);
    sprite = new CreditSprite(content, (width/2) - 223.2, (height/4) + 1550);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Happening-Words.png", 4);
    sprite = new CreditSprite(content, (width/2) - 238.2, (height/4) + 1600);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Courtesy-Audio.png", 4);
    sprite = new CreditSprite(content, (width/2) - 356.6, (height/4) + 1700);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("FMA-Logo.png", 4);
    sprite = new CreditSprite(content, (width/2) - 109, (height/4) + 1750);
    creditStage.add(sprite);
    
    creditView = creditStage.getView();
    creditView.setBounds(0, 0, width, height);
    contentPane.add(creditView);
    
    content = FACTORY.createContent("Courtesy-FX.png", 4);
    sprite = new CreditSprite(content, (width/2) - 410, (height/4) + 1900);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("OGA-Logo.png", 4);
    sprite = new CreditSprite(content, (width/2) - 40, (height/4) + 1950);
    sprite.setScale(2, 2);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("512-FX.png", 4);
    sprite = new CreditSprite(content, (width/2) - 243.6, (height/4) + 2100);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("512-Link.png", 4);
    sprite = new CreditSprite(content, (width/2) - 330, (height/4) + 2150);
    sprite.setScale(0.2, 0.2);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Acknowledgements.png", 4);
    sprite = new CreditSprite(content, (width/2) - 396.5, (height/4) + 2250);
    creditStage.add(sprite);
    
    content = FACTORY.createContent("Bernstein-Thanks.png", 4);
    sprite = new CreditSprite(content, (width/2) - 402.2, (height/4) + 2350);
    sprite.setScale(0.4, 0.4);
    creditStage.add(sprite);
    
    // Initialize the credit timer so we know when to return to the main menu
    creditTimer = 0;
    creditStage.start();
    creditsPaused = false;
    creditStage.addKeyListener(this);
    creditStage.getMetronome().addListener(this);
    
    contentPane.repaint();
  }

  /**
   * Helper function that updates the content pane with the appropriate components for the
   * highscores section of the game.
   */
  private void setupHighscores()
  {
    JButton                     back;
    TransformableContent        scoreLogo, stars, asteroidContent, first, second, third, fourth,
                                fifth;
    VisualizationView           scoreView;
    Asteroid                    asteroid;
    JLabel                      entry;
    // Initial location of the highscore JLabels. Incremented
    // appropriately in the for loop that adds the labels
    int                         labelLocation;
    
    state = HIGHSCORES_STATE;
    
    contentPane.removeAll();
    
    scoreStage = new Stage(50);
    
    // Add the menu button
    back = new JButton(MENU);
    back.setBounds((width / 2) - 75, height - 70, 150, 50);
    back.setContentAreaFilled(false);
    back.setOpaque(true);
    back.setForeground(Color.WHITE);
    back.setBorder(BORDER);
    back.setFont(BUTTON_FONT);
    back.setBackground(Color.BLACK);
    contentPane.add(back);
    back.addActionListener(this);
    
    // Add the background
    stars = FACTORY.createContent(BACKGROUND);
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    scoreStage.add(stars);
    
    asteroidContent = FACTORY.createContent(ASTEROID_FILE, 4);
    asteroid = new Asteroid(asteroidContent, width, height);
    asteroid.setScale(0.08, 0.08);
    scoreStage.add(asteroid);
    scoreStage.start();
    
    // Add the appropriate content to the panel
    scoreLogo = FACTORY.createContent("Highscores.png", 4);
    scoreLogo.setLocation((width / 2) - 255, 20);
    scoreLogo.setScale(0.85,  0.85);
    scoreStage.add(scoreLogo);
    
    first = FACTORY.createContent("one.png", 4);
    first.setLocation((width / 4), 150);
    first.setScale(0.75, 0.75);
    scoreStage.add(first);
    
    second = FACTORY.createContent("two.png", 4);
    second.setLocation((width / 4), 250);
    second.setScale(0.75, 0.75);
    scoreStage.add(second);
    
    third = FACTORY.createContent("three.png", 4);
    third.setLocation((width / 4), 350);
    third.setScale(0.75, 0.75);
    scoreStage.add(third);
    
    fourth = FACTORY.createContent("four.png", 4);
    fourth.setLocation((width / 4), 450);
    fourth.setScale(0.75, 0.75);
    scoreStage.add(fourth);
    
    fifth = FACTORY.createContent("five.png", 4);
    fifth.setLocation((width / 4), 550);
    fifth.setScale(0.75, 0.75);
    scoreStage.add(fifth);
    
    // Setup and populate the JLabels used to display the highscores
    labelLocation = 55;
    for (String highscore : highscores)
    {
      entry = new JLabel(highscore, SwingConstants.CENTER);
      entry.setBounds((width / 2) - 250, 150, 500, labelLocation);
      entry.setFont(HIGHSCORE_FONT);
      entry.setForeground(Color.WHITE);
      contentPane.add(entry);
      labelLocation += 200;
    }
    
    scoreView = scoreStage.getView();
    scoreView.setBounds(0, 0, width, height);
    contentPane.add(scoreView);
    
    back.addActionListener(this);
    scoreStage.addKeyListener(this);
    
    contentPane.repaint();
  }
  
  /**
   * Helper function that updates the content pane with the appropriate components for the controls
   * section of the game.
   */
  private void setupControls()
  {
    JButton back;
    VisualizationView controlsView;
    TransformableContent stars, asteroidContent, controls, aDirection, dDirection, spaceDirection, 
                         escDirection;
    Asteroid asteroid;
    
    state = CONTROLS_STATE;
    
    contentPane.removeAll();
    
 // Add the menu button
    back = new JButton(MENU);
    back.setBounds((width / 2) - 75, height - 70, 150, 50);
    back.setContentAreaFilled(false);
    back.setOpaque(true);
    back.setForeground(Color.WHITE);
    back.setBorder(BORDER);
    back.setFont(BUTTON_FONT);
    back.setBackground(Color.BLACK);
    contentPane.add(back);
    back.addActionListener(this);
    
    controlsStage = new Stage(50);
    
    // Create the background content and add it to the Visualization
    stars = FACTORY.createContent(BACKGROUND);
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    controlsStage.add(stars);
    
    asteroidContent = FACTORY.createContent(ASTEROID_FILE, 4);
    asteroid = new Asteroid(asteroidContent, width, height);
    asteroid.setScale(0.08, 0.08);
    controlsStage.add(asteroid);
    controlsStage.start();
    
    controls = FACTORY.createContent("Controls.png", 4);
    controls.setScale(0.85, 0.85);
    controls.setLocation((width / 2) - 200, 20);
    controlsStage.add(controls);
    
    aDirection = FACTORY.createContent("ADirect.png", 4);
    aDirection.setScale(0.9, 0.9);
    aDirection.setLocation((width / 2) - 343.35, 150);
    controlsStage.add(aDirection);
    
    dDirection = FACTORY.createContent("DDirect.png", 4);
    dDirection.setScale(0.9, 0.9);
    dDirection.setLocation((width / 2) - 359.1, 250);
    controlsStage.add(dDirection);
    
    spaceDirection = FACTORY.createContent("SpaceDirect.png", 4);
    spaceDirection.setScale(0.9, 0.9);
    spaceDirection.setLocation((width / 2) - 461.7, 350);
    controlsStage.add(spaceDirection);
    
    escDirection = FACTORY.createContent("ESCDirect.png", 4);
    escDirection.setScale(0.9, 0.9);
    escDirection.setLocation((width / 2) - 446.4, 450);
    controlsStage.add(escDirection);
    
    controlsView = controlsStage.getView();
    controlsView.setBounds(0, 0, width, height);
    controlsView.setBackground(Color.BLACK);
    contentPane.add(controlsView);
    
    controlsStage.addKeyListener(this);
    
    contentPane.repaint();
  }
  
  /**
   * Function that displays a game over message and ends the game.
   */
  private void gameOver()
  {
    TransformableContent gameOver;
    
    // Display the game over message and play the appropriate audio
    ship.setVisible(false);
    gameOver = FACTORY.createContent("Game-Over.png", 4);
    gameOver.setLocation(258, height * 0.4);
    gameStage.add(gameOver);
    gameStage.stop();
    bernStage.stop();
    gameMusic.stop();
    defeat.start();
    
    // Update the highscores ArrayList and write it to Highscores.txt
    for (int i = 0; i < highscores.size(); i++)
    {
      if (score > Integer.parseInt(highscores.get(i)))
      {
        highscores.add(i, score + "");
        highscores.remove(highscores.size() - 1);
        writeHighscores();
        break;
      }
    }
  }
  
  /**
   * Spawns an enemy into the game by adding it to the gameStage.
   */
  private void spawnEnemy()
  {
    TransformableContent[] contents;
    Enemy enemy;
    
    contents = new TransformableContent[3];
    contents[2] = FACTORY.createContent("F-Ship.png", 4);
    contents[1] = FACTORY.createContent("F-Ship-Two.png", 4);
    contents[0] = FACTORY.createContent("F-Ship-Three.png", 4);
    
    enemy = new Enemy(contents, width - 300, height, ship, enemyDamage);
    enemy.setScale(0.085, 0.085);
    enemies.add(enemy);
    gameStage.add(enemy);
    gameStage.toBack(healthBar);
  }
  
  /**
   * Reads the highscores from Highscores.txt and populates the highscores ArrayList with the
   * information read from the file.
   */
  private void readHighscores()
  {
    File highscoreFile;
    BufferedReader reader;
    String line;
    
    try
    {
      highscoreFile = new File(HIGHSCORES_FILE);
      reader = new BufferedReader(new FileReader(highscoreFile));
      
      while ((line = reader.readLine()) != null)
      {
        highscores.add(line);
      }
      
      reader.close();
    }
    catch (IOException ioe)
    {
      System.out.println("Error reading highscores.");
    }
  }
  
  /**
   * Writes the highscores contained within the highscores ArrayList to Highscores.txt.
   */
  private void writeHighscores()
  {
    File highscoreFile;
    BufferedWriter writer;
    
    try
    {
      highscoreFile = new File(HIGHSCORES_FILE);
      writer = new BufferedWriter(new FileWriter(highscoreFile));
      
      for (String highscore : highscores)
      {
        writer.write(highscore);
        writer.newLine();
      }
      writer.close();
    }
    catch (IOException ioe)
    {
      System.out.println("Error writing highscores.");
    }
  }
  
  /**
   * Listener for KeyEvents. Used to return to the main menu using the escape key.
   */
  @Override
  public void keyPressed(KeyEvent stroke)
  {
    char key;
    
    key = stroke.getKeyChar();
    
    if (key == KeyEvent.VK_ESCAPE)
    {
      if (state.equals(GAME_STATE))
      {
        gameMusic.stop();
        gameStage.stop();
      }
      
      if (state.equals(CREDIT_STATE))
      {
        creditTimer = 0;
        creditStage.stop();
      }
      
      setupMenu();
    }
  }

  /**
   * Listener for KeyEvents. Used for bullet production on screen.
   */
  @Override
  public void keyReleased(KeyEvent stroke)
  {
    char key;
    
    key = stroke.getKeyChar();
    
    if (key == ' ' && state.equals(GAME_STATE))
    {
      TransformableContent bulletContent = FACTORY.createContent("bullet.png", 4);
      Bullet bullet = new Bullet(bulletContent, ship.getX() + 21, ship.getY() - 15, 
          gameStage, enemies);
      bullet.setScale(0.1, 0.1);
      gameStage.add(bullet);
      laser.setMicrosecondPosition(0);
      laser.start();
    }
  }

  /**
   * Listener for KeyEvents. Currently unused.
   */
  @Override
  public void keyTyped(KeyEvent stroke)
  {
    // UNUSED
  }

  /**
   * Listener for ActionEvents. Used to navigate through the application using the various buttons
   * throughout the different menus.
   */
  @Override
  public void actionPerformed(ActionEvent button)
  {
    System.out.println(button.getActionCommand());
    if (button.getActionCommand().equals(START))
      setupGame();
    
    if (button.getActionCommand().equals(CREDITS))
      setupCredits();
    
    if (button.getActionCommand().equals(HIGHSCORES))
      setupHighscores();
    
    if (button.getActionCommand().equals(MENU))
    {
      if (state.equals(GAME_STATE))
      {
        gameStage.stop();
        bernStage.stop();
        gameMusic.stop();
      }
      
      if (state.equals(CREDIT_STATE))
      {
        creditTimer = 0;
        creditStage.stop();
      }
      
      setupMenu();
    }
    
    if (button.getActionCommand().equals(CONTROLS))
      setupControls();
    
    if (button.getActionCommand().equals(PAUSE))
    {
      if (creditsPaused)
      {
        creditStage.start();
        creditsPaused = false;
      }
      else
      {
        creditStage.stop();
        creditsPaused = true;
      }
    }
      
  }
  
  /**
   * Called when the windows is minimized.
   */
  @Override
  public void stop()
  {
    switch (state)
    {
      case MENU_STATE:
        menuMusic.stop();
        menuStage.stop();
        break;
      case GAME_STATE:
        gameStage.stop();
        bernStage.stop();
        gameMusic.stop();
        break;
      case CREDIT_STATE:
        if (!creditsPaused)
        {
          creditStage.stop();
          creditsPaused = false;
        }
        menuMusic.stop();
        break;
      case HIGHSCORES_STATE:
        scoreStage.stop();
        menuMusic.stop();
        break;
      case CONTROLS_STATE:
        controlsStage.stop();
        menuMusic.stop();
        break;
      default:
        break;
    }
  }
  
  /**
   * Called when the window is maximized.
   */
  @Override
  public void start()
  {
    switch (state)
    {
      case MENU_STATE:
        menuMusic.start();
        menuStage.start();
        break;
      case GAME_STATE:
        gameStage.start();
        bernStage.start();
        gameMusic.start();
        break;
      case CREDIT_STATE:
        if (!creditsPaused)
        {
          creditStage.start();
          creditsPaused = false;
        }
        menuMusic.start();
        break;
      case HIGHSCORES_STATE:
        scoreStage.start();
        menuMusic.start();
        break;
      case CONTROLS_STATE:
        controlsStage.start();
        menuMusic.start();
        break;
      default:
        break;
    }
  }

  /**
   * Listener for Metronome ticks. Used to update the score, spawn more enemies, end the game, 
   * and return to the main menu after the credit sequence is over. 
   */
  @Override
  public void handleTick(int tick)
  {
    if (state.equals(GAME_STATE))
    {
      score++;
      scoreLabel.setText(String.format(SCORE_FORMAT, score));
      
      messageTimer++;
      if (messageTimer % 175 == 0)
      {
        textArea.setText(MESSAGES[RNG.nextInt(MESSAGES.length)]);
      }
      
      if (enemies.size() < 10)
      {
        score += (10 - enemies.size()) * 100;
        scoreLabel.setText(String.format(SCORE_FORMAT, score));
        spawnEnemy();
      }
      
      if (ship.getHealth() <= 0)
      {
        gameOver();
      }
    }
    
    if (state.equals(CREDIT_STATE) && !creditsPaused)
    {
      System.out.println(creditTimer);
      creditTimer++;
      if (creditTimer > 900)
      {
        setupMenu();
      }
    }
  }
}
