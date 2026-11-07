package StockProtofolioTracker;


import java.util.*;

class Stock {
    private String symbol;
    private String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}


class Transaction {
    private String symbol;
    private String type; 
    private int quantity;
    private double price;
    private Date timestamp;

    public Transaction(String symbol, String type, int quantity, double price) {
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = new Date();
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + type + " " + quantity + " shares of " + symbol + " at ₹" + String.format("%.2f", price);
    }
}

class Portfolio {
    private double cashBalance;
    private Map<String, Integer> holdings; 
    private List<Transaction> history;

    public Portfolio(double initialBalance) {
        this.cashBalance = initialBalance;
        this.holdings = new HashMap<>();
        this.history = new ArrayList<>();
    }

    public double getCashBalance() { return cashBalance; }
    public Map<String, Integer> getHoldings() { return holdings; }
    public List<Transaction> getHistory() { return history; }

    public void buyStock(Stock stock, int quantity) {
        double totalCost = stock.getPrice() * quantity;
        if (cashBalance >= totalCost) {
            cashBalance -= totalCost;
            holdings.put(stock.getSymbol(), holdings.getOrDefault(stock.getSymbol(), 0) + quantity);
            history.add(new Transaction(stock.getSymbol(), "BUY", quantity, stock.getPrice()));
            System.out.println("Successfully bought " + quantity + " shares of " + stock.getSymbol() + ".");
        } else {
            System.out.println("Insufficient funds to complete this purchase.");
        }
    }

    public void sellStock(Stock stock, int quantity) {
        int currentQty = holdings.getOrDefault(stock.getSymbol(), 0);
        if (currentQty >= quantity) {
            cashBalance += stock.getPrice() * quantity;
            holdings.put(stock.getSymbol(), currentQty - quantity);
            if (holdings.get(stock.getSymbol()) == 0) {
                holdings.remove(stock.getSymbol());
            }
            history.add(new Transaction(stock.getSymbol(), "SELL", quantity, stock.getPrice()));
            System.out.println("Successfully sold " + quantity + " shares of " + stock.getSymbol() + ".");
        } else {
            System.out.println("You do not own enough shares to sell this quantity.");
        }
    }

    public void displayPortfolio(Map<String, Stock> market) {
        System.out.println("\n--- Portfolio Performance ---");
        System.out.println("Cash Balance: ₹" + String.format("%.2f", cashBalance));
        double totalStockValue = 0.0;
        System.out.println("Holdings:");
        if (holdings.isEmpty()) {
            System.out.println("  No stocks owned currently.");
        } else {
            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                String symbol = entry.getKey();
                int qty = entry.getValue();
                double currentPrice = market.get(symbol).getPrice();
                double value = qty * currentPrice;
                totalStockValue += value;
                System.out.println("  - " + symbol + ": " + qty + " shares @ ₹" + String.format("%.2f", currentPrice) + " = ₹" + String.format("%.2f", value));
            }
        }
        System.out.println("Total Portfolio Value: ₹" + String.format("%.2f", (cashBalance + totalStockValue)));
    }
}

public class StockProtofolioTracker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Stock> market = new HashMap<>();
        
        market.put("RELIANCE", new Stock("RELIANCE", "Reliance Industries Ltd.", 2950.00));
        market.put("TCS", new Stock("TCS", "Tata Consultancy Services Ltd.", 3880.50));
        market.put("INFY", new Stock("INFY", "Infosys Ltd.", 1520.00));
        market.put("HDFCBANK", new Stock("HDFCBANK", "HDFC Bank Ltd.", 1640.75));

        Portfolio userPortfolio = new Portfolio(100000.00);

        while (true) {
            System.out.println("\n=== Indian Stock Trading Platform ===");
            System.out.println("1. Display Market Data");
            System.out.println("2. View Portfolio & Performance");
            System.out.println("3. Buy Stock");
            System.out.println("4. Sell Stock");
            System.out.println("5. View Transaction History");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next(); 
                continue;
            }

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("\n--- Indian Market Data (NSE/BSE) ---");
                    for (Stock stock : market.values()) {
                        System.out.println(stock.getSymbol() + " (" + stock.getName() + ") - ₹" + String.format("%.2f", stock.getPrice()));
                    }
                    break;
                case 2:
                    userPortfolio.displayPortfolio(market);
                    break;
                case 3:
                    System.out.print("Enter stock symbol to buy (e.g., RELIANCE): ");
                    String buySymbol = scanner.next().toUpperCase();
                    if (market.containsKey(buySymbol)) {
                        System.out.print("Enter quantity: ");
                        int qty = scanner.nextInt();
                        if (qty > 0) {
                            userPortfolio.buyStock(market.get(buySymbol), qty);
                        } else {
                            System.out.println("Quantity must be greater than zero.");
                        }
                    } else {
                        System.out.println("Stock symbol not found in market.");
                    }
                    break;
                case 4:
                    System.out.print("Enter stock symbol to sell: ");
                    String sellSymbol = scanner.next().toUpperCase();
                    if (market.containsKey(sellSymbol)) {
                        System.out.print("Enter quantity: ");
                        int qty = scanner.nextInt();
                        if (qty > 0) {
                            userPortfolio.sellStock(market.get(sellSymbol), qty);
                        } else {
                            System.out.println("Quantity must be greater than zero.");
                        }
                    } else {
                        System.out.println("Stock symbol not found in market.");
                    }
                    break;
                case 5:
                    System.out.println("\n--- Transaction History ---");
                    List<Transaction> history = userPortfolio.getHistory();
                    if (history.isEmpty()) {
                        System.out.println("No transactions made yet.");
                    } else {
                        for (Transaction t : history) {
                            System.out.println(t);
                        }
                    }
                    break;
                case 6:
                    System.out.println("Exiting platform. Happy trading!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please select between 1 and 6.");
            }
        }
    }
}