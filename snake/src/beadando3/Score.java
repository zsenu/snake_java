// Készítette Fiók Nándor, GSTQLI
package beadando3;

public class Score
{
    private final String name;
    private final int score;
    /**
     * Score adattípus konstruktora.
     * @param n
     * @param s 
     */
    protected Score(String n, int s)
    {
        this.name = n;
        this.score = s;
    }
    /**
     * Az adott rekord nevét visszatérítő függvény.
     * @return 
     */
    protected String getName() { return name; }
    /**
     * Az adott rekord elért pontszámát visszatérítő függvény.
     * @return 
     */
    protected int getScore() { return score; }
}