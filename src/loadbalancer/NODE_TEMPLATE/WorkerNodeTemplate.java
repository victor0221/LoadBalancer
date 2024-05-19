package loadbalancer.NODE_TEMPLATE;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author usula
 */
public class WorkerNodeTemplate implements IWorkerNodeTemplate {
    private String _nodeName;
    private String _nodeHost;
    private int _nodePort;
    private int _nodeCapacity;

    //default constructor
    public WorkerNodeTemplate() {
    }
    
    public WorkerNodeTemplate(String nodeName, String nodeHost, int nodePort, int nodeCapacity) {
        _nodeName = nodeName;
        _nodeHost = nodeHost;
        _nodePort = nodePort;
        _nodeCapacity = nodeCapacity;
    }

    public String getNodeName() {
        return _nodeName;
    }

    public String getNodeHost() {
        return _nodeHost;
    }

    public int getNodePort() {
        return _nodePort;
    }
    
    public int getNodeCapacity() {
        return _nodeCapacity;
    }

    public void setNodeName(String nodeName) {
        _nodeName = nodeName;
    }

    public void setNodeHost(String nodeHost) {
        _nodeHost = nodeHost;
    }

    public void setNodePort(int nodePort) {
        _nodePort = nodePort;
    }
    
    public void setNodeCapacity(int nodeCapacity) {
        _nodeCapacity = nodeCapacity;
    }

    public void incrementNodeCapacity() {
        _nodeCapacity++;
    }

    public void decrementNodeCapacity() {
        _nodeCapacity--;
        if (_nodeCapacity < 0) {
            _nodeCapacity = 0;
        }
    }
}
