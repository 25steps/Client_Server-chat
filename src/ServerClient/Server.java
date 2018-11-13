package ServerClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        ServerSocket server = null;
        Socket socket = null;

        try {
            server = new ServerSocket(8185);
            System.out.println("Server run, wait connect...");
            socket = server.accept();
            System.out.println("Client connect...");
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner console = new Scanner(System.in);

            Thread cl = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        String str = in.nextLine();
                        if (str.equals("/end")){
                            out.println("/end");
                            break;
                        }
                        System.out.println("Client: " + str);
                    }
                }
            });
            cl.start();
            Thread sr = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        String str_s = console.nextLine();
                        out.println(str_s);
                    }
                }
            });
            sr.setDaemon(true);
            sr.start();
            try {
                cl.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.out.println("Error initializing server");
            e.printStackTrace();
        }finally {
            try {
                socket.close();
                System.out.println("Client close...");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
                System.out.println("Server close.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
