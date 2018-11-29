package applications;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import app.JApplication;
import event.MetronomeListener;
import io.ResourceFinder;
import sprites.Asteroid;
import sprites.Bullet;
import sprites.CreditSprite;
import sprites.Enemy;
import sprites.Jaw;
import sprites.Ship;
import visual.VisualizationView;
import visual.dynamic.described.Stage;
import visual.statik.TransformableContent;
import visual.statik.sampled.ContentFactory;


public class BernsteinBlaster extends JApplication implements KeyListener, ActionListener, MetronomeListener
{ 
  private static final Color BORDER_COLOR = new Color(254, 45, 194);
  private static final MatteBorder BORDER = new MatteBorder(4, 4, 4, 4, BORDER_COLOR);
  private static final Font BUTTON_FONT = new Font("Arial Black", Font.BOLD, 16);
  private static final Font SCORE_FONT  = new Font("Arial Black", Font.BOLD, 24);
  private static final ResourceFinder FINDER = ResourceFinder.createInstance(resources.Marker.class);
  private static final ContentFactory FACTORY = new ContentFactory(FINDER);
  
  private JPanel contentPane;
  private Stage menuStage, gameStage;
  private Clip menuMusic;
  private boolean menuPlaying, muted;
  private String state;
  private Ship ship;
  private int score;
  private JLabel scoreLabel;
  TransformableContent threeHearts, twoHearts, oneHeart;

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
    
    muted = false;
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
    JButton start, highScores, credits, controls;
    VisualizationView view;
    TransformableContent logo, stars, asteroidContent;
    Asteroid asteroid;
    
    // Clear the content pane. This isn't necessary on startup but will be after the 
    // implementation of an in game quit button
    contentPane.removeAll();
    
    state = "MENU";
    
    // Setup the start button and add it to the panel
    start = new JButton("START");
    start.setBounds(400, 550, 150, 50);
    start.setContentAreaFilled(false);
    start.setOpaque(true);
    start.setForeground(Color.WHITE);
    start.setBackground(Color.BLACK);
    start.setFont(BUTTON_FONT);
    start.setBorder(BORDER);
    contentPane.add(start);
    
    // Setup the highscores button and add it to the panel
    highScores = new JButton("HIGHSCORES");
    highScores.setBounds(730, 550, 150, 50);
    highScores.setContentAreaFilled(false);
    highScores.setOpaque(true);
    highScores.setForeground(Color.WHITE);
    highScores.setBackground(Color.BLACK);
    highScores.setFont(BUTTON_FONT);
    highScores.setBorder(BORDER);
    contentPane.add(highScores);
    
    // Setup the credits and acknowledgments button
    credits = new JButton("CREDITS");
    credits.setBounds(width - 150, height - 50, 150, 50);
    credits.setContentAreaFilled(false);
    credits.setOpaque(true);
    credits.setForeground(Color.WHITE);
    credits.setBackground(Color.BLACK);
    credits.setFont(BUTTON_FONT);
    credits.setBorder(BORDER);
    contentPane.add(credits);
    
    controls = new JButton("CONTROLS");
    controls.setBounds(0, height - 50, 150, 50);
    controls.setContentAreaFilled(false);
    controls.setOpaque(true);
    controls.setForeground(Color.WHITE);
    controls.setBackground(Color.BLACK);
    controls.setFont(BUTTON_FONT);
    controls.setBorder(BORDER);
    contentPane.add(controls);
    
    // Visualization for the main menu containing the stars background as well
    // as the game logo
    menuStage = new Stage(50);
    
    // Create the background content and add it to the Visualization
    stars = FACTORY.createContent("stars.png");
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    menuStage.add(stars);
    
    asteroidContent = FACTORY.createContent("Asteroid.png", 4);
    asteroid = new Asteroid(asteroidContent, width, height);
    asteroid.setScale(0.08, 0.08);
    menuStage.add(asteroid);
    menuStage.start();
    
    // Create the logo content and add it to the Visualization
    logo = FACTORY.createContent("Bernstein.png", 4);
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
    controls.addActionListener(this);
    menuStage.addKeyListener(this);
    
