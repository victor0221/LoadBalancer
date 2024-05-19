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
import java.util.Comparator;
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
    public void testWeightedRoundRobin() {
    _nodes.add(new WorkerNodeTemplate("node1", "localhost", 12345, 10));
        
    if(weightedRoundRobin()){
        
    }else{
        fail("fails");
    }
        
    }
    
    @Test
    public void testMatchNodeCapacity() {
        matchNodeCapacity("node1", "localhost", 12345, 20);
        assertEquals("Capacity should be updated to 20", 20, _nodes.get(0).getNodeCapacity());
    }
    
        @Test
    public void TestHandleMessageRegistered() {
    if(mockHandleIncomingMessages("REGISTER") == "registered"){
        
    }else{
        fail("registration does not work");
    }  
    }
    
    @Test
    public void TestHandleMessageNODECAPACITY() {

        if (mockHandleIncomingMessages("NODE_CAPACITY") == "node capacity matched") {

        } else {
            fail("capacity not incremented");
        }

    }
    
    @Test
    public void TestHandleMessageADDTOQUEUE() {

        
    if(mockHandleIncomingMessages("ADD_TO_JOB_QUEUE") == "added"){
        
    }else{
        fail("job does not get added");
    }
        
        
    }
    
    @Test
    public void TestHandleMessageJOBCOMPLETION() {

        if (mockHandleIncomingMessages("JOB_COMPLETION") == "node capacity incremented") {

        } else {
            fail("capacity not incremented");
        }

    }
    
    @Test
    public void testIncrementNodeCapacity() {
        WorkerNodeTemplate node = new WorkerNodeTemplate("node2", "localhost", 2000, 10);
        _nodes.add(node);
        incrementNodeCapacity("node2", "localhost", 2000);
        assertEquals("Node capacity should be incremented", 11, node.getNodeCapacity());
    }

    @Test
    public void testDecrementNodeCapacity() {
        WorkerNodeTemplate node = new WorkerNodeTemplate("node3", "localhost", 3000, 10);
        _nodes.add(node);
        decrementNodeCapacity("node3", "localhost", 3000);
        assertEquals("Node capacity should be decremented", 9, node.getNodeCapacity());
    }
    
    public static int mockJobQueueProcess() {

        synchronized (_queuedJobs) {
            _nodes.add(new WorkerNodeTemplate("testNode", "localhost", 12345, 10));
            Job mockJob = new Job("job1", 5000);
            Boolean sendJobToWorkerNode = false;
            _queuedJobs.add(mockJob);
            assertEquals("job1", _queuedJobs.get(0).getJobName());
            assertEquals(5000, _queuedJobs.get(0).getJobTime());
            if (!_queuedJobs.isEmpty()) {
                Job job = _queuedJobs.get(0);
                if (!_nodes.isEmpty()) {
                    _nodes.sort(Comparator.comparingInt(WorkerNodeTemplate::getNodeCapacity).reversed());
                    WorkerNodeTemplate workerNode = _nodes.remove(0);
                    if (!(workerNode.getNodeCapacity() <= 0)) {
                        _queuedJobs.remove(0);
                        sendJobToWorkerNode = true;
                    }
                }
            }
        }
        return _queuedJobs.size();
    }
    
    private boolean mockIsNodeRegistered(String host, int port) {
        return _nodes.stream().anyMatch(node -> node.getNodeHost().equals(host) && node.getNodePort() == port);
    }
    
    private static Boolean weightedRoundRobin() {
        synchronized (_nodes) {
            if (!_nodes.isEmpty()) {
                _nodes.sort(Comparator.comparingInt(WorkerNodeTemplate::getNodeCapacity).reversed());
                WorkerNodeTemplate workerNode = _nodes.remove(0);
                _nodes.add(workerNode);
                return true;
            } else {
                return false;
            }
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
                case "NODE_CAPACITY":
                    matchNodeCapacity("node1", "localhost", 12345, 5);
                    return ("node capacity matched");
                case "ADD_TO_JOB_QUEUE":
                    Job queuedJob = new Job("job1", 5000);
                    return "added";
                case "JOB_COMPLETION":
                    incrementNodeCapacity("node1", "localhost", 12345);
                    return "node capacity incremented";
                default:
                    break;
            }
        }
    }

    private static void matchNodeCapacity(String nodeName, String nodeHost, int nodePort, int nodeCapacity) {
        for (WorkerNodeTemplate node : _nodes) {
            if (node.getNodeName().equals(nodeName) && node.getNodeHost().equals(nodeHost) && node.getNodePort() == nodePort) {
                node.setNodeCapacity(nodeCapacity);
                return;
            }
        }
    }

    private void incrementNodeCapacity(String nodeName, String nodeHost, int nodePort) {
        synchronized (_nodes) {
            for (WorkerNodeTemplate node : _nodes) {
                if (node.getNodeName().equals(nodeName) && node.getNodeHost().equals(nodeHost) && node.getNodePort() == nodePort) {
                    node.incrementNodeCapacity();
                }
            }
        }
    }

    private void decrementNodeCapacity(String nodeName, String nodeHost, int nodePort) {
        synchronized (_nodes) {
            for (WorkerNodeTemplate node : _nodes) {
                if (node.getNodeName().equals(nodeName) && node.getNodeHost().equals(nodeHost) && node.getNodePort() == nodePort) {
                    node.decrementNodeCapacity();
                }
            }
        }
    }
}


