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

    //default constructor
    public WorkerNodeTemplate() {
    }
    
    public WorkerNodeTemplate(String nodeName, String nodeHost, int nodePort) {
        _nodeName = nodeName;
        _nodeHost = nodeHost;
        _nodePort = nodePort;
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

    public void setNodeName(String nodeName) {
        _nodeName = nodeName;
    }

    public void setNodeHost(String nodeHost) {
        _nodeHost = nodeHost;
    }

    public void setNodePort(int nodePort) {
        _nodePort = nodePort;
    }
}
