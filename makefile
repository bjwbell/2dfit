Test: Test.java
	javac -Xlint:deprecation -classpath jts-1.10.jar Test.java Util.java

Runner: Runner.java
	javac -Xlint:deprecation -classpath jts-1.10.jar Runner.java Util.java
clean:
	rm *.class