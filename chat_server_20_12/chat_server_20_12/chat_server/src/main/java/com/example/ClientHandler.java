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
                        uscitaClient();
                        break;
                    default:
                        break;
                }
                
                pr.flush();
                
                if(scelta.equals("3")) { break; }
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
            pr.close();
            br.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    /*
     * Metodo che serve per mandare i messaggi in broadcast
     * 1. Communicazione
     *      1.1 il msg viene trasformato in b@msg
     *      2.2 invio del msg a tutti gli utenti online
     */
    
    public void messaggioPubblico() throws IOException
    {
        pr.println("?b");
        while(true)
            {
                String msgbroadcast = br.readLine();
                if(!msgbroadcast.equals("exit"))
                {
                    for (ClientHandler clientHandler : listaClient) 
                    {
                        String msgb = "b@["+username+"]: "+msgbroadcast;       
                        if(!clientHandler.username.equals(username))
                        {
                            clientHandler.pr.println(msgb);
                        }               
                    }
                }
                else
                { 
                    pr.println("b@exitb");
                    break;
                 }        
            }
    }

    /*
     * Metodo per inviare il msg privato verso utente B 
     * 1.seleziona l'utente con cui si vuole communicare
     * 2. Communicazione
     *      2.1 il msg viene trasformato in p@userB@msg
     *      2.2 invio del msg all'utenteB
     */
    public void messaggioPrivato() throws IOException
    {
       
       // risponde al client con le persone online
        pr.println("?p");
                
        utenteOnline();

        // server si salva il nome dell'utente con cui vuole communicare
        String userB = br.readLine();
                
        pr.println(userB);

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
               
            if(!m.equals("exit"))
            {
                m="p@"+username+"@"+m;
                // invio del messaggio verso il client B
                ch.pr.println(m);
            }
            else
            { 
                System.out.println("sono uscito dal chat privata");
                m="p@"+userB+"@exit";
                pr.println(m);
                break;
            }
        }   
    }

    /*
     * Metodo che stampa le persone online 
     */
    public void utenteOnline()
    {
        pr.println(listaClient.size());        

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
