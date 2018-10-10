package ChatA;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class TCPServer1 {
    public static void main(String[] args) {
        System.out.println("=============SERVER==============");
        final int PORT_LISTEN = 5656;
        ArrayList<Client> clients = new ArrayList<>();

        try {
            ServerSocket server = new ServerSocket(PORT_LISTEN);

            System.out.println("Server starting...\n");

            while (true) {

                Socket socket = server.accept();


                String clientIp = socket.getInetAddress().getHostAddress();
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();
                System.out.println("Client connected");
                System.out.println("IP: " + clientIp);
                System.out.println("PORT: " + socket.getPort());


                byte[] dataUn = new byte[1024];
                input.read(dataUn);
                String joinMsg = new String(dataUn);
                joinMsg = joinMsg.trim();
                System.out.println(joinMsg);

                if (joinMsg.contains("JOIN")) {

                    int indexOfComma = joinMsg.lastIndexOf(",");
                    String username = joinMsg.substring(5, indexOfComma);
                    Client client = new Client();



                    // Hvis username er lig med et allerede eksisterende username
                    // slet bruger
                    for(Client c : clients){
                        if(c.getUsername().equalsIgnoreCase(username)){
                            String errorMessage = "JR_ER\n";
                            sendToClient(output, errorMessage);
                        }else{
                            sendToClient(output, "JR_OK\n");
                        }
                    }

                    validateUsername(username, socket);

                    client.setIp(socket.getInetAddress().getHostAddress());
                    client.setUsername(username);
                    client.setSocket(socket);
                    client.setInput(socket.getInputStream());
                    client.setOutput(socket.getOutputStream());
                    clients.add(client);
                    String allClientsString = "All connected clients: ";
                    for(Client c : clients){
                        allClientsString +=  c.getUsername() + ", ";
                    }
                    for(Client c : clients){
                        sendToClient(c.getOutput(), allClientsString);
                    }




                    ArrayList<Thread> receiverList = new ArrayList<>();
                    Thread receiver = new Thread(() -> {
                        while (true) {
                            try {
                                InputStream inputStream = client.getInput();
                                byte[] dataIn = new byte[1024];
                                inputStream.read(dataIn);
                                String msgIn = new String(dataIn);
                                msgIn = msgIn.trim();

                                //   System.out.println("IN FROM:" + username + "-->" + msgIn + "<--");

                                /*String msgToSend = "SERVER: [sender:" + clientIp + "]: " + msgIn;
                                sendToClient(output, msgToSend);*/
                                if (msgIn.equalsIgnoreCase("QUIT")) {
                                    socket.close();
                                    break;
                                } else if (msgIn.equalsIgnoreCase("IMAV")) {
                                    client.setSecondsSinceLastHeartbeat(0);
                                    System.out.println("IMAV");

                                }else if(msgIn.trim().length() > 250){
                                    String JR_ER_toolong = "JR_ER Message too long" + msgIn.trim().length();
                                    sendToClient(output, JR_ER_toolong);
                                }else {
                                    String msgToClients = client.getUsername() + ": " + msgIn;
                                    System.out.println(msgToClients);
                                    for (Client c : clients) {
                                        sendToClient(c.getOutput(), msgToClients);
                                    }

                                }
                                if (client.getSecondsSinceLastHeartbeat() > 10) {
                                    socket.close();
                                    break;
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    });
                    receiverList.add(receiver);

                    for (Thread t : receiverList) {
                        t.start();
                    }


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateUsername(String username, Socket socket) {
        String check = "^[a-zA-Z0-9\\-_]{1,12}$";

        if (!username.matches(check)) {
            //Send msg to client that username is invalid and connection hs been terminated
            try {
                OutputStream output = socket.getOutputStream();
                System.out.println("1");
                sendToClient(output, "JR_ER 500: Invalid Username. Connection terminated.");
                System.out.println("JR_ER 500: Invalid Username: " + username + ". Connection terminated.");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            return true;
        }


    }

    public static void sendToClient(OutputStream output, String msgToSend) {
        try {
            byte[] dataToSend = msgToSend.getBytes();
            output.write(dataToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}