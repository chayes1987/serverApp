import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FTPServer implements Runnable{
    private int port_number;
    private final boolean LISTENING = true;
    private MyServerDatagramSocket socket;
    private ServerGUI gui;


    public FTPServer(ServerGUI gui, int port_number){
        this.gui = gui;
        this.port_number = port_number;
    }

    public boolean initialize(){
        try{
            socket = new MyServerDatagramSocket(port_number);
            listen();
        }catch (SocketException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void listen() throws IOException {
        Thread t = new Thread(this);
        t.start();
    }

    private boolean createUploadDirectory(String username) throws IOException{
        String filename = "C:\\" + username;
        File userDirectory = new File(filename);
        gui.server_output.append("\nChecking if Directory '" + username + "' exists...");
        if (!userDirectory.exists()) {
            gui.server_output.append("\nDirectory '" + username + "' not found...");
            gui.server_output.append("\nCreating Directory '" + username + "'...");

            try{
                userDirectory.mkdir();
                gui.server_output.append("\nDirectory '" + username + "' created...");
            } catch(SecurityException se){
                gui.server_output.append("\nError creating directory..." + se.toString());
                return false;
            }
		}else{
            gui.server_output.append("\nDirectory '" + username + "' already exists...");
		}
        gui.server_output.append("\nServer Ready to serve " + username + "...");
        return true;
	}

    private boolean upload(String username, String filename, String fileContents) {
        String path = "C:\\" + username + "\\" + filename;
        byte[] file = fileContents.getBytes();
        try {
            Files.write(Paths.get(path), file);
            gui.server_output.append("\nFile '" + filename + "' created at " + path.replace(filename, "") + "...");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String download(String username, String filename) throws IOException {
        String path =  "C:\\" + username + "\\" + filename;
        File selectedFile = new File(path);
        if (!selectedFile.exists()) {
            gui.server_output.append("\nServer could not locate the file '" + filename + "'...");
            return "";
        }

        String file;
        try{
            file = new String(Files.readAllBytes(Paths.get(path)));
        }catch(IOException e){
            e.printStackTrace();
            return "";
        }
        gui.server_output.append("\nRequested file '" + filename + "' sent to client...");
        return file;
    }

    @Override
    public void run() {
        try {
            while (LISTENING) {
                DatagramMessage message = socket.receiveMessageAndSender();

                String[] args = message.getMessage().split("###");

                if (args[0].equals("LOG")) {
                    gui.server_output.append("\n" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) +
                            " - Request to Login received from " + args[1] + "...");
                    if (createUploadDirectory(args[1]))
                        socket.sendMessage(message.getAddress(), message.getPort(), ERROR_CODE.OK.toString());
                    else
                        socket.sendMessage(message.getAddress(), message.getPort(), ERROR_CODE.INTERNAL_SERVER_ERROR.toString());
                } else if (args[0].equals("UPL")) {
                    gui.server_output.append("\n" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) +
                            " - Request to Upload File received from " + args[1] + "...");
                    if (upload(args[1], args[2], args[3]))
                        socket.sendMessage(message.getAddress(), message.getPort(), ERROR_CODE.OK.toString());
                    else
                        socket.sendMessage(message.getAddress(), message.getPort(), ERROR_CODE.INTERNAL_SERVER_ERROR.toString());
                } else if (args[0].equals("DOW")) {
                    gui.server_output.append("\n" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) +
                            " - Request to Download File received from " + args[1] + "...");
                    String file = download(args[1], args[2]);
                    if (!file.isEmpty())
                        socket.sendMessage(message.getAddress(), message.getPort(), file);
                    else
                        socket.sendMessage(message.getAddress(), message.getPort(), ERROR_CODE.FILE_NOT_FOUND.toString());
                } else if (args[0].equals("OUT")){
                    gui.server_output.append("\n" + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) +
                            " - Request to Logout received from " + args[1] + "...\nLog out successful...");
                    socket.sendMessage(message.getAddress(), message.getPort(), ERROR_CODE.OK.toString());
                }
            }
        } catch (IOException e) {
            // Do Nothing
        }
    }

    public void shutdown(){
        socket.close();
    }
}
