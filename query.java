import java.io.IOException;
import java.sql.*;
import java.util.*;

public class query {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static String DB_URL = "jdbc:mysql://localhost:3306/";
    static String DB;
    static String USER;
    static String PASS;
    public static void exportCSV(List<String[]> productInfo, String filename) throws IOException{
        CSVExporter csv = new CSVExporter(productInfo);     
        String[] title = {"Album", "Release Year", "Song"};
        csv.writeCSV(title, filename);
    }
    public static void main(String[] args) {
        Connection  conn = null;
        Statement   stmt = null;
        Scanner sc= new Scanner(System.in); 
        try {
            // Register driver
            Class.forName(JDBC_DRIVER);
            // Open link
            System.out.println("Please enter database:");
            DB = sc.nextLine();
            System.out.println("Please enter database user:");
            USER = sc.nextLine();
            System.out.println("Please enter database password:");
            PASS = sc.nextLine();
            System.out.println("Connecting Database...");
            conn = DriverManager.getConnection(DB_URL+DB, USER, PASS);
            // Execute query
            System.out.println("Instancize Statement...");
            stmt = conn.createStatement();
            String sql, searchName;
            List<String[]> list = new ArrayList<String[]>();
            ResultSet rs;
            while (true){
                System.out.println("Enter the name of the band you want to search for: "); 
                searchName = sc.nextLine();
                sql = "SELECT a.name AS 'Album Name',  a.release_year AS 'Release Year', s.name AS 'Song Name' FROM albums a JOIN songs s ON a.id = s.album_id JOIN bands b ON a.band_id = b.id WHERE b.name = '" + searchName + "'GROUP BY a.name, a.release_year, s.name ORDER BY a.name;";
                rs = stmt.executeQuery(sql);
                // Query results
                System.out.printf("%-50s%-25s%s\n","Album Name","Release Year","Song Name");
                while (rs.next()) {
                    // The field type needs to be specified
                    String name      = rs.getString("Album Name");
                    int    release_year = rs.getInt("Release Year");
                    String song_name = rs.getString("Song Name");
                    // Print information
                    System.out.printf("%-50s%-25d%-50s\n", name, release_year, song_name);
                    String[] info = {name, Integer.toString(release_year), song_name};
                    list.add(info);
                }
                System.out.println("Do you want to export the result to a CSV file? (Y/N)");
                String answer = sc.nextLine();
                if (answer.equals("Y") || answer.equals("y")){
                    System.out.println("Enter the name of the file you want to export: ");
                    String filename = sc.nextLine();
                    exportCSV(list, filename);
                    list.clear();
                }
                System.out.println("Do you want to search again? (Y/N)");
                answer = sc.nextLine();
                if (answer.equals("N") || answer.equals("n")){
                    break;
                }
            }
            // Disconnect database
            rs.close();
            stmt.close();
            conn.close();
            sc.close();
        } catch (SQLException  se) {
            // JDBC error
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the database connection again
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
        }
    }
}
