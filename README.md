# Running

This project requires Scala 2.8.1 because of apparat binary dependencies which doesn't seem to be maintained anymore.  
So you'll need Java 7 to run this without erorrs. Because Scala 2.8 SBT compiler interface isn't built on Java 8.

 * Download and install Java 7 (setup your JAVA_HOME if needed)
 * Download and install SBT 
 * Clone this repo
 * run `sbt run`

The programm will show further instructions. To make somethings fancier you could try:

```
sbt 'run VPAIDSoundTest.swf dump' # print a readable binary dump of SWF/SWC file
sbt 'run VPAIDSoundTest.swf proxify' # adds a hook see Hook.as for each addEventListener call right in binary file
sbt 'run VPAIDSoundTest.swf mainClass' # prints a main class of the SWF file
sbt 'run VPAIDSoundTest.swf soundCheck' # detects if SWF has access to SoundMixer.soundTransfrom = and prints those places
```

# Compiling

```
sbt assembly
```

This command procues a JAR file which includes all necessary libraries to run it on JVM with no
scala or other dependencies. The result file could be run as:

```
java -jar target/scala-2.8.1/as3-proxy-assembly-0.1-SNAPSHOT.jar VPAIDSoundTest.swf soundCheck
```


