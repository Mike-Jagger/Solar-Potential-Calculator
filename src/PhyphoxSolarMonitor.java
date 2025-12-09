import java.util.List;
import java.util.Scanner;

public class PhyphoxSolarMonitor {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Connect to Phyphox
        System.out.print("Enter Phyphox base URL (e.g., http://192.168.1.5:8080): ");
        String baseUrl = scanner.nextLine().trim();
        PhyphoxClient client = new PhyphoxClient(baseUrl);

        Double testLux = client.fetchIlluminance();
        if (testLux == null) {
            System.err.println("Error: Could not connect to Phyphox.");
            return;
        }
        System.out.println("Connected! Current Lux: " + testLux);

        // 2. Rooftop setup
        System.out.print("Enter rooftop area (m^2): ");
        double area = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter shading factor (0–1): ");
        double shading = Double.parseDouble(scanner.nextLine());
        System.out.print("Enter orientation (e.g., South): ");
        String orientation = scanner.nextLine();
        Rooftop rooftop = new Rooftop(area, shading, orientation);

        // 3. Panel selection
        System.out.println("Choose panel type: 1=Polycrystalline, 2=Monocrystalline");
        String choice = scanner.nextLine();
        SolarPanel panel;
        if (choice.equals("1")) {
            panel = new PolycrystallinePanel(0.18, rooftop.getEffectiveArea(), 0.05);
        } else {
            panel = new MonocrystallinePanel(0.20, rooftop.getEffectiveArea(), 0.02);
        }

        // 4. Calibration
        Calibration calibration = new Calibration();
        System.out.println("Calibration: 1=Standard, 2=Reference");
        String calChoice = scanner.nextLine();
        if (calChoice.equals("2")) {
            System.out.print("Enter reference DNI (W/m^2): ");
            double referenceDni = Double.parseDouble(scanner.nextLine());
            calibration.calibrate(referenceDni, client.fetchIlluminance());
        }

        // 5. SolarCalculator setup
        List<Appliance> appliances = ApplianceDatabase.getAppliances();
        SolarCalculator calculator = new SolarCalculator(rooftop, panel, appliances, calibration.getFactor());

        // 6. Monitoring loop
        System.out.println("Starting Live Monitor");
        while (true) {
            try {
                Double lux = client.fetchIlluminance();
                if (lux != null) {
                    double power = calculator.calculatePower(lux);   
                    System.out.println("Lux: " + lux);
                    System.out.println("Power Output: " + power + " W");
                    calculator.matchAppliances(power);              
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("Connection lost: " + e.getMessage());
                break;
            }
        }
    }
}
