import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MyServerDatagramSocket extends DatagramSocket {
    static final int MAX_LEN = 100;

    public MyServerDatagramSocket(int portNo) throws SocketException{
        super(portNo);
    }

    public void sendMessage(InetAddress receiverHost, int receiverPort, String message) throws IOException {
        byte[] sendBuffer = message.getBytes( );
        DatagramPacket datagram = new DatagramPacket(sendBuffer, sendBuffer.length, receiverHost, receiverPort);
        this.send(datagram);
    }

    public DatagramMessage receiveMessageAndSender() throws IOException {
        byte[] receiveBuffer = new byte[MAX_LEN];
        DatagramPacket datagram = new DatagramPacket(receiveBuffer, MAX_LEN);
        this.receive(datagram);
        return new DatagramMessage(new String(receiveBuffer).trim(), datagram.getAddress(), datagram.getPort());
   }
}
