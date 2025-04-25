package bankserver;

import utils.EncryptionUtils;
import utils.KeyManager;
import utils.Logger;
import utils.SecurityUtils;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private static final HashMap<String, String> users = BankServer.getUsers();
    private static final HashMap<String, Double> balances = BankServer.getBalances();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            while (true) {

                String choice = input.readLine();

                switch (choice) {
                    case "1":
                        handleLogin();
                        break;
                    case "2":
                        handleRegister();
                        break;
                    case "3":
                        handleDeleteAccount();
                        break;
                    case "4":
                        socket.close();
                        return;
                    default:
                        output.println("Invalid option.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin() throws IOException {
        String username = input.readLine();
        String password = input.readLine();

        if (users.containsKey(username) && SecurityUtils.verifyPassword(password, users.get(username))) {
            output.println("Authentication successful!");
            Logger.logTransaction(username, "LOGIN SUCCESS");
            handleTransactions(username);
        } else {
            output.println("Invalid credentials.");
        }
    }

    private void handleRegister() throws IOException {
        String username = input.readLine();
        String password = input.readLine();
        String depositAmountStr = input.readLine();

        if (users.containsKey(username)) {
            output.println("Username already taken.");
            return;
        }

        try {
            double initialDeposit = Double.parseDouble(depositAmountStr);
            if (initialDeposit < 0) {
                output.println("Deposit amount must be at least $0.");
                return;
            }

            String hashedPassword = SecurityUtils.hashPassword(password);
            users.put(username, hashedPassword);
            balances.put(username, initialDeposit);
            BankServer.saveDatabase();
            output.println("Registration successful!");
            Logger.logTransaction(username, "ACCOUNT CREATED with $" + initialDeposit);
        } catch (NumberFormatException e) {
            output.println("Invalid deposit amount.");
        }
    }


    private void handleDeleteAccount() throws IOException {
        String username = input.readLine();
        String password = input.readLine();

        if (users.containsKey(username) && SecurityUtils.verifyPassword(password, users.get(username))) {
            users.remove(username);
            balances.remove(username);
            BankServer.saveDatabase();
            output.println("Account deleted successfully.");
            Logger.logTransaction(username, "ACCOUNT DELETED");
        } else {
            output.println("Invalid credentials.");
        }
    }

    private void handleTransactions(String username) throws IOException {
        SecretKey encryptionKey = KeyManager.getEncryptionKey();
        SecretKey macKey = KeyManager.getMacKey();

        while (true) {
            if (socket.isClosed()) {
                System.out.println("[SERVER] Client socket closed. Exiting transaction handler.");
                return;
            }

            String encryptedRequest = input.readLine();
            if (encryptedRequest == null) {
                System.out.println("[SERVER] Client disconnected.");
                return;
            }

            String receivedMac = input.readLine();

            // Decrypt the request
            String request;
            try {
                request = EncryptionUtils.decryptAES(encryptedRequest, encryptionKey);
            } catch (Exception e) {
                System.err.println("[SERVER] Error decrypting request: " + e.getMessage());
                output.println("Decryption failed.");
                continue;
            }

            String[] parts = request.split(":");
            String command = parts[0];

            String response;
            switch (command.toLowerCase()) {
                case "deposit":
                    double depositAmount = Double.parseDouble(parts[1]);
                    balances.put(username, balances.get(username) + depositAmount);
                    response = "Deposit successful! New Balance: $" + balances.get(username);
                    Logger.logTransaction(username, "DEPOSIT: $" + depositAmount);
                    sendSecureResponse(response, encryptionKey, macKey);
                    BankServer.saveDatabase();
                    break;

                case "withdraw":
                    double withdrawAmount = Double.parseDouble(parts[1]);
                    if (withdrawAmount > balances.get(username)) {
                        sendSecureResponse("Insufficient funds.", encryptionKey, macKey);
                    } else {
                        balances.put(username, balances.get(username) - withdrawAmount);
                        response = "Withdrawal successful! New Balance: $" + balances.get(username);
                        Logger.logTransaction(username, "WITHDRAWAL: $" + withdrawAmount);
                        sendSecureResponse(response, encryptionKey, macKey);
                        BankServer.saveDatabase();
                    }
                    break;

                case "balance":
                    response = "Current Balance: $" + balances.get(username);
                    Logger.logTransaction(username, "BALANCE CHECK");
                    sendSecureResponse(response, encryptionKey, macKey);
                    break;

                case "exit":
                    System.out.println("[SERVER] Client exiting.");
                    return;

                default:
                    sendSecureResponse("Invalid command.", encryptionKey, macKey);
                    break;
            }
        }
    }

    private void sendSecureResponse(String message, SecretKey encryptionKey, SecretKey macKey) throws IOException {
        String encryptedMessage = EncryptionUtils.encryptAES(message, encryptionKey);
        String mac = SecurityUtils.generateMac(encryptedMessage, macKey);

        output.println(encryptedMessage);
        output.println(mac);
        output.flush();  // Ensure data is sent immediately
    }
}