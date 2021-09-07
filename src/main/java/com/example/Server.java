package com.example;

import java.io.BufferedReader;
import java.io.PrintWriter;

import java.net.ServerSocket;
import java.net.Socket;
import com.mongodb.client.gridfs.*;

import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Server {
    public static File myFile;
    public final static String FILE_TO_RECEIVED = "fileSalvar.txt";
    int dataTest;
    public final static int FILE_SIZE = 6022386;
    public static void main( String[] args ) {
        final ServerSocket serverSocket;
        final Socket clientSocket;
        final PrintWriter out;
        final BufferedReader in;
        
        int bytesRead;
        int current;
        String data;

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;;

        try {
            serverSocket  = new ServerSocket(3000);
            System.out.println("Servidor iniciado! (" + serverSocket.getInetAddress() + ")");
            
            
            try {
                byte [] byteArr = new byte[FILE_SIZE];
                data = FILE_TO_RECEIVED;
                clientSocket = serverSocket.accept();
                System.out.println("Conexão estabelecida com " + clientSocket.getInetAddress());
                
                System.out.println("Aguardando arquivo do cliente... ");
                
                System.out.println("Aguardando novo arquivo: ");
                InputStream is = clientSocket.getInputStream();
                fos = new FileOutputStream(FILE_TO_RECEIVED);
                bos = new BufferedOutputStream(fos);
                bytesRead = is.read(byteArr, 0, byteArr.length);
   
                current = bytesRead;

                do {
                    bytesRead = is.read(byteArr, current, (byteArr.length-current));
                    if(bytesRead >= 0) current += bytesRead;
                }  while(bytesRead > -1);
                 
                bos.write(byteArr, 0 , current);
                bos.flush();
                System.out.println("Arquivo recebido: " + FILE_TO_RECEIVED);
                System.out.println("Tamanho: " + current + " bytes");
   
                data = FILE_TO_RECEIVED;
                   
                System.out.println("dado recebido: " + data);
                MongoClient mongoClient = new MongoClient("localhost", 27017);
                
                MongoDatabase dataBase = mongoClient.getDatabase("fileStorageDb");
                //Document document = new Document();
                
                GridFSBucket gridFSBucket = GridFSBuckets.create(dataBase, "files");

                ObjectId fileId = gridFSBucket.uploadFromStream(data, is);

                //document.put("fileName", data);
            
                //MongoCollection myCollection = dataBase.getCollection("files");
                
                //myCollection.insertOne(document);
                mongoClient.close();
                
                System.out.println("Cliente desconectado!");
                clientSocket.close();
                serverSocket.close();
            } finally {
                if (fos != null) fos.close();
                if (bos != null) bos.close();
                if (serverSocket != null) serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
