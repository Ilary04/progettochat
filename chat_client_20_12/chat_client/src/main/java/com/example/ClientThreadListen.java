package com.example;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThreadListen extends Thread
{

    private Socket socket;
    private BufferedReader input;

    // Classe che implementa l'ascolto del canale.
    // tutti i messaggi vengono inviati con un prefix
    // che identificano il tipo di messaggio
    // ?b per il broadcast
    // un messaggio boradcast è costruito: b@msg
    // ?p per msg privato
    // un messaggio privato è costruito: p@user@msg

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
                
                if(m.equals("?b")) // ascolto di messaggi di tipo broadcast
                {
                    while(true)
                    {
                        String msg = input.readLine();
                        String[] arrOfStr = msg.split("@", 2);
                        if(arrOfStr[0].equals("b"))
                        {
                            if(arrOfStr[1].equals("exitb")) { break; } 
                            System.out.println(arrOfStr[1]);
                        }
                        
                        
                    }
                    
                }
                else if(m.equals("?p")) // ascolto di messaggi di tipo privato
                {

                    // stampa delle persone online
                    System.out.println("Persone online");
                    String size_s = input.readLine();
                    int size = Integer.valueOf(size_s);
            
                    for (int i = 0; i < size; i++) 
                    {
                        System.out.println(input.readLine());
                    }

                    System.out.println("Inserisci il nome della persona con cui vuoi chattare");
                    System.out.print("altrimenti premi [exit] per uscire: ");

                    
                    String ub = input.readLine();
                    

                    while(true)
                    {
                        //parsing
                        String msg = input.readLine();
                        String[] arrOfStr = msg.split("@", 3);
                        
                        // controllo che utente da cui ricevo sia quello che ho selezionato
                
                        if( arrOfStr[0].equals("p") && arrOfStr[1].equals(ub)) 
                        {
                            if(arrOfStr[2].equals("exit")) { break; }
                            
                            // stampa del messaggio
                            System.out.println("["+ub+"]: "+arrOfStr[2]); 
                        }
                    }

                }
             
                
            }       
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    
    public void clear() 
    {
       this.stop();
        
    }
}
