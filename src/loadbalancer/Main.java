package loadbalancer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import jobsender.JOB_TEMPLATE.Job;

public class Main {
   

public static void main(String[] args) throws ClassNotFoundException {
    LoadBalancer lb = new LoadBalancer();
    lb.runLoadBalancer();
    }
}
