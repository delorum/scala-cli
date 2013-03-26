scala-cli
=========

Command Line Interface for scala programs

Usage
=====

Maven
-----

Add to your `<repositories>` section:

      <repository>
          <id>dunnololda's maven repo</id>
          <url>https://raw.github.com/dunnololda/mvn-repo/</url>
      </repository>
      
Add to your `<dependencies>` section:

      <dependency>
          <groupId>com.github.dunnololda</groupId>
          <artifactId>cli_${scala.version}</artifactId>
          <version>1.0</version>
      </dependency>
      
Where `scala.version` is one of `2.9.1`, `2.9.2`, `2.10.0`

SBT
---

Add to resolvers:

    resolvers += "dunnololda's repo" at "https://raw.github.com/dunnololda/mvn-repo/"
    
Add to dependencies:

    libraryDependencies ++= Seq(
      // ...
      "com.github.dunnololda" %% "cli" % "1.0",
      // ...
    )

Code
----

In your main object extend `com.github.dunnololda.Cli` instead of `App`:

    import com.github.dunnololda.Cli
    
    object MyApp extends Cli {
      
Then add command line descriptions:

    commandLineArg("a1", "arg1",  "argument one",         has_value = true,  required = true)
    commandLineArg("b1", "bool1", "boolean argument one", has_value = false, required = false)
    commandLineArg("a2", "arg2",  "argument two",         has_value = true,  required = false)
    commandLineArg("b2", "bool2", "boolean argument two", has_value = false, required = true)

And then parse the arguments your program received:

    parseCommandLineArgs()
    
or you can write it like this:

    commandLineArgsAndParse(("a1", "arg1",  "argument one",         true,  true),
                            ("b1", "bool1", "boolean argument one", false, false),
                            ("a2", "arg2",  "argument two",         true,  false),
                            ("b2", "bool2", "boolean argument two", false, true))
                            
Access cli arguments in your app this way:

    val arg1  = intProperty("arg1")
    val bool1 = property("bool1", false)
    val arg2  = property("arg2", "default arg2 property")
    val bool2 = booleanProperty("bool2")
    
Now you can pass arguments when invoke your program with short or long names, for example:

    $ java -jar myapp.jar -a1 5 --arg2 Hello World! -b2
    
Also additional argument `-help`/`--help` is provided: it print to console usage information with descriptions for all arguments.

Alternatively you can provide all options in text file as `<key>`=`<value>` rows:

    myfile.txt
    
    arg1 = 5
    bool1 = false
    arg2 = Hello World!
    bool2 = true
    
Invoke your program this way in order to use options from file:

    $ java -jar myapp.jar -Dapp.properties=myfile.txt
    
For boolean values these keywords are supported: true/false, yes/no, on/off, 1/0.
For number values you can write simple arithmetic expressions:

    arg1 = 5*2
    
Please feel free to ask me any questions about this lib. Hope you enjoy it!
