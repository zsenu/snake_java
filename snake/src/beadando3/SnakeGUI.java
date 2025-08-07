// Készítette Fiók Nándor, GSTQLI
package beadando3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import java.sql.*;
import java.util.ArrayList;
/**
 * A játék grafikus felületét kezelő osztály.
 * @author fiokn
 */
public class SnakeGUI extends JPanel
{
    private static final int DELAY = 130;
    private static final int SIZE = 14;
    private static final int RES = 658;
    private static final int GRID = RES / SIZE;
    
    private final SnakeGame game;
    private final Timer timer;
    private final JFrame frame;
    private final JMenuBar menuBar;
    
    private BufferedImage body = null;
    private BufferedImage head = null;
    private BufferedImage apple = null;
    private BufferedImage obstacle = null;
    
    private Leaderboard leaderboard;
    
    private boolean playing;
    private int direction;
    private int nextDir;
    private int step;
    /**
     * Inicializálja a játékot, majd elkészíti a játék keretét, a menüt hozzá, és az időzítőt.
     */
    protected SnakeGUI()
    {
        try
        {
            body = ImageIO.read(new File("./body.png"));
            head = ImageIO.read(new File("./head.png"));
            apple = ImageIO.read(new File("./apple.png"));
            leaderboard = new Leaderboard();
        }
        catch (IOException ex) { System.err.println("image io error"); }
        catch (SQLException ex) { System.err.println("database error"); }
        game = new SnakeGame(SIZE);
        direction = game.getDirection();
        nextDir = direction;
        step = 0;
        playing = true;
        
        frame = new JFrame();
        frame.setTitle("Kígyó Játék");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(RES + 15, RES + GRID + 15);
        frame.setFocusable(true);
        frame.setResizable(false);
        
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        JMenuItem newGame = new JMenuItem("Új játék");
        menuBar.add(newGame);
        newGame.addActionListener((ActionEvent e) -> { newGame(); });
        JMenuItem pauseGame = new JMenuItem("Szünet");
        menuBar.add(pauseGame);
        pauseGame.addActionListener((ActionEvent e) -> { togglePause(); });
        JMenuItem leaderboardMenu = new JMenuItem("Ranglista");
        menuBar.add(leaderboardMenu);
        leaderboardMenu.addActionListener((ActionEvent e) -> { showLeaderboard(); });
        JMenuItem exitGame = new JMenuItem("Kilépés");
        menuBar.add(exitGame);
        exitGame.addActionListener((ActionEvent e) -> { System.exit(0); });
        
        frame.addKeyListener(new InputHandler());
        frame.add(this);
        frame.setVisible(true);
        timer = new Timer(DELAY, new TimePassed());
        timer.start();
    } 
    /**
     * Új játék indítását kezelő eljárás.
     */
    private void newGame()
    {
        try { head = ImageIO.read(new File("./head.png")); }
        catch (IOException ex) { System.err.println("image io error"); }
        
        game.init();
        direction = game.getDirection();
        nextDir = direction;
        playing = true;
        step = 0;
        repaint();
        timer.start();
    }
    /**
     * A játék szüneteltetését kezelő eljárás.
     */ 
    private void togglePause()
    {
        if (!game.isAlive()) return;
        if (playing)
        {
            timer.stop();
            repaint();
        }
        else
        {
            repaint();
            try { Thread.sleep(500); } catch(InterruptedException ex) { Thread.currentThread().interrupt(); }    
            timer.start();
        }
        playing = !playing;
    }
    /**
     * A ranglista megjelenítését végző eljárás.
     */
    private void showLeaderboard()
    {
        if (playing) togglePause();
        ArrayList<Score> scores = null;
        StringBuilder sb = new StringBuilder();
        
        try { scores = leaderboard.getHighScores(); }
        catch (SQLException ex) { System.err.println("database error"); }
        catch (NullPointerException ex) { System.err.println("null error"); }
        
        if (scores == null) { sb.append("Hiba az adatbázis betöltésével."); }
        else for (int i = 0; i < scores.size(); i++)
        {
            String record = ((i + 1) + ". " + scores.get(i).getName() + " - " + scores.get(i).getScore() + "\n");
            sb.append(record);
        }
        JOptionPane.showMessageDialog(frame, sb.toString(), "Ranglista", JOptionPane.INFORMATION_MESSAGE);
        togglePause();
    }
    /**
     * A játék végét kezelő eljárás.
     */
    private void endGame()
    {
        try { head = ImageIO.read(new File("./headbonk.png")); }
        catch (IOException ex) { System.err.println("image io error!"); }
        timer.stop();
        
        String message = "Játék vége!\nElért pontszámod: " + game.getScore() + "\nAdd meg a neved:";
        String name = JOptionPane.showInputDialog(frame, message, "Vége a játéknak!", JOptionPane.INFORMATION_MESSAGE);
        if (name == null || name.isBlank()) { newGame(); return; }
        
        try { leaderboard.insertScore(name, game.getScore()); }
        catch (NullPointerException ex) { System.err.println("null error"); }
        catch (SQLException ex) { System.err.println("database error"); }
        newGame();
        
    }
    /**
     * A képernyőre rajzoláshoz szükséges metódus implementálása.
     * @param g 
     */
    @Override
    protected void paintComponent (Graphics g)
    {
        super.paintComponent(g);
        drawGame(g);
    }
    /**
     * Kirajzolja a játékteret a képernyőre.
     * @param g 
     */
    private void drawGame(Graphics g)
    {
        int[][] board = game.getBoard();
        for (int i = 0; i < SIZE; i++)
        {
            for (int j = 0; j < SIZE; j++)
            {
                g.setColor(((i + j) % 2 == 0) ? new Color(255, 245, 217) : new Color(255, 238, 189));
                switch (board[i][j])
                {
                    case 0 -> g.fillRect(j * GRID, i * GRID, GRID, GRID);
                    case 1 ->
                    {
                        g.fillRect(j * GRID, i * GRID, GRID, GRID);
                        g.setColor(new Color(155, 155, 155));
                        g.fillOval(j * GRID + 5, ((8 * i + 7) * GRID) / 8, GRID - 10, GRID / 8);
                        if (step % 8 < 4) g.drawImage(apple, j * GRID, i * GRID, GRID, GRID, frame);
                        else g.drawImage(apple, j * GRID, i * GRID - 10, GRID, GRID, frame);
                    } 
                    case 2 ->
                    {
                        g.fillRect(j * GRID, i * GRID, GRID, GRID);
                        g.setColor(new Color(155, 155, 155));
                        g.fillOval(j * GRID + 5, ((8 * i + 7) * GRID) / 8, GRID - 10, GRID / 8);
                        try
                        {
                            // erősen pszeudorandom szám a véletlenSZERŰ akadályokhoz
                            if ((i * j + i - j) % 10 < 7) obstacle = ImageIO.read(new File("./rock.png"));
                            else obstacle = ImageIO.read(new File("./cactus.png"));
                            g.drawImage(obstacle, j * GRID, i * GRID, GRID, GRID, frame);
                        }
                        catch (IOException ex) { System.err.println("image io error!"); }
                    }
                    case 3 -> g.drawImage(head, j * GRID, i * GRID, GRID, GRID, frame);
                    case 4 -> g.drawImage(body, j * GRID, i * GRID, GRID, GRID, frame);
                }
            }
        }
        if (!playing)
        {
            g.setColor(new Color(50, 50, 50));
            g.fillRect(RES / 2 - 32, RES / 2 - 32, 24, 64);
            g.fillRect(RES / 2 + 8, RES / 2 - 32, 24, 64);
            g.setColor(new Color(227, 86, 71));
            g.fillRect(RES / 2 - 30, RES / 2 - 30, 20, 60);
            g.fillRect(RES / 2 + 10, RES / 2 - 30, 20, 60);
        }
    }
    /**
     * Eseménykezelő a lépésre jutó idő lejárásához.
     */
    private class TimePassed implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            repaint();
            direction = nextDir;
            if (step == 0)
            {
                timer.stop();
                try { Thread.sleep(1000); } catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
                timer.start();
            }
            game.setDirection(direction);
            game.move();
            step++;
            if (!game.isAlive()) endGame();
        }
    }
    /**
     * Eseménykezelő a játékos bemenetéhez.
     */
    private class InputHandler extends KeyAdapter
    {
        @Override
        public void keyPressed(KeyEvent e)
        {
            if (!playing) return;
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_W ->
                {
                    if(direction != 2)
                    { nextDir = 0; }
                }
                case KeyEvent.VK_A ->
                {
                    if(direction != 3)
                    { nextDir = 1; }
                }
                case KeyEvent.VK_S ->
                {
                    if(direction != 0)
                    { nextDir = 2; }
                }
                case KeyEvent.VK_D ->
                {
                    if(direction != 1)
                    { nextDir = 3; }
                }
            }
        }
    }
}