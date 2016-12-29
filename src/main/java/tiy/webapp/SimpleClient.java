package tiy.webapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by dbashizi on 12/14/16.
 */
public class SimpleClient {

    Socket clientSocket;
    PrintWriter socketOutput;
    BufferedReader socketInput;

    public SimpleClient(String serverIP, int portNumber) {
        this(serverIP, portNumber, false);
    }

    public SimpleClient(String serverIP, int portNumber, boolean shutdownFlag) {
        try {
            System.out.println("Connection to " + serverIP + " on port " + portNumber);
            clientSocket = new Socket(serverIP, portNumber);
            System.out.println("Initializing output and input for socket ...");
            socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Socket I/O initialized");
            if (shutdownFlag) {
                System.out.println("Sending server shutdown message");
                sendMessage(SimpleServer.SHUTDOWN_MESSAGE);
            } else {
                System.out.println("Sending message to start a new connection ...");
                sendMessage(SimpleServer.CONNECTION_START);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public String sendMessage(String message) throws IOException {
        System.out.println("Sending message to server: " + message);
        socketOutput.println(message);
        String response = socketInput.readLine();
        System.out.println("Received message from server: " + response);
        return response;
    }

    public void closeClient() throws IOException {
        System.out.println("**** Closing client connection");
        // the client is responsible for telling its connection handling it's done
        sendMessage(SimpleServer.CONNECTION_END);
    }
}