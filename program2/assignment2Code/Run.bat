@echo off

javac PositionalIndex.java

java PositionalIndex N doc1 Y for \k1 schizophrenia
java PositionalIndex N doc1 Y new \k4 schizophrenia
java PositionalIndex N doc1 Y patients \k2 schizophrenia

echo "*************************************************"
echo "Results when BiDirection is Not used"

java PositionalIndex N doc1 N drug \k1 schizophrenia
java PositionalIndex N doc1 N drug \k4 patients
java PositionalIndex N doc1 N schizophrenia \k4 breakthrough
pause;