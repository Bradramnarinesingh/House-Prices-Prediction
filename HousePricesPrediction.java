import java.io.*;
import java.util.*;

public class HousePricesPrediction {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String filePath = "Average_Resale_Home_Prices.csv"; // Change to your actual file path

        Map<Integer, List<Double>> data = loadData(filePath);

        String option = "";
        while (!"0".equals(option)) {
            System.out.println("\n\nMenu:");
            System.out.println("-------------------------------------");
            System.out.println("0 - Exit Program");
            System.out.println("1 - Calculate percent increase of 2 years:");
            System.out.println("2 - Calculate the average resale price of all types of homes in:");
            System.out.println("-------------------------------------");
            System.out.print("Enter your choice: ");
            option = sc.nextLine();

            switch (option) {
                case "1":
                    handlePercentageIncrease(data, sc);
                    break;
                case "2":
                    handleAveragePrice(data, sc);
                    break;
                case "0":
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        sc.close();
    }

    private static Map<Integer, List<Double>> loadData(String filePath) {
        Map<Integer, List<Double>> yearToPrices = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Skip header line
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                int year = Integer.parseInt(values[0]); // Assuming the first column is the year
                double price = Double.parseDouble(values[1]); // Assuming the second column is the price
                yearToPrices.computeIfAbsent(year, k -> new ArrayList<>()).add(price);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return yearToPrices;
    }

    private static void handlePercentageIncrease(Map<Integer, List<Double>> data, Scanner sc) {
        System.out.print("Enter the first year: ");
        int firstYear = Integer.parseInt(sc.nextLine());
        System.out.print("Enter the second year: ");
        int secondYear = Integer.parseInt(sc.nextLine());

        if (data.containsKey(firstYear) && data.containsKey(secondYear)) {
            double avgFirstYear = data.get(firstYear).stream().mapToDouble(a -> a).average().orElse(Double.NaN);
            double avgSecondYear = data.get(secondYear).stream().mapToDouble(a -> a).average().orElse(Double.NaN);
            double increase = ((avgSecondYear - avgFirstYear) / avgFirstYear) * 100;
            System.out.printf("The percentage increase from %d to %d is %.2f%%.\n", firstYear, secondYear, increase);
        } else {
            System.out.println("Data for one or both of the years is missing.");
        }
    }

    private static void handleAveragePrice(Map<Integer, List<Double>> data, Scanner sc) {
        System.out.print("Enter the year: ");
        int year = Integer.parseInt(sc.nextLine());
        if (data.containsKey(year)) {
            double average = data.get(year).stream().mapToDouble(a -> a).average().orElse(Double.NaN);
            System.out.printf("The average resale price of homes in %d is $%.2f.\n", year, average);
        } else {
            System.out.println("No data available for the specified year to calculate the average price.");
        }
    }
}
