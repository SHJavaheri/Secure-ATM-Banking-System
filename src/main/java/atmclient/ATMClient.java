package atmclient;

import gui.RegistrationPage;
import gui.DeleteAccountPage;
import gui.TransactionPage;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ATMClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 5000;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea statusArea;

    public ATMClient() {
        setupUI();
        connectToServer();
    }

    private void setupUI() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Secure Banking System - ATM");
        frame.setSize(450, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> loginUser());
        panel.add(loginButton, gbc);

        gbc.gridx = 1;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> new RegistrationPage(this));
        panel.add(registerButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JButton deleteButton = new JButton("Delete Account");
        deleteButton.addActionListener(e -> new DeleteAccountPage(this));
        panel.add(deleteButton, gbc);

        gbc.gridx = 1;
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        panel.add(exitButton, gbc);

        addButtonStyling(loginButton);
        addButtonStyling(registerButton);
        addButtonStyling(deleteButton);
        addButtonStyling(exitButton);

        addInputFieldStyling(usernameField);
        addInputFieldStyling(passwordField);

        frame.add(panel, BorderLayout.CENTER);

        statusArea = new JTextArea(3, 30);
        statusArea.setEditable(false);
        frame.add(new JScrollPane(statusArea), BorderLayout.SOUTH);

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void addButtonStyling(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    private void addInputFieldStyling(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(new Color(50, 50, 50));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            statusArea.setText("Connected to the bank server.");
        } catch (IOException e) {
            statusArea.setText("Error connecting to server.");
            e.printStackTrace();
        }
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusArea.setText("Please enter both username and password.");
            return;
        }

        try {
            output.println("1");
            output.println(username);
            output.println(password);

            String response = input.readLine();
            if (response.contains("successful")) {
                statusArea.setText("Login successful! Opening transaction panel...");
                new TransactionPage(this, username);
                frame.dispose();
            } else {
                statusArea.setText("Invalid credentials. Please try again.");
                usernameField.setText("");
                passwordField.setText("");
            }
        } catch (IOException e) {
            statusArea.setText("Error communicating with server.");
            e.printStackTrace();
        }
    }

    public PrintWriter getOutput() {
        return output;
    }

    public BufferedReader getInput() {
        return input;
    }

    public static void main(String[] args) {
        new ATMClient();
    }
}