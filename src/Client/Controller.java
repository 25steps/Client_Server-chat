package Client;

import Server.AuthService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sun.security.util.Password;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    TextArea textArea;
    @FXML
    TextField textField;
    @FXML
    VBox upperPanel;
    @FXML
    VBox addClientPanel;
    @FXML
    TextField logField;
    @FXML
    PasswordField passField;
    @FXML
    TextField nickField;
    @FXML
    HBox bottomPanel;
    @FXML
    TextField loginField;
    @FXML
    PasswordField passwordField;
    @FXML
    ListView<String> clientList;

    private boolean isAuthorized = false; // флажок для авторизации

    public void authorized(boolean isAuthorized){
        this.isAuthorized = isAuthorized;
        if (!isAuthorized){
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            bottomPanel.setVisible(false);
            bottomPanel.setManaged(false);
            clientList.setVisible(false);
            clientList.setManaged(false);
        }else {
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            bottomPanel.setVisible(true);
            bottomPanel.setManaged(true);
            clientList.setVisible(true);
            clientList.setManaged(true);
        }
    }

    Socket socket;
    DataInputStream in; // изходящий поток
    DataOutputStream out; //входящий поток
    final String IP_ADRESS = "localhost";
    final int PORT = 8180;

    public void connect() {
        try {
            socket = new Socket(IP_ADRESS, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            //authorized(false);

            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String str = in.readUTF();
                            if (str.startsWith("/authok")) {
                                authorized(true);
                                break;
                            }
                            if (str.equals("/addclientok")){
                                visibleAddClientPanel(false);
                                //authorized(false);
                                //break;
                            }
                            else {
                                textArea.appendText(str + "\n");
                            }
                        }

                        while (true){
                            String str = in.readUTF();
                            if (str.startsWith("/")){
                                if (str.equals("/addclientok")){
                                    visibleAddClientPanel(false);
                                    authorized(false);
                                    break;
                                }
                                if (str.equals("/serverclosed")){
                                    authorized(false);
                                    textArea.clear();
                                    break;
                                }
                                if (str.startsWith("/clientlist")){
                                    String[] token = str.split(" ");
                                    Platform.runLater(new Runnable() { // допалнительный поток, который позволяет менять в реальном времени(листвью)
                                        @Override
                                        public void run() {
                                            clientList.getItems().clear();
                                            for (int i = 1; i < token.length; i++){
                                                clientList.getItems().add(token[i]);
                                            }
                                        }
                                    });
                                }
                            }else {
                                textArea.appendText(str + "\n");
                            }

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            socket.close();
                            System.out.println("Socket close...");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t1.setDaemon(true);
            t1.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(){
        try {
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()){
            connect();
        }
        try {
            out.writeUTF("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void addClient(ActionEvent actionEvent) {
        visibleAddClientPanel(true);
    }
    public void exitAddClient(ActionEvent actionEvent) {
        visibleAddClientPanel(false);
    }

    public void visibleAddClientPanel(boolean flag) {
        if (flag){
            upperPanel.setVisible(false);
            upperPanel.setManaged(false);
            addClientPanel.setVisible(true);
            addClientPanel.setManaged(true);
            textArea.appendText("Log/Pass/Nick must be longer than 4 characters!\n");
        }else{
            upperPanel.setVisible(true);
            upperPanel.setManaged(true);
            addClientPanel.setVisible(false);
            addClientPanel.setManaged(false);
            textArea.clear();
        }

    }

    public void authClient(ActionEvent actionEvent) {
        if (logField.getText().length() >= 4 && passField.getText().length() >= 4 && nickField.getText().length() >= 4 ){
            if (socket == null || socket.isClosed()){
                connect();
            }
            try {
                out.writeUTF("/addclient " + logField.getText()+" "+ passField.getText() + " " + nickField.getText());
                logField.clear();
                passField.clear();
                nickField.clear();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        }


//    public void authClient(ActionEvent actionEvent) {
//
//        if (logField.getText().length() >= 4 && passField.getText().length() >= 4 && nickField.getText().length() >= 4 ){
//            textArea.appendText("Log/Pass/Nick - verification.\n");
//            if (socket == null || socket.isClosed()){
//                connect();
//            }
//            try {
//                out.writeUTF("/addclient " + logField.getText()+" "+ passField.getText() + " " + nickField.getText());
//                logField.clear();
//                passField.clear();
//                nickField.clear();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
}
