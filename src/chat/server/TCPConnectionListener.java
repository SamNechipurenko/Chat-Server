package chat.server;

import java.io.IOException;

public interface TCPConnectionListener {
    void onReceiveString (TCPConnection tcpConnect, String value);
    void onDisconnect (TCPConnection tcpConnect);
    public void onConnectionReady(TCPConnection aThis);
    public void onException(TCPConnection aThis, IOException ex);
}