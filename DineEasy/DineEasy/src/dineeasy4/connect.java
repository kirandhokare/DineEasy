package dineeasy4;
import java.sql.*;

public class connect {
    Connection conn;
    Statement stmt;

    connect(){
        try{
                Class.forName("com.mysql.cj.jdbc.Driver");
                 conn= DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dineEasy","root","1234");
                 stmt = conn.createStatement();

                 if(conn ==null)
                 {
                     System.out.println("conncetion failed");
                 }else {
                     System.out.println("success connection");
                 }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
