import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SupermarketBillingSystem extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private static final String USER_DATA_FILE = "user_data.txt";
    private static final String STOCK_DATA_FILE = "stock_data.txt";

    private static Map<String, String> userCredentials;
    private static Map<String, ProductInfo> stockData;
    private Scanner scanner;

    public SupermarketBillingSystem() {
        setTitle("Supermarket Billing System");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);

        usernameField = new JTextField();
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        panel.add(loginButton);

        add(panel);
        scanner = new Scanner(System.in);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);

            if ("admin".equals(username) && "password".equals(password)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                startBillingSystem();
            }
          else if("rithika".equals(username) && "password".equals(password))
          {
         JOptionPane.showMessageDialog(this, "Login Successful!");
                startBillingSystem();
           }
          else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }
        }
    }

    private void startBillingSystem() {
        userCredentials = loadUserCredentials();
        stockData = loadStockData();

        while (true) {
            String choice = JOptionPane.showInputDialog(
                    "Welcome to Supermarket Billing System:\n" +
                            "1. Manager Login\n" +
                            "2. Cashier Login\n" +
                            "3. Exit");

            if (choice == null) {
                saveUserCredentials();
                saveStockData();
                JOptionPane.showMessageDialog(this, "Exiting Supermarket Billing System.");
                dispose();
                break;
            }

            switch (choice) {
                case "1":
                    managerLogin();
                    break;
                case "2":
                    cashierLogin();
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Invalid choice. Please try again.");
            }
        }
    }

    private void managerLogin() {
        String username = getDynamicUsername("manager");

        if (userCredentials.containsKey(username)) {
            String password = userCredentials.get(username);
            String enteredPassword = getPasswordInput();

            if (enteredPassword.equals(password)) {
                JOptionPane.showMessageDialog(this, "Manager login successful.");
                managerActivities();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid password. Login failed.");
            }
        } else {
            String password = getPasswordInput();
            userCredentials.put(username, password);
            JOptionPane.showMessageDialog(this, "New manager account created. Login successful.");
            managerActivities();
        }
    }

    private void cashierLogin() {
        String username = getDynamicUsername("cashier");

        if (userCredentials.containsKey(username)) {
            String password = userCredentials.get(username);
            String enteredPassword = getPasswordInput();

            if (enteredPassword.equals(password)) {
                JOptionPane.showMessageDialog(this, "Cashier login successful.");
                cashierActivities();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid password. Login failed.");
            }
        } else {
            String password = getPasswordInput();
            userCredentials.put(username, password);
            JOptionPane.showMessageDialog(this, "New cashier account created. Login successful.");
            cashierActivities();
        }
    }

    private String getDynamicUsername(String userType) {
        return JOptionPane.showInputDialog("Enter " + userType + " username:");
    }

    private String getPasswordInput() {
        JPasswordField passwordField = new JPasswordField();
        int option = JOptionPane.showConfirmDialog(this, passwordField, "Enter password", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            return new String(passwordField.getPassword());
        } else {
            return null;
        }
    }

    private void managerActivities() {
        while (true) {
            String choice = JOptionPane.showInputDialog(
                    "Manager Activities:\n" +
                            "1. Enter Stock Details\n" +
                            "2. Stock Report\n" +
                            "3. Exit");

            if (choice == null) {
                saveStockData();
                JOptionPane.showMessageDialog(this, "Exiting manager activities.");
                break;
            }

            switch (choice) {
                case "1":
                    enterStockDetails();
                    break;
                case "2":
                    stockReport();
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Invalid choice. Please try again.");
            }
        }
    }

    private void enterStockDetails() {
        String productName = JOptionPane.showInputDialog("Enter product name:");
        String barcode = JOptionPane.showInputDialog("Enter product barcode:");
        int quantity = Integer.parseInt(JOptionPane.showInputDialog("Enter stock quantity:"));
        double price = Double.parseDouble(JOptionPane.showInputDialog("Enter price for one quantity:"));

        if (stockData.containsKey(barcode)) {
            ProductInfo existingProduct = stockData.get(barcode);
            existingProduct.setQuantity(existingProduct.getQuantity() + quantity);
            existingProduct.setPrice(price);
        } else {
            stockData.put(barcode, new ProductInfo(productName, quantity, price));
        }

        JOptionPane.showMessageDialog(this, "Stock details updated.");
    }

    private void stockReport() {
        StringBuilder report = new StringBuilder("Stock Report:\n");
        report.append(String.format("%-20s %-15s %-15s %-15s%n",
                "Product Name", "Barcode", "Stock Quantity", "Price (Per Unit)"));

        for (Map.Entry<String, ProductInfo> entry : stockData.entrySet()) {
            ProductInfo productInfo = entry.getValue();
            report.append(String.format("%-20s %-15s %-15d %-15.2f%n",
                    productInfo.getProductName(), entry.getKey(),
                    productInfo.getQuantity(), productInfo.getPrice()));
        }

        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(report.toString())));
    }

    private void cashierActivities() {
        Map<String, BillInfo> billMap = new HashMap<>();

        while (true) {
            String barcode = JOptionPane.showInputDialog("Enter product barcode (0 to exit):");

            if ("0".equals(barcode)) {
                break;
            }

            if (stockData.containsKey(barcode)) {
                int quantity = Integer.parseInt(JOptionPane.showInputDialog(
                        "Enter quantity for " + stockData.get(barcode).getProductName() + ":"));

                ProductInfo productInfo = stockData.get(barcode);
                if (quantity <= productInfo.getQuantity()) {
                    productInfo.setQuantity(productInfo.getQuantity() - quantity);

                    if (!billMap.containsKey(barcode)) {
                        billMap.put(barcode, new BillInfo(quantity, productInfo.getPrice()));
                    } else {
                        billMap.get(barcode).increaseQuantity(quantity);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient stock. Please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid barcode. Please try again.");
            }
        }

        double totalAmount = 0;

        StringBuilder billingDetails = new StringBuilder("\nBilling Details:\n");
        billingDetails.append(String.format("%-20s %-10s %-10s %-10s%n",
                "Product Name", "Quantity", "Price", "Total Cost"));

        for (Map.Entry<String, BillInfo> entry : billMap.entrySet()) {
            String barcode = entry.getKey();
            BillInfo billInfo = entry.getValue();
            ProductInfo productInfo = stockData.get(barcode);

            billingDetails.append(String.format("%-20s %-10d %-10.2f %-10.2f%n",
                    productInfo.getProductName(), billInfo.getQuantity(),
                    productInfo.getPrice(), billInfo.calculateTotalAmount()));

            totalAmount += billInfo.calculateTotalAmount();
        }

        double gstAmount = totalAmount * 0.18;
        double finalAmount = totalAmount + gstAmount;

        billingDetails.append(String.format("%nTotal Cost of All Products: %.2f%n", totalAmount));
        billingDetails.append(String.format("GST (18%%): %.2f%n", gstAmount));
        billingDetails.append(String.format("Final Amount: %.2f%n", finalAmount));

        billingDetails.append("\nPayment Options:\n");
        billingDetails.append("1. Cash\n");
        billingDetails.append("2. Card\n");
        billingDetails.append("3. UPI\n");

        int paymentOption = Integer.parseInt(JOptionPane.showInputDialog(
                billingDetails.toString() + "Enter payment option (1/2/3):"));

        switch (paymentOption) {
            case 1:
                JOptionPane.showMessageDialog(this, "Cash payment successful. Thank you!");
                break;
            case 2:
                String cardPin = JOptionPane.showInputDialog("Enter card password/PIN:");
                cardPayment(finalAmount, cardPin);
                break;
            case 3:
                upiPayment(finalAmount);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid payment option. Transaction failed.");
        }
    }

    private void cardPayment(double amount, String cardPin) {
        // Implement card payment logic
        JOptionPane.showMessageDialog(this, "Processing card payment for amount: " + amount);
    }

    private void upiPayment(double amount) {
        // Implement UPI payment logic
        JOptionPane.showMessageDialog(this, "Processing UPI payment for amount: " + amount);
    }

    private static Map<String, String> loadUserCredentials() {
        Map<String, String> map = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                map.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            // File not found or error reading, ignore
        }

        return map;
    }

    private static void saveUserCredentials() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE))) {
            for (Map.Entry<String, String> entry : userCredentials.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, ProductInfo> loadStockData() {
        Map<String, ProductInfo> map = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(STOCK_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                map.put(parts[0], new ProductInfo(parts[1],
                        Integer.parseInt(parts[2]),
                        Double.parseDouble(parts[3])));
            }
        } catch (IOException e) {
            // File not found or error reading, ignore
        }

        return map;
    }

    private static void saveStockData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STOCK_DATA_FILE))) {
            for (Map.Entry<String, ProductInfo> entry : stockData.entrySet()) {
                String barcode = entry.getKey();
                ProductInfo productInfo = entry.getValue();

                writer.write(barcode + "," + productInfo.getProductName() + "," +
                        productInfo.getQuantity() + "," + productInfo.getPrice());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ProductInfo {
        private String productName;
        private int quantity;
        private double price;

        public ProductInfo(String productName, int quantity, double price) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    private static class BillInfo {
        private int quantity;
        private double unitPrice;

        public BillInfo(int quantity, double unitPrice) {
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public int getQuantity() {
            return quantity;
        }

        public void increaseQuantity(int quantity) {
            this.quantity += quantity;
        }

        public double calculateTotalAmount() {
            return quantity * unitPrice;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SupermarketBillingSystem system = new SupermarketBillingSystem();
            system.setVisible(true);
        });
    }
}
