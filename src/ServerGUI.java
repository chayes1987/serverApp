import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ServerGUI extends JFrame implements ActionListener {
    private JTextField server_port_number;
    JTextArea server_output;
    private JButton start_server;
    private FTPServer server;

    public ServerGUI(){
        buildAppWindow();
        buildServerGUI();
    }

    private void buildServerGUI() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(250, 150));
        p.setLayout(new GridLayout(3, 1));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Server Configuration"));
        JLabel l = new JLabel("Port Number");
        p.add(l);
        server_port_number = new JTextField("1000");
        p.add(server_port_number);
        start_server = new JButton("Start Server");
        start_server.addActionListener(this);
        p.add(start_server);
        add(p);
        p = new JPanel();
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Server Output"));
        p.setLayout(new GridLayout(1, 1));
        p.setPreferredSize(new Dimension(450, 150));
        server_output = new JTextArea();
        DefaultCaret caret = (DefaultCaret)server_output.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        server_output.setLineWrap(true);
        server_output.setEditable(false);
        p.add(new JScrollPane (server_output, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        add(p);
    }

    private void buildAppWindow() {
        setSize(750, 200);
        setResizable(false);
        setLayout(new FlowLayout());
        Calendar sysDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        setTitle("Server App" + " - " + sdf.format(sysDate.getTime()));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("Start Server")){
            if (server_port_number.getText().isEmpty()){
                JOptionPane.showMessageDialog(null, "Please enter the Port Number.");
            }else{
                server = new FTPServer(this, Integer.parseInt(server_port_number.getText()));
                if (server.initialize()) {
                    server_output.setText("FTP Server is running...");
                    start_server.setText("Stop Server");
                }else {
                    server_output.setText("Failed to connect to FTP Server...");
                }
            }
        }else if (e.getActionCommand().equals("Stop Server")){
            server.shutdown();
            server_output.setText("");
            start_server.setText("Start Server");
        }
    }
}
