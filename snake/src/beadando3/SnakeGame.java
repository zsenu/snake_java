// Készítette Fiók Nándor, GSTQLI
package beadando3;

import java.util.Random;
import java.util.ArrayList;

/**
 * A játék mögöttes logikáját kezelő osztály.
 * @author fiokn
 */
public final class SnakeGame
{   
    private Snake snake;
    
    private final Random rand = new Random();
    private final int SIZE;
    private int obstacleCount;
    private int[][] board;
    // 0 == empty cell
    // 1 == apple cell
    // 2 == obstacle cell
    // 3 == snake head cell
    // 4 == snake body cell
    private int direction; 
    // 0 == up    / W
    // 1 == left  / A
    // 2 == down  / S
    // 3 == right / D
    private int score;
    private boolean alive;
    private ArrayList<Integer> bodyX = new ArrayList<>();
    private ArrayList<Integer> bodyY = new ArrayList<>();
    /**
     * s-t beállítja a játéktér méretének, majd új játékot indít.
     * @param s
     */
    protected SnakeGame(int s) { SIZE = s; init(); }
    /**
     * A játéktér állapotát lekérdező függvény.
     * @return 
     */
    protected int[][] getBoard() { return board; }
    /**
     * A kígyó aktuális menetirányát lekérdező függvény.
     * @return 
     */
    protected int getDirection() { return direction; }
    /**
     * A kígyó mozgási irányát megváltoztató függvény.
     * @param dir 
     */
    protected void setDirection(int dir) { if (dir >= 0 && dir <= 3) direction = dir; }
    /**
     * A játék során elért pontszámot lekérdező függvény.
     * @return 
     */
    protected int getScore() { return score; }
    /**
     * Megadja, hogy él-e még a kígyó, azaz folyamatban van-e még a játék.
     * @return 
     */
    protected boolean isAlive() { return alive; }
    /**
     * Inicializáló függvény, mely az első vagy új játék indítását kezeli.  
     */
    protected void init()
    {
        board = new int[SIZE][SIZE];
        score = 0;
        obstacleCount = 0;
        alive = true;
        direction = rand.nextInt(4);
        // bodyX.clear();
        // bodyY.clear();
        generateSnake();
        generateApple();
    }
    /**
     * Menetirányba néző, 2 hosszú kígyó elhelyezése a pályán.
     */
    private void generateSnake()
    {
        int x = rand.nextInt(SIZE - 2) + 1;
        int y = rand.nextInt(SIZE - 2) + 1;
        snake = new Snake(x, y, direction);
        board[x][y] = 3;
        // bodyX.add(x); bodyY.add(y);
        
        switch (direction)
        {
            case 0 -> x++;
            case 1 -> y++;
            case 2 -> x--;
            case 3 -> y--;
        }
        board[x][y] = 4;
        // bodyX.add(x); bodyY.add(y);
    }
    /**
     * Alma generálása egy üres és elérhető pozícióra.
     */
    private void generateApple()
    {
        int x, y;
        boolean ok;
        do
        {
            ok = true;
            x = rand.nextInt(SIZE);
            y = rand.nextInt(SIZE);
            if (board[x][y] != 0) ok = false;
        }
        while(!ok);
        board[x][y] = 1;
    }
    /**
     * Akadály generálásához, a kígyó arca elé nem generálhat.
     */
    private void generateObstacle()
    {
        boolean ok;
        int x, y, xx, yy;
        do
        {
            ok = true;
            x = rand.nextInt(SIZE);
            y = rand.nextInt(SIZE);
            xx = x;
            yy = y;
            if (board[x][y] != 0) ok = false;
            switch (direction)
            {
                case 0 -> xx = (x == (SIZE - 1) ? x : x + 1);
                case 1 -> yy = (y == (SIZE - 1) ? y : y + 1);
                case 2 -> xx = (x == 0 ? x : x - 1);
                case 3 -> yy = (y == 0 ? y : y - 1);
            }
            if (board[xx][yy] == 3) ok = false;
        }
        while (!ok);
        board[x][y] = 2;
        obstacleCount++;
    }
    /**
     * A kígyó aktúális menetirányába való mozgást kezelő eljárás.
     */
    protected void move()
    {
        this.bodyX = snake.getBodyX();
        this.bodyY = snake.getBodyY();
        
        boolean eats = false;
        
        int[] target = new int[2];
        switch (direction)
        {
            case 0 ->
            {
                System.out.println("case 0" + (bodyX.get(0) - 1) + " " + bodyY.get(0));
                target[0] = bodyX.get(0) - 1;
                target[1] = bodyY.get(0);
            }
            case 1 ->
            {
                System.out.println("case 1" + (bodyX.get(0)) + " " + (bodyY.get(0) - 1));
                target[0] = bodyX.get(0);
                target[1] = bodyY.get(0) - 1;
            }
            case 2 ->
            {
                System.out.println("case 2" + (bodyX.get(0) + 1) + " " + (bodyY.get(0)));
                target[0] = bodyX.get(0) + 1;
                target[1] = bodyY.get(0);
            }
            case 3 ->
            {
                System.out.println("case 3" + (bodyX.get(0)) + " " + (bodyY.get(0)) + 1);
                target[0] = bodyX.get(0);
                target[1] = bodyY.get(0) + 1;
            }
        }
        if (!inBounds(target[0], target[1])) { alive = false; return; }
        
        System.out.println("dir = " + direction + ", location = " + target[0] + " " + target[1]);
        switch (board[target[0]][target[1]])
        {
            case 1 ->
            {
                eats = true;
                
                board[bodyX.get(0)][bodyY.get(0)] = 4;
                board[bodyX.get(score)][bodyY.get(score)] = 4;
                bodyX.add(1, bodyX.get(0));
                bodyY.add(1, bodyY.get(0));
                board[target[0]][target[1]] = 3;
                bodyX.set(0, target[0]);
                bodyY.set(0, target[1]);
                score++;
                if (score % 3 == 1 && obstacleCount < SIZE * SIZE * 0.15) generateObstacle();
                if (SIZE * SIZE - (score + 2 + obstacleCount) > 0) generateApple();
            }
            case 2 -> { alive = false; return; }
            case 4 -> { if(!(bodyX.getLast() == target[0] && bodyY.getLast() == target[1])) { alive = false; return; } }
        }
        board[bodyX.get(0)][bodyY.get(0)] = 4;
        if (board[bodyX.get(score + 1)][bodyY.get(score + 1)] == 4) board[bodyX.get(score + 1)][bodyY.get(score + 1)] = 0;
        board[bodyX.get(score)][bodyY.get(score)] = 4;
        for (int i = score + 1; i > 0; i--)
        {
            bodyX.set(i, bodyX.get(i - 1));
            bodyY.set(i, bodyY.get(i - 1));
        }
        board[target[0]][target[1]] = 3;
        bodyX.set(0, target[0]);
        bodyY.set(0, target[1]);
        snake.setBodyX(this.bodyX);
        snake.setBodyY(this.bodyY);
        
        snake.move(target[0], target[1], eats);
    }
    /**
     * x, y koordinátáról meghatározza, hogy az a játéktéren belül van-e.
     * @param x
     * @param y
     * @return 
     */
    private boolean inBounds(int x, int y)
    { return (x >= 0 && x < SIZE && y >= 0 && y < SIZE); }
}