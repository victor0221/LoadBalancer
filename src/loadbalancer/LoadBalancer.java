package loadbalancer;

import java.io.EOFException;
import loadbalancer.NODE_TEMPLATE.WorkerNodeTemplate;
import loadbalancer.HELPERS.PromptHandler;
import loadbalancer.CONFIG.Config;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import jobsender.JOB_TEMPLATE.Job;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author usula
 */
public class LoadBalancer {

    private static List<WorkerNodeTemplate> _nodes = new ArrayList<>();
    private static List<Job> _queuedJobs = new ArrayList<>();

    public LoadBalancer() {

    }

    public void runLoadBalancer() {
        PromptHandler pm = new PromptHandler();
        Config config = configDataCapture(pm);

        try (ServerSocket serverSocket = new ServerSocket(config.getPort())) {
            pm.handlePrompt("lbRunning", config.getPort(), null, null);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
                new Thread(() -> handleIncomingMessages(clientSocket, inputStream, pm)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void weightedRoundRobin(Job job, PromptHandler pm) {
        synchronized (_nodes) {
            if (!_nodes.isEmpty()) {
                _nodes.sort(Comparator.comparingInt(WorkerNodeTemplate::getNodeCapacity).reversed());
                WorkerNodeTemplate workerNode = _nodes.get(0);
                pm.handlePrompt("weightedRoundRobin", job.getJobTime(), job.getJobName(), workerNode.getNodeName());
                sendJobToWorkerNode(workerNode, job, pm);
            } else {
                pm.handlePrompt("noNodes", 0, null, null);
            }
        }
    }

    private static void sendJobToWorkerNode(WorkerNodeTemplate workerNode, Job job, PromptHandler pm) {
        try {
            //establish connection with selected node and send job (time)
            Socket socket = new Socket(workerNode.getNodeHost(), workerNode.getNodePort());
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(job);
            decrementNodeCapacity(workerNode.getNodeName(), workerNode.getNodeHost(), workerNode.getNodePort());
            socket.close();
        } catch (IOException e) {
            pm.handlePrompt("failedJob", 0, job.getJobName(), workerNode.getNodeName());
            addToJobQueue(job, pm);
        }
    }

    //helper functions
    private static Config configDataCapture(PromptHandler pm) {
        pm.handlePrompt("projectHeader", 0, null, null);
        String activeHost = captureString("host", pm);
        int activePort = captureInt("port", pm);
        pm.handlePrompt("nodeHeader", 0, null, null);
        int activeNodeCount = captureInt("nodeAmount", pm);
        
        for (int i = 0; i < activeNodeCount; i++) {
            String activeNodeName = captureString("nodeName", pm);
            String activeNodeHost = captureString("nodeHost", pm);
            int activeNodePort = captureInt("nodePort", pm);
            int activeNodeCapacity = captureInt("nodeCapacity", pm);
            _nodes.add(new WorkerNodeTemplate(activeNodeName, activeNodeHost, activeNodePort, activeNodeCapacity));
            pm.handlePrompt("nodeSuccess", 0, null, null);
        }
        return new Config(activePort, activeHost);
    }
    
    private static int captureInt(String promptCode, PromptHandler pm) {
        int validInt = 0;
        boolean validInput = false;
        while (!validInput) {
            Scanner scanner = new Scanner(System.in);
            pm.handlePrompt(promptCode, 0, null, null);
            if (scanner.hasNextInt()) {
                validInt = scanner.nextInt();
                validInput = true; 
            } else {
                pm.handlePrompt("invalidInt", 0, null, null);
                scanner.next(); 
            }
        }
        return validInt;
    }
    
    private static String captureString(String promptCode, PromptHandler pm) {
        String validString = "";
        boolean validInput = false;
        while (!validInput) {
            Scanner scanner = new Scanner(System.in);
            pm.handlePrompt(promptCode, 0, null, null);
            validString = scanner.nextLine().trim();
            if (!validString.isEmpty()) {
                validInput = true;
            } else {
                pm.handlePrompt("invalidString", 0, null, null);
            }
        }
        return validString;
    }

    private static void errorHandler(Exception e, PromptHandler pm) {
        pm.handlePrompt("generalErr", 0, null, null);
        Scanner myObj = new Scanner(System.in);
        pm.handlePrompt("pressY", 0, null, null);
        String userInput = myObj.nextLine();
        if ("y".equals(userInput)) {
            e.printStackTrace();
        }
    }

    ;

    private void handleIncomingMessages(Socket clientSocket, ObjectInputStream inputStream, PromptHandler pm) {
        try {
            while (true) {  // Loop to handle multiple messages
                String messageType = inputStream.readUTF();
                switch (messageType) {
                    case "REGISTER": // Workernode requests to check registraion
                        String nodeName = inputStream.readUTF();
                        String nodeHost = inputStream.readUTF();
                        int nodePort = inputStream.readInt();
                        if (isNodeRegistered(nodeHost, nodePort)) {
                            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                            out.writeUTF("REGISTERED");
                            out.flush();
                            pm.handlePrompt("nodeRegistered", nodePort, nodeName, nodeHost);
                        } else {
                            pm.handlePrompt("unknownNode", 0, nodeName, null);
                        }
                        break;
                    case "JOB_SUBMISSION":
                        Job job = (Job) inputStream.readObject();
                        weightedRoundRobin(job, pm);
                        break;
                    case "ADD_TO_JOB_QUEUE":
                        Job queuedJob = (Job) inputStream.readObject();
                        addToJobQueue(queuedJob, pm);
                        break;
                    case "NODE_CAPACITY":
                        String _nodeName = inputStream.readUTF();
                        String _nodeHost = inputStream.readUTF();
                        int _nodePort = inputStream.readInt();
                        int _nodeCapacity = inputStream.readInt();
                        matchNodeCapacity(_nodeName, _nodeHost, _nodePort, _nodeCapacity);
                        new Thread(() -> processQueuedJobs(pm)).start();
                        break;
                    case "JOB_COMPLETION": // Worker sends this when job is completed
                        String compNodeName = inputStream.readUTF();
                        String compNodeHost = inputStream.readUTF();
                        int compNodePort = inputStream.readInt();
                        String completionMessage = inputStream.readUTF();
                        pm.handlePrompt("jobCompleted", 0, completionMessage, null);
                        incrementNodeCapacity(compNodeName, compNodeHost, compNodePort);
                        break;
                    case "CLOSE_CONNECTION":  // Actioned when user inputs no more jobs from JobSender
                        pm.handlePrompt("noMoreJobs", 0, null, null);
                        return;  // Exit the method, this will end the thread and close the socket outside
                    default:
                        pm.handlePrompt("unknownMessageType", 0, null, null);
                        break;
                }
            }
        } catch (EOFException e) {
//            Don't do anything in this case
        } catch (IOException | ClassNotFoundException e) {
            errorHandler(e, pm);
        } finally {
            try {
                clientSocket.close();  // Close the socket here after exiting the loop
            } catch (IOException e) {
                pm.handlePrompt("socketCloseError", 0, e.getMessage(), null);
            }
        }
    }

    private boolean isNodeRegistered(String host, int port) {
        return _nodes.stream().anyMatch(node -> node.getNodeHost().equals(host) && node.getNodePort() == port);
    }
    
    private static void addToJobQueue(Job queuedJob, PromptHandler pm) {
        synchronized (_queuedJobs) {
            _queuedJobs.add(new Job(queuedJob.getJobName(), queuedJob.getJobTime()));
            pm.handlePrompt("jobQueued", queuedJob.getJobTime(), queuedJob.getJobName(), null);
        }
    }
    
    private void processQueuedJobs(PromptHandler pm) {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (_queuedJobs) {
                if (!_queuedJobs.isEmpty()) {
                    Job job = _queuedJobs.get(0);
                    if (!_nodes.isEmpty()) {
                        _nodes.sort(Comparator.comparingInt(WorkerNodeTemplate::getNodeCapacity).reversed());
                        WorkerNodeTemplate workerNode = _nodes.get(0);
                        if (!(workerNode.getNodeCapacity() <= 0)) {
                            pm.handlePrompt("weightedRoundRobin", job.getJobTime(), job.getJobName(), workerNode.getNodeName());
                            _queuedJobs.remove(0);
                            sendJobToWorkerNode(workerNode, job, pm);
                        }
                    }
                }
            }
            try {
                Thread.sleep(1000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
                return;
            }
        }
    }  

    private static void matchNodeCapacity(String nodeName, String nodeHost, int nodePort, int nodeCapacity) {
        // Finding the matching node 
        for (WorkerNodeTemplate node : _nodes) {
            if (node.getNodeName().equals(nodeName) && node.getNodeHost().equals(nodeHost) && node.getNodePort() == nodePort) {
//              Correcting node capacity to be one sent by WN itself.
//              This is done just in case the user mistakenly enters wrong node capacity in LB config.
                node.setNodeCapacity(nodeCapacity);
                return;  
            }
        }
    }
    
    private static void incrementNodeCapacity(String nodeName, String nodeHost, int nodePort) {
        synchronized (_nodes) {
            for (WorkerNodeTemplate node : _nodes) {
                if (node.getNodeName().equals(nodeName) && node.getNodeHost().equals(nodeHost) && node.getNodePort() == nodePort) {
                    node.incrementNodeCapacity();
                    return; 
                }
            }
        }
    }

    private static void decrementNodeCapacity(String nodeName, String nodeHost, int nodePort) {
        synchronized (_nodes) {
            for (WorkerNodeTemplate node : _nodes) {
                if (node.getNodeName().equals(nodeName) && node.getNodeHost().equals(nodeHost) && node.getNodePort() == nodePort) {
                    node.decrementNodeCapacity();
                    return;  
                }
            }
        }
    }
    
}
