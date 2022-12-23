package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {
    private Socket s;
    private PrintWriter pr = null;
    private BufferedReader br = null;
    private String scelta, username;
    private ArrayList<ClientHandler> listaClient;

    public ClientHandler(Socket s, ArrayList<ClientHandler> listaClient) throws IOException 
    {
        //socket per la communicazione
        this.s = s;
        // lista dei client connessi al server
        this.listaClient = listaClient;
        // per parlare
        pr = new PrintWriter(s.getOutputStream(), true);
        // per ascoltare
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public void run() {
        try {
            
            // ricezione del username
            username = br.readLine(); 
            System.out.println("[SERVER] Client connesso:"+ username );

            // ricezione della scelta del menu
            
            while(true)
            {
                scelta= br.readLine();
                switch (Integer.valueOf(scelta))
                {
                    case 1:
                        messaggioPrivato();
                        break;
                    case 2:
                        messaggioPubblico();
                        break;
                    case 3:
                        utenteOnline();
                        break;
                    case 4:
                        uscitaClient();
                        break;
                    default:
                        break;
                }
                if(scelta.equals("4")) { break; }
            }
     
        }
        catch (IOException e)
        {
            e.printStackTrace();
            closeSocket(s);
        }        
    }

    // Metodo che server per la chiusura del socket
    public void closeSocket(Socket s)
    {
        try 
        {
            s.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    //Metodo che serve per mandare i messaggi in broadcast
    public void messaggioPubblico() throws IOException
    {
        while(true)
            {
                String msgbroadcast = br.readLine();
                if(msgbroadcast.equals("exit"))
                {
                    //esce dal
                    break;
                }
                else
                {
                    for (ClientHandler clientHandler : listaClient) 
                    {
                        String msgb = "["+username+"]: "+msgbroadcast;       
                        if(!clientHandler.username.equals(username))
                        {
                            clientHandler.pr.println(msgb);
                        }               
                    }
                }        
            }
    }
    
    //Metodo per inviare il msg privato verso utente B
    public void messaggioPrivato() throws IOException
    {
        // risponde al client con le persone online
        utenteOnline();

        // server si salva il nome dell'utente con cui vuole communicare

        String userB = br.readLine();

        

        // seleziono l'oggetto clientHandler dalla lista
        ClientHandler ch = null;

        for (ClientHandler clientHandler : listaClient) {
            if(clientHandler.username.equals(userB))
            {
                ch = clientHandler;
                break;
            }
        }

        while(true)
        {
            String m = br.readLine();
            m="["+userB+"]:"+m;
            if(!m.equals("exit"))
            {
                // invio del messaggio verso il client B
                ch.pr.println(m);
            }
            else{ break; }
        }
    }
    
    //Metodo che stampa le persone online
    public void utenteOnline()
    {
        pr.println(listaClient.size());
        
        System.out.println(listaClient.size());

        for (int i = 0; i < listaClient.size(); i++)
        {
            // il formato che invio è fatto: "[nomeusername]:messaggio"
            pr.println("["+(i+1)+"] "+listaClient.get(i).username);        
        } 
    }
    
    // Metodo per chiudere il socket
    public void uscitaClient()
    {
        System.out.println("Client "+ username+" uscito dal server!");
        // bisogna levarlo anche dall'array dei clients
        listaClient.remove(this);
        closeSocket(s);
    }


}
