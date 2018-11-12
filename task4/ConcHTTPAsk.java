import java.net.*;
import java.io.*;

public class ConcHTTPAsk implements Runnable {

    Socket clientSocket;

    public ConcHTTPAsk(Socket s) {
        clientSocket = s;
    }

    @Override
    public void run() {
        String guestLine;
        String[] requests;
        String[] getrequest;
        String get;
        String hostname;
        String query;
        String clientPort;
        try {
            BufferedReader startConversation = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            DataOutputStream keepItGoing = new DataOutputStream(clientSocket.getOutputStream());
            System.err.println("Accepted connection\r\n");
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
                } else {
                    //http://localhost:8080/ask?host=time.gov&port=13&string=asdads
                    requests = guestLine.split("/ask");
                    get = "GET   " + requests[1];
                    System.out.println("The get request is: " + get);
                    getrequest = requests[1].split("(host=)|(&port=)|(&string=)|( )");
                    hostname = getrequest[1];
                    System.out.println("The hostname is: " + hostname);
                    clientPort = getrequest[2];
                    System.out.println("The port number is: " + clientPort);
                    if (get.contains("string=")) {
                        query = getrequest[3];
                        System.out.println("The query is: " + query);
                        keepItGoing.writeBytes("HTTP/1.1 200 \r\n");
                        keepItGoing.writeBytes("Content-Type: text/plain \r\n");
                        keepItGoing.writeBytes("Connection: close \r\n");
                        keepItGoing.writeBytes("\r\n");
                        if (askServer(hostname, Integer.parseInt(clientPort), query).equals("not found")) {
                            keepItGoing.writeBytes("HTTP/1.1 404 Page not found \r\n");
                            keepItGoing.writeBytes("Content-Type: text/plain \r\n");
                            keepItGoing.writeBytes("Connection: close \r\n");
                            keepItGoing.writeBytes("\r\n");
                        } else {
                            keepItGoing.writeBytes(askServer(hostname, Integer.parseInt(clientPort), query) + "\r\n");
                        }
                    } else {
                        keepItGoing.writeBytes("HTTP/1.1 200 \r\n");
                        keepItGoing.writeBytes("Content-Type: text/plain \r\n");
                        keepItGoing.writeBytes("Connection: close \r\n");
                        keepItGoing.writeBytes("\r\n");
                        keepItGoing.writeBytes(askServer(hostname, Integer.parseInt(clientPort)) + "\r\n");
                    }
                    break;
                }
            }
            keepItGoing.close();
            startConversation.close();
            clientSocket.close();
        } catch (IOException | NumberFormatException ex) {
            System.err.println("Connection Denied\r\n");
        }

    }

    public static void main(String[] args) throws IOException {
        try {
            //Port and server socket
            int port = Integer.parseInt(args[0]);
            ServerSocket welcomeSocket = new ServerSocket(port);

            while (true) {
                Socket connectionSocket = welcomeSocket.accept();

                //Client socket
                Runnable r = new ConcHTTPAsk(connectionSocket);
                new Thread(r).start();

            }
        } catch (IOException ex) {
            System.err.println("Something went wrong");
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

    public static String askServer(String hostname, int port) throws IOException {
        return askServer(hostname, port, " ");
    }
}
