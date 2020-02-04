package chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public class TCPConnection {
    private Socket socket;
    private Thread mThreadForReading, mThreadForWriting;
    private BufferedReader in;
    private BufferedWriter out;
    private TCPConnectionListener eventListener;
    
    public TCPConnection (TCPConnectionListener eventListener, Socket socket) throws IOException{
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        mThreadForReading = new Thread(() -> {
            try{
                eventListener.onConnectionReady(TCPConnection.this);
                while(!mThreadForReading.isInterrupted()){
                    eventListener.onReceiveString(TCPConnection.this ,in.readLine());
                }
            }catch(IOException ex){
                eventListener.onException(TCPConnection.this, ex);
            }finally{
                disconnect();
            }
        });
        mThreadForWriting = new Thread( new Runnable(){
            @Override
            public void run(){
                while (!mThreadForWriting.isInterrupted()) {
                    String msg = (new Scanner(System.in, "Cp1251")).nextLine();
                    eventListener.onReceiveString(TCPConnection.this,"ADMIN" + ": " + msg);
                }
                disconnect();
            }
        });
        mThreadForWriting.start();
        mThreadForReading.start();
    }
    
    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException ex) {
            eventListener.onException(TCPConnection.this, ex);
            disconnect();
        }
    }
    public synchronized void disconnect(){
        mThreadForReading.interrupt();
        mThreadForWriting.interrupt(); 
        eventListener.onDisconnect(this);
        try {
            socket.close();
        } catch (IOException ex) {
            eventListener.onException(TCPConnection.this, ex);
        }
    }
    
    public String toString(){
        return "TCPconnection: " + socket.getInetAddress()+ " port: " + socket.getPort();
    }
}
