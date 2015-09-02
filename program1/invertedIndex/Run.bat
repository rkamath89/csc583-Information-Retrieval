@echo off

javac InvertedIndex.java

java InvertedIndex N doc1 schizophrenia AND drug
java InvertedIndex N doc1 breakthrough OR new
java InvertedIndex N doc1 ( drug OR treatment ) AND schizophrenia
pause;