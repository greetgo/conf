package kz.greetgo.conf.hot;

/**
 * Lets change config parameters dynamically in runtime
 *
 * @author pompei
 */
public interface HotConfigModifier {
  /**
   * Activates/deactivates resetting behavior through method {@link HotConfigFactory#reset()}
   *
   * @param resetEnabled <code>true</code> - to activate, another value - to deactivate
   */
  void setResetEnabled(boolean resetEnabled);

  /**
   * Set parameter value
   *
   * @param configInterface config interface
   * @param name            parameter name
   * @param value           parameter value
   */
  void set(Class<?> configInterface, String name, Object value);
}
