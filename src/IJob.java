/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */


/**
 *
 * @author usula
 */
public interface IJob {
    public void setJobName(String name);
    public void setJobTime(int time);
    public String getJobName();
    public int getJobTime();
    public void setJob(String name, int time);
    public Object[] getJob();
    
}
