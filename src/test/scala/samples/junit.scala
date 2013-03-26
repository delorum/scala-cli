package samples

import org.junit._
import Assert._
import com.github.dunnololda.Cli

object TestCliApp extends Cli {
  commandLineArg("a1", "arg1",  "argument one",         has_value = true,  required = true)
  commandLineArg("b1", "bool1", "boolean argument one", has_value = false, required = false)
  commandLineArg("a2", "arg2",  "argument two",         has_value = true,  required = false)
  commandLineArg("b2", "bool2", "boolean argument two", has_value = false, required = true)
  parseCommandLineArgs()

  private def arg1  = intProperty("arg1")
  private def bool1 = property("bool1", false)
  private def arg2 =  property("arg2", "default arg2 property")
  private def bool2 = booleanProperty("bool2")

  def receivedCliArgs = arg1+":"+bool1+":"+arg2+":"+bool2
}

@Test
class AppTest {
    @Test
    def testOK() = {
      val args = "-a1 5 --arg2 Hello World! -b2".split(" ")
      TestCliApp.main(args)
      assertTrue(TestCliApp.receivedCliArgs == "5:false:Hello World!:true")
    }

//    @Test
//    def testKO() = assertTrue(false)

}


