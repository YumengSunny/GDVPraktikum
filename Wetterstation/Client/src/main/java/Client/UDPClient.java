/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

/**
 *
 * @author admin
 */
public class UDPClient {
    
    
    public static void main(String[] args) throws Exception{
        
       String ip=args[0];
       int port =Integer.parseInt(args[1]);
       String sensortype=args[2];
      

       
       System.out.println("Sensor starts: "+sensortype);
       
       
       while(true){
           try{
               Thread.sleep(1000);
               int sensorvalue;
               sensorvalue=(int) (Math.random() * 30);
               
               String message="["+sensortype+"] "+Integer.toString(sensorvalue);
               InetAddress IPaddress=InetAddress.getByName(ip);
               DatagramSocket socket=new DatagramSocket();
               byte[] buf=new byte[1024];
               buf=message.getBytes();
              
               DatagramPacket packet=new DatagramPacket(buf,buf.length,IPaddress,port); 
               socket.send(packet);
               socket.close();
               
               System.out.println(message);
               
            } catch (InterruptedException ex) {
                return;
            } catch (IOException ex) {
                return;
            }
       }
    }
    
    public void sendMessage(){
        
    }
}

