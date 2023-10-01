import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TraceNet {
    static volatile boolean scanInProgress = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("TraceNet");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setBackground(Color.DARK_GRAY);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.DARK_GRAY);

        JButton scanButton = new JButton("Scan");
        scanButton.setFont(new Font("Arial", Font.BOLD, 18));
        scanButton.setBackground(Color.GREEN);
        scanButton.setForeground(Color.WHITE);
        scanButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 18));
        cancelButton.setBackground(Color.RED);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton endProgramButton = new JButton("End Program");
        endProgramButton.setFont(new Font("Arial", Font.BOLD, 18));
        endProgramButton.setBackground(Color.BLUE);
        endProgramButton.setForeground(Color.WHITE);
        endProgramButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea resultsArea = new JTextArea();
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        resultsArea.setForeground(Color.WHITE);
        resultsArea.setBackground(Color.BLACK);
        resultsArea.setWrapStyleWord(true);
        resultsArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        scanButton.addActionListener(e -> {
            String localIp = GetLocalIp.getAllLocalIps().get(0);

            resultsArea.setText("");
            scanInProgress = true;
            scanButton.setEnabled(false);

            ExecutorService executor = Executors.newFixedThreadPool(20);

            String baseIp = localIp.substring(0, localIp.lastIndexOf('.') + 1);
            for (int i = 1; i <= 254; i++) {
                final String currentIp = baseIp + i;
                executor.execute(() -> {
                    if (!scanInProgress) return;

                    try {
                        InetAddress currentInetAddress = InetAddress.getByName(currentIp);
                        if (currentInetAddress.isReachable(1000)) {
                            String hostname = currentInetAddress.getCanonicalHostName();
                            SwingUtilities.invokeLater(() -> resultsArea.append("Device at " + currentIp + " (" + hostname + ") is reachable.\n"));
                        } else {
                            SwingUtilities.invokeLater(() -> resultsArea.append("No device at " + currentIp + "\n"));
                        }
                    } catch (IOException ex) {
                        SwingUtilities.invokeLater(() -> resultsArea.append("Error scanning " + currentIp + "\n"));
                    }
                });
            }

            executor.shutdown();

            new Thread(() -> {
                try {
                    while (!executor.isTerminated()) {
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                SwingUtilities.invokeLater(() -> scanButton.setEnabled(true));
            }).start();
        });

        cancelButton.addActionListener(e -> scanInProgress = false);

        endProgramButton.addActionListener(e -> {
            scanInProgress = false;
            frame.dispose();
            System.exit(0);
        });

        panel.add(Box.createVerticalStrut(50));
        panel.add(scanButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(cancelButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(endProgramButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(50));

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}



