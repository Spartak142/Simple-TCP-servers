/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpclient;

import java.net.*;
import java.io.*;

public class TCPClient {

    public static String askServer(String hostname, int port, String ToServer) throws IOException {
        String response;
        StringBuilder sb = new StringBuilder();
        Socket clientSocket = new Socket(hostname, port);
        DataOutputStream outToS = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromS = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        outToS.writeBytes(ToServer + '\n');
        clientSocket.setSoTimeout(10000);

        while ((response = inFromS.readLine()) != null) {
            sb.append(response + '\n');
            if (sb.length() > 10000) {
                break;
            }
        }

        clientSocket.close();
        return sb.toString();
    }

    public static String askServer(String hostname, int port) throws IOException {
        return askServer(hostname, port, "");
    }
}
