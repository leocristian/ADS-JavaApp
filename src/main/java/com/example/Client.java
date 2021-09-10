package com.example;

import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public final static int SOCKET_PORT = 3000;  // you may change this
    public static void main( String[] args ) {
        
        Socket clientSocket;
        OutputStream out;
        BufferedReader in;
        Scanner sc = new Scanner(System.in);

        FileInputStream fis = null;
        BufferedInputStream bis = null;

        try {
            int port = 3000;
            
            System.out.print("Digite o IP do servidor: ");
            String serverIP = sc.nextLine();
            
            clientSocket = new Socket(serverIP, port);
            out = clientSocket.getOutputStream();          
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            System.out.println("Digite o nome do arquivo com extensão: ");
            String fileSent = sc.nextLine();
            
            File myFile = new File(fileSent);
            
            if (myFile.exists()) {
                System.out.println("Arquivo encontrado!");
                System.out.println("Nome: " + myFile.getName());
                System.out.println("Tamanho: " + myFile.length());
            } else {
                System.out.println("Arquivo não encontrado!");
            }
            
            byte [] byteArr = new byte[(int)myFile.length()];
            
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);
            
            bis.read(byteArr, 0, byteArr.length);
            
            out.write(byteArr, 0, byteArr.length);
            out.flush();    
            
            System.out.println("Arquivo enviado: " + fileSent);
            System.out.println("Tamanho: " + byteArr.length + " bytes");

            System.out.println("Esperando confirmação do servidor...");
            String msg = in.readLine();

            System.out.println("Resposta do servidor: " + msg);
            //     if (msg == null) {
            //         System.out.println("Conexão encerrada.");
            //         break;
            //     }
            // }

            out.close();
            clientSocket.close();

        } catch (Exception e) {
            System.out.println("Servidor não encontrado!");
        }
    }
}