/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.Serializable;

/**
 *
 * @author usula
 */
public class Job implements IJob, Serializable{
    
    private int _time;
    private String _name;
    
    public Job(String name, int time){
        _time = time;
        _name = name;
    }
    
    public int getJobTime(){
        return _time;
    }
    public String getJobName(){
        return _name;
    }
    
    //not sure how useful this will be but does not hurt to have it in case.
    public Object[] getJob(){
        return new Object[]{_name, _time};
    }
    public void setJobTime(int time){
        _time = time;
    }
    public void setJobName(String name){
        _name = name;
    }
    public void setJob(String name, int time){
        _name = name;
        _time = time;
    }
    
}
