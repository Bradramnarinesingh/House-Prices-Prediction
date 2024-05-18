import java.util.Scanner;
import java.io.*;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;
import weka.core.converters.CSVLoader;
import weka.core.converters.ArffSaver;

public class Peel_House_Prediction {

    public static String readAndConvertCSV(String csvFilePath) throws Exception {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(csvFilePath));
        Instances data = loader.getDataSet();
        
        // Assuming the last column is the class attribute
        if (data.numAttributes() > 0) {
            data.setClassIndex(data.numAttributes() - 1);
        }

        String arffFilePath = csvFilePath.replace(".csv", ".arff");
        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(arffFilePath));
        saver.writeBatch();

        return arffFilePath;
    }

    public static void trainAndEvaluate(String arffFilePath) throws Exception {
        DataSource source = new DataSource(arffFilePath);
        Instances data = source.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);

        J48 tree = new J48();
        tree.buildClassifier(data);

        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(tree, data, 10, new java.util.Random(1));
        System.out.println(eval.toSummaryString("\nResults\n======\n", false));
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in); 
        String option = "-";
        
        try {
            String csvFilePath = "Average_Resale_Home_Prices.csv"; // Adjust path as necessary
            String arffFilePath = readAndConvertCSV(csvFilePath);
            System.out.println("Data converted to ARFF and ready for ML processing.");

            while (!option.equals("0")) {
                System.out.println("\n\nMenu:");
                System.out.println("-------------------------------------");
                System.out.println("0 - Exit Program");
                System.out.println("1 - Train and Evaluate Decision Tree Model");
                System.out.println("-------------------------------------");
    
                System.out.print("Enter your choice: ");
                option = sc.nextLine();
    
                if (option.equals("1")) {
                    trainAndEvaluate(arffFilePath);
                } else if (!option.equals("0")) {
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
}
