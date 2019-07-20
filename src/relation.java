import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class relation {
	
	int noOfColumns;
	String relationName;
	ArrayList<String> columnDataType;
	ArrayList<String> columnNames;
	String mapFilePath;
	String blocksFilePath;
	int pointer;
	boolean inLine;
	int mapStartsAt;
	
	public relation()
	{
		noOfColumns = 0;
		relationName = null;
		columnDataType = null;
		columnNames = null;
		pointer = 0;
		inLine = false;
		mapFilePath = null;
		blocksFilePath = null;
		blocksFilePath = null;
		mapStartsAt = 0;
	}
	
	public void changesForMapInline(int mapStartsAt)
	{
		this.noOfColumns--;
		this.relationName = relationName + "subrelation";
		this.columnDataType.remove(0);//Removes the first column
		this.columnNames.remove(0);//Removes the first column
		this.mapFilePath = this.blocksFilePath;
		this.inLine = true;
		this.mapStartsAt = mapStartsAt;
	}
	
	public ArrayList<String> extractColumnDataTypesFromMap(int noOfColumns, String columnDataTypeInBinaryNumber)
	{
		ArrayList<String> columnDataType = new ArrayList<String>();
		for(int i=0; i<noOfColumns; i++)
		{
			if((columnDataTypeInBinaryNumber.length()>i)&&(columnDataTypeInBinaryNumber.charAt(i)=='1'))
				columnDataType.add("Integer");
			else
				columnDataType.add("String");
		}
		
		return columnDataType;
	}
	
	public void initialiseRelationFilePath(String mapFilePath, String blocksFilePath)
	{
		this.mapFilePath = mapFilePath;
		this.blocksFilePath = blocksFilePath;
	}
	
	public String findStringFromGivenOffsetAndDelimeter(int offset, String delimeter, DataInputStream dos, boolean incrementPointer) throws IOException
	{
		if(offset!=-1)//Ignore offset
		{
			dos.skipBytes(offset-1);
			if (incrementPointer)
				pointer = offset;
		}
		String value = "";

		while(true)
		{
			byte[] str = " ".getBytes();
			str[0] = dos.readByte(); if (incrementPointer) pointer++;
			String string = new String(str, "ASCII");
			if(string.equals(delimeter))
				break;
			else
				value+=string;
		}

		return value;
	}
	
	public int findIntegerFromGivenOffsetAndDelimeter(int offset, DataInputStream dos, boolean incrementPointer) throws IOException
	{
		if(offset!=-1)//Ignore offset
		{
			dos.skipBytes(offset-1);
			if (incrementPointer)
				pointer = offset;
		}
		int value = dos.readInt(); if (incrementPointer) pointer=pointer+4;
		
		return value;
	}
	
	public void processMapHeader()//Only if not inLine
	{
		File file = new File(mapFilePath); 

		if ((file.exists()) && (!inLine))
		{
			
			try (InputStream inputStream = new FileInputStream(mapFilePath);
					DataInputStream dos = new DataInputStream(inputStream); //DOS because Double can only be loaded into file using this library
					) {
				
				noOfColumns = dos.readInt(); pointer = pointer+4;
				
				//Find Data type of Columns
				int columnDataTypeInBinaryString = dos.readInt(); pointer = pointer+4;
				String columnDataTypeInBinaryNumber = Integer.toBinaryString(columnDataTypeInBinaryString);
				columnDataType = extractColumnDataTypesFromMap(noOfColumns, columnDataTypeInBinaryNumber);
				
				//Find Table Name
				relationName = findStringFromGivenOffsetAndDelimeter(-1, ",", dos, true);

				//Find Column Names
				columnNames = new ArrayList<String>();
				for(int i=0; i<noOfColumns; i++)
				{
					columnNames.add(findStringFromGivenOffsetAndDelimeter(-1, ",", dos, true));
//					System.out.println("columnNames:"+columnNames.get(i));
				}
				
				this.mapStartsAt = pointer+1;
//				System.out.println("pointer:"+pointer);

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void locationOfTuples(Object val)
	{
		File file = new File(mapFilePath);
		if (file.exists())
		{

			try (InputStream inputStream = new FileInputStream(mapFilePath);
					DataInputStream dos = new DataInputStream(new BufferedInputStream(inputStream)); //DOS because Double can only be loaded into file using this library
					) {
				if(!inLine)
				{
					dos.skipBytes(mapStartsAt-1);
					System.out.println(mapStartsAt);
					dos.mark(500);//random integer inserted for now
					while(dos.available()>0)
					{
						byte[] str = " ".getBytes();
						str[0] = dos.readByte();pointer++;
						String string = new String(str, "ASCII");
						System.out.println("String:"+string);
						if(string.equals("_"))
						{
							//						System.out.println("value:"+findIntegerFromGivenOffsetAndDelimeter(-1, dos, false));
							dos.skipBytes(5);
						}
						else if(string.equals(","))
						{
							System.out.println("value:"+findStringFromGivenOffsetAndDelimeter(-1, "_", dos, false));
							dos.skipBytes(4);
						}
						else
							break;
					}
				}

			}catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		
		relation kd = new relation();
		
		kd.initialiseRelationFilePath("src/map.bin", "src/blocks.bin");
		kd.processMapHeader();
		System.out.println("relationName:"+kd.relationName);
		kd.locationOfTuples("hi");
		
		
//		relation kdsubrel = new relation();
//		kdsubrel.initialiseRelationFilePath("src/map.bin", "src/blocks.bin");
//		kdsubrel.processMapHeader();
//		System.out.println("relationName:"+kdsubrel.relationName);
//		kdsubrel.changesForMapInline();
		
	}

}
