package gui;

import atmclient.ATMClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TransactionPage extends JFrame {
    private JTextArea statusArea;
    private ATMClient client;
    private String username;

    public TransactionPage(ATMClient client, String username) {
        this.client = client;
        this.username = username;
        setTitle("Banking Transactions");
        setSize(400, 300);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> handleTransaction("deposit"));
        panel.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(e -> handleTransaction("withdraw"));
        panel.add(withdrawButton);

        JButton balanceButton = new JButton("Check Balance");
        balanceButton.addActionListener(e -> checkBalance());
        panel.add(balanceButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        panel.add(logoutButton);

        add(panel, BorderLayout.CENTER);

        statusArea = new JTextArea(3, 30);
        statusArea.setEditable(false);
        add(new JScrollPane(statusArea), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleTransaction(String type) {
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount:");
        if (amountStr == null || amountStr.isEmpty()) return;

        try {
            double amount = Double.parseDouble(amountStr);
            client.getOutput().println(type);
            client.getOutput().println(amount);

            String response = client.getInput().readLine();
            statusArea.setText(response);
        } catch (NumberFormatException e) {
            statusArea.setText("Invalid amount entered.");
        } catch (IOException e) {
            statusArea.setText("Error communicating with server.");
            e.printStackTrace();
        }
    }

    private void checkBalance() {
        try {
            client.getOutput().println("balance");
            String response = client.getInput().readLine();
            statusArea.setText(response);
        } catch (IOException e) {
            statusArea.setText("Error communicating with server.");
            e.printStackTrace();
        }
    }

    private void logout() {
        client.getOutput().println("exit");
        JOptionPane.showMessageDialog(this, "Logged out successfully.");
        new ATMClient();  // Return to login screen
        dispose();
    }
}