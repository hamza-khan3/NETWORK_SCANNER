import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkScanner {
    private String host;
    private int startPort;
    private int endPort;

    public NetworkScanner(String host, int startPort, int endPort) {
        this.host = host;
        this.startPort = startPort;
        this.endPort = endPort;
    }

    public void scanPort(int port) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 1000);
        socket.close();
    }

    public int getStartPort() {
        return startPort;
    }

    public int getEndPort() {
        return endPort;
    }
}
