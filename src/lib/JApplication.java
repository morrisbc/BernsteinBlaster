package lib;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * A JApplication is a swing application that looks and behaves like a
 * JApplet.
 * 
 * <p>
 * Specializations of this class must implement the init() method.
 * They may also implement the destroy(), start(), and stop() methods.
 * </p>
 * 
 * @author  Prof. David Bernstein, James Madison University
 * @see     "The Design and Implementation of Multimedia Software � 2011"
 * @version 2.0
 */
public abstract class      JApplication 
                implements RootPaneContainer, Runnable, WindowListener
{
  protected final int           height, width;    
  private         JFrame        mainWindow;
  protected       String[]      args;

  /**
   * Explicit Value Constructor.
   *
   * @param args    The command line arguments
   * @param width   The width of the content (in pixels)
   * @param height  The height of the content (in pixels)
   */
  public JApplication(String[] args, int width, int height)
  {
    this.args = args;
    this.width  = width;
    this.height = height;       
  }

  /**
   * Explicit Value Constructor.
   *
   * @param width   The width of the content (in pixels)
   * @param height  The height of the content (in pixels)
   */
  public JApplication(int width, int height)
  {
    this(null, width, height);
  }

  //[constructMainWindow1
  /**
   * Construct the main window.
   */
  private void constructMainWindow()
  {
    try 
    {
      String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
      UIManager.setLookAndFeel(lookAndFeel);
    }
    catch (Exception e)
    {
      // Use the default look and feel
    }

    ImageIcon icon = null;
    icon = new ImageIcon(icondata);
    //]constructMainWindow1

    //[constructMainWindow2
    mainWindow = new JFrame();       
    mainWindow.setTitle("Bernstein Blaster");
    mainWindow.setResizable(false);       

    JPanel contentPane = (JPanel)mainWindow.getContentPane();       
    contentPane.setLayout(null);       
    contentPane.setDoubleBuffered(false);       
    //]constructMainWindow2
    mainWindow.setIconImage(icon.getImage());

    //[constructMainWindow3
    mainWindow.setDefaultCloseOperation(
        JFrame.DO_NOTHING_ON_CLOSE);      
    mainWindow.addWindowListener(this);
    //]constructMainWindow3
    //[constructMainWindow4
  }
  //]constructMainWindow4

  /**
   * This method is called just after the main window is
   * disposed.
   *
   * The default implementation does nothing.
   */
  public void destroy()
  {
  }

  //[exit
  /**
   * Exist this JApplication (after prompting the user
   * for a verification).
   */
  private void exit()
  {
    int        response;

    response = JOptionPane.showConfirmDialog(mainWindow,
        "Are you willing to let Lord Bernstein conquer the galaxy?",
        "Had Enough?", 
        JOptionPane.YES_NO_OPTION);

    if (response == JOptionPane.YES_OPTION)
    {
      mainWindow.setVisible(false);       
      stop();       
      mainWindow.dispose();          
    }
  }
  //]exit

  //[gettersRootPaneContainer
  /**
   * Returns the content pane
   * (required by RootPaneContainer).
   */
  public Container getContentPane()
  {
    return mainWindow.getContentPane();       
  }

  /**
   * Returns the glass pane
   * (required by RootPaneContainer).
   */
  public Component getGlassPane()
  {
    return mainWindow.getGlassPane();       
  }

  /**
   * Returns the layered pane
   * (required by RootPaneContainer).
   */
  public JLayeredPane getLayeredPane()
  {
    return mainWindow.getLayeredPane();       
  }

  /**
   * Returns the root pane
   * (required by RootPaneContainer).
   */
  public JRootPane getRootPane()
  {
    return mainWindow.getRootPane();       
  }
  //]gettersRootPaneContainer

  //[init
  /**
   * This method is called just before the main window
   * is first made visible.
   */
  public abstract void init();
  //]init

  //[run
  /**
   * The actual entry point into this JApplication
   * (required by Runnable).
   * <p>
   * This method is called in the event dispatch thread.
   * </p>
   */
  public final void run()
  {
    constructMainWindow();       
    init();
    mainWindow.setVisible(true);       
  }
  //]run

  /**
   * Resize the content (and the JFrame accordingly)
   * after the JFrame has been made visible.
   */
  private void resize()
  {
    Insets        insets;
    JPanel        contentPane;

    contentPane = (JPanel)mainWindow.getContentPane();       

    insets = mainWindow.getInsets();
    contentPane.invalidate();       
    mainWindow.setSize(
        width + insets.left + insets.right, 
        height + insets.top + insets.bottom);       
    contentPane.setSize(width, height);       
    contentPane.validate();       
  }
  //[settersRootPaneContainer
  /**
   * Set the content pane
   * (required by RootPaneContainer).
   */
  public void setContentPane(Container contentPane)
  {
    mainWindow.setContentPane(contentPane);       
  }

  /**
   * Set the glass pane
   * (required by RootPaneContainer).
   */
  public void setGlassPane(Component glassPane)
  {
    mainWindow.setGlassPane(glassPane);       
  }

  /**
   * Set the layered pane
   * (required by RootPaneContainer).
   */
  public void setLayeredPane(JLayeredPane layeredPane)
  {
    mainWindow.setLayeredPane(layeredPane);       
  }
  //]settersRootPaneContainer

  /**
   * This method is called when the main window is first
   * made visible and then each time it is de-iconified.
   * <p>
   * The default implementation does nothing.
   * </p>
   */
  public void start()
  {
  }

  /**
   * This method is called each time the main window is iconified
   * and just before it is disposed.
   * <p>
   * The default implementation does nothing.
   * </p>
   */
  public void stop()
  {
  }

  //[windowActivated
  /**
   * Handle windowActivated messages -- when the windows gains the focus
   * (required by WindowListener).
   *
   * @param event   The WindowEvent that generated the message
   */
  public void windowActivated(WindowEvent event)
  {
  }
  //]windowActivated

  //[windowClosed
  /**
   * Handle windowClosed messages -- when the window is disposed
   * (required by WindowListener).
   *
   * @param event   The WindowEvent that generated the message
   */
  public void windowClosed(WindowEvent event)
  {
    destroy();       
    System.exit(0);       
  }
  //]windowClosed

  //[windowClosing
  /**
   * Handle windowClosing messages 
   * (required by WindowListener).
   *
   * @param event   The WindowEvent that generated the message
   */
  public void windowClosing(WindowEvent event)
  {
    exit();
  }
  //]windowClosing

  //[windowDeiconified
  /**
   * Handle windowDeiconified messages -- when the window is maximized
   * (required by WindowListener).
   *
   * @param event   The WindowEvent that generated the message
   */
  public void windowDeiconified(WindowEvent event)
  {
    start();       
  }
  //]windowDeiconified

  //[windowDeactivated
  /**
   * Handle windowDeactivated messages -- when the windows loses the focus
   * (required by WindowListener).
   *
   * @param event   The WindowEvent that generated the message
   */
  public void windowDeactivated(WindowEvent event)
  {
    // Ignore
  }
  //]windowDeactivated

  //[windowIconified
  /**
   * Handle windowIconified messages -- when the window is minimized
   * (required by WindowListener).
   *
   * @param event   The WindowEvent that generated the message
   */
  public void windowIconified(WindowEvent event)
  {
    stop();       
  }
  //]windowIconified

  //[windowOpened
  /**
   * Handle windowOpened messages -- the first time the window is
   * made visible (required by WindowListener).
   *
   * @param event   The WindowEvent that generated the message
   */
  public void windowOpened(WindowEvent event)
  {
    resize();       
    start();       
  }
  //]windowOpened

  //[invoke
  /**
   * Invoke a Runnable in the event dispatch thread 
   * (by calling SwingUtilities.invokeAndWait()).
   * The advantage of this method is that it displays an error
   * dialog if an exception is thrown.
   * 
   * @param runnable  The Runnable
   */
  protected static void invokeInEventDispatchThread(Runnable runnable)
  {
    try
    {
      SwingUtilities.invokeAndWait(runnable);
    }
    catch (Exception e)
    {
      JOptionPane.showMessageDialog(null, 
          "Unable to start the application.",
          "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
  //]invoke

  // The icon
  public static byte[] icondata = {
      -119,	80,	78,	71,	13,	10,	26,	10,	0,	0,	0,	13,	73,	72,	68,	
      82,	0,	0,	0,	32,	0,	0,	0,	32,	8,	2,	0,	0,	0,	-4,	
      24,	-19,	-93,	0,	0,	0,	9,	112,	72,	89,	115,	0,	0,	
      11,	19,	0,	0,	11,	19,	1,	0,	-102,	-100,	24,	0,	0,	7,	
      -85,	73,	68,	65,	84,	120,	94,	77,	81,	-37,	-82,	28,	87,	21,	
      -84,	90,	-5,	-42,	-35,	-45,	115,	57,	51,	-25,	28,	31,	-37,	
      39,	-15,	45,	33,	14,	14,	-124,	68,	32,	-60,	-97,	-16,	-64,	
      95,	-16,	-60,	-113,	-16,	-56,	95,	32,	-108,	103,	-124,	4,	
      -120,	23,	94,	-120,	-60,	37,	56,	-79,	19,	-5,	-36,	111,	51,	
      115,	-90,	-69,	-9,	94,	11,	103,	100,	75,	-108,	74,	123,	
      -41,	86,	-41,	-86,	90,	82,	115,	52,	29,	3,	52,	51,	108,	
      -79,	21,	4,	104,	52,	24,	65,	-64,	-16,	-10,	52,	21,	33,	32,	
      20,	-77,	-83,	81,	-128,	-83,	-109,	0,	-72,	-51,	32,	9,	110,	
      63,	-68,	123,	122,	-93,	97,	-85,	-80,	5,	-73,	70,	18,	102,	
      -40,	102,	110,	-75,	66,	-124,	32,	-33,	-39,	8,	-24,	-74,	
      83,	64,	66,	8,	-128,	2,	-86,	-111,	-37,	62,	-126,	98,	91,	35,	
      60,	8,	108,	-11,	-69,	85,	-1,	-65,	12,	-128,	-67,	19,	-128,	
      16,	34,	80,	3,	65,	-112,	-58,	-83,	-55,	-124,	-128,	123,	107,	
      120,	-69,	-97,	-68,	27,	34,	61,	43,	-102,	18,	25,	106,	38,	6,	
      40,	0,	-30,	45,	-20,	109,	25,	9,	33,	72,	16,	70,	80,	-52,	-80,	
      -51,	-126,	-110,	-92,	35,	-67,	-128,	32,	96,	6,	1,	68,	96,	16,	
      16,	2,	-6,	-97,	-3,	-22,	23,	55,	-35,	-26,	-12,	-27,	-15,	
      102,	121,	-101,	-105,	-99,	110,	-118,	14,	-90,	69,	81,	12,	
      -125,	89,	-122,	1,	80,	-5,	30,	106,	-90,	-128,	-39,	-37,	95,	
      -93,	32,	-73,	116,	66,	79,	10,	-115,	20,	-127,	-56,	-10,	-94,	
      -112,	-37,	-122,	-33,	-66,	-8,	-3,	21,	-69,	-45,	-51,	-39,	
      113,	127,	116,	126,	121,	-78,	-70,	-66,	-79,	-84,	-35,	0,	
      -127,	19,	17,	-25,	72,	120,	7,	113,	6,	49,	74,	49,	25,	84,	6,	
      -45,	78,	-19,	54,	-105,	-82,	-41,	-66,	112,	80,	20,	-96,	-112,	
      -125,	81,	115,	41,	90,	-122,	-94,	86,	84,	75,	86,	-27,	-81,	-1,	
      -11,	-69,	94,	-52,	-118,	-43,	-95,	84,	-114,	-27,	-20,	116,	
      56,	89,	-99,	124,	91,	-11,	46,	48,	6,	-115,	35,	-6,	-38,	-41,	
      49,	-42,	-66,	-86,	67,	-86,	66,	83,	-89,	-108,	98,	-109,	98,	-27,	
      67,	10,	33,	58,	-97,	24,	26,	-122,	10,	82,	67,	-110,	-46,	-85,	74,	
      46,	78,	75,	41,	102,	-71,	-16,	-105,	127,	-5,	-115,	11,	34,	101,	
      -23,	55,	39,	31,	-73,	-109,	122,	-7,	-75,	-5,	-17,	17,	46,	-4,	
      -15,	73,	126,	77,	-67,	96,	26,	92,	96,	-59,	80,	-69,	80,	-7,	84,	
      -71,	-22,	13,	71,	-66,	106,	67,	-35,	-124,	122,	20,	-85,	81,	85,	
      -41,	-23,	13,	43,	23,	26,	-105,	18,	66,	-123,	42,	105,	-109,	-48,	
      86,	101,	-111,	-54,	-82,	-1,	-22,	-81,	127,	-102,	63,	62,	12,	
      126,	-80,	23,	-49,	-25,	-77,	-55,	-67,	-85,	-93,	-67,	111,	
      46,	-58,	75,	-74,	39,	58,	92,	93,	119,	99,	94,	-113,	52,	-121,	-78,	
      110,	-43,	-75,	-120,	13,	-4,	-124,	9,	-116,	-127,	41,	-71,	-124,	
      88,	105,	72,	125,	-86,	-5,	-74,	41,	-87,	90,	-41,	-11,	-51,	-76,	
      -70,	-39,	107,	47,	30,	-116,	-42,	-113,	-3,	122,	-28,	-14,	-40,	
      31,	-3,	-27,	75,	-23,	-82,	119,	-97,	-20,	-73,	-29,	-54,	29,	
      -65,	-62,	-86,	91,	95,	-24,	-18,	-85,	-110,	-106,	-71,	20,	70,	
      -79,	-109,	65,	111,	90,	94,	6,	116,	-126,	82,	-100,	95,	-77,	-68,	
      102,	-97,	-47,	-35,	-78,	-39,	8,	-34,	-92,	44,	-25,	-72,	122,	
      -60,	-37,	71,	49,	127,	34,	-27,	-61,	13,	14,	-105,	97,	106,	41,	97,	
      -52,	52,	-122,	31,	78,	55,	-25,	127,	127,	85,	-25,	82,	-43,	-24,	
      76,	79,	-113,	55,	-35,	-85,	-31,	-18,	-107,	-76,	-33,	105,	-14,	
      126,	-81,	-80,	-46,	112,	70,	81,	-113,	-34,	-92,	-77,	-64,	-100,	
      124,	-87,	83,	63,	-118,	-3,	-94,	-54,	-9,	-126,	62,	38,	62,	-52,	
      -18,	-23,	42,	44,	110,	-21,	-42,	106,	-113,	70,	92,	66,	-100,	115,	
      -6,	-98,	-50,	-97,	-126,	7,	63,	26,	5,	-121,	-35,	73,	-102,	71,	62,	
      -34,	-83,	-18,	-33,	-24,	-50,	-41,	-3,	65,	-15,	-29,	43,	-73,	25,	
      -22,	-34,	29,	12,	-74,	119,	45,	-113,	94,	26,	87,	38,	39,	69,	-111,	99,	
      86,	-121,	50,	117,	-7,	126,	-28,	123,	-47,	29,	-90,	-76,	-105,	-38,	
      -38,	-115,	68,	-58,	-58,	22,	-11,	-62,	22,	15,	-20,	-18,	51,	-67,	
      -13,	-39,	48,	126,	80,	124,	127,	66,	35,	-105,	103,	22,	-63,	-45,	
      -105,	-101,	61,	9,	-72,	-118,	-53,	-20,	101,	24,	69,	62,	100,	-8,	
      121,	35,	-17,	-115,	38,	-49,	-52,	-57,	-29,	91,	29,	110,	-50,	-81,	
      -70,	-101,	46,	-85,	-26,	-87,	-39,	126,	12,	119,	82,	-102,	-11,	
      117,	28,	-58,	18,	39,	90,	47,	48,	-66,	-97,	23,	-49,	-122,	-39,	103,	
      -85,	-8,	-12,	-70,	-97,	117,	75,	113,	94,	55,	82,	-56,	91,	-111,	11,	
      19,	87,	-40,	102,	70,	-101,	68,	77,	-67,	54,	34,	15,	123,	62,	-47,	-12,	
      4,	-13,	-61,	121,	112,	-11,	85,	-103,	-41,	7,	71,	-89,	-85,	-25,	
      -73,	93,	41,	109,	-74,	-119,	-122,	81,	110,	-93,	-76,	-122,	-35,	33,	
      -36,	-19,	70,	31,	-33,	52,	63,	-66,	12,	-97,	95,	-106,	-125,	85,	31,	92,	
      70,	-35,	25,	61,	-31,	85,	-103,	-51,	-83,	-23,	94,	111,	4,	57,	9,	124,	
      -27,	118,	-41,	118,	32,	-74,	104,	-84,	-94,	85,	-66,	-25,	36,	-119,	
      28,	-70,	-6,	-58,	-90,	-11,	-36,	-99,	-25,	2,	94,	-78,	12,	-11,	-59,	
      102,	84,	100,	-1,	-42,	61,	-67,	-108,	-113,	-82,	-14,	-25,	-105,	-73,	
      -17,	-81,	49,	-42,	78,	-22,	107,	54,	-125,	117,	32,	-67,	-63,	1,	-52,	
      -10,	-67,	112,	-22,	-113,	25,	-43,	66,	46,	-51,	-60,	29,	-20,	113,	4,	
      -83,	92,	-12,	-34,	-45,	13,	82,	81,	-102,	22,	117,	107,	31,	-19,	-61,	
      -90,	-3,	-71,	-84,	47,	-14,	-7,	-85,	-7,	73,	126,	120,	-51,	-97,	92,	
      119,	79,	54,	-68,	91,	52,	-122,	1,	-75,	65,	-78,	106,	96,	-120,	38,	-82,	
      -86,	-57,	-128,	24,	-123,	-26,	85,	124,	70,	28,	-84,	-38,	96,	-47,	-55,	
      78,	-119,	79,	-6,	112,	56,	-56,	108,	96,	42,	-126,	-38,	96,	-94,	-29,	29,	
      -67,	-13,	112,	-104,	127,	-70,	26,	-3,	-12,	-75,	124,	-6,	93,	120,	-10,
      28,	63,	124,	61,	124,	124,	97,	-117,	77,	31,	-83,	-125,	91,	2,	43,	-45,	12,
      83,	-10,	111,	-24,	-86,	106,	98,	116,	-128,	3,	3,	-24,	-115,	-107,	90,	
      117,	107,	97,	-83,	-15,	-72,	79,	-21,	52,	17,	-33,	-44,	-98,	-75,	-109,
      -15,	46,	125,	93,	118,	30,	-45,	63,	88,	-90,	15,	86,	-7,	-47,	-51,	-28,	
      -95,	-60,	-3,	92,	-19,	38,	87,	55,	-108,	-47,	26,	97,	-80,	-40,	3,	4,	-68,	
      -47,	67,	-99,	-119,	75,	-43,	12,	20,	48,	-112,	30,	8,	-80,	84,	-32,	12,	-56,	
      -92,	119,	45,	-128,	-37,	-18,	76,	75,	-33,	107,	-98,	-43,	126,	58,	-54,	
      -11,	7,	27,	28,	-98,	112,	-1,	38,	-19,	73,	-100,	86,	59,	-51,	-99,	-87,	-65,	
      83,	113,	-113,	108,	-95,	-87,	-43,	54,	-103,	31,	33,	77,	45,	45,	108,	-6,	80,	
      -9,	-67,	-110,	-124,	19,	8,	32,	64,	-91,	16,	-95,	66,	-122,	65,	46,	-82,	-7,	-91,	
      -14,	76,	-29,	88,	-45,	127,	116,	116,	-17,	100,	122,	63,	44,	18,	102,	77,	
      113,	-33,	-114,	71,	26,	-45,	-92,	-89,	120,	-92,	-127,	-55,	-125,	123,	-42,	
      -74,	54,	119,	34,	-122,	-95,	-75,	-70,	70,	106,	16,	-90,	120,	-59,	-15,	-20,	
      125,	-46,	17,	94,	-60,	17,	-119,	8,	-114,	-63,	-117,	-125,	39,	-93,	-85,	93,	83,	
      -43,	-87,	-98,	-90,	-35,	-35,	-39,	-18,	-99,	-86,	-39,	-41,	-23,	39,	-51,	
      -18,	15,	-38,	59,	-69,	-43,	56,	-50,	114,	61,	14,	126,	113,	9,	31,	-40,	100,	
      -53,	-30,	88,	89,	28,	115,	20,	33,	32,	-93,	93,	-18,	-40,	23,	-98,	-40,	-126,	5,	
      48,	-46,	-60,	13,	-16,	-50,	-68,	-109,	8,	73,	-94,	-43,	-86,	-101,	80,	119,	-88,	
      -77,	120,	-67,	-97,	-4,	-66,	-75,	-63,	-19,	112,	119,	103,	83,	45,	-77,	-37,	
      -113,	31,	102,	-100,	-102,	84,	115,	46,	84,	52,	107,	-127,	97,	-116,	-90,	-110,	-104,	
      109,	-75,	-80,	63,	-98,	-27,	47,	56,	-103,	31,	-110,	36,	40,	-50,	-47,	57,	113,	112,	
      -127,	18,	-123,	9,	-46,	-104,	107,	5,	19,	-56,	-4,	13,	-23,	14,	36,	44,	66,	-104,	71,	
      -73,	83,	-71,	73,	21,	70,	99,	-119,	83,	-60,	84,	-89,	38,	-7,	-15,	-32,	55,	78,	88,	
      107,	21,	16,	-68,	121,	-47,	127,	-18,	-24,	31,	70,	-101,	115,	78,	22,	-9,	40,	-92,	
      80,	28,	-60,	-117,	4,	97,	84,	-87,	-55,	26,	-46,	42,	-57,	-80,	89,	-31,	-116,	-36,	
      -15,	-100,	-112,	109,	-112,	89,	-19,	70,	53,	-22,	58,	84,	-115,	-44,	-47,	-113,	28,	43,
      23,	71,	21,	-125,	-46,	91,	-128,	87,	64,	-12,	44,	109,	-2,	60,	-69,	-70,	-28,	-111,	
      121,	-118,	82,	68,	60,	-60,	27,	-93,	50,	10,	-126,	105,	44,	76,	-80,	-58,	56,	82,	
      -116,	10,	82,	4,	-56,	34,	-110,	-95,	-99,	-87,	87,	113,	25,	-82,	19,	-89,	-26,	
      -125,	23,	-105,	-3,	96,	-59,	-32,	45,	-117,	-125,	109,	120,	-5,	15,	119,	115,	-66,	
      58,	53,	-1,	111,	-13,	-16,	10,	81,	-117,	-80,	72,	70,	-77,	68,	-117,	-54,	-92,	90,
      41,	-110,	50,	20,	82,	80,	68,	7,	-111,	-34,	-119,	23,	-105,	-100,	5,	51,	-33,	-45,	27,
      -126,	106,	41,	-102,	37,	119,	68,	81,	-21,	-112,	-83,	-40,	-6,	121,	125,	-7,	50,	-81,
      -19,	-10,	-123,	-60,	-81,	-44,	-45,	23,	58,	32,	2,	-111,	26,	-63,	96,	76,	106,	-115,	
      34,	-86,	-119,	-102,	-46,	58,	-125,	51,	6,	104,	54,	-21,	-107,	93,	17,	111,	-22,	88,	
      66,	-63,	80,	74,	-25,	84,	-123,	-125,	-119,	39,	32,	24,	-50,	120,	-2,	82,	-41,	57,	-33,
      -128,	-33,	104,	62,	55,	111,	-95,	-48,	17,	2,	19,	-62,	23,	-117,	-54,	10,	26,	-118,	
      8,	-96,	-44,	-63,	-32,	-124,	3,	-47,	-85,	121,	-91,	27,	116,	3,	19,	58,	47,	-38,
      9,	92,	-89,	106,	46,	8,	28,	41,	30,	121,	-61,	-85,	-105,	-43,	117,	-113,	65,	-20,	
      -56,	-16,	-54,	-12,	22,	30,	65,	-31,	-120,	0,	-117,	74,	-81,	16,	-64,	104,	-48,	98,
      48,	117,	-52,	98,	3,	33,	10,	14,	66,	35,	52,	-5,	66,	79,	-21,	-73,	29,	16,	-119,	106,	
      -125,	-48,	123,	-77,	-50,	46,	-113,	-46,	-59,	82,	7,	43,	75,	-16,	-123,	-23,	37,	77,	
      -51,	-43,	119,	-125,	36,	32,	-86,	69,	51,	49,	56,	-104,	40,	4,	70,	-126,	98,	112,	-76,	
      109,	35,	0,	103,	-108,	66,	-89,	-108,	44,	94,	97,	-123,	90,	104,	10,	80,	51,	108,	121,	
      45,	-57,	23,	97,	-91,	92,	-125,	-81,	-119,	35,	96,	67,	14,	-12,	-88,	-43,	-126,	-87,	
      -37,	-50,	-109,	70,	-104,	-127,	69,	-116,	98,	74,	0,	80,	64,	-115,	-86,	28,	20,	78,	-51,	
      -101,	9,	-117,	81,	42,	-79,	90,	-84,	56,	116,	-118,	-66,	-109,	-53,	27,	127,	-83,	80,	
      -24,	10,	-10,	-38,	112,	-115,	98,	-122,	12,	111,	65,	-51,	41,	28,	-60,	-63,	-116,	-86,	
      98,	70,	40,	-95,	48,	5,	60,	64,	-93,	-103,	-86,	-78,	20,	103,	10,	53,	27,	-60,	8,	-46,	
      41,	-60,	6,	-128,	25,	-41,	75,	119,	85,	-84,	-64,	10,	121,	74,	-69,	52,	27,	-88,	106,	
      -24,	-15,	63,	-87,	17,	68,	20,	30,	-52,	-89,	-76,	0,	0,	0,	0,	73,	69,	78,	68,	-82,	
      66,	96,	-126};

}
