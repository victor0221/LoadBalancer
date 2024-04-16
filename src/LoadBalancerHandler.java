import java.io.IOException;
import java.net.Socket;

public class LoadBalancerHandler implements Runnable {
    private Socket _clientSocket;

    public LoadBalancerHandler(Socket clientSocket) {
        _clientSocket = clientSocket;
    }

    @Override
    public void run() {
        //we need to implement client requests and create the load balancer logic here.
        //try catch probabaly the most clean way of doing this.
        //try {
        //
        //}catch (IOException e) {
        //e.printStackTrace();
        //}
    }
}
