package ServerClient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Socket socket = null;

        try {
            socket = new Socket("localhost", 8185);

            final Scanner in = new Scanner(socket.getInputStream());
            final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            final Scanner console = new Scanner(System.in);

            Thread client = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        String str = in.nextLine();
                        if (str.equals("/end")){
                            out.println("/end");
                            break;
                        }
                        System.out.println("Server: " + str);
                    }

                }
            });
            client.start();

            Thread server = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        String str = console.nextLine();
                        out.println(str);
                    }
                }
            });
            server.setDaemon(true);
            server.start();

            try {
                client.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
