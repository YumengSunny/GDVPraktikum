/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author admin
 */
public class UDPServer extends Thread{
    
    private int port;
    private DatagramSocket socket;
    private String dburl;
    
    
    public UDPServer (String dburl) throws SocketException{
        
        this.dburl=dburl;
        this.socket=new DatagramSocket(this.port);
    }
    
    public void listen() throws IOException{
        byte[] received=new byte[1024];
        while(true){
            DatagramPacket rec=new DatagramPacket(received,received.length);
            this.socket.receive(rec);
            this.port=rec.getPort();
            this.receivedMessage(new String(rec.getData(),0,rec.getLength()),this.port,rec.getAddress().toString());
        }
    }
    
    public void receivedMessage(String msg, int port, String ip){
        System.out.println(msg);
        try{
            System.out.println(msg+";"+port+";"+ip);
            String sensortype=msg.substring(msg.indexOf("[")+1,msg.lastIndexOf("]"));
            int value=Integer.parseInt(msg.split(" ")[1]);
            
            Connection conn=DriverManager.getConnection(this.dburl);
            String sql="INSERT INTO sensors(sensortype, value) VALUES(?, ?)";
            PreparedStatement stmt=conn.prepareStatement(sql);
            stmt.setString(1,sensortype);
            stmt.setInt(2,value);
            stmt.executeUpdate();
            conn.close();
        }catch(SQLException ex){
             System.out.println(ex.getMessage()); 
        }
    }
    
    @Override
    public void run(){
      try {
            this.listen();
        } catch (IOException ex) {
            return;
        }
    }

    

    
}

