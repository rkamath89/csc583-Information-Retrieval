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
import java.util.Stack;
import java.util.TreeMap;


public class InvertedIndex {
   
	public static enum operation
	{
		AND,OR,MIX;
	};
	static operation op;
	static boolean debug = false;
	static List<String> fileNames = new ArrayList<String>(); // Keep Track of File Names
	static int documentId = 0; // Keep Track Of  Document Ids
	static TreeMap<String,LinkedList<Integer>> docListings = new TreeMap<String,LinkedList<Integer>>(); // Document Id and its posting
	
	
	static void mixedQueries(String query)
	{
		Stack<String> queryStack = new Stack<String>();
		String[] splitQuery = query.split(" ");
		StringBuilder queryFetched = new StringBuilder();
		for(String splitWord:splitQuery)
		{
			if(")".equalsIgnoreCase(splitWord))
			{
				while(!queryStack.isEmpty())
				{
					String value = queryStack.pop();
					if("(".equalsIgnoreCase(value))
					{
						System.out.println("Query :"+queryFetched.toString());
						break;
					}
					else
					{
						StringBuilder temp = new StringBuilder(value);
						temp.append(" "+queryFetched);
						queryFetched = temp;
					}
				}
			}
			else
			{
				queryStack.push(splitWord);
			}
				
		}
		StringBuilder tempStr = new StringBuilder();
		while(!queryStack.isEmpty())
		{
			String value = queryStack.pop();
			StringBuilder temp = new StringBuilder(value);
			temp.append(" "+tempStr);
			tempStr = temp;
		}
		queryFetched.append(tempStr);
		System.out.println("Query :"+queryFetched.toString());
		
	}
	
	static List<Integer> orQueries(String query)
	{
		List<Integer> result = new ArrayList<Integer>();
		List<Integer> tempResult = new ArrayList<Integer>();
		String terms[] = query.split(" ");
		List<String> termsWithoutKeyWord = new ArrayList<String>();
		for(String key:terms)
		{
			if("OR".equals(key))
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
			System.out.print("Terms to find OR Query on are : ");
			for(String word:termsWithoutKeyWord)
			{
				System.out.print(word+" ,");
			}
			System.out.println();
		}
		for(String queryWord: termsWithoutKeyWord)
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
					if(docListings.containsKey(queryWord))
					{
						LinkedList<Integer> tempList = docListings.get(queryWord);
						for(Integer val:tempList)
						{
							if(!result.contains(val))
							{
								result.add(val);
							}
						}
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
			System.out.println("Final Result for : "+query);
			Collections.sort(result);
			for(Integer res:result)
			{
				System.out.print(res+" ,");
			}
			System.out.println();
		}
		return result;
	}
	
	
	
	
	static List<Integer> andQueries(String query)
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
			System.out.println("Final Result for : "+query);
			Collections.sort(result);
			for(Integer res:result)
			{
				System.out.print(res+" ,");
			}
			System.out.println();
		}
		return result;
	} 
	
	
	static void printContentsOfMap(TreeMap<String,LinkedList<Integer>> printMap)
	{
		System.out.println("--------------------------------------------");
		System.out.println("Inverted Index Contents");
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
		System.out.println("--------------------------------------------");
		System.out.println();
			
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
			boolean firstToken = true; // Skip the 1st Token this is the Document ID
			for(String token: splitLine)
			{
				if(firstToken)
				{
					firstToken = false;
					continue;
				}
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
	public static void printResults(List<Integer> results,String query)
	{
		if(!results.isEmpty())
		{
			Collections.sort(results);
		}
		System.out.println();
		System.out.println("Final Result for : "+query);
		for(Integer val:results)
		{
			System.out.print(val+" ,");
			
		}
		System.out.println();
	}
	public static void checkTypeOfOperation(String queryReceived)
	{
		String ops = "";
		String queryParsed[] = queryReceived.split(" ");
		for(String word:queryParsed)
		{
			if("AND".equals(word) && (ops.equals("") || ops.equals("AND")))
			{
				ops = "AND";
			}
			else if("OR".equals(word) && (ops.equals("") || ops.equals("OR")))
			{
				ops = "OR";
			}
			else if( ("AND".equals(word) && "OR".equals(ops)) || ("OR".equals(word) && "AND".equals(ops)))
			{
				ops = "MIX";
				break;
			}
		}
		if("AND".equals(ops))
		{
			op = operation.AND;
		}
		else if("OR".equals(ops))
		{
			op = operation.OR;
		}
		else
		{
			op = operation.MIX;
		}
	}
	public static void main(String[] args) 
	{
		//Argumennts will Be of the FOrm [ Y/N InputFile.txt drug and or
		boolean encounteredOp = false;
		StringBuilder query = new StringBuilder();
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
			else
			{
				query.append(arguments+" ");
			}
		}
		
		checkTypeOfOperation(query.toString());// Check if AND ,OR , MIX
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
		
		// Print the Inverted Index
		printContentsOfMap(docListings);
		
		
		if(op == operation.AND)
		{
			if(debug)
			{
				System.out.println("Performing AND Processing");	
			}
			results = andQueries(query.toString());
		}
		else if(op == operation.OR)
		{
			if(debug)
			{
				System.out.println("Performing OR Processing");	
			}
			results = orQueries(query.toString());
		}
		else if(op == operation.MIX)
		{
			mixedQueries(query.toString());
		}
		//System.out.println("Sorted Values");
		//sortMap();
		//if(debug)
			//printContentsOfMap(sortedDocListings);
		printResults(results,query.toString());

	}

}