    // Attempt to start the menu background music
    try
    {
      if (!menuPlaying && !muted) {
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
  }
  
  /**
   * Helper function that updates the content pane with the appropriate components for the main
   * section of the game.
   */
  private void setupGame()
  {
    Stage bernStage;
    VisualizationView gameView, bernView;
    JTextField textField;
    JButton back;
    TransformableContent stars, bernNPC, blur, jawContent, enemyContent, shipContent;
    Jaw jaw;
    Enemy enemy;
    
    // Clear the components from the main menu
    contentPane.removeAll();
    
    state = "GAME";
    
    back = new JButton("MENU");
    back.setBounds(0, height - 54, 320, 54);
    back.setContentAreaFilled(false);
    back.setOpaque(true);
    back.setForeground(Color.WHITE);
    back.setBorder(BORDER);
    back.setFont(BUTTON_FONT);
    back.setBackground(Color.BLACK);
    contentPane.add(back);
    back.addActionListener(this);
    
    // Setup the text field where NPC communications will appear
    textField = new JTextField("Put some text here");
    textField.setBounds(0, 320, 320, 350);
    textField.setOpaque(true);
    textField.setBackground(Color.BLACK);
    textField.setBorder(BORDER);
    textField.setForeground(BORDER_COLOR);
    contentPane.add(textField);
    
    score = 0;
    scoreLabel = new JLabel(String.format("%08d", score));
    scoreLabel.setFont(SCORE_FONT);
    scoreLabel.setBounds(1135, 5, 150, 30);
    scoreLabel.setForeground(Color.WHITE);
    contentPane.add(scoreLabel);
    
    // Create the stage for the main portion of the game containing the ship
    // sprites and the player model
    gameStage = new Stage(50);
    
    // Construct and add the star background
    stars = FACTORY.createContent("stars.png", 4);
    stars.setScale(0.8, 0.8);
    stars.setLocation(0, 0);
    gameStage.add(stars);
    
    // Construct and add the ship player model
    shipContent = FACTORY.createContent("spaceship.png", 4);
    ship = new Ship(shipContent, 0, 900);
    ship.setScale(0.1, 0.1);
    
    gameStage.add(ship);
    gameStage.addKeyListener(ship);
    
    for (int i = 0; i < 10; i++) 
    {
      enemyContent = FACTORY.createContent("bear.png", 4);
      enemy = new Enemy(enemyContent, width - 300, height, ship);
      enemy.setScale(0.05, 0.05);
      gameStage.add(enemy);
    }
    
    oneHeart = FACTORY.createContent("one_heart.png", 4);
    oneHeart.setScale(0.07, 0.07);
    oneHeart.setLocation(0, -25);
    gameStage.add(oneHeart);
    
    twoHearts = FACTORY.createContent("two_hearts.png", 4);
    twoHearts.setScale(0.07, 0.07);
    twoHearts.setLocation(0, -25);
    gameStage.add(twoHearts);
    
    threeHearts = FACTORY.createContent("full_hearts.png", 4);
    threeHearts.setScale(0.07, 0.07);
    threeHearts.setLocation(0, -25);
    gameStage.add(threeHearts);
    
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
    
    // Stop the music from the main menu
    menuMusic.stop();
    menuPlaying = false;
    
    // Refresh the content pane for the changes to be visible
    contentPane.repaint();
  }
  
  private void setupCredits()
  {
    Stage creditStage;
    VisualizationView creditView;
    TransformableContent stars, logoContent, asteroidContent;
    Asteroid asteroid;
    CreditSprite logoSprite;
    
    contentPane.removeAll();
    
    state = "CREDITS";
    
    creditStage = new Stage(50);
    
    stars = FACTORY.createContent("stars.png", 4);
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    creditStage.add(stars);
    
    asteroidContent = FACTORY.createContent("Asteroid.png", 4);
    asteroid = new Asteroid(asteroidContent, width, height);
    asteroid.setScale(0.08, 0.08);
    creditStage.add(asteroid);
    
    logoContent = FACTORY.createContent("Bernstein.png", 4);
    logoSprite = new CreditSprite(logoContent, (width/2) - 239, (height/4));
    creditStage.add(logoSprite);
    
    creditView = creditStage.getView();
    creditView.setBounds(0, 0, width, height);
    contentPane.add(creditView);
    
    creditStage.start();
    creditStage.addKeyListener(this);
    
    contentPane.repaint();
  }

  private void setupHighscores()
  {
    JButton back;
    TransformableContent scoreLogo, stars, asteroidContent, first, second, third, fourth, fifth;
    Stage scoreStage;
    VisualizationView scoreView;
    Asteroid asteroid;
    
    contentPane.removeAll();
    
    state = "HIGHSCORES";
    
    scoreStage = new Stage(50);
    
    back = new JButton("MENU");
    back.setBounds((width / 2) - 75, height - 70, 150, 50);
    back.setContentAreaFilled(false);
    back.setOpaque(true);
    back.setForeground(Color.WHITE);
    back.setBorder(BORDER);
    back.setFont(BUTTON_FONT);
    back.setBackground(Color.BLACK);
    contentPane.add(back);
    
    stars = FACTORY.createContent("stars.png");
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    scoreStage.add(stars);
    
    asteroidContent = FACTORY.createContent("Asteroid.png", 4);
    asteroid = new Asteroid(asteroidContent, width, height);
    asteroid.setScale(0.08, 0.08);
    scoreStage.add(asteroid);
    scoreStage.start();
    
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
    
    scoreView = scoreStage.getView();
    scoreView.setBounds(0, 0, width, height);
    contentPane.add(scoreView);
    
    back.addActionListener(this);
    scoreStage.addKeyListener(this);
    
    contentPane.repaint();
  }
  
  private void setupControls()
  {
    Stage controlsStage;
    VisualizationView controlsView;
    TransformableContent stars, asteroidContent, controls;
    Asteroid asteroid;
    
    contentPane.removeAll();
    
    controlsStage = new Stage(50);
    
    // Create the background content and add it to the Visualization
    stars = FACTORY.createContent("stars.png");
    stars.setScale(1.1, 1);
    stars.setLocation(0, 0);
    controlsStage.add(stars);
    
    asteroidContent = FACTORY.createContent("Asteroid.png", 4);
    asteroid = new Asteroid(asteroidContent, width, height);
    asteroid.setScale(0.08, 0.08);
    controlsStage.add(asteroid);
    controlsStage.start();
    
    controls = FACTORY.createContent("Controls.png", 4);
    controls.setScale(0.85, 0.85);
    controls.setLocation((width / 2) - 200, 20);
    controlsStage.add(controls);
    
    controlsView = controlsStage.getView();
    controlsView.setBounds(0, 0, width, height);
    controlsView.setBackground(Color.BLACK);
    contentPane.add(controlsView);
    
    controlsStage.addKeyListener(this);
    
    contentPane.repaint();
  }
  
  private void gameOver()
  {
    TransformableContent gameOver;
    
    ship.setVisible(false);
    gameOver = FACTORY.createContent("Game-Over.png", 4);
    gameOver.setLocation(258, height * 0.4);
    gameStage.add(gameOver);
    gameStage.stop();
  }
  
  @Override
  public void keyPressed(KeyEvent stroke)
  {
    char key;
    
    key = stroke.getKeyChar();
    
    if (key == ' ')
    {
       TransformableContent bulletContent = FACTORY.createContent("full_hearts.png", 4);
       Bullet bullet = new Bullet(bulletContent, ship.getX() + 10, ship.getY() - 15, gameStage);
       bullet.setScale(0.02, 0.02);
       gameStage.add(bullet);
    }
    
    if (key == KeyEvent.VK_ESCAPE)
    {
      setupMenu();
    }
    
    if (key == 'm')
    {
      if (!menuPlaying)
      {
        menuMusic.start();
        menuPlaying = true;
        muted = false;
      }
      else
      {
        menuMusic.stop();
        menuPlaying = false;
        muted = true;
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent stroke)
  {
    
  }

  @Override
  public void keyTyped(KeyEvent stroke)
  {
    
  }

  @Override
  public void actionPerformed(ActionEvent button)
  {
    System.out.println(button.getActionCommand());
    if (button.getActionCommand().equals("START"))
      setupGame();
    
    if (button.getActionCommand().equals("CREDITS"))
      setupCredits();
    
    if (button.getActionCommand().equals("HIGHSCORES"))
      setupHighscores();
    
    if (button.getActionCommand().equals("MENU")) 
      setupMenu();
    
    if (button.getActionCommand().equals("CONTROLS"))
      setupControls();
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
    if (!menuPlaying && !muted) {
      menuMusic.start();
      menuPlaying = true;
    }
  }

  @Override
  public void handleTick(int tick)
  {
    if (ship.getHealth() <= 0)
    {
      gameOver();
    }
    score++;
    scoreLabel.setText(String.format("%08d", score));
  }
}
