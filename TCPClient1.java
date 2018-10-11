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
    static Thread IMAV;
    static OutputStream output;

    public static void main(String[] args) throws IOException {
        System.out.println("=============CLIENT==============");
        Scanner sc = new Scanner(System.in);


        Thread receiver = null;
        Thread sender = null;


        System.out.print("Type your username: ");
        String message = "JOIN" + sc.nextLine();


        System.out.print("What is the IP for the server (type 0 for localhost): ");
        String ipToConnect = args.length >= 1 ? args[0] : sc.nextLine();

        System.out.print("What is the PORT for the server: ");
        int portToConnect = args.length >= 2 ? Integer.parseInt(args[1]) : sc.nextInt();


        final int PORT_SERVER = portToConnect;
        final String IP_SERVER_STR = ipToConnect.equals("0") ? "127.0.0.1" : ipToConnect;
        InetAddress ip = InetAddress.getByName(IP_SERVER_STR);
        Socket socket = new Socket(ip, PORT_SERVER);
        InputStream input = socket.getInputStream();
        output = socket.getOutputStream();

        String username = message.substring(4);
        System.out.println("\nConnecting...");
        System.out.println("USERNAME: " + username);
        System.out.println("SERVER IP: " + IP_SERVER_STR);
        System.out.println("SERVER PORT: " + PORT_SERVER + "\n");

        heartBeater();
        //connect

        if (message.contains("JOIN")) {
            try {


                String joinMsg = "JOIN " + username + ", " + IP_SERVER_STR + ":" + portToConnect;
                byte[] dataToSend = joinMsg.getBytes();
                output.write(dataToSend);
                if (!username.matches("^[a-zA-Z0-9\\-_]{1,12}$")) {
                    System.out.println("JR_ER 500: Invalid Username. Connection terminated.");
                    socket.close();
                } else {
                    System.out.println("Connected");
                }

                if (socket.isConnected()) {
                    sender = new Thread(() -> {
                        while (true) {
                            do {
                                Scanner send = new Scanner(System.in);
                                String msgToSend = "DATA " + username + ": " + send.nextLine();
                                msgToSend = msgToSend.trim();
                                if (msgToSend.trim().length() < 250) {
                                    sendToServer(output, msgToSend);
                                    break;
                                }
                            }
                            while (socket.isConnected());
                        }


                    });
                    sender.start();


                    receiver = new Thread(() -> {
                        try {
                            while (true) {
                                byte[] dataIn = new byte[1024];

                                input.read(dataIn);

                                String msgIn = new String(dataIn);
                                msgIn = msgIn.trim();
                                System.out.println(msgIn);
                                if (msgIn.contains("JR_Quit")) {
                                    socket.close();
                                    IMAV.stop();
                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                    receiver.start();

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


    public static void sendToServer(OutputStream output, String msgToSend) {
        try {
            byte[] dataToSend = msgToSend.getBytes();
            output.write(dataToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void heartBeater() {
        IMAV = new Thread(() -> {
            try {

                while (true) {
                    Thread.sleep(60000);
                    String heartbeat = "IMAV";
                    sendToServer(output, heartbeat);
                    System.out.println(heartbeat);
                }

            } catch (InterruptedException e) {

            }
        });
        IMAV.start();
    }


}


