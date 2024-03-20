package JavaReview;
import java.util.Scanner;
import java.io.*;

public class CPT {
	
	public static double [][] readfile (String file2) {
		String file = file2;
		File inFile = new File (file); //input file
		FileReader in;
		BufferedReader readFile;
		
		double [][] matrix;
		
		int rows=0;
		int cols=0;
		
		// read the file to figure out how many rows ans columns there are
		try
		{
			// open the file
			in = new FileReader (inFile); //input
		    readFile = new BufferedReader (in);
		    String line;
		    
		    // read each line in the file
		    while ((line = readFile.readLine()) != null) {
		    	String[] match = line.split(",");
		    	rows+=1; // count the rows
		    	cols= match.length; // count the columns
		    } // while
		    readFile.close(); //close
		    in.close();
		} // try
		catch (IOException e) {
			System.out.println("Problem READING file.");
			System.err.println("IOException: " + e.getMessage());
		} // catch
		
		/// System.out.println("rows cols"); System.out.print(rows );  System.out.print(" ");  System.out.println (cols );
		
		
		// we have the number of rows and columns. we can define the size of the matrix
		// we need to use cols -1  because we are ignoring  the first column, because it's the line number and we don't need it
		matrix = new double [rows][cols-1];
		
		//read the file again to populate the matrix
		try
		{
			in = new FileReader (inFile); //input
		    readFile = new BufferedReader (in);
		    String fix;
		    
		    rows=0;
		    
				    		
		    // cycle through rows
		    
		    while ((fix = readFile.readLine()) != null) {
		    	
		    	//change each row to a string array
		    	String[] match = fix.split(",");
		    	for (int i =0; i<match.length; i++) {
		    		// convert each cell to integer and populate the matrix 
		    		
		    		if (i == 0) {
		    			// ignore the first column because it just has the line number
		    		}
		    		else if (i == 1) {
		    			// convert the second column into a double
		    			// 0.0 == Peel
		    			// 1.0 == Caledon
		    			// 2.0 == Mississauga
		    			// 3.0 == Brampton
		    			
		    			// we use i - 1 because we are ignring the first column 
		    			
		    			if (match[i].equalsIgnoreCase("Peel")) {
		    				matrix[rows][i-1] = 0.0;
		    			}
		    			else if (match[i].equalsIgnoreCase("Caledon")) {
		    				matrix[rows][i-1] = 1.0;
		    			}
		    			else if (match[i].equalsIgnoreCase("Mississauga")) {
		    				matrix[rows][i-1] = 2.0;
		    			}
		    			else if (match[i].equalsIgnoreCase("Brampton")) {
		    				matrix[rows][i-1] = 3.0;
		    			}
		    			else {
		    				matrix[rows][i-1] = -1.0;
		    			}
		    				
		    		}
		    		else {
		    			matrix[rows][i-1] = Double.parseDouble (match[i]);
		    		}
		    	}
		    	rows++;
		    	
		    	
		    } // while
		    readFile.close(); //close
		    in.close();
		} // try
		catch (IOException e) {
			System.out.println("Problem READING file.");
			System.err.println("IOException: " + e.getMessage());
		} // catch
		
		return matrix;
		
		
	}
	
	public static void displayData (double [][] matrix) {
		for (int i =0; i<matrix.length; i++) {
			for (int j=0; j<matrix[0].length; j++) {
				if (j==0) {
					// 0.0 == Peel
	    			// 1.0 == Caledon
	    			// 2.0 == Mississauga
	    			// 3.0 == Brampton
					if (matrix[i][j] == 0.0) {
						System.out.print("Peel\t");
					}
					else if (matrix[i][j] == 1.0) {
						System.out.print("Caledon\t");
					} 
					else if (matrix[i][j] == 2.0) {
						System.out.print("Mississauga");
					}
					else if (matrix[i][j] == 3.0) {
						System.out.print("Brampton");
					}
				}
				else if (j==1) {
					System.out.print((int) matrix[i][j]);
				}
				
				else {
					System.out.print(matrix[i][j]);					
				}
				System.out.print("\t");
			}
			System.out.println("");
		}
		
	}
	
	public static double getTotals (double [][] matrix, int year) {
		
		double total = 0.0;
		for (int i =0; i<matrix.length; i++) {
			
			if (matrix[i][1] == (double) year ) {
				
				total = total + matrix[i][2] + matrix[i][3]+ matrix[i][4]+ matrix[i][5]+ matrix[i][6];
			}
		}
		/*System.out.print (year);
		System.out.print (" ");
		System.out.println (total);*/
		
		return total;
		
	}
	
	public static void main(String[] args){
		double [][] data = readfile ("data.txt");
		
		//displayData (data);
		
		// declare scanner variable
		Scanner sc = new Scanner(System.in); 

		
		String option = "-";
		
		while (option.equals("0") == false) {
			System.out.println ("\n\nMenu");
			System.out.println ("-------------------------------------");
			System.out.println ("0 - Exit");
			System.out.println ("1 - Calculate Average Home Prices");
			System.out.println ("2 - Part 2");
			System.out.println ("3 - Part 3");
			System.out.println ("4 - etc, etc ... (if there are other options)");
			System.out.println ("-------------------------------------");
			
			System.out.print ("Enter your choice: ");
			option = sc.nextLine();
			
			if (option.equals("0"))  {
				System.out.println ("\nExiting ... ");
			}
			else if (option.equals("1")) {
				System.out.println ("Calculating Average Home Prices ... (2005 - 2019)\n");
				
				System.out.print ("Enter the year you want to compare with year 2: ");
				int year1 = Integer.parseInt (sc.nextLine()) ;
				
				System.out.print ("Enter the year you want to compare with year 1: ");
				int year2 = Integer.parseInt (sc.nextLine()) ;
				
				System.out.println ("\nComparing "+ Integer.toString(year1) + " with " + Integer.toString(year2));
				double y1total = getTotals (data, year1);
				double y2total = getTotals (data, year2);
				
				double percentInc = (y2total-y1total)/y1total;
				
				if (percentInc ==  0) {
					System.out.print ("House prices have not incresed nor decreased between ");
					System.out.print (year1);
					System.out.print (" and ");
					System.out.print (year2);
					System.out.println (".\n");
				}
				else if (percentInc > 0) {
					System.out.print ("The house prices have increased by ");
					System.out.print (percentInc* 100);
					System.out.print ("% between ");
					System.out.print (year1);
					System.out.print (" and ");
					System.out.print (year2);
					System.out.println (".\n");
				}
				else {
					System.out.print ("The house prices have decreased by ");
					System.out.print (percentInc * -100);
					System.out.print ("% between ");
					System.out.print (year1);
					System.out.print (" and ");
					System.out.print (year2);
					System.out.println (".\n");
				}
				
			}
			else if (option.equals("2")) {
				
			}
			else if (option.equals("3")) {
	
			}
			else {
				System.out.println ("\nInvalid Option! Try again\n");
			}
		
		}
		System.out.println ("\nFinished!");
	}
}