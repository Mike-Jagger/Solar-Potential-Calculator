import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class PhyphoxSolarMonitor {
    private static final int MAX_RETRIES = 5;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Connect to Phyphox with Retry Logic
        System.out.print("Enter Phyphox base URL (e.g., http://192.168.1.5:8080): ");
        String baseUrl = scanner.nextLine().trim();
        PhyphoxClient client = new PhyphoxClient(baseUrl);

        if (!establishInitialConnection(client)) {
            System.err.println("\n\nFatal Error: Could not establish connection after multiple attempts. " +
                    "\n Please try running the app again and provide the right Phyphox base URL.");
            return;
        }

        // Rooftop setup
        System.out.println("\n--- Rooftop Configuration ---");
        double area = getValidPositiveDouble(scanner, "Enter rooftop area (m^2): ");
        double shading = getValidDouble(scanner, "Enter shading factor (0.0 to 1.0): ", 0, 1);
        String orientation = getValidOption(
                scanner,
                "Enter orientation (south [S], north [N], east [E], west [W]): ",
                List.of(new String[]{"S", "N", "E", "W"}));
        Rooftop rooftop = new Rooftop(area, shading, orientation);

        // Panel selection
        System.out.println("\n--- Panel Configuration ---");
        String choice = getValidOption(
                scanner,
                "Choose panel type: 1=Polycrystalline, 2=Monocrystalline: ",
                List.of(new String[]{"1", "2"}));
        SolarPanel panel;
        if (choice.equals("1")) {
            panel = new PolycrystallinePanel(0.18, rooftop.getEffectiveArea(), 0.05);
        } else {
            panel = new MonocrystallinePanel(0.20, rooftop.getEffectiveArea(), 0.02);
        }

        // Calibration
        System.out.println("\n--- Calibration ---");
        Calibration calibration = new Calibration();
        String calChoice = getValidOption(
                scanner,
                "Method: 1=Standard (Default), 2=Reference DNI: ",
                List.of(new String[]{"1", "2"}));

        if (calChoice.equals("2")) {
            double referenceDni = getValidPositiveDouble(scanner, "Enter reference DNI (W/m^2): ");
            Double lux = client.fetchIlluminance();
            if (lux != null) {
                calibration.calibrate(referenceDni, lux);
            } else {
                System.out.println("Warning: Could not fetch Lux for calibration. Using default.");
            }
        }

        // Setup Calculator
        List<Appliance> appliances = ApplianceDatabase.getAppliances();
        SolarCalculator calculator = new SolarCalculator(rooftop, panel, appliances, calibration.getFactor());

        // Monitoring loop with Reconnection Logic
        System.out.println("\n--- Starting Live Monitor (Ctrl+C to stop) ---");
        int consecutiveErrors = 0;

        while (true) {
            try {
                Double lux = client.fetchIlluminance();

                if (lux != null) {
                    consecutiveErrors = 0;
                    double power = calculator.calculatePower(lux);

                    // Clear console trick (may not work in all IDEs)
                    System.out.print("\033[H\033[2J");
                    System.out.flush();

                    System.out.println("==========================================");
                    System.out.printf("Current Illuminance : %.2f Lux%n", lux);
                    System.out.printf("Est. Power Output   : %.2f Watts%n", power);
                    System.out.println("------------------------------------------");
                    calculator.matchAppliances(power);
                    System.out.println("==========================================");
                } else {
                    throw new Exception("Received null data from sensor");
                }

                Thread.sleep(1000);

            } catch (Exception e) {
                consecutiveErrors++;
                System.out.println("Connection instability detected (" + consecutiveErrors + "/" + MAX_RETRIES + ")");

                if (consecutiveErrors >= MAX_RETRIES) {
                    System.err.println("Lost connection to Phyphox. Please check if the phone is awake.");
                    break;
                }

                // Simple backoff wait, TODO: A full implementation of the algorithm could proof useful
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        }
    }

    // Helper to check if input is a valid positive double
    private static double getValidPositiveDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val < 0) {
                    System.out.println("Value must be positive (>= 0). Please try again.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid positive number.");
            }
        }
    }

    // Helper to check if valid double is within min and max inclusive
    private static double getValidDouble(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val < min || val > max) {
                    System.out.printf("Value must be between %.4f and %.4f (inclusive). Please try again.%n", min, max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid number.");
            }
        }
    }

    // Helper to check if option given is within range of options (case-insensitive)
    // and returns the valid option in lowercase
    private static String getValidOption(Scanner scanner, String prompt, List<String> validOptions) {
        // Precompute lowercase set for efficiency
        Set<String> lowerOptions = new HashSet<>();
        for (String opt : validOptions) {
            lowerOptions.add(opt.toLowerCase());
        }

        while (true) {
            System.out.print(prompt);
            String val = scanner.nextLine().trim().toLowerCase();
            if (lowerOptions.contains(val)) {
                return val;
            }
            System.out.println("Invalid option. Valid options are: " + validOptions);
        }
    }

    // Helper for initial connection retry
    private static boolean establishInitialConnection(PhyphoxClient client) {
        System.out.print("Connecting");
        for (int i = 0; i < MAX_RETRIES; i++) {
            if (client.fetchIlluminance() != null) {
                System.out.println("\nConnected successfully!");
                return true;
            }
            System.out.print(".");
            try { Thread.sleep(1000 * (i + 1)); } catch (InterruptedException e) {} // Exponential backoff
        }
        return false;
    }
}