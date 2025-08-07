// Készítette Fiók Nándor, GSTQLI
package beadando3;

import java.sql.*;
import java.util.ArrayList;

/**
 * Ranglista a játék legjobb eredményeinek eltárolásához.
 * @author fiokn
 */
public class Leaderboard
{
    private final PreparedStatement insertStatement;
    private final Connection connection;
    /**
     * Ranglista konstruktora.
     * @throws SQLException 
     */
    public Leaderboard() throws SQLException
    {
        String dbURL = "jdbc:derby://localhost:1527/snakescores;";
        connection = DriverManager.getConnection(dbURL);
        String insertQuery = "INSERT INTO SNAKESCORES (TIMESTAMP, NAME, SCORE) VALUES (?, ?, ?)";
        insertStatement = connection.prepareStatement(insertQuery);
    }
    /**
     * 10 legjobb pontszámú rekord lekérdezéséhez használt függvény.
     * Azonos pontszámok esetén a korábban elért pont feljebb jelenik meg.
     * @return
     * @throws SQLException 
     */
    protected ArrayList<Score> getHighScores() throws SQLException
    {
        String query = "SELECT * FROM SNAKESCORES ORDER BY SCORE DESC, TIMESTAMP FETCH FIRST 10 ROWS ONLY";
        Statement stmt = connection.createStatement();
        ResultSet results = stmt.executeQuery(query);
        ArrayList<Score> highScores = new ArrayList<>();
        while (results.next())
        {
            String name = results.getString("NAME");
            int score = results.getInt("SCORE");
            highScores.add(new Score(name, score));
        }
        return highScores;
    }
    /**
     * Rekord adatbázisba való beszúrásához használt függvény.
     * @param n
     * @param s
     * @throws SQLException 
     */
    protected void insertScore(String n, int s) throws SQLException
    {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        insertStatement.setTimestamp(1, ts);
        insertStatement.setString(2, n);
        insertStatement.setInt(3, s);
        insertStatement.executeUpdate();
    }
}