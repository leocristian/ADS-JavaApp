package com.example;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;


public class Server {
    public static void main( String[] args ) {
        final ServerSocket serverSocket;
        final Socket clientSocket;
        final PrintWriter out;
        final BufferedReader in;
        try {
            serverSocket  = new ServerSocket(3000);
            System.out.println("Servidor iniciado! (" + serverSocket.getInetAddress() + ")");

            MongoClient mongoClient = new MongoClient("localhost", 27017);

            
            MongoDatabase dataBase = mongoClient.getDatabase("fileStorageDb");
            //MongoCollection dbCollection = dataBase.getCollection("data");
            String name = dataBase.getName();

            
            System.out.println("Lista de bancos de dados!");
            System.out.println(name);
            
            MongoCollection myCollection = dataBase.getCollection("files");

            Document document = new Document();
            document.put("fileName", "arquivo2");
            document.put("fileSize", 50);
            myCollection.insertOne(document);

            Long qtdData = myCollection.count();
            System.out.print("Quantidade de dados na collection: " + qtdData);




            clientSocket = serverSocket.accept();
            System.out.println("Conexão estabelecida com " + clientSocket.getInetAddress());

            System.out.println("Aguardando arquivo do cliente... ");
        
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread receive = new Thread(new Runnable(){
                String data;
                @Override
                public void run() {
                    try {
                        data = in.readLine();
                        System.out.println("dado recebido: " + data);
                        while (data != null) {
                            String[] dataSplit = data.split(";");
                            System.out.println("Dado recebido do Cliente!");
                            System.out.println("Nome do arquivo: " + dataSplit[0]);
                            System.out.println("Tamanho do arquivo: " + dataSplit[1]);
                            data = in.readLine();
                        }
                        
                        System.out.println("Cliente desconectado!");
                        out.close();
                        clientSocket.close();
                        serverSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            receive.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
