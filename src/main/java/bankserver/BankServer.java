package bankserver;

import utils.Logger;
import utils.SecurityUtils;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;


public class BankServer {
    private static final int PORT = 5000;
    private static final String DATABASE_FILE = "database.txt";
    private static HashMap<String, String> users = new HashMap<>();
    private static HashMap<String, Double> balances = new HashMap<>();

    public static void main(String[] args) {
        loadDatabase();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Bank Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                users.put(parts[0], parts[1]);
                balances.put(parts[0], Double.parseDouble(parts[2]));
            }
            System.out.println("Database loaded successfully.");
        } catch (IOException e) {
            System.out.println("No database found. Creating a new database.");
        }
    }

    static void saveDatabase() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATABASE_FILE))) {
            for (String user : users.keySet()) {
                writer.println(user + ":" + users.get(user) + ":" + balances.get(user));
            }
            System.out.println("Database saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static HashMap<String, String> getUsers() {
        return users;
    }

    static HashMap<String, Double> getBalances() {
        return balances;
    }
}