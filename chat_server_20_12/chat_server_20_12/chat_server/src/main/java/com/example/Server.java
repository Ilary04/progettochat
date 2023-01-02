package com.example;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {
  public static void main(String[] args) throws Exception {

    // Arraylist per la communicazione privata e broadcast
    ArrayList<ClientHandler> listaClient = new ArrayList<ClientHandler>();

    ServerSocket ss = new ServerSocket(3000);
    System.out.println("[SERVER] In ascolto sulla porta 3000.");
    
    boolean running = true;
    
    while (running) 
    {
      
      Socket s = ss.accept(); 
      // passo anche la lista dei client collegati a ogni thread 
      ClientHandler client = new ClientHandler(s, listaClient);
      
      // aggiungo il thread associato al client appena collegato
      // dentro l'arraylist 
      listaClient.add(client);
      
      // esecuzione del thread associato al client
      client.start();
    }
    
    ss.close();
  }
}