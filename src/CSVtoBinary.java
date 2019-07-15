/*Assumptions
1. The sub-relations can be stored in Main Memory
2. The CSV file is sorted
3. The CSV files doesn't have any inverted commas around, even though it is String
4. The CSV file at least has two columns. If it has just one column, then offset builder fails.

To Do
1. Make sure there no spaces after or before the comma in CSV
2. If bcol1.colType is integer then Convert the bcol1 value to integer before writing to file because bcol1 is string*/

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CSVtoBinary {
	
	String tableName; //Need to passed as parameterized constructor
	String[] columnDatatypes; //Need to passed as parameterized constructor - can be any among the following:"Integer", "Double", "String"
	int noOfColumns; //Need to passed as parameterized constructor
	
	public CSVtoBinary(String tableName, String[] columnDatatypes, int noOfColumns){
		this.tableName=tableName;
		this.columnDatatypes = columnDatatypes;
		this.noOfColumns = noOfColumns;
	}
	
	ArrayList<String> columnNames = new ArrayList<String>();; //Name of the columns extracted from CSV's first column
	int offsetCounter = -1; //?
	
	//Requires initialize to be called for the initialization
	String bcol1; 	//Stores the value of the current row's column 1's value
	int bcol1Freq; 	//Stores the value of the current row's column 1's value's frequency
	int bcol1Offset;//Stores the value of the current row's column 1's value's offset
	ArrayList bcols;//Stores the unique values of the column 2..N's value sequentially. It's basically flattening the row after row, but only keeping unique values.
	ArrayList<Integer> bcolsFreq;	//Stores the respective frequency of the bcols values
	ArrayList<Integer> bcolsOffset;	//Stores the respective offset of the bcols values
	ArrayList<Integer> bcolsColumnType;//Stores the respective data-type of the bcols values
	ArrayList prevColValueSeen;
	
	//create MAP.bin and BLOCKS.bin
	public void createBinaryFile(String filePath) throws IOException
	{
	}
	
//	outputStream.write(byteRead);
	public void writeIntoFile(String filePath, String data)
	{
	}
	
	public void readFromFile(String filePath, String data)
	{
	}
	
	public String getColumnTypeForBcols(int bcolsIndex)
	{
		return columnDatatypes[bcolsColumnType.get(bcolsIndex)];
	}
	
	public int getByteSizeBasedOnDataType(String dataType)
	{
		if (dataType.equals("String"))
			return 2;
		return 4;
	}
	
	public int findByteForBcolsBasedOnIndex(int index)
	{
		if(getColumnTypeForBcols(index).equals("String"))
			return 2*bcols.get(index).toString().length();
		return 4;
	}
	
	public int totalNumberOfMapsInBlock()
	{
		int prevCol = 0; int totalMaps=0;
		for(int i=0;i<bcolsColumnType.size();i++)
		{
			if(bcolsColumnType.get(i)>prevCol)
			{
				totalMaps++;
			}
			prevCol = bcolsColumnType.get(i);
		}
		return totalMaps;		
	}
	
	public int getTotalSizeOfBlock()
	{
		System.out.println("offsetCounter: "+offsetCounter);
		int offset = 0;
		for(int i=0; i<bcols.size();i++)
		{
			offset += findByteForBcolsBasedOnIndex(i) + 4;
//			System.out.println("findByteForBcolsBasedOnIndex(i): "+findByteForBcolsBasedOnIndex(i));
		}
//		System.out.println("offset: "+offset);
		offset *= 2; //Since the map and general v and c will take same amount of space.
		if(columnDatatypes[0].equals("String"))
		{
			offset += offsetCounter + 2*bcol1.length() + 4;//4 for Integer for Frequency of Column 1
		}
		else
		{
			offset += offsetCounter + 4 + 4;//4 for Integer for Frequency of Column 1
		}
		
		offset += 4*totalNumberOfMapsInBlock(); //For delimeters
		
		System.out.println("offset: "+offset);
		return offset;
	}
	
	public void createInlineMapDeleteLater()
	{
		ArrayList<ArrayList<Pair>> maps = new ArrayList<ArrayList<Pair>>();
		
		//create list of Pairs for each columns
		for(int i=0; i<noOfColumns;i++)
		{
			maps.add(new ArrayList<Pair>());
		}
		
		//iterate through each value in bcols and insert the value based on the column
		for(int i=0; i<bcols.size();i++)
		{
			maps.get(bcolsColumnType.get(i)-1).add(new Pair(bcols.get(i),0));//Gets the arraylist for that column. Add the pair with dummyInt=0
		}
		for(int i=0; i<noOfColumns;i++)
		{
			for(int j=0; j<maps.get(i).size(); j++)
				System.out.println(maps.get(i).get(j));
			System.out.println();
		}
		
	}
	
	
	public void createBlocks()
	{
		ArrayList list = new ArrayList();
		int[] noOfValuesToAdd = new int[noOfColumns];
		int prevCol = 0;
		int offset = getTotalSizeOfBlock();
		for(int i=0;i<bcols.size();i++)
		{
			bcolsOffset.add(0);
		}
		System.out.println("bcols.size(): " +bcols.size());
		System.out.println("bcolsColumnType.size(): " +bcolsColumnType.size());
		for(int i=bcols.size()-1;i>=-1;i--)
		{
			System.out.println("i: " +i);
			ArrayList dummyMap = new ArrayList();
			if((i==-1)||bcolsColumnType.get(i)<prevCol)
			{
				int counter=i+1;
				dummyMap.clear();
				dummyMap.add(" ");offset -= 2;
				while((noOfValuesToAdd[prevCol]!=0)||(i==-1))
				{
					if(i==-1)
						i=-2;
					if(bcolsColumnType.get(counter)==prevCol)
					{
						noOfValuesToAdd[prevCol]--;
						dummyMap.add(bcols.get(counter));
						dummyMap.add(bcolsOffset.get(counter));
						
						offset -= (findByteForBcolsBasedOnIndex(counter) + 4);
						counter++;
						for(int val: noOfValuesToAdd)
							System.out.print(" val: "+val);
						System.out.println();
//						System.out.println("bcols.get(counter): "+bcols.get(counter-1));
					}
					else
						counter++;
				}
				dummyMap.add(" ");offset -= 2;
			}
			dummyMap.addAll(list); list=dummyMap;
			if(i==-2)
				break;
			prevCol = bcolsColumnType.get(i);
			noOfValuesToAdd[prevCol]++;
			
			ArrayList dummy2 = new ArrayList();
			dummy2.add(bcols.get(i));
			dummy2.add(bcolsFreq.get(i));
			dummy2.addAll(list); list=dummy2;
			
			offset -= (findByteForBcolsBasedOnIndex(i)+4);
			bcolsOffset.set(i, offset);
			System.out.println("List: "+list);
			
		}
		System.out.println("List: "+list);
	}
	
	//create MAP.bin and BLOCKS.bin
	//To create an offset of an element, you need to calculate previous 
	public void offsetBuilder()
	{
		createBlocks();
	}
	
	public void initialise()
	{
		bcol1 = "";
		bcol1Freq = 0;
		bcol1Offset = 0;
		bcols = new ArrayList(); //It won't store first column values
		bcolsFreq = new ArrayList<Integer>();
		bcolsOffset = new ArrayList<Integer>();
		bcolsColumnType = new ArrayList<Integer>(); //It won't mention the type of column for first column
		
		prevColValueSeen = new ArrayList();
		for(int i=0; i<noOfColumns-1;i++)
		{
			prevColValueSeen.add("");
		}
		
	}
	
	public void performCSVtoBinary(String fileName) throws IOException
	{
//		createBinaryFile("src/MAP.bin");
//		createBinaryFile("src/BLOCKS.bin");
		
		Path pathToFile = Paths.get(fileName);
		boolean firstLine = true;
		initialise();
		
        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) { // create an instance of BufferedReader using try with resource, Java 7 feature to close resources

            String line = br.readLine(); // read the first line from the text file

            while (line != null) { // loop until all lines are read
            	
            	String[] attributes = line.split(","); // use string.split to load a string array with the values from each line of the file, using a comma as the delimiter

            	if(firstLine) //Checks if it's the first line of CSV
            	{
            		for(String colName:attributes)
            		{
            			columnNames.add(colName);
            		}
            		firstLine = false;
            	}
            	else
            	{            		
            		for(int i=0; i<noOfColumns; i++)//Iterate over each column for that row
            		{
            			if(i==0)//If it's first column
            			{
            				if (!bcol1.equals(attributes[i]))//If new value of first column is found
            				{
            					System.out.println("bcol1:" +bcol1);
            					if(offsetCounter == -1)//If it's 1 column and 1 row, so it's start of the first block
            						offsetCounter = 0;
            					
            					else
            					{
            						//Update MAP.bin
            						offsetBuilder();
            						//Update BLOCKS.bin
            						System.out.println(bcol1 + " " + bcol1Freq + " " + bcols + " " + bcolsFreq);
            						initialise();
            					}
            					
            					bcol1 = attributes[i];
            				}
            				++bcol1Freq;
//            				System.out.println("bcol1:" +bcol1);
//            				System.out.println("bcol1Freq:" +bcol1Freq);
            			}
            			else//If it's 2...N column
            			{
            				boolean valueAlreadyInBlock = false;
//            				System.out.println("Size of bcols" +bcols.size());
            				for(int j=0; j<bcols.size();j++)//Check each value in partially formed block for the same value with same previous columns attributes
            				{
            					if(bcolsColumnType.get(j)!=i)//Checks if it belongs to the same column
            					{
            						prevColValueSeen.set(bcolsColumnType.get(j)-1, bcols.get(j));
//            						System.out.println("prevColValueSeen:" +prevColValueSeen+" attribute:"+attributes[i]);
            					}
            					else
            					{
            						if(bcols.get(j).equals(attributes[i]))//Check if same value
            						{
            							valueAlreadyInBlock = true;
            							for(int k=1; k<=i-1;k++)
            							{
            								if(!attributes[k].equals(prevColValueSeen.get(k-1)))
            								{
            									valueAlreadyInBlock=false;
            									break;
            								}
            							}
            						}
            					}
            					if(valueAlreadyInBlock)
            					{
            						bcolsFreq.set(j, bcolsFreq.get(j)+1); //Increment the value at bcolsFreq[j] by 1
            						break;
            					}	
            				}
            				if(!valueAlreadyInBlock)
            				{
            					bcols.add(attributes[i]);
            					bcolsFreq.add(1);
            					bcolsColumnType.add(i);
            				}
//            				System.out.println(bcol1 + " " + bcol1Freq + " " + bcols + " " + bcolsFreq);
            			}
            		}
            		
            	}
                // read next line before looping if end of file reached, line would be null
                line = br.readLine();
            }
            System.out.println("bcol1:" +bcol1);
        	//Update MAP.bin
			offsetBuilder();
			//Update BLOCKS.bin
			System.out.println(bcol1 + " " + bcol1Freq + " " + bcols + " " + bcolsFreq);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
            	
            	
            	
            	
	}
	
	public static void main(String[] args) throws IOException {
		
		CSVtoBinary KD = new CSVtoBinary("KD", new String[]{"Integer", "Integer", "String", "String"}, 4);
		KD.performCSVtoBinary("src/kd.csv");
		
//		KD.createBinaryFile("src/dummy.bin");
		
	}

}
