/*
 * Positional Index Implementation
 * Assignment 2
 * Text Retreival
 * Rahul Pradeep Kamath
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;


public class PositionalIndex 
{

	// This is my Main Index, i have split the Logic to make parsing and debugging easier
	static TreeMap<String,TreeMap<Integer,LinkedList<Integer>>>positionalIndexContent = 
			new TreeMap<String,TreeMap<Integer,LinkedList<Integer>>>();
	// This is my Sub List which is a part of my main List
	static TreeMap<Integer,LinkedList<Integer>> docIdAndPosition;
	
	static boolean debug = false;
	static List<String> fileNames = new ArrayList<String>(); // Keep Track of File Names
	static int documentId = 0; // Keep Track Of  Document Ids
	static int pos =0;
	static boolean biDirectional = true;
	static StringBuffer finalQuery= new StringBuffer();
	
	static void parseLineForDocId(int docId,String line)
	{
		String splitLine[] = line.split(" ");
		int position = 1; // To Keep Track Of Position of the Word in the Document
		if(splitLine != null)
		{
			boolean firstToken = true; // Skip the 1st Token this is the Document ID
			for(String token: splitLine)
			{
				if(firstToken)
				{
					firstToken = false;
					continue;
				}
				if(!positionalIndexContent.containsKey(token))
				{
					docIdAndPosition = new TreeMap<Integer,LinkedList<Integer>>(); // Create  a new Map to hold the Values
					LinkedList<Integer> positionList = new LinkedList<Integer>(); // Create a Position List to maintain the positions
					positionList.add(position); // Add the Position into the Position List
					docIdAndPosition.put(docId, positionList);
					positionalIndexContent.put(token, docIdAndPosition);
				}
				else
				{
					docIdAndPosition = positionalIndexContent.get(token);
					if(!docIdAndPosition.containsKey(docId))// This is when the word is in the Main Dictionary, Put it does not exist for some DocumentId
					{
						LinkedList<Integer> positionList = new LinkedList<Integer>(); // Create a Position List to maintain the positions
						positionList.add(position);
						docIdAndPosition.put(docId, positionList);
						positionalIndexContent.put(token,docIdAndPosition);
					}
					else // If it exists in the MainDictionary and It exists in the SubDictionary , just add the NewPosition
					{
						LinkedList<Integer> positionList = docIdAndPosition.get(docId);
						positionList.add(position);
						docIdAndPosition.put(docId, positionList);
						positionalIndexContent.put(token,docIdAndPosition);
					}
					
				}
				position++;// Increment for the Next WOrd
			}
		}
	}
	static void printThePositionalIndex()
	{
		// I would like to 1st get the MainDictionary and Iterate Over its contents
		for(String word: positionalIndexContent.keySet())
		{
			System.out.println("Positional Index For Word : " + word);
			TreeMap<Integer,LinkedList<Integer>> positionalIndex = positionalIndexContent.get(word);
			for(Integer docId: positionalIndex.keySet())
			{
				System.out.println("in DocId : "+docId);
				LinkedList<Integer> listOfPositionInDocument = positionalIndex.get(docId);
				for(Integer pos:listOfPositionInDocument)
				{
					System.out.print(pos+" ,");
				}
				System.out.println("");
			}
			System.out.println("");
		}
		
	}
	static void readFileContents()
	{
		for(String fname: fileNames)
		{

			String line = null;
			try
			{
				FileReader inFile = new FileReader(fname);
				BufferedReader bufffer = new BufferedReader(inFile);
				while((line = bufffer.readLine()) != null) 
				{
					documentId++;
					parseLineForDocId(documentId,line);
				} 
				bufffer.close();

			}
			catch(Exception e)
			{
				System.out.println("Failed in Read File Contents for Fname "+fname);
				System.out.println("Exception :: "+e);
				e.printStackTrace();
			}


		}
	}
	public static void addContentsToResults(TreeMap<Integer,LinkedList<Integer>>answers,int docId,int startPos,int endPos)
	{

		if(!answers.containsKey(docId))
		{
			LinkedList<Integer> positions = new LinkedList<Integer>();
			positions.add(startPos);
			positions.add(endPos);
			answers.put(docId, positions);
		}
		else
		{
			LinkedList<Integer> positions = answers.get(docId);
			positions.add(startPos);
			positions.add(endPos);
			answers.put(docId, positions);
		}

	}
	public static TreeMap<Integer,LinkedList<Integer>> proximityIntersection(TreeMap<Integer,LinkedList<Integer>> positionalIndex1,TreeMap<Integer,LinkedList<Integer>> positionalIndex2,int k)
	{
		int i=0,j=0;
		List<Integer> docId1 = new ArrayList<Integer>();
		docId1.addAll(positionalIndex1.keySet());
		List<Integer> docId2 = new ArrayList<Integer>();
		docId2.addAll(positionalIndex2.keySet());
		int numberOfDocIds1 = docId1.size();
		int numberOfDocIds2 = docId2.size();
		TreeMap<Integer,LinkedList<Integer>> answers = new TreeMap<Integer,LinkedList<Integer>>();
		
		while(i < numberOfDocIds1 && j < numberOfDocIds2)
		{
			if(docId1.get(i) == docId2.get(j))
			{
				List<Integer> l = new ArrayList<Integer>();
				List<Integer> pp1 = positionalIndex1.get(docId1.get(i));
				List<Integer> pp2 = positionalIndex2.get(docId2.get(j));
				int a=0,b=0;
				while(a < pp1.size())
				{
					while(b < pp2.size())
					{
						if(biDirectional && Math.abs((pp1.get(a)-pp2.get(b))) <= pos)
						{
							l.add(pp2.get(b));
						}
						else if(!biDirectional)
						{
							 if((pp1.get(a) < pp2.get(b)) && ((pp1.get(a)-pp2.get(b)) <= pos))
							 {
								 l.add(pp2.get(b)); 
							 }
							 else
							 {
								 a++;
							 }
							 	
						}
						else if(pp2.get(b) > pp1.get(a))
							break;
						b++;
					}
					while(!l.isEmpty() && ((Math.abs(l.get(0) - pp1.get(a))) > pos))
					{
						l.remove(0);
					}
					for(Integer ps:l)
					{
						addContentsToResults(answers,docId1.get(i),pp1.get(a),ps);
					}
					a++;// Go to the Next Position in positionalIndex list pp1 <- next(pp1)
					
				}
				i++; // p1<-next(p1)
				j++; // p2<-next(p2)
			}
			else 
			{
				if(docId1.get(i) < docId2.get(j) )
				{
					i++; // Check the Next DocID
				}
				else
				{
					j++;
				}
			}
			
		}
		return answers;
		
	}
	public static void main(String args[])
	{
		//Argumennts will Be of the FOrm [ Y/N InputFile.txt drug and or
		LinkedList<String> queryTerms = new LinkedList<String>();
		List<Integer> results = new ArrayList<Integer>();
		int argCount =0;
		for (String arguments : args) 
		{
			if(argCount == 0)// Check if we are Debugging
			{
				if(arguments.equalsIgnoreCase("Y"))
				{
					debug = true;
				}
				else
				{
					debug= false;
				}
				argCount++;
				continue;
			}
			else if(argCount == 1) // This is Input File
			{
				StringBuilder stringBuild = new StringBuilder(arguments);
				stringBuild.append(".txt");
				fileNames.add(stringBuild.toString());
				argCount++;
			}
			else if(argCount == 2)// Check if we are Debugging
			{
				if(arguments.equalsIgnoreCase("Y"))
				{
					biDirectional = true;
				}
				else
				{
					biDirectional = false;
				}
				argCount++;
			}
			else if(arguments.contains("\\k"))
			{
				char val = arguments.charAt(arguments.length()-1);
				pos = Integer.parseInt(String.valueOf(val));
			}
			else
			{
				queryTerms.add(arguments);
			}
		}
		if(debug)
		{
			System.out.println("Proximity Value is :: " +pos);
			System.out.print("Query terms are :: ");
			finalQuery.append(queryTerms.get(0));
			finalQuery.append(" \\K"+pos+" ");
			finalQuery.append(queryTerms.get(1));
			System.out.println("Query is : "+finalQuery);
			System.out.print("File Name : ");
			for(String fname: fileNames)
			{
				System.out.print(fname);
			}
		}
		System.out.println();
		readFileContents();
		if(debug == true)
			printThePositionalIndex();
		
		TreeMap<Integer,LinkedList<Integer>> finalAnser  = 
				proximityIntersection(positionalIndexContent.get(queryTerms.get(0)),positionalIndexContent.get(queryTerms.get(1)),pos);
		
			System.out.println();
			System.out.println("DocId where the words were found for the query");
			System.out.println(finalQuery.toString());
		if(!finalAnser.isEmpty())
		{
			Set<Integer> foundDocId = finalAnser.keySet();
			for(Integer docIdFoundIn : foundDocId)
			{
				System.out.println("DocId : "+docIdFoundIn);
				LinkedList<Integer> startAndEndPosition = finalAnser.get(docIdFoundIn);
				for(int i=0;i < startAndEndPosition.size();i=i+2)
				{
					System.out.println(startAndEndPosition.get(i)+" "+startAndEndPosition.get(i+1));
				}
			}
		}
		else
		{
			System.out.println("No results for for the Query");
		}
	}
}
