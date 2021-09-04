package com.example;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public final static int SOCKET_PORT = 13267;  // you may change this
    public static void main( String[] args ) {
        final Socket clientSocket;
        final OutputStream out;
        final Scanner sc = new Scanner(System.in);

        BufferedInputStream bis = null;
        FileInputStream fis = null;

        try {
            int port = 3000;
            
            // System.out.print("Digite o IP do servidor: ");
            // String serverIP = sc.nextLine();
            
            clientSocket = new Socket("0.0.0.0", port);                
            
            System.out.print("Digite o nome do arquivo com extensão: ");
            String fileSent = sc.nextLine();

            File myFile = new File(fileSent);

            // if (myFile.exists()) {
            //     System.out.println("Arquivo encontrado!");
            //     System.out.println("Nome: " + myFile.getName());
            //     System.out.println("Tamanho: " + myFile.length());
            // } else {
            //     System.out.println("Arquivo não encontrado!");
            // }

            byte [] byteArr = new byte[(int)myFile.length()];

            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);

            bis.read(byteArr, 0, byteArr.length);
            System.out.println(bis);

            out = clientSocket.getOutputStream();
            System.out.println("Arquivo enviado: " + fileSent);
            System.out.println("Tamanho: " + byteArr.length + " bytes");
            
            out.write(byteArr, 0, byteArr.length);
            
            out.flush();
            // Thread sender = new Thread(new Runnable(){
                
            //     @Override
            //     public void run() {
            //         String fileName = myFile.getName();
            //         Long fileSize = myFile.length();

            //         System.out.println("Dado enviado com sucesso!");
            //         String dataSent = (fileName + ";" + fileSize);
            //         out.println(dataSent);
            //         out.flush();
            //     }
            // });
            // sender.start();
                
                
        } catch (Exception e) {
            System.out.println("Servidor não encontrado!");
        }
    }
}