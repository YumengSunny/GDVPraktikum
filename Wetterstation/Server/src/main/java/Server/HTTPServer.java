/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;


import com.sun.net.httpserver.Headers;
import java.io.IOException;  
import java.net.InetSocketAddress;  
import com.sun.net.httpserver.HttpServer;  
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.spi.HttpServerProvider;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
/*
 * a simple static http server
*/
public class HTTPServer extends Thread {
    
    private ServerSocket socket;
    private int port;
    private String dburl;
    
    public HTTPServer(int port) throws IOException {
        
        this.port = port;
        this.socket = new ServerSocket(this.port);
    }
    
    private String writeHttpHeader(String status, String length) {
        String httpHeader = "HTTP/1.1 " + status + "\r\n";
        httpHeader += "Content-Length: " + length + "\r\n";
        httpHeader += "Access-Control-Allow-Origin: *\r\n";
        httpHeader += "Content-Type: application/json\r\n\r\n";
                
        return httpHeader;
    }
    
    private void restGetSensorValues(Socket connection, DataOutputStream httpOut, HTTPRequest httpReq) throws IOException{
        try{
            String sensortype=httpReq.path.split("/")[2].replaceAll("%20"," ");
            Connection dbconn=DriverManager.getConnection(this.dburl);
            String sql="select Top 3 from (select * from sensors where sensortype='"+sensortype+"' oder by id desc)";
            Statement stmt=dbconn.createStatement();
            ResultSet rs=stmt.executeQuery(sql);
            
            JSONArray json=new JSONArray();
            while(rs.next()){
                JSONObject record=new JSONObject();
                record.put("name", rs.getString("sensortype"));
                record.put("value", rs.getInt("value"));
                record.put("timestamp",rs.getString("time"));
                json.put(record);
            }
            dbconn.close();
            
            if(json.length()==0){
                String header = this.writeHttpHeader("404 Not Found","0");
                httpOut.writeBytes(header);
                return;
            }else{
                String body=json.toString();
                String header=this.writeHttpHeader("200 Ok", Integer.toString(body.length()));
                httpOut.writeBytes(header.concat(body));
                return;
            }
            
        }catch(Exception ex){
           String header = this.writeHttpHeader("500 Internal Server Error", "0");
            httpOut.writeBytes(header);
            return;
        }
    }
    
    private void restGetLastSensorValues(Socket connection, DataOutputStream httpOut, HTTPRequest httpReq) throws IOException{
        try{
            
            
            /*Connection dbconn=DriverManager.getConnection(this.dburl);
            String sql="select * from sensorvalues where id in (select max(id) from sensorvalues group by sensortype)";
            Statement stmt=dbconn.createStatement();
            ResultSet rs=stmt.executeQuery(sql);
            
            JSONArray json=new JSONArray();
            while(rs.next()){
                JSONObject record=new JSONObject();
                record.put("name", rs.getString("sensortype"));
                record.put("value", rs.getInt("value"));
                record.put("timestamp",rs.getString("time"));
                json.put(record);
            }
            dbconn.close();*/
            JSONArray json=new JSONArray();
            if(json.length()==0){
                String header = this.writeHttpHeader("404 Not Found","0");
                httpOut.writeBytes(header);
                return;
            }else{
                String body=json.toString();
                String header=this.writeHttpHeader("200 Ok", Integer.toString(body.length()));
                httpOut.writeBytes(header.concat(body));
                return;
            }
            
        }catch(Exception ex){
           String header = this.writeHttpHeader("500 Internal Server Error", "0");
            httpOut.writeBytes(header);
            return;
        }
        
    }
   

    @Override
    public void run() {
       System.out.println("API server starts");
       boolean error=false;
       while(!error){
           try{
               Socket connection=this.socket.accept();
               BufferedReader httpIn = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               DataOutputStream httpOut = new DataOutputStream(connection.getOutputStream());
               HTTPRequest httpReq = new HTTPRequest(httpIn);
               if(httpReq.method.equals("GET")){
                   
                   if(httpReq.path.equals("/sensors/")){
                       this.restGetLastSensorValues(connection, httpOut, httpReq);
                   }else if((boolean)Pattern.matches("\\/sensors\\/.*\\/$",httpReq.path)){
                       this.restGetSensorValues(connection, httpOut, httpReq);
                   }else{
                       String header = this.writeHttpHeader("404 Not Found", "0");
                        httpOut.writeBytes(header);
                   }
                 
               }else{
                   String header = this.writeHttpHeader("501 Not Implemented", "0");
                   httpOut.writeBytes(header);
               }
               
               connection.close();
               
           }catch(IOException ex)
           {
               error=true;
           }
       }
    }
    
   
}
