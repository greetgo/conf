package kz.greetgo.conf.hot;

public interface HotConfig {
  Object getElementValue(String elementName);
  
  boolean isElementExists(String elementName);
}
