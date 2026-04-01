package io.github.beanmerge.copier.impl;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.Sets;
import io.github.dummiejava.dummie.Dummie;
import io.github.dummiejava.dummie.configuration.GenerationStrategy;
import io.github.beanmerge.base.data.SimpleObjectA;
import io.github.beanmerge.base.data.SimpleObjectB;
import io.github.beanmerge.copier.CustomerCopierAdapter;
import org.junit.Test;

public class CustomCopierTest {

  @Test
  public void should_get_copy_use_custom_generator() throws NoSuchFieldException {
    SimpleObjectA from = Dummie.withStrategy(GenerationStrategy.RANDOM).create(SimpleObjectA.class);
    SimpleObjectB to = new SimpleObjectB();
    CustomCopier copier = new CustomCopier(Sets.newHashSet(new CustomerCopierAdapter<>(Object.class, a -> 1000)));

    Object result = copier.copy(to.getClass().getDeclaredField("scalarTypeInt"), from.getScalarTypeInt(), to.getScalarTypeInt(), "");

    assertEquals(result, 1000);
  }
}