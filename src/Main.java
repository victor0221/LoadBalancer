import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
public class Main {

public static void main(String[] args) {
        Scanner hostCap = new Scanner(System.in);
        System.out.println("Enter your host number (e.g. 'localhost'): ");
        String activeHost = hostCap.nextLine();
        Scanner portCap = new Scanner(System.in);
        System.out.println("Enter port number: ");
        int activePort = portCap.nextInt();
        Config config = new Config(activePort,activeHost);
        
        try {
            System.out.println("Load balancer started. Listening on port " + config.getPort());
            ServerSocket serverSocket = new ServerSocket(config.getPort());
            
            //block below will capture sent data from job sender.
            //FYI BufferedReader is a package specifct to Strings to make it easier for the 
            // program to read it.
            Socket clientSocket = serverSocket.accept();
            BufferedReader r = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String jb = r.readLine();
            System.out.println("jobSender said...: " + jb);
            clientSocket.close();
            
            while (true) {
                //foreshadowing...
                //Socket clientSocket = serverSocket.accept();
               // new Thread(new LoadBalancerHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
