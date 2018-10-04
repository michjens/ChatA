package ChatA;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TCPServer1 {
    public static void main(String[] args) {
        System.out.println("=============SERVER==============");
        boolean test = true;
        final int PORT_LISTEN = 5656;

        try {
            ServerSocket server = new ServerSocket(PORT_LISTEN);

            System.out.println("Server starting...\n");

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

            int indexOfComma = joinMsg.lastIndexOf(",");
            String username = joinMsg.substring(5, indexOfComma);

            validateUsername(username, socket);


            System.out.println("USERNAME: " + username + "\nJR_OK\n\n");


            while (socket.isConnected()) {
                byte[] dataIn = new byte[1024];
                input.read(dataIn);
                String msgIn = new String(dataIn);
                msgIn = msgIn.trim();

                System.out.println("IN FROM:" + username + "-->" + msgIn + "<--");

                String msgToSend = "SERVER: [sender:" + clientIp + "]: " + msgIn;
                sendToClient(output, msgToSend);
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