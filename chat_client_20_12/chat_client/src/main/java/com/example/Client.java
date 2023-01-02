package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;



public class Client
{

    private int num_port = 3000;
    private String username;
    private Socket s;   
    
    // oggetti per la communicazione
    private PrintWriter pr;
    private BufferedReader br;
    private BufferedReader tastiera;

    // Classe che effettuera l'ascolto
    private ClientThreadListen c;
   

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
    

    public void execute() throws IOException, InterruptedException
    {
        c = new ClientThreadListen(s);
        String scelta; 
        System.out.print("[CLIENT]: Inserisci il tuo nome: ");
        
        setUsername(tastiera.readLine());
        
        System.out.println(getUsername());
        // Invio al server il nome
        pr.println(getUsername()); 

        System.out.println("[CLIENT]: Connessione avvenuta con successo!");
        clearScreen();
        System.out.println("Benvenuto su chatMeucci: " + username);
        c.start();
        while(true) // esce quando l'utente preme 3 
        {
            
            System.out.println("DIGITA:");
            System.out.println("[1] Per inviare messaggio privato");
            System.out.println("[2] Per inviare messaggio pubblico");
            System.out.println("[3] Esci");
            System.out.print("[Scelta]:");
            
            // invio della scelta del client al server
            scelta = tastiera.readLine(); 
            pr.println(scelta);
            clearScreen();
            System.out.println("Username:"+username);
            switch (Integer.valueOf(scelta)) {
                case 1:
                    messaggioPrivato();
                    break;
                case 2:
                    messaggioPubblico();
                    break;
                case 3:
                    System.out.println("Uscita dal server...");
                    // prima di uscire invio al server che sto chiudendo il socket
                    closeSocket(s);
                    break;
                default:
                    clearScreen();
                    break;
                
            }

            // se l'utente ha scelto "Esci" esce dal while
            if(scelta.equals("3")) {
                break;
            }
            
          clearScreen();
            
            
        }// chiusura del while      
    }


    /* 
     * Metodo che serve per il messaggio privato. 
     * Prima viene mostrato le persone online, l'utente seleziona l'utente
     * con cui vuole parlare e inizia la communicazione.
     * Il messaggio viene inviato al server dove viene creato una stringa
     * per identificare il messaggio privato e il server lo invia all'utente 
     * target
    */
    public void messaggioPrivato() throws IOException, InterruptedException 
    {
        // il threadAscolto stampa una lista di persone online, l'utente deve scegliere uno di loro

        String utenteB = tastiera.readLine();
                                    
        // invio dell'username con cui parlare
                
        pr.println(utenteB);

        clearScreen();
                
        System.out.println("CHAT PRIVATA CON:"+utenteB);
        System.out.println("Per uscire dalla chat premi [exit]");

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
        clearScreen();     
    }

    /*
     * Metodo che server per la chiusura del socket e oggetti annessi
     */
    public void closeSocket(Socket s)
    {
        try {
            s.close();
            pr.close();
            br.close();
            tastiera.close();
            c.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
     * Metodo che server per fare il msg broadcast. 
     * il messaggio viene inviato il server che provede a
     * inviare a tutti gli utenti online
    */
    public void messaggioPubblico() throws IOException
    {

        System.out.println("CHAT PUBBILICA");
        System.out.println("per uscire premi [exit] ");
        pr.flush(); // cancello la roba dentro lo stream altrimenti mi prende la scelta
      
        String msgtobroadcast = "";

        while(true)
        {
            msgtobroadcast = tastiera.readLine();
            // bisogna inviarlo verso tutti 
            pr.println(msgtobroadcast);
            if(msgtobroadcast.equals("exit"))
            {
                break;
            }   
        }
    }

    /*
     * Metodo per pulire il terminale
    */ 
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
