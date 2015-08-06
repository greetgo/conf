package kz.greetgo.conf.hot;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.io.PrintStream;

import org.testng.annotations.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
public class HotConfigFactoryTest {
  @Test
  public void createDefault() throws Exception {
    {
      File f = new File("target/asd/HotConfig1.hot");
      if (f.exists()) f.delete();
    }
    
    TestHotconfigFab fab = new TestHotconfigFab("target/asd", ".hot");
    HotConfig1 conf = fab.createConfig1();
    
    assertThat(conf.intExampleValue()).isEqualTo(0);
    assertThat(conf.intExampleValue2()).isEqualTo(349);
    assertThat(conf.boolExampleValue()).isEqualTo(true);
    assertThat(conf.strExampleValue()).isEqualTo("def value for strExampleValue");
  }
  
  @Test
  public void createDefaultRest() throws Exception {
    {
      File f = new File("target/asd/HotConfig1.hot");
      if (f.exists()) f.delete();
      
      PrintStream out = new PrintStream(f, "UTF-8");
      out.println("intExampleValue = 711");
      out.close();
    }
    
    TestHotconfigFab fab = new TestHotconfigFab("target/asd", ".hot");
    HotConfig1 conf = fab.createConfig1();
    
    assertThat(conf.intExampleValue()).isEqualTo(711);
    assertThat(conf.intExampleValue2()).isEqualTo(349);
    assertThat(conf.boolExampleValue()).isEqualTo(true);
    assertThat(conf.strExampleValue()).isEqualTo("def value for strExampleValue");
  }
  
  @Test
  public void readExists() throws Exception {
    {
      File f = new File("target/asd/HotConfig1.hot");
      if (f.exists()) f.delete();
      
      PrintStream out = new PrintStream(f, "UTF-8");
      out.println("intExampleValue = 7111");
      out.println("intExampleValue2 = 444");
      out.println("boolExampleValue = 0");
      out.println("strExampleValue = parabelum");
      out.close();
    }
    
    TestHotconfigFab fab = new TestHotconfigFab("target/asd", ".hot");
    HotConfig1 conf = fab.createConfig1();
    
    assertThat(conf.intExampleValue()).isEqualTo(7111);
    assertThat(conf.intExampleValue2()).isEqualTo(444);
    assertThat(conf.boolExampleValue()).isEqualTo(false);
    assertThat(conf.strExampleValue()).isEqualTo("parabelum");
  }
  
  @Test
  public void reset() throws Exception {
    
    File f = new File("target/asd/HotConfig1.hot");
    if (f.exists()) f.delete();
    
    {
      PrintStream out = new PrintStream(f, "UTF-8");
      out.println("intExampleValue = 7111");
      out.println("intExampleValue2 = 444");
      out.println("boolExampleValue = 0");
      out.println("strExampleValue = parabelum");
      out.close();
    }
    
    TestHotconfigFab fab = new TestHotconfigFab("target/asd", ".hot");
    HotConfig1 conf = fab.createConfig1();
    
    assertThat(conf.intExampleValue()).isEqualTo(7111);
    assertThat(conf.intExampleValue2()).isEqualTo(444);
    assertThat(conf.boolExampleValue()).isEqualTo(false);
    assertThat(conf.strExampleValue()).isEqualTo("parabelum");
    
    {
      PrintStream out = new PrintStream(f, "UTF-8");
      out.println("intExampleValue = 999");
      out.println("intExampleValue2 = 111");
      out.println("boolExampleValue = 1");
      out.println("strExampleValue = quantum");
      out.close();
    }
    
    fab.reset();
    
    assertThat(conf.intExampleValue()).isEqualTo(999);
    assertThat(conf.intExampleValue2()).isEqualTo(111);
    assertThat(conf.boolExampleValue()).isEqualTo(true);
    assertThat(conf.strExampleValue()).isEqualTo("quantum");
    
  }
}
