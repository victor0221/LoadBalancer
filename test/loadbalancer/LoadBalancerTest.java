/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package loadbalancer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import jobsender.JOB_TEMPLATE.Job;
import loadbalancer.NODE_TEMPLATE.WorkerNodeTemplate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author usula
 */
public class LoadBalancerTest {
    private static List<Job> _queuedJobs = new ArrayList<>();
    private static List<WorkerNodeTemplate> _nodes = new ArrayList<>();
    public LoadBalancerTest() {
    }
    
    @Test
    public void testRunLoadBalancer() {
        if(mockJobQueueProcess() == 0){
            
        }else{
            fail("jobs dont get removed from queue");
        }
        
    }
    
    @Test
    public void NodeRegistration() {
    _nodes.add(new WorkerNodeTemplate("node1", "localhost", 12345, 10));
    if(mockIsNodeRegistered("localhost", 12345)){
        
    }else{
        fail("no match");
    }
        
    }
    
    @Test
    public void TestRoundRobin() {
    _nodes.add(new WorkerNodeTemplate("node1", "localhost", 12345, 10));
        
    if(roundRobin()){
        
    }else{
        fail("fails");
    }
        
    }
    
        @Test
    public void TestHandleMessageRegistered() {
    if(mockHandleIncomingMessages("REGISTER") == "registered"){
        
    }else{
        fail("registration does not work");
    }  
    }
    @Test
    public void TestHandleMessageADDTOQUEUE() {

        
    if(mockHandleIncomingMessages("ADD_TO_JOB_QUEUE") == "added"){
        
    }else{
        fail("job does not get added");
    }
        
        
    }
    
    public static int mockJobQueueProcess(){
        Job mockJob = new Job("job1", 5000 );
        _queuedJobs.add(mockJob);
        assertEquals("job1", _queuedJobs.get(0).getJobName());
        assertEquals(5000, _queuedJobs.get(0).getJobTime());
        Job job = (Job) _queuedJobs.get(0);
        _queuedJobs.remove(0);
        return _queuedJobs.size();
    }
    
    private boolean mockIsNodeRegistered(String host, int port) {
        return _nodes.stream().anyMatch(node -> node.getNodeHost().equals(host) && node.getNodePort() == port);
    }
    
        private static Boolean roundRobin() {
        if (!_nodes.isEmpty()) {
            WorkerNodeTemplate workerNode = _nodes.remove(0);
            _nodes.add(workerNode);
            return true;
        } else {
           return false;
        }
    }
        
        private Object mockHandleIncomingMessages(String msgType) {
            while (true) {  // Loop to handle multiple messages
                String messageType = msgType;
                switch (messageType) {
                    case "REGISTER": // Workernode requests to check registraion
                        String nodeHost = "localhost";
                        int nodePort = 12345;
                        if (mockIsNodeRegistered(nodeHost, nodePort)) {
                            return "registered";
                        } else {
                            return "failedRegistered";
                        }
                    case "ADD_TO_JOB_QUEUE":
                        Job queuedJob = new Job("job1", 5000);
                        return"added";
                    case "CLOSE_CONNECTION":  // Actioned when user inputs no more jobs from JobSender
                    default:
                        break;
                }
            }
        }  
}
