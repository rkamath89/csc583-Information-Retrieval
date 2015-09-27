This has been implemented in Java

IMPORTANT
*****Please insert a space between all characters*****

Understanding the CommandLine Arguments:
	The format is : java PositionaIndex Debug[Y/N] InputFileName BiDirecctional[Y/N] Query
	e.g : java PositionalIndex N doc1 Y for \k1 schizophrenia

To Compile the code :
	javac PositionalIndex.java

To Run the Code :
PositionalIndex N doc1 Y for \k1 schizophrenia
Results :
	DocId : 1
	4 5
	DocId : 4
	3 4
PositionalIndex N doc1 Y new \k4 schizophrenia
Results :
	DocId : 2
	1 3
	DocId : 4
	1 4

PositionalIndex N doc1 Y patients \k2 schizophrenia
Results :
	DocId : 2
	5 3
	DocId : 4
	5 4

PositionalIndex N doc1 N drug \k1 schizophrenia
Results :
	No results for for the Query

PositionalIndex N doc1 N drug \k4 patients
Results :
	DocId : 2
	4 5

PositionalIndex N doc1 N schizophrenia \k4 breakthrough
Results :
	DocId : 3
	6 7	
	DocId : 4
	4 6