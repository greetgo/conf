package kz.greetgo.conf.core.fields;

import kz.greetgo.conf.ConfUtil;
import kz.greetgo.conf.core.util.MethodName;
import kz.greetgo.conf.hot.Description;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FieldDefParserDefault implements FieldDefParser {

  public static final FieldDefParser instance = new FieldDefParserDefault();

  @Override
  public List<FieldDef> parse(Class<?> aClass) {

    LinkedHashMap<String, Optional<Description>> descriptionMap = new LinkedHashMap<>();
    Map<String, String>                          defaultValues  = new HashMap<>();

    for (Field field : aClass.getFields()) {
      if (field.getAnnotation(ConfIgnore.class) != null) {
        continue;
      }

      descriptionMap.put(field.getName(), Optional.ofNullable(field.getAnnotation(Description.class)));

      String defaultValue = ConfUtil.extractStrDefaultValue(field.getAnnotations(), x -> x);
      defaultValues.put(field.getName(), defaultValue);
    }

    for (Method method : aClass.getMethods()) {
      if (method.getParameterCount() != 0) continue;

      String name = MethodName.extractGet(method.getName());

      if (name == null) {
        continue;
      }

      String defaultValue = ConfUtil.extractStrDefaultValue(method.getAnnotations(), x -> x);
      if (defaultValue != null) {
        defaultValues.put(name, defaultValue);
      }

      Description description = method.getAnnotation(Description.class);
      if (description != null) {
        descriptionMap.remove(name);
        descriptionMap.put(name, Optional.of(description));
      }

    }

    for (Method method : aClass.getMethods()) {

      if (method.getParameterCount() != 1) continue;

      String name = MethodName.extractSet(method.getName());

      if (name == null) {
        continue;
      }

      Description description = method.getAnnotation(Description.class);
      if (description != null) {
        descriptionMap.remove(name);
        descriptionMap.put(name, Optional.of(description));
      }

      String defaultValue = ConfUtil.extractStrDefaultValue(method.getAnnotations(), x -> x);
      if (defaultValue != null) {
        defaultValues.put(name, defaultValue);
      }
    }

    return descriptionMap.entrySet().stream().map(e -> new FieldDef() {
      @Override
      public String name() {
        return e.getKey();
      }

      @Override
      public List<String> descriptionLines() {
        return e.getValue()
                .map(Description::value)
                .map(str -> str.split("\n"))
                .map(Arrays::asList)
                .orElseGet(ArrayList::new)
          ;
      }

      @Override
      public String defaultValue() {
        return defaultValues.get(e.getKey());
      }
    }).collect(Collectors.toList());
  }

}
