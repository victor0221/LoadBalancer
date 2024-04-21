import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import jobsender.Job;

public class Main {
    
private static List<WorkerNodeTemplate> _nodes = new ArrayList<>();

public static void main(String[] args) throws ClassNotFoundException{
        PromptHandler pm = new PromptHandler();
        Config config = configDataCapture(pm);
        
    try {
        //open to jobs from job sender.
        ServerSocket serverSocket = new ServerSocket(config.getPort());
        pm.handlePrompt("lbRunning", config.getPort(), null);
        while (true) {
            //capture jobs from job sedner
            Socket clientSocket = serverSocket.accept();
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            try {
                while (true) {
                    Job job = (Job) inputStream.readObject();
                    //send out incoming jobs from job sender to worker nodes.
                    roundRobin(job, pm);
                }
            } catch (IOException | ClassNotFoundException e) {
                pm.handlePrompt("noMoreJobs", 0, null);
            } finally {
                clientSocket.close();
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void roundRobin(Job job, PromptHandler pm) {
        if (!_nodes.isEmpty()) {
            WorkerNodeTemplate workerNode = _nodes.remove(0);
            pm.handlePrompt("roundRobin", job.getJobTime(), workerNode.getNodeName());
            sendJobToWorkerNode(workerNode, job, pm);
            _nodes.add(workerNode); 
        } else {
            pm.handlePrompt("noNodes", 0, null);
        }
    }

    private static void sendJobToWorkerNode(WorkerNodeTemplate workerNode, Job job, PromptHandler pm) {
        try {
            //establish connection with selected node and send job (time)
            Socket socket = new Socket(workerNode.getNodeHost(), workerNode.getNodePort());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(job);
            socket.close();
        } catch (IOException e) {
            pm.handlePrompt("failedJob", 0, workerNode.getNodeName());
        }
    }
    
    
    //helper fucntions
    private static Config configDataCapture(PromptHandler pm){
        pm.handlePrompt("projectHeader", 0, null);
        Scanner hostCap = new Scanner(System.in);
        pm.handlePrompt("host", 0, null);
        String activeHost = hostCap.nextLine();
        Scanner portCap = new Scanner(System.in);
        pm.handlePrompt("port", 0, null);
        int activePort = portCap.nextInt();
        pm.handlePrompt("nodeHeader", 0, null);
        Scanner nodeCountCap = new Scanner(System.in);
        pm.handlePrompt("nodeAmount", 0, null);
        int activeNodeCount = nodeCountCap.nextInt();
        for(int i = 0; i < activeNodeCount; i++ ){     
        Scanner nodeNameCap = new Scanner(System.in);
        pm.handlePrompt("nodeName", 0, null);
        String activeNodeName = nodeNameCap.nextLine();  
        Scanner nodeHostCap = new Scanner(System.in);
        pm.handlePrompt("nodeHost", 0, null);
        String activeNodeHost = nodeHostCap.nextLine(); 
        Scanner nodePortCap = new Scanner(System.in);
        pm.handlePrompt("nodePort", 0, null);
        int activeNodePort = nodePortCap.nextInt();
        _nodes.add(new WorkerNodeTemplate(activeNodeName, activeNodeHost, activeNodePort));
        pm.handlePrompt("nodeSuccess", 0, null);
        
        }
        return new Config(activePort,activeHost);
    }
}
