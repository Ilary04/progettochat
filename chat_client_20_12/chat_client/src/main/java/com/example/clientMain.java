package com.example;

import java.io.IOException;
import java.net.UnknownHostException;

public class clientMain {

    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException 
    {
        Client c = new Client();  
        c.execute();
    }
    
}
