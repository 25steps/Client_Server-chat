package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    private Server server;
    private Socket socket;
    DataInputStream in;
    DataOutputStream out;
    String nick = null;
    List<String> blackList;

    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blackList = new ArrayList<String>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        while (true){
//                            String str = in.readUTF();
//                            if (str.startsWith("/addclient")){
//                                String[] token = str.split(" ");
//                                if (AuthService.checkLoginAndNick(token[1], token[3]).equals("val")){
//                                    if (AuthService.addLoginPassNick(token[1], token[2], token[3])) {
//                                        sendMsg("/addclientok");
//                                        sendMsg("Client with nick: " + token[3] + " add chat.");
//                                        break;
//                                    } else {
//                                        sendMsg("Error...try again..");
//                                    }
//
//                                }else{
//                                    sendMsg("This Log/Nick - exists. Try again...");
//                                }
//                            }
//                        }
                        while (true){
                            String str = in.readUTF();
                            if (str.startsWith("/auth")){
                                String[] token = str.split(" ");
                                String newNick = AuthService.getNickByLoginAndPass(token[1], token[2]);
                                //System.out.println(AuthService.checkLoginAndNick("login", "nick1"));
                                if (newNick != null){
                                    if (!server.isNickBusy(newNick)){
                                        sendMsg("/authok");
                                        nick = newNick;
                                        server.addClient(ClientHandler.this);
                                        break;
                                    }else {
                                        sendMsg(newNick + " - this nickname is busy!");
                                    }

                                }else {
                                    sendMsg("Wrong loggin/password");
                                }
                            }
                            if (str.startsWith("/addclient")){
                                String[] token = str.split(" ");
                                if (AuthService.checkLoginAndNick(token[1], token[3]).equals("val")){
                                    if (AuthService.addLoginPassNick(token[1], token[2], token[3])) {
                                        sendMsg("/addclientok");
                                        sendMsg("Client with nick: " + token[3] + " add chat.");
                                        //break;
                                    } else {
                                        sendMsg("Error...try again..");
                                    }

                                }else{
                                    sendMsg("This Log/Nick - exists. Try again...");
                                }
                            }
                        }

                        while (true) {
                            String str = in.readUTF();
                            String[] token = str.split(" ", 3);
                            if (token[0].equals("/w")){
                                if (server.isNickBusy(token[1])){
                                    server.privatMsg(token[1],nick + "(prv_msg): " + token[2]);
                                    sendMsg("to " + token[1] + "(prv_msg): " + token[2]);
                                }else{
                                    sendMsg(token[1] + " - this nickname is not exit.");
                                }

                            }if (str.equals("/end")){
                                out.writeUTF("/serverclosed");
                                break;
                            }if (str.startsWith("/blacklist")){
                                if (nick.equals(token[1])){
                                    sendMsg("You can't add in blacklist yourself");
                                }else {
                                    blackList.add(token[1]);
                                    sendMsg("You add in blacklist nickname: " + token[1]);
                                }
                                }else{
                                server.broadcastMsg(ClientHandler.this,nick + ": " + str);
                            }

                            //server.broadcastMsg(nick + ": " + str);
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        server.removeClient(ClientHandler.this);
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkBlackList(String nick){
        return blackList.contains(nick);
    }
}
