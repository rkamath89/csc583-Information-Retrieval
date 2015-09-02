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
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeMap;


public class InvertedIndex
{
	public static enum operation
	{
		AND,OR,MIX;
	};
	static operation op;
	static boolean debug = false;
	static List<String> fileNames = new ArrayList<String>(); // Keep Track of File Names
	static int documentId = 0; // Keep Track Of  Document Ids
	static TreeMap<String,LinkedList<Integer>> docListings = new TreeMap<String,LinkedList<Integer>>(); // Document Id and its posting



	public static LinkedList<Integer> performShuntingsAlgo(String expression)
	{
		String[] tokens = expression.split(" ");

		// Stack for List of Documetns containing word
		Stack<LinkedList<Integer>> documents = new Stack<LinkedList<Integer>>();

		// Stack for Operators AND , OR
		Stack<String> ops = new Stack<String>();

		for(String token:tokens)
		{
			// Current token is a Word, push it to stack for words
			if (!"(".equals(token) && !")".equals(token) && !"AND".equals(token) && !"OR".equals(token))
			{
				if(docListings.containsKey(token))
					documents.push(docListings.get(token));
				else
					documents.push(new LinkedList<Integer>());
			}
			// If opening Brace push it onto OP's
			else if ("(".equals(token))
				ops.push(token);

			// Solve it if closing brace encountered
			else if (")".equals(token))
			{
				while (!"(".equals(ops.peek()))
					documents.push(solveExpression(ops.pop(), documents.pop(), documents.pop()));
				ops.pop();
			}

			// Current token is an operator.
			else if ("AND".equals(token) || "OR".equals(token))
			{
				// While top of 'ops' has same or greater precedence to current
				// token, which is an operator. Apply operator on top of 'ops'
				// to top two elements in values stack
				while (!ops.empty() && hasPrecedence(token, ops.peek()))
					documents.push(solveExpression(ops.pop(), documents.pop(), documents.pop()));

				// Push current token to 'ops'.
				ops.push(token);
			}
		}

		// Evaluate remaining Values
		while (!ops.empty())
			documents.push(solveExpression(ops.pop(), documents.pop(), documents.pop()));

		return documents.pop(); // Returning Result
	}

	// Returns true if 'op2' has higher or same precedence as 'op1',
	// otherwise returns false.
	public static boolean hasPrecedence(String op1, String op2)
	{
		if ("(".equals(op2) || ")".equals(op2))
			return false;
		if ("AND".equals(op1) && "OR".equals(op2))
			return false;
		else
			return true;
	}

	public static LinkedList<Integer>  solveExpression(String op, LinkedList<Integer> b, LinkedList<Integer> a)
	{
		LinkedList<Integer> result = new LinkedList<Integer>();
		if("AND".equals(op))
		{
			result = performAndOperation(a,b);
		}
		else if("OR".equals(op))
		{
			result = performOrOperation(a, b);
		}
		return result;
	}

	public static LinkedList<Integer> performAndOperation(LinkedList<Integer> ll1,LinkedList<Integer> ll2)
	{
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(Integer docId:ll1)
		{
			for(Integer docId2:ll2)
			{
				if((docId == docId2) && !result.contains(docId))
				{
					result.add(docId);
				}
			}
		}

		return result;
	}
	public static LinkedList<Integer> performOrOperation(LinkedList<Integer> ll1,LinkedList<Integer> ll2)
	{
		LinkedList<Integer> result = new LinkedList<Integer>();
		for(Integer docId:ll1)
		{
			if(!result.contains(docId))
			{
				result.add(docId);
			}

		}
		for(Integer docId2:ll2)
		{
			if(!result.contains(docId2))
			{
				result.add(docId2);
			}

		}

		return result;
	}

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
				if(tempResult.isEmpty())
				{
					if(docListings.containsKey(queryWord))
						tempResult.addAll(docListings.get(queryWord));
				}
				else
				{
					if(docListings.containsKey(queryWord))
					{
						int i=0,j=0;
						LinkedList<Integer> fetchedList = docListings.get(queryWord);
						while(i < tempResult.size() && j < fetchedList.size())
						{
							if((tempResult.get(i).intValue() == fetchedList.get(j).intValue()) && (!result.contains(tempResult.get(i))))
							{
								result.add(tempResult.get(i));
								i++;
								j++;
							}
							else if(tempResult.get(i).intValue() < fetchedList.get(j).intValue())
							{
								i++;
							}
							else
							{
								j++;
							}
						}
						tempResult.clear();
						tempResult.addAll(result);
					}
					else
					{
						result.clear();
						tempResult.clear();
						throw new Exception("No Document Contains Key :"+queryWord);
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
		else
		{
			System.out.println("No results Found For : "+query);	
			return;
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
		else if("MIX".equals(ops))
		{
			op = operation.MIX;
		}
		else
		{
			if(debug)
			{
				System.out.println(" Looks like only one Word was provided , defaulting to OR");
			}
			op = operation.OR;// Safe condition in case it is just one word
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
			//mixedQueries(query.toString());
			results = performShuntingsAlgo(query.toString());
		}
		//System.out.println("Sorted Values");
		//sortMap();
		//if(debug)
		//printContentsOfMap(sortedDocListings);
		printResults(results,query.toString());

	}

}
