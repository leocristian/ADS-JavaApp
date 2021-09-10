package com.example;

import java.net.ServerSocket;
import java.net.Socket;

import com.mongodb.client.gridfs.*;
import com.mongodb.gridfs.GridFS;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.Binary;

public class Server {
    public static File myFile;
    int dataTest;
    public final static int FILE_SIZE = 6022386;
    public static void main( String[] args ) {
        ServerSocket serverSocket;
        Socket clientSocket;
        OutputStream out;
        
        int bytesRead;
        int current;
        
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;;
        int countArq = 0;
        try {
            String FILE_TO_RECEIVED = "fileSalvar.pdf";
            
            serverSocket  = new ServerSocket(3000);
            System.out.println("Servidor iniciado! (" + serverSocket.getInetAddress() + ")");

            while(true){
                System.out.println("Aguardando arquivo...");
                try {
                    long inicial = System.currentTimeMillis();
                    //Runtime rt = Runtime.getRuntime();
                    
                    while(true) {
                        countArq++;

                        System.out.println("Esperando connexao...");
                        clientSocket = serverSocket.accept();
                        ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                        System.out.println("Conexão estabelecida com " + clientSocket.getInetAddress());
                        
                        //System.out.println("Memória disponível:" + rt.totalMemory());
                        
                        //receber arquivo do cliente
                        byte [] byteArr = new byte[FILE_SIZE];
                        InputStream input = clientSocket.getInputStream();

                        FILE_TO_RECEIVED = "Arquivo-" + countArq + ".pdf";
                        
                        fos = new FileOutputStream(FILE_TO_RECEIVED);
                        bos = new BufferedOutputStream(fos);
                        
                        bytesRead = input.read(byteArr, 0, byteArr.length);
                        current = bytesRead;
                        
                        do {
                            bytesRead = input.read(byteArr, current, (byteArr.length-current));
                            if(bytesRead >= 0){
                                current += bytesRead;
                            }
                            //System.out.println("bytes lidos: " + bytesRead);
                        }  while(bytesRead > -1);
                        
                        bos.write(byteArr, 0 , current);
                        bos.flush();
                        
                        System.out.println("Arquivo recebido: " + FILE_TO_RECEIVED);
                        System.out.println("Tamanho: " + current + " bytes");
                        System.out.println("Tempo de execução: " + (System.currentTimeMillis() - inicial));
                        
                        salvarMongoDB(byteArr, input);
                        // Enviar confirmação pro cliente
                        System.out.println("Enviando resposta ao cliente...");
                       // output.writeUTF("ok");
                        //output.flush();
                        
                    }
                } finally {
                    if (fos != null) fos.close();
                    if (bos != null) bos.close();
                    if (serverSocket != null) serverSocket.close();
                }
            }
            // System.out.println("Cliente desconectado!");
            // clientSocket.close();
            // serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean salvarMongoDB(byte[] fileRCVD, InputStream iStream) {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
                    
        MongoDatabase dataBase = mongoClient.getDatabase("fileStorageDb");
        Document doc = new Document();

        //GridFSBucket gridFSBucket = GridFSBuckets.create(dataBase, "files");
        //-----SALVAR ARQUIVO NO MONGODB-------
        try {
            doc.put("arquivos", fileRCVD);

            MongoCollection mycCollection = dataBase.getCollection("files");

            mycCollection.insertOne(doc);
            //Binary fileId = gridFSBucket.uploadFromStream(fileRCVD, iStream);
            mongoClient.close();
            return false;
        } catch (Exception e) {
            System.out.println("Erro ao salvar: " + e);
            return false;
        }
    }
}
