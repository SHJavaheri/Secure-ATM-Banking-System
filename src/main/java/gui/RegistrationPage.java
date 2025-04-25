package gui;

import atmclient.ATMClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RegistrationPage extends JFrame {
    private JTextField usernameField, depositField;
    private JPasswordField passwordField, confirmPasswordField;
    private JTextArea statusArea;
    private ATMClient client;

    public RegistrationPage(ATMClient client) {
        this.client = client;
        setTitle("Register New Account");
        setSize(400, 300);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
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

        panel.add(new JLabel("Initial Deposit:"));
        depositField = new JTextField();  // FIX: This was missing
        panel.add(depositField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> registerUser());
        panel.add(registerButton);

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> dispose());
        panel.add(backButton);

        add(panel, BorderLayout.CENTER);

        statusArea = new JTextArea(2, 30);
        statusArea.setEditable(false);
        add(new JScrollPane(statusArea), BorderLayout.SOUTH);

        setVisible(true);
    }

    private void registerUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String depositAmount = depositField.getText();  // FIX: Now correctly reads the deposit amount

        if (username.isEmpty() || password.isEmpty() || depositAmount.isEmpty()) {
            statusArea.setText("All fields must be filled.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            statusArea.setText("Passwords do not match. Try again.");
            return;
        }

        try {
            client.getOutput().println("2");
            client.getOutput().println(username);
            client.getOutput().println(password);
            client.getOutput().println(depositAmount);

            String response = client.getInput().readLine();
            statusArea.setText(response);
        } catch (IOException e) {
            statusArea.setText("Error communicating with server.");
            e.printStackTrace();
        }
    }
}