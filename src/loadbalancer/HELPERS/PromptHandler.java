package loadbalancer.HELPERS;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author usula
 */
public class PromptHandler {
    public PromptHandler(){
        
    }
    
    public void handlePrompt(String code, int optionalInt, String optionalString, String optionalString2){
        switch(code){
            case "lbRunning":
                System.out.println("Load balancer running (port: "+optionalInt+")");
                break;
            case "nodeRegistered":
                System.out.println(optionalString + " node registered successfully on " + optionalString2 + ":" + optionalInt);
                break;
            case "unknownNode":
                System.out.println("Unrecognised node " + "'" + optionalString + "'" + " attempted to register.");
                break;                            
            case "noMoreJobs":
                System.out.println("No more jobs from JobSender.");
                break;
            case "weightedRoundRobin":
                System.out.println("Distributing " + optionalString + " with duration " + optionalInt + "ms to " + optionalString2 + " node.");
                break;
            case "noNodes":
                System.out.println("No nodes to handle job.");
                break;
            case "jobCompleted":
                System.out.println(optionalString);
                break;
            case "jobQueued":
                System.out.println("JOB QUEUED: " + optionalString + " with duration " + optionalInt + "ms sent to Job queue.");
                break;
            case "failedJob":
                System.out.println("Failed to send " + optionalString + " to " + optionalString2 + " node.");
                break;
            case "projectHeader":
                System.out.println("---PROJECT CONFIGURATION SECTION---");
                break;
            case "host":
                 System.out.println("Enter your host number (e.g. 'localhost'): ");
                break;
            case "port":
                System.out.println("Enter port number: ");
                break;
            case "nodeCapacity":
                System.out.println("Enter node capacity: ");
                break;
            case "nodeHeader":
                System.out.println("---NODE CONFIGURATION---");
                break;
            case "nodeAmount":
                System.out.println("Enter amount of nodes you want to connect to: ");
                break;
            case "nodeName":
                System.out.println("Enter node name: ");
                break;
            case "nodeHost":
                System.out.println("Enter node host (e.g. 'localhost'):");
                break;
            case "nodePort":
                System.out.println("Enter node port number:");
                break;
            case "nodeSuccess":
                System.out.println("node added!");
                break;
            case "unknownMessage":
                System.out.println("Received an unknown message.");
                break;
            case "socketCloseError":
                System.err.println("Error closing client socket: " + optionalString);
                break;
            case "pressY":
                System.out.println("enter 'y' to view stack:");
                break;
            case "invalidInt":
                System.out.println("Invalid input. Please enter a valid integer.");
                break;
            case "invalidString":
                System.out.println("No input. Please enter something, this cannot be left blank.");
                break;
        }
    }
}
