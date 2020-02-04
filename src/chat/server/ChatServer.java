package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
public class ChatServer implements TCPConnectionListener{

    public static void main(String[] args) {
        new ChatServer();
    }
    
    private ArrayList<TCPConnection> connections = new ArrayList<>();
    
    private ChatServer(){
        System.out.println("Server is running...");
        try {
            ServerSocket serverSocket = new ServerSocket(7707);
            while (true) {                
                try{
                    new TCPConnection(this, serverSocket.accept());
                }catch(IOException ex){
                    System.out.println("TCP connection exception");
                }
            }
        } catch (IOException ex) {
            System.out.println("TCP connection exception");
        }
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnect, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        sendToAllConnections("Client disconnected " + tcpConnection);
        connections.remove(tcpConnection);
        
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("Client connected " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection aThis, IOException ex) {
        System.out.println("TCP exception: " + ex);
    }
    
    private void sendToAllConnections(String value){
        System.out.println(value);
        for (TCPConnection con : connections) con.sendString(value);
    }
    
}
