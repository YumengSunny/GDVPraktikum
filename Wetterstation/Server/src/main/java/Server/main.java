/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Server.UDPServer;
import java.io.IOException;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author admin
 */
public class main {
    public static void main(String[]args) throws ClassNotFoundException, SocketException{
         System.out.println("Server main");
        Class.forName("org.sqlite.JDBC");
        String dburl="jdbc:sqlite:vs.db";
        
        try {
            Connection conn = DriverManager.getConnection(dburl);
            if(conn != null) {
                
                String sqlTableCreate = "CREATE TABLE IF NOT EXISTS sensors ("
                        + "id INTEGER PRIMARY KEY, "
                        + "sensortype VARCHAR(25) NOT NULL, "
                        + "value INTEGER,"
                        + "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ")";
                Statement stmt = conn.createStatement();
                stmt.execute(sqlTableCreate);
                
                Statement stmt2 = conn.createStatement();
                stmt2.execute("DELETE FROM sensors");
       
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Closing because of error with database");
            return;
        }
        
        try{
            Thread udpServer=new UDPServer(dburl);
            int port=Integer.parseInt(args[0]);
            Thread httpServer=new HTTPServer(port);
            
            udpServer.start();
            httpServer.start();
        }catch(IOException ex){
           System.out.println("Closing because of error with server");
           return; 
        
        }
        
    }
    
}
