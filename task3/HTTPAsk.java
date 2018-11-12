
import java.net.*;
import java.io.*;

public class HTTPAsk {

    public static void main(String[] args) throws IOException {
        int port;
        String[] requests;
        String[] getrequest;
        int clientPort1;
        String guestLine;
        String get;
        String hostname;
        String query;
        String clientPort;
        port = Integer.parseInt(args[0]);
        ServerSocket openDoor = new ServerSocket(port);
        System.err.println("Started server on port " + port + "\r\n");

        while (true) {
            Socket hall = openDoor.accept();
            System.err.println("Accepted connection\r\n");
            BufferedReader startConversation = new BufferedReader(new InputStreamReader(hall.getInputStream()));
            DataOutputStream keepItGoing = new DataOutputStream(hall.getOutputStream());
            while ((guestLine = startConversation.readLine()) != null) {
                if (guestLine.length() == 0) {
                    break;
                } 
                if (!guestLine.contains("ask")) {
                    keepItGoing.writeBytes("HTTP/1.1 400 Bad Request \r\n"); 
                    keepItGoing.writeBytes("Content-Type: text/plain \r\n"); 
                    keepItGoing.writeBytes("Connection: close \r\n"); 
                    keepItGoing.writeBytes("\r\n"); 
                    break;
                } 
                else {
                    //http://localhost:8080/ask?host=time.gov&port=13&string=asdads
                    requests = guestLine.split("/ask");
                    get = "GET   " + requests[1];
                    System.out.println("The get request is: " + get);
                    getrequest = requests[1].split("(host=)|(&port=)|(&string=)|( )");
                    hostname = getrequest[1];
                    System.out.println("The hostname is: " + hostname);
                    clientPort = getrequest[2];
                    System.out.println("The port number is: " + clientPort);
                    if (get.contains("string=")){
                    query = getrequest[3];
                    System.out.println("The query is: " + query);
                    keepItGoing.writeBytes("HTTP/1.1 200 \r\n"); 
                    keepItGoing.writeBytes("Content-Type: text/plain \r\n"); 
                    keepItGoing.writeBytes("Connection: close \r\n"); 
                    keepItGoing.writeBytes("\r\n");   
                    if (askServer(hostname, Integer.parseInt(clientPort), query).equals("not found")){
                    keepItGoing.writeBytes("HTTP/1.1 404 Page not found \r\n"); 
                    keepItGoing.writeBytes("Content-Type: text/plain \r\n"); 
                    keepItGoing.writeBytes("Connection: close \r\n"); 
                    keepItGoing.writeBytes("\r\n"); 
                    }
                    else
                    keepItGoing.writeBytes(askServer(hostname, Integer.parseInt(clientPort), query)+ "\r\n"); }
                    else{
                   keepItGoing.writeBytes("HTTP/1.1 200 \r\n"); 
                    keepItGoing.writeBytes("Content-Type: text/plain \r\n"); 
                    keepItGoing.writeBytes("Connection: close \r\n"); 
                    keepItGoing.writeBytes("\r\n"); 
                    keepItGoing.writeBytes(askServer(hostname, Integer.parseInt(clientPort))+ "\r\n");
                    }
                break;
                } 
            } 
            keepItGoing.close();
            startConversation.close();
            hall.close();
        }
    }
    
    
    public static String askServer(String hostname, int port, String ToServer) throws IOException {
        String response;
        StringBuilder sb = new StringBuilder();
        try (Socket clientSocket = new Socket(hostname, port)) {
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
        } catch (UnknownHostException e) {

            return "not found";

        }
        return sb.toString();
    }
    public static String askServer(String hostname, int port) throws  IOException {
    	return askServer(hostname, port, " ");
    }
}

