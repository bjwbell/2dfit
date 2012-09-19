Test: Test.java
	javac -Xlint:deprecation -classpath jts-1.12.jar Test.java Util.java Shape.java TypeOfShape.java

Runner: Runner.java
	javac -Xlint:deprecation -classpath jts-1.12.jar Runner.java Util.java Shape.java TypeOfShape.java
clean:
	rm *.class