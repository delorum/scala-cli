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
          <url>https://raw.github.com/dunnololda/mvn-repo/master</url>
      </repository>
      
Add to your `<dependencies>` section:

      <dependency>
          <groupId>com.github.dunnololda</groupId>
          <artifactId>cli_${scala.version}</artifactId>
          <version>1.2</version>
      </dependency>
      
Where `scala.version` is `2.9.1`, `2.9.2`, or `2.10.1`

SBT
---

Add to resolvers:

    resolvers += "dunnololda's repo" at "https://raw.github.com/dunnololda/mvn-repo/master"
    
Add to dependencies:

    libraryDependencies ++= Seq(
      // ...
      "com.github.dunnololda" %% "cli" % "1.2",
      // ...
    )

Code
----

In your main object extends `com.github.dunnololda.cli.Cli` instead of `App`:

    import com.github.dunnololda.cli.Imports._
    
    object MyApp extends Cli {
      
Then add command line descriptions:

    commandLineArg("a1", "arg1",  "argument one",         has_value = true,  required = true)
    commandLineArg("b1", "bool1", "boolean argument one", has_value = false, required = false)
    commandLineArg("a2", "arg2",  "argument two",         has_value = true,  required = false)
    commandLineArg("b2", "bool2", "boolean argument two", has_value = false, required = true)

And then parse the arguments your program has received:

    parseCommandLineArgs()
    
or you can write it shorter:

    commandLineArgsAndParse(("a1", "arg1",  "argument one",         true,  true),
                            ("b1", "bool1", "boolean argument one", false, false),
                            ("a2", "arg2",  "argument two",         true,  false),
                            ("b2", "bool2", "boolean argument two", false, true))
                            
Access cli arguments in your app this way:

    val arg1  = intProperty("arg1")
    val bool1 = property("bool1", false)
    val arg2  = property("arg2", "default arg2 property")
    val bool2 = booleanProperty("bool2")
    
See AppProperties.scala source-code for all supported variants:

https://github.com/dunnololda/scala-cli/blob/master/src/main/scala/com/github/dunnololda/cli/AppProperties.scala
    
Now you can pass arguments when invoke your programm with short or long names, for example:

    $ java -jar myapp.jar -a1 5 --arg2 Hello World! -b2
    
Also additional argument `-help`/`--help` is provided: it prints to console usage information with descriptions for every 
argument.

    $ java -jar myapp.jar --help
    Test App with command line interface
    Options:
    -a1       --arg1 arg (required)         argument one
    -b1       --bool1                       boolean argument one
    -a2       --arg2 arg                    argument two
    -b2       --bool2 (required)            boolean argument two
    -help     --help                        show this usage information
    
"Test App with command line interface" is an optional program description. You can add it this way:

    programDescription = "Test App with command line interface"

If no `programDescription` provided, this string will be omitted in `--help` output.

Alternatively you can provide all options in the text file as `<key>`=`<value>` rows:

    myfile.txt
    
    arg1 = 5
    bool1 = false
    arg2 = Hello World!
    bool2 = true
    
Invoke your program this way in order to use options from file:

    $ java -jar myapp.jar -Dapp.properties=myfile.txt
    
For boolean values these keywords are supported: `true/false`, `yes/no`, `on/off`, `1/0`.
For number values you can write simple arithmetic expressions:

    arg1 = 5*2
    
To read more about supported math see the formula parser source code:

https://github.com/dunnololda/scala-cli/blob/master/src/main/scala/com/github/dunnololda/cli/FormulaParser.scala
    
Also scala-cli allows you to access `version` and `artifactId` from `pom.xml` programmatically. 
To do this, add to resources file `maven.properties` with following content:

    app.version = ${project.version}
    app.name = ${project.artifactId}
    
Then for example you can menition version in the provided programDescription:

    programDescription = "Test App v"+appVersion+" with command line interface"
    
Please feel free to ask me any questions about this lib. Hope you enjoy it!
