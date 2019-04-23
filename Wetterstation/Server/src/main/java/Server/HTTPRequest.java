/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author admin
 */
class HTTPRequest {
     //used in rest api
    public String path;
    public String method;
   
    
    //additional
    public String protocolversion;
    public String cache;
    public String connection;
    public String useragent;
    public String accept;
    
    public HTTPRequest(BufferedReader httpIn) throws IOException {
        String line = httpIn.readLine();
        boolean firstrow = true;
        while(line != null && !line.equals("")) {
            
            //System.out.println(line);
            if(firstrow) {
                firstrow = false;
                
                this.method = line.split(" ")[0];
                this.protocolversion = line.split(" ")[2];
                
                this.path = line.split(" ")[1]; 
                
            } else {
                int seperatorIndex = line.indexOf(":");
                if(seperatorIndex != -1) {
                    String key = line.substring(0, seperatorIndex);
                    String value = line.substring(seperatorIndex +1);
                    
                    switch(key) {
                        case "Connection":
                            this.connection = value;
                            break;
                        case "User-Agent":
                            this.useragent = value;
                            break;
                        case "Accept":
                            this.accept = value;
                            break;
                        case "Cache-Control":
                            this.cache = value;
                            break;
                    }
                }
            }
            
            line = httpIn.readLine();
        }
        
    }
    
    
    
}
