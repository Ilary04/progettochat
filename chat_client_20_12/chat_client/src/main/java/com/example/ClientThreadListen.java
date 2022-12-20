package com.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThreadListen extends Thread
{

    private Socket socket;
    private BufferedReader input;

    // construttore usato per fare l'ascolto nel caso del chat pubblico
    public ClientThreadListen(Socket s) throws IOException 
    {
        this.socket = s;
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    @Override
    public void run()
    {
        try
        {
            while(true)
            {
                String m = input.readLine();
                // stampa del messaggio
                System.out.println(m);
            }       
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    
    public void clear() 
    {
        input = null;
        socket = null;
        this.stop();
        
    }
}
