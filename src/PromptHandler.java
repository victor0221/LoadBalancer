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
            case "noMoreJobs":
                System.out.println("No more jobs from JobSender.");
                break;
            case "roundRobin":
                System.out.println("Distributing " + optionalString + " with duration " + optionalInt + "ms to " + optionalString2 + " node.");
                break;
            case "noNodes":
                System.out.println("No nodes to handle job.");
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
            case "pressY":
                System.out.println("enter 'y' to view stack:");
                break;
        }
    }
}
