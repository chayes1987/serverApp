import java.net.InetAddress;

public class DatagramMessage{
   private String message;
   private InetAddress senderAddress;
   private int senderPort;

   public DatagramMessage(String message, InetAddress address, int port) {
      this.message = message;
      this.senderAddress = address;
      this.senderPort = port;
   }

   public String getMessage( ) {
      return this.message;
   }

   public InetAddress getAddress( ) {
      return this.senderAddress;
   }

   public int getPort( ) {
      return this.senderPort;
   }
} 
