package com.github.edgarespina.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.edgarespina.handlebars.context.FieldValueResolver;
import com.github.edgarespina.handlebars.context.JavaBeanValueResolver;
import com.github.edgarespina.handlebars.context.MapValueResolver;
import com.github.edgarespina.handlebars.context.MethodValueResolver;

/**
 * Unit test for {@link Context}.
 *
 * @author edgar.espina
 * @since 0.1.0
 */
public class ValueResolverTest {

  static class Base {

    String base;

    String child;

    public Base(final String base, final String child) {
      this.base = base;
      this.child = child;
    }

    public String getBaseProperty() {
      return base;
    }

    public String getChildProperty() {
      return child;
    }
  }

  @Test
  public void javaBeanResolver() {
    Context context = Context
        .newBuilder(new Base("a", "b"))
        .resolver(JavaBeanValueResolver.INSTANCE)
        .build();
    assertNotNull(context);
    assertEquals("a", context.get("baseProperty"));
    assertEquals("b", context.get("childProperty"));
  }

  @Test
  public void methodResolver() {
    Context context = Context
        .newBuilder(new Base("a", "b"))
        .resolver(MethodValueResolver.INSTANCE)
        .build();
    assertNotNull(context);
    assertEquals("a", context.get("getBaseProperty"));
    assertEquals("b", context.get("getChildProperty"));
  }

  @Test
  public void fieldResolver() {
    Context context = Context
        .newBuilder(new Base("a", "b"))
        .resolver(FieldValueResolver.INSTANCE)
        .build();
    assertNotNull(context);
    assertEquals("a", context.get("base"));
    assertEquals("b", context.get("child"));
  }

  @Test
  public void mapResolver() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("base", "a");
    map.put("child", "b");

    Context context = Context
        .newBuilder(map)
        .resolver(MapValueResolver.INSTANCE)
        .build();
    assertNotNull(context);
    assertEquals("a", context.get("base"));
    assertEquals("b", context.get("child"));
  }

  @Test
  public void multipleValueResolvers() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("base", "a");
    map.put("child", "b");

    Context context =
        Context
            .newBuilder(new Base("a", "b"))
            .combine("map", map)
            .resolver(
                MapValueResolver.INSTANCE,
                JavaBeanValueResolver.INSTANCE,
                MethodValueResolver.INSTANCE,
                FieldValueResolver.INSTANCE)
            .build();
    assertNotNull(context);
    // by field
    assertEquals("a", context.get("base"));
    assertEquals("b", context.get("child"));
    // by javaBean
    assertEquals("a", context.get("baseProperty"));
    assertEquals("b", context.get("childProperty"));
    // by method name
    assertEquals("a", context.get("getBaseProperty"));
    assertEquals("b", context.get("getChildProperty"));
    // by map
    assertEquals("a", context.get("map.base"));
    assertEquals("b", context.get("map.child"));
  }
}
