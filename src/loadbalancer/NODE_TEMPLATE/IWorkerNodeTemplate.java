package loadbalancer.NODE_TEMPLATE;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */

/**
 *
 * @author usula
 */
public interface IWorkerNodeTemplate {
    public String getNodeName();
    public String getNodeHost();
    public int getNodePort();
    public int getNodeCapacity();
    public void setNodeName(String nodeName);
    public void setNodeHost(String nodeHost);
    public void setNodePort(int nodePort);
    public void setNodeCapacity(int nodeCapacity);
    public void incrementNodeCapacity();
    public void decrementNodeCapacity();
}
