import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class Main {

public static void main(String[] args) {
        final int PORT = 42069; //<---- DONT TOUCH WILL BREAK EVERYTHING
        
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Load balancer started. Listening on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                //still needs doing... probs best to handle client connection in new thread.
                new Thread(new LoadBalancerHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
