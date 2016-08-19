package verySimpleChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


public class VerySimpleChatServer {


    private ArrayList<Object> clientOutputStream;

    public static void main(String[] args) {
        new VerySimpleChatServer().go();
    }

    public void go() {
        clientOutputStream = new ArrayList<>();

        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStream.add(writer);

                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
                System.out.println("got a connection");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;

        public ClientHandler(Socket clientSocket) {

            this.clientSocket = clientSocket;
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                reader = new BufferedReader(inputStreamReader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            String message;

            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read: " + message);
                    tellEveryone(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void tellEveryone(String message) {
        Iterator<Object> iterator = clientOutputStream.iterator();
        while (iterator.hasNext()) {
            PrintWriter writer = (PrintWriter) iterator.next();
            writer.println(message);
            writer.flush();
        }
    }
}
