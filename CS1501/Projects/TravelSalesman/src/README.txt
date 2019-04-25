Approximately Solve Euclidean TSP with MST-Walk
Daesang Yoon - day42
Nov 2018

CONTENTS

Implements MST-Walk algorithm to find approximate solution to
Euclidean TSP.  The following files should be present:

  City.java
  Edge.java
  EuclideanTSP.java
  Map.java
  DemonstrateEuclideanTSP.java

USAGE

  javac DemonstrateEuclideanTSP.java
  java DemonstrateEuclideanTSP NUMBER_OF_CITIES

This will generate random NUMBER_OF_CITIES cities of random coordinates, find and display
the MST, then find and display the MST-Walk tour.
Also it prints MST edge weight and length of tour on the console so be sure to check command line of terminal you are using.

PROBLEMS
No problem.
Since we can randomly assign city locations(coordinates) by ourselves, we do not have to use Priority queue just like text book prim's algorithm.
However, if we are actually computing real city location, textbook's version will be better.


