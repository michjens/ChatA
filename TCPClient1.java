package ChatAssignment;

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
    public static void main(String[] args) throws Exception {
        System.out.println("=============CLIENT==============");
        Scanner sc = new Scanner(System.in);
        String msgToSend = "";


        System.out.println("Type your username: ");
        String username = sc.nextLine();


        System.out.print("What is the IP for the server (type 0 for localhost): ");
        String ipToConnect = args.length >= 1 ? args[0] : sc.nextLine();

        System.out.print("What is the PORT for the server: ");
        int portToConnect = args.length >= 2 ? Integer.parseInt(args[1]) : sc.nextInt();


        // username = validateUsername(username);


        final int PORT_SERVER = portToConnect;
        final String IP_SERVER_STR = ipToConnect.equals("0") ? "127.0.0.1" : ipToConnect;

        try {
            InetAddress ip = InetAddress.getByName(IP_SERVER_STR);

            System.out.println("\nConnecting...");
            System.out.println("USERNAME: " + username);
            System.out.println("SERVER IP: " + IP_SERVER_STR);
            System.out.println("SERVER PORT: " + PORT_SERVER + "\n");

            Socket socket = new Socket(ip, PORT_SERVER);

            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();

            try {
                String joinMsg = "JOIN " + username + ", " + IP_SERVER_STR + ":" + portToConnect;
                byte[] dataToSend = joinMsg.getBytes();
                output.write(dataToSend);
            } catch (IOException e) {

            }
           /* do {

                sendToServer(output, msgToSend);
                byte[] dataIn = new byte[1024];
                input.read(dataIn);
                String msgIn = new String(dataIn);
                msgIn = msgIn.trim();


            }*/
            while (socket.isConnected()) {

                System.out.println("What do you want to send? ");
                msgToSend = sc.nextLine();
                byte[] dataToSend = msgToSend.getBytes();
                output.write(dataToSend);

                byte[] dataIn = new byte[1024];
                input.read(dataIn);
                String msgIn = new String(dataIn);
                msgIn = msgIn.trim();

                System.out.println("IN -->" + msgIn + "<--");
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
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


