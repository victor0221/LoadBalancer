import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
public class Main {

public static void main(String[] args) throws ClassNotFoundException{
        Scanner hostCap = new Scanner(System.in);
        System.out.println("Enter your host number (e.g. 'localhost'): ");
        String activeHost = hostCap.nextLine();
        Scanner portCap = new Scanner(System.in);
        System.out.println("Enter port number: ");
        int activePort = portCap.nextInt();
        Config config = new Config(activePort,activeHost);
        
        try {
            ServerSocket serverSocket = new ServerSocket(config.getPort());
            System.out.println("Load balancer (server) running, listening on port " + config.getPort());
            
            Socket clientSocket = serverSocket.accept();
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            int job = (int) inputStream.readObject();
            System.out.println("Recived Job " + job + "ms from JobSender");  
            clientSocket.close();
            
            while (true) {
               
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
