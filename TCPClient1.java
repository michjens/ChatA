package ChatA;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Matcher;

public class TCPClient1 {
    public static void main(String[] args) throws IOException {
        System.out.println("=============CLIENT==============");
        Scanner sc = new Scanner(System.in);


        Thread heartBeater = null;
        Thread receiver = null;


        System.out.print("Type JOIN + your username: ");
        String message = sc.nextLine();


        System.out.print("What is the IP for the server (type 0 for localhost): ");
        String ipToConnect = args.length >= 1 ? args[0] : sc.nextLine();

        System.out.print("What is the PORT for the server: ");
        int portToConnect = args.length >= 2 ? Integer.parseInt(args[1]) : sc.nextInt();


        // username = validateUsername(username);


        final int PORT_SERVER = portToConnect;
        final String IP_SERVER_STR = ipToConnect.equals("0") ? "127.0.0.1" : ipToConnect;
        InetAddress ip = InetAddress.getByName(IP_SERVER_STR);
        Socket socket = new Socket(ip, PORT_SERVER);
        InputStream input = socket.getInputStream();
        OutputStream output = socket.getOutputStream();


        System.out.println("\nConnecting...");
        System.out.println("USERNAME: " + message);
        System.out.println("SERVER IP: " + IP_SERVER_STR);
        System.out.println("SERVER PORT: " + PORT_SERVER + "\n");



            //connect

        if (message.contains("JOIN")) {
            try {
            System.out.println("Connected");

            String joinMsg = message + ", " + IP_SERVER_STR + ":" + portToConnect;
            byte[] dataToSend = joinMsg.getBytes();
            output.write(dataToSend);



            if (socket.isConnected()) {
                receiver = new Thread(() -> {
                    try {
                        while (socket.isConnected()) {

                            Scanner send = new Scanner(System.in);
                            System.out.println("What do you want to send? ");
                            String msgToSend = send.nextLine();
                            msgToSend = msgToSend.trim();
                            sendToServer(output, msgToSend);

                            byte[] dataIn = new byte[1024];
                            input.read(dataIn);
                            String msgIn = new String(dataIn);
                            msgIn = msgIn.trim();

                            System.out.println("IN -->" + msgIn + "<--");
                        }
                    } catch (IOException e) {
                     e.printStackTrace();
                    }
                });
                receiver.start();

                heartBeater = new Thread(() -> {
                    try {

                        String heartbeat = "IMAV";

                        while (true) {
                            Thread.sleep(60000);
                            sendToServer(output, heartbeat);
                            //System.out.println(heartbeat);
                        }

                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                });
                heartBeater.start();

            } else {
                System.out.println("Username is malformed:\n Please enter new username with with letters, digits, underscore or hyphen.\n Must not be longer than 12 characters.");
            }
            } catch (IOException e) {
                e.printStackTrace();
        }

        } else {
            OutputStream outputStream = socket.getOutputStream();
            sendToServer(outputStream, message);
        }


    }

    //heartBeater.join();


    //Heartbeats


    public static void sendToServer(OutputStream output, String message) {
        try {
            byte[] dataToSend = message.getBytes();
            output.write(dataToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*  public static void sendToServer(OutputStream output, String msgToSend) throws Exception{


     */


}





   /* public static String validateUsername(String username){
        Scanner uName = new Scanner(System.in);
        System.out.println("Type your username");
        username = uName.next();
        String check = "^[a-zA-Z0-9\\-_]{1,12}$";

        if (!username.matches(check)){
            System.out.println("Invalid characters. Try again");
            username = "";
            validateUsername(username);

        }
        System.out.println("Your username is: " + username);
        return username;


        } */


