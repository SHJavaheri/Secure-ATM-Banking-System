package gui;

import atmclient.ATMClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class DeleteAccountPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextArea statusArea;
    private ATMClient client;

    public DeleteAccountPage(ATMClient client) {
        this.client = client;
        setTitle("Delete Account");
        setSize(400, 250);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        panel.add(confirmPasswordField);

        JButton deleteButton = new JButton("Delete Account");
        deleteButton.addActionListener(e -> confirmDelete());
        panel.add(deleteButton);

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> dispose());
        panel.add(backButton);

        add(panel, BorderLayout.CENTER);

        statusArea = new JTextArea(2, 30);
        statusArea.setEditable(false);
        add(new JScrollPane(statusArea), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void confirmDelete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account? This cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            deleteUser();
        }
    }

    private void deleteUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusArea.setText("Username and password cannot be empty.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusArea.setText("Passwords do not match. Try again.");
            return;
        }

        try {
            client.getOutput().println("3");
            client.getOutput().println(username);
            client.getOutput().println(password);

            String response = client.getInput().readLine();
            statusArea.setText(response);
        } catch (IOException e) {
            statusArea.setText("Error communicating with server.");
            e.printStackTrace();
        }
    }
}
