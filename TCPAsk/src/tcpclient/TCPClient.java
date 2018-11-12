/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclient;

import java.net.*;
import java.io.*;

public class TCPClient {
    
    public static String askServer(String hostname, int port, String ToServer) throws  IOException {
     String response;
     try(Socket clientSocket = new Socket ("hostname",13)) { DataOutputStream outToS= new DataOutputStream(clientSocket.getOutputStream());
     BufferedReader inFromS= new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));
     outToS.writeBytes(ToServer);
     response=inFromS.readLine();
     return response;}
    catch (Exception IOException){
    return "Didnt work";
    }}

    public static String askServer(String hostname, int port) throws  IOException {
         return "asdf";
    }

    public static void askServer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

