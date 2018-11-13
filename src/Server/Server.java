package Server;

import ServerClient.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {
    private Vector<ClientHandler> clients;
    ServerSocket server = null;
    Socket socket = null;

    public Server() throws SQLException {

        clients = new Vector<>();

        try {
            AuthService.connect();
            server = new ServerSocket(8180); // создаем сервер
            System.out.println("Server run!");

            while (true){
                socket = server.accept(); // сработает когда клиент подключиться к серверу
                System.out.println("Client include!");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }
    public void broadcastMsg(ClientHandler from, String msg){
        for (ClientHandler o : clients){
            if (!o.checkBlackList(from.nick)){
                o.sendMsg(msg);
            }
        }

    }
    public void privatMsg(String nick, String msg){
        for (ClientHandler o : clients){
            if (nick.equals(o.nick)){
                o.sendMsg(msg);
            }
        }

    }
    public void addClient(ClientHandler client){
        clients.add(client);
        boardCastClientList();
        System.out.println("Client add");
    }
    public void removeClient(ClientHandler client){
        clients.remove(client);
        boardCastClientList();
        System.out.println("Client exit!");
    }

    public boolean isNickBusy(String nick){
        for (ClientHandler o : clients){
            if (nick.equals(o.nick)){
                return true;
            }
        }
        return false;
    }
    public void boardCastClientList(){
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");
        for (ClientHandler o : clients){
            sb.append(o.nick + " ");
        }
        String out = sb.toString();
        for (ClientHandler o : clients){
            o.sendMsg(out);
        }
    }
}
