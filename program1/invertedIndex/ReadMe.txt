This has been implemented in Java

IMPORTANT
*****Please insert a space between all characters*****
*****operator AND and OR MUST be in capitals****** [I am considering them as Keywords]

Understanding the CommandLine Arguments:
	The format is : java InvertedIndex Debug[Y/N] InputFileName Query
	e.g : java InvertedIndex N doc1 drug AND for

To Compile the code :
	javac InvertedIndex.java

To Run the Code :
	5.1 :
		java InvertedIndex N doc1 schizophrenia AND drug
		Result : 1 and 2

	5.2 :
		java InvertedIndex N doc1 breakthrough OR new
		Result : 1, 2, 3 and 4
	
	5.3:
		java InvertedIndex N doc1 ( drug OR treatment ) AND schizophrenia
		Result : 1,2 and 3

Inverted Index is Displayed after each Execution :

Inverted Index Contents
Key is : approach
Values are : [3]
Key is : breakthrough
Values are : [1]
Key is : drug
Values are : [1, 2]
Key is : for
Values are : [1, 3, 4]
Key is : hopes
Values are : [4]
Key is : new
Values are : [2, 3, 4]
Key is : of
Values are : [3]
Key is : patients
Values are : [4]
Key is : schizophrenia
Values are : [1, 2, 3, 4]
Key is : treatment
Values are : [3]
