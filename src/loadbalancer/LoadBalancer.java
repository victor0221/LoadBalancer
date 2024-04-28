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

    private static void roundRobin(Job job, PromptHandler pm) {
        if (!_nodes.isEmpty()) {
            WorkerNodeTemplate workerNode = _nodes.remove(0);
            pm.handlePrompt("roundRobin", job.getJobTime(), job.getJobName(), workerNode.getNodeName());
            sendJobToWorkerNode(workerNode, job, pm);
            _nodes.add(workerNode);
        } else {
            pm.handlePrompt("noNodes", 0, null, null);
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
            pm.handlePrompt("failedJob", 0, job.getJobName(), workerNode.getNodeName());
        }
    }

    //helper fucntions
    private static Config configDataCapture(PromptHandler pm) {
        pm.handlePrompt("projectHeader", 0, null, null);
        Scanner hostCap = new Scanner(System.in);
        pm.handlePrompt("host", 0, null, null);
        String activeHost = hostCap.nextLine();
        Scanner portCap = new Scanner(System.in);
        pm.handlePrompt("port", 0, null, null);
        int activePort = portCap.nextInt();
        pm.handlePrompt("nodeHeader", 0, null, null);
        Scanner nodeCountCap = new Scanner(System.in);
        pm.handlePrompt("nodeAmount", 0, null, null);
        int activeNodeCount = nodeCountCap.nextInt();
        for (int i = 0; i < activeNodeCount; i++) {
            Scanner nodeNameCap = new Scanner(System.in);
            pm.handlePrompt("nodeName", 0, null, null);
            String activeNodeName = nodeNameCap.nextLine();
            Scanner nodeHostCap = new Scanner(System.in);
            pm.handlePrompt("nodeHost", 0, null, null);
            String activeNodeHost = nodeHostCap.nextLine();
            Scanner nodePortCap = new Scanner(System.in);
            pm.handlePrompt("nodePort", 0, null, null);
            int activeNodePort = nodePortCap.nextInt();
            _nodes.add(new WorkerNodeTemplate(activeNodeName, activeNodeHost, activeNodePort));
            pm.handlePrompt("nodeSuccess", 0, null, null);

        }
        return new Config(activePort, activeHost);
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
                        roundRobin(job, pm);
                        break;
                    case "JOB_COMPLETION": // Worker sends this when job is completed
                        String completionMessage = inputStream.readUTF();
                        pm.handlePrompt("jobCompleted", 0, completionMessage, null);
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

}
