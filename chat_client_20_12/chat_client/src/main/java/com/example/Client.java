package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;



public class Client
{

    private int num_port = 3000;
    private String username;
    private Socket s;   
    // oggetti per la communicazione
    private PrintWriter pr;
    private BufferedReader br;
    private BufferedReader tastiera;
   

    public Client() throws UnknownHostException, IOException
    {
        // bloccante, significa che il server finche non accetta non va avanti!
        s = new Socket("localhost", num_port);

        // Oggetto che serve per la communicazione con il server
        pr = new PrintWriter(s.getOutputStream(), true);
 
        // Oggetto per serve per l'ascolto dal canale
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
 
        // Oggetto che serve per salvare quello che il client scrive sulla tastiera
        tastiera = new BufferedReader(new InputStreamReader(System.in));
    }
    

    public void execute() throws IOException
    {

        String scelta; 
        System.out.print("[CLIENT]: Inserisci il tuo nome: ");
        
        setUsername(tastiera.readLine());
        
        System.out.println(getUsername());
        // Invio al server il nome
        pr.println(getUsername()); 

        System.out.println("[CLIENT]: Connessione avvenuta con successo!");
        clearScreen();
        System.out.println("Benvenuto su chatMeucci: " + username);
        while(true) // esce quando l'utente preme 5 - esci
        {
            System.out.println("DIGITA:");
            System.out.println("[1] Per inviare messaggio privato");
            System.out.println("[2] Per inviare messaggio pubblico");
            System.out.println("[3] Persone online");
            System.out.println("[4] Esci");
            System.out.print("[Scelta]:");

            // invio della scelta del client al server
            scelta = tastiera.readLine(); 
            pr.println(scelta);

            switch (Integer.valueOf(scelta)) {
                case 1:
                    clearScreen();
                    messaggioPrivato();
                    break;
                case 2:
                    clearScreen();
                    messaggioPubblico();
                    break;
                case 3:
                    clearScreen();
                    String Y = "";
                    while(!Y.equals("y"))
                    {
                        personeOnline();
                        System.out.print("Premi y per uscire: ");
                        Y = tastiera.readLine();
                    }
                    break;
                case 4:
                    System.out.println("Uscita dal server...");
                    // prima di uscire invio al server che sto chiudendo il socket
                    closeSocket(s);
                    break;
                default:
                    System.out.println("errore digitazione");
                    break;
                
            }

            // se l'utente ha scelto "Esci" esce dal while
            if(scelta.equals("4")) {
                break;
            }
            
          clearScreen();
            
            
        }// chiusura del while      
    }



    public void messaggioPrivato() throws IOException 
    {
        while(true)
        {
            System.out.println("[1]: Iniziare una chat privata");
            System.out.println("Premi y per uscire: ");
            System.out.print("[SCELTA]:");
            String scelta = tastiera.readLine();
            
            if(scelta.equals("y")){ break;} // condizione di uscita

            if(scelta.equals("1"))
            {
                clearScreen();
                // richiesta di stampa delle persone online
                personeOnline();
                System.out.println("Inserisci il numero della persona con cui vuoi chattare");
                System.out.print("altrimenti premi [exit] per uscire: ");
                String utenteB = tastiera.readLine();

                if(utenteB.equals("exit")){ break; }
                
                // invio dell'username con cui parlare
                pr.println(utenteB);

                clearScreen();
                
                System.out.println("CHAT PRIVATA CON:"+utenteB);

                ClientThreadListen clientlistener = new ClientThreadListen(s);
                clientlistener.start();
            
                // messaggio 
                String msgtouserb = "";
                while(true)
                {
                    msgtouserb = tastiera.readLine();
                    // bisogna inviarlo verso tutti
                    pr.println(msgtouserb);
                    if(msgtouserb.equals("exit"))
                    {
                        break;
                    }
                }
                clientlistener.clear();
                clientlistener = null;

                    
            } 
        }
        

            
         
    }

    // metodo che server per la chiusura del socket
    public void closeSocket(Socket s)
    {
        try {
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // metodo che server per fare il msg pubblic
    public void messaggioPubblico() throws IOException
    {
        System.out.println("CHAT PUBBILICA");
        pr.flush(); // cancello la roba dentro lo stream altrimenti mi prende la scelta
        ClientThreadListen clientlistener = new ClientThreadListen(s);
        clientlistener.start();
        String msgtobroadcast = "";
        System.out.println(msgtobroadcast);

        while(true)
        {
            msgtobroadcast = tastiera.readLine();
            // bisogna inviarlo verso tutti
            if(!msgtobroadcast.equals("exit"))
            {
                pr.println(msgtobroadcast);
            }
            else{ break; }
            
            
        }
        clientlistener.clear();
        clientlistener = null;
    }

    // persone online
    public void personeOnline() throws NumberFormatException, IOException
    {
        System.out.println("Persone online");
        int size = Integer.valueOf(br.readLine());

        for (int i = 0; i < size; i++) 
        {
            System.out.println(br.readLine());
        }
        
    }
    // metodo per pulire il terminale
    public void clearScreen()
    {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    //getter and setters
    public void setUsername(String username) {
        this.username = username;
        
    }
    public String getUsername() {
        return this.username;
        
    }
    
}