# 💳 Secure ATM Banking System

A secure Java-based ATM and Bank Server application with encrypted communication, modern GUI, and full transaction support.

## 🔐 Overview

This project simulates a secure banking system where users can:
- Register and create new accounts with hashed passwords
- Log in securely using encrypted credentials
- Deposit and withdraw funds
- Check their balance
- Delete their accounts
- Log all activity (plain and encrypted) with timestamps

---

## 🖥 Technologies Used

- **Java (JDK 17)** – Core development language  
- **Java Swing + FlatLaf** – Modern, dark-mode UI design  
- **AES Encryption** – Secure communication using symmetric encryption  
- **Message Authentication Code (MAC)** – Integrity validation  
- **SHA-256** – Password hashing  
- **Socket Programming** – Client-server connection over TCP  
- **File I/O** – Persistent account and audit log storage  
- **Git & GitHub** – Version control and code hosting  

---

## 🛠 Features

✅ AES-encrypted client-server communication  
✅ Dynamic master key generation with MAC verification  
✅ Secure user registration, login, and deletion  
✅ Full transaction flow: deposit, withdraw, balance  
✅ Persistent database via `database.txt`  
✅ Audit logging in both plain (`audit_log.txt`) and encrypted (`audit_log_encrypted.txt`) form  
✅ Responsive and modern GUI using FlatLaf dark theme  

---

## 🧪 How to Run

1. Clone the repository:
```bash
git clone https://github.com/your-username/Secure-ATM-Banking-System.git

Open in IntelliJ or your favorite IDE

Run BankServer.java first

Then run ATMClient.java

Use the GUI to register, log in, and perform transactions
```

## 📖 Authors
```bash
Hamid Javaheri (Me)

Syed Ammar Ali

Simrat Gill

Mohammad Al-Shalabi
```
