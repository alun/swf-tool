# Running

This project requires Scala 2.8.1 because of apparat binary dependencies which doen't seem to be maintained anymore. So you'll need Java 7 to run this without erorrs. Because Scala 2.8 SBT compiler interface isn't built on Java 8.

 * Download and install Java 7 (setup your JAVA_HOME if needed)
 * Download and install SBT 
 * Clone this repo
 * run `sbt run`

The programm will show further instructions. To make somethings fancier you could try:

```
sbt 'run VPAIDSoundTest.swf dump' # print a readable binary dump of SWF/SWC file
sbt 'run VPAIDSoundTest.swf proxify' # adds a hook see Hook.as for each addEventListener call right in binary file
sbt 'run VPAIDSoundTest.swf mainClass' # prints a main class of the SWF file
```

