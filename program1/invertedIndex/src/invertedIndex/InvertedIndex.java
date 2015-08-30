package invertedIndex;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;


public class InvertedIndex {
   
	public static enum operation
	{
		AND,OR,MIX;
	};
	static operation op;
	static boolean debug = true;
	static List<String> fileNames = new ArrayList<String>(); // Keep Track of File Names
	static int documentId = 0; // Keep Track Of  Document Ids
	static TreeMap<String,LinkedList<Integer>> docListings = new TreeMap<String,LinkedList<Integer>>(); // Document Id and its posting
	
	static void andQueries(String query)
	{
		List<Integer> result = new ArrayList<Integer>();
		List<Integer> tempResult = new ArrayList<Integer>();
		String terms[] = query.split(" ");
		List<String> termsWithoutKeyWord = new ArrayList<String>();
		for(String key:terms)
		{
			if("AND".equals(key))
			{
				continue;
			}
			else
			{
				termsWithoutKeyWord.add(key);
			}
		}
		if(debug)
		{
			System.out.println();
			System.out.print("Terms to find AND Query on are : ");
			for(String word:termsWithoutKeyWord)
			{
				System.out.print(word+" ,");
			}
			System.out.println();
		}
		
		for(String queryWord:termsWithoutKeyWord)
		{
			try
			{
				if(result.isEmpty())
				{
					if(docListings.containsKey(queryWord))
						result.addAll(docListings.get(queryWord));
				}
				else
				{
					for(Integer existingResult: result)
					{
						if(docListings.containsKey(queryWord))
						{
							for(Integer newResult:docListings.get(queryWord))
							{
								if(existingResult.intValue() == newResult.intValue())
								{
									if(!tempResult.contains(existingResult))
										tempResult.add(existingResult);
								}
							}	
						}
						else
						{
							result.clear();
							tempResult.clear();
							throw new Exception("No Document Contains Key :"+queryWord);
						}
					}
					result.clear();
					result.addAll(tempResult);
					if(debug)
					{
						System.out.println("Contents of Result after Key :: "+queryWord);
						System.out.println(result);
					}

				}
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				result.clear();
				tempResult.clear();
			}
			
		}
		if(debug)
		{
			System.out.println();
			System.out.println("Final Result");
			for(Integer res:result)
			{
				System.out.print(res+" ,");
			}
			System.out.println();
		}
	} 
	
	
	static void printContentsOfMap(TreeMap<String,LinkedList<Integer>> printMap)
	{
		if(printMap != null && printMap.entrySet() != null)
		{
			Iterator iter =  printMap.entrySet().iterator();
			if(iter != null)
			{
				while(iter.hasNext())
				{
					Entry entry = (Entry)iter.next();
					System.out.println("Key is : "+entry.getKey());
					System.out.println("Values are : "+entry.getValue());
				}
			}
		}
			
	}
	/*Old function , i shiftd to TreeMap this maintains ordering
	 * 
	 * static void sortMap()
	{
		List<String> keys = new ArrayList<String>(docListings.keySet());
		Collections.sort(keys);
		for(int i=0;i <keys.size();i++)
		{
			String key = keys.get(i);
			if(!sortedDocListings.containsKey(key))
			{
				if(debug)
				{
					System.out.println("Sorted List created with Key : "+key);
					System.out.println("Values added  : "+docListings.get(key));
				}
				LinkedList<Integer> newList = new LinkedList<Integer>();
				newList.addAll(docListings.get(key));
				sortedDocListings.put(key,newList);
			}
			else
			{
				if(debug)
				{
					System.out.println("Sorted List Exists with Key : "+key);
					System.out.println("Values added  : "+docListings.get(key));
				}
				LinkedList<Integer> existingList = sortedDocListings.get(key);
				existingList.addAll(docListings.get(key));
				sortedDocListings.put(key,existingList);
			}
		}
	}*/
	static void parseLineForDocId(int docId,String line)
	{
		String splitLine[] = line.split(" ");
		if(splitLine != null)
		{
			for(String token: splitLine)
			{
				if(!docListings.containsKey(token))
				{
					LinkedList<Integer> newList = new LinkedList<Integer>();
					newList.add(docId);
					docListings.put(token,newList);
				}
				else
				{
					LinkedList<Integer> existingList = docListings.get(token);
					existingList.add(docId);
					docListings.put(token,existingList);
				}
			}
		}
	}
	static void readFileContents()
	{
		for(String fname: fileNames)
		{
			documentId++;
			String line = null;
			try
			{
				FileReader inFile = new FileReader(fname);
				BufferedReader bufffer = new BufferedReader(inFile);
				  while((line = bufffer.readLine()) != null) 
				  {
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
	public static void main(String[] args) 
	{
		boolean encounteredOp = false;
		StringBuilder query = new StringBuilder();
		for (String arguments : args) 
		{
			if(!encounteredOp && (arguments.equalsIgnoreCase("-1") || arguments.equalsIgnoreCase("-2") || arguments.equalsIgnoreCase("-3")))
			{
				encounteredOp = true;
				if(arguments.equalsIgnoreCase("-1"))
				{
					op = operation.AND;
				}
				else if(arguments.equalsIgnoreCase("-2"))
				{
					op = operation.OR;
				}
				else if(arguments.equalsIgnoreCase("-3"))
				{
					op = operation.MIX;
				}
				continue;
			}
			if(!encounteredOp)
			{
				StringBuilder stringBuild = new StringBuilder(arguments);
				stringBuild.append(".txt");
				fileNames.add(stringBuild.toString());
			}
			else
			{
				query.append(arguments+" ");
			}
		}
		if(debug)
		{
			System.out.println("Query is :: " +query.toString());
			System.out.println("Operation :: "+op);
			for(String fname: fileNames)
			{
				System.out.println(fname);
			}
		}
		readFileContents();
		//System.out.println(" Unsorted Values");
		if(debug)
			printContentsOfMap(docListings);
		if(op == operation.AND)
		{
			if(debug)
			{
				System.out.println("Performing AND Processing");	
			}
			andQueries(query.toString());
		}
		//System.out.println("Sorted Values");
		//sortMap();
		//if(debug)
			//printContentsOfMap(sortedDocListings);

	}

}
