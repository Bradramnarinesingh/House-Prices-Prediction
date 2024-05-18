import java.util.Scanner;
import java.io.*;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.Evaluation;
import weka.core.converters.CSVLoader;
import weka.core.converters.ArffSaver;

public class Peel_House_Prediction {

    public static String readAndConvertCSV(String csvFilePath) throws Exception {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(csvFilePath));
        Instances data = loader.getDataSet();

        if (data.numAttributes() > 0) {
            data.setClassIndex(data.numAttributes() - 1); // Assuming the last column is the target attribute
        }

        String arffFilePath = csvFilePath.replace(".csv", ".arff");
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(arffFilePath));
        saver.writeBatch();

        return arffFilePath;
    }

    public static LinearRegression trainRegressionModel(String arffFilePath) throws Exception {
        DataSource source = new DataSource(arffFilePath);
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        LinearRegression model = new LinearRegression();
        model.buildClassifier(data);
        return model;
    }

    public static void evaluateModel(Instances data, LinearRegression model) throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(model, data, 10, new java.util.Random(1));
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
    }

    public static double predictPrice(LinearRegression model, Instances data, double[] newValues) throws Exception {
        Instances newData = new Instances(data, 0);
        newData.add(new weka.core.DenseInstance(1.0, newValues));
        newData.setClassIndex(data.numAttributes() - 1);
        return model.classifyInstance(newData.firstInstance());
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); 
        String option = "";

        try {
            String csvFilePath = "Average_Resale_Home_Prices.csv"; // Adjust path as necessary
            String arffFilePath = readAndConvertCSV(csvFilePath);
            DataSource source = new DataSource(arffFilePath);
            Instances data = source.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);

            LinearRegression model = trainRegressionModel(arffFilePath);
            evaluateModel(data, model);

            while (!option.equals("0")) {
                System.out.println("\n\nMenu:");
                System.out.println("-------------------------------------");
                System.out.println("0 - Exit Program");
                System.out.println("1 - Calculate percent increase of 2 years:");
                System.out.println("2 - Predict the price of home in future year:");
                System.out.println("3 - Calculate the average resale price of all types of homes in:");
                System.out.println("-------------------------------------");

                System.out.print("Enter your choice: ");
                option = sc.nextLine();

                switch(option) {
                    case "1":
                        System.out.println("Enter the first year:");
                        int firstYear = Integer.parseInt(sc.nextLine());
                        System.out.println("Enter the second year:");
                        int secondYear = Integer.parseInt(sc.nextLine());
                        calculatePercentageIncrease(data, firstYear, secondYear);
                        break;
                    case "2":
                        System.out.println("Predict the average resale price of a home in a future year:");
                        System.out.print("Enter the year: ");
                        double year = Double.parseDouble(sc.nextLine());
                        double[] newValues = new double[]{year}; // Assume year is the only input needed
                        double predictedPrice = predictPrice(model, data, newValues);
                        System.out.println("Predicted house price for year " + year + ": $" + predictedPrice);
                        break;
                    case "3":
                        System.out.println("Enter the year to calculate the average resale price:");
                        int avgYear = Integer.parseInt(sc.nextLine());
                        calculateAveragePrice(data, avgYear);
                        break;
                    default:
                        System.out.println("\nInvalid Option! Please Try again\n");
                }
            }
            System.out.println("\nExiting ... Thank you.");
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }

    private static void calculatePercentageIncrease(Instances data, int firstYear, int secondYear) {
        double firstYearPrice = 0, secondYearPrice = 0;
        int firstYearCount = 0, secondYearCount = 0;

        for (int i = 0; i < data.numInstances(); i++) {
            double year = data.instance(i).value(0); // Assuming year is the first attribute
            double price = data.instance(i).classValue(); // Assuming price is the target attribute

            if ((int) year == firstYear) {
                firstYearPrice += price;
                firstYearCount++;
            } else if ((int) year == secondYear) {
                secondYearPrice += price;
                secondYearCount++;
            }
        }

        if (firstYearCount > 0 && secondYearCount > 0) {
            firstYearPrice /= firstYearCount;
            secondYearPrice /= secondYearCount;

            double increase = ((secondYearPrice - firstYearPrice) / firstYearPrice) * 100;
            System.out.printf("The percentage increase from %d to %d is %.2f%%.\n", firstYear, secondYear, increase);
        } else {
            System.out.println("Data for the provided years is insufficient to calculate the increase.");
        }
    }

    private static void calculateAveragePrice(Instances data, int year) {
        double totalPrice = 0;
        int count = 0;

        for (int i = 0; i < data.numInstances(); i++) {
            double dataYear = data.instance(i).value(0); // Assuming year is the first attribute
            double price = data.instance(i).classValue(); // Assuming price is the target attribute

            if ((int) dataYear == year) {
                totalPrice += price;
                count++;
            }
        }

        if (count > 0) {
            double averagePrice = totalPrice / count;
            System.out.printf("The average resale price of homes in %d is $%.2f.\n", year, averagePrice);
        } else {
            System.out.println("No data available for the specified year to calculate the average price.");
        }
    }
}
