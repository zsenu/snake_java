package beadando3;

import java.util.ArrayList;

public class Snake
{
    private ArrayList<Integer> bodyX = new ArrayList<>();
    private ArrayList<Integer> bodyY = new ArrayList<>();
    
    public Snake(int x, int y, int dir)
    {
        bodyX.clear();
        bodyY.clear();
        bodyX.add(x);
        bodyY.add(y);
        switch (dir)
        {
            case 0 -> x++;
            case 1 -> y++;
            case 2 -> x--;
            case 3 -> y--;
        }
        bodyX.add(x);
        bodyY.add(y);
    }
    
    public ArrayList<Integer> getBodyX() { return bodyX; }
    public ArrayList<Integer> getBodyY() { return bodyY; }
    
    public void setBodyX(ArrayList<Integer> x) { bodyX = x; }
    public void setBodyY(ArrayList<Integer> y) { bodyY = y; }
    
    public void move(int x, int y, boolean eats)
    {
        if (eats)
        {
            bodyX.set(1, bodyX.get(1));
            bodyY.set(1, bodyY.get(1));
        }
        else for (int i = bodyX.size() - 1; i > 0; i--)
        {
            bodyX.set(i, bodyX.get(i));
            bodyY.set(i, bodyY.get(i));
        }
        bodyX.set(0, bodyX.get(0));
        bodyY.set(0, bodyY.get(0));
    }
}