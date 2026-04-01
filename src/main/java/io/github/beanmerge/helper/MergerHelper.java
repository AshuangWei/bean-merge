package io.github.beanmerge.helper;

import io.github.beanmerge.MergeConfiguration;
import io.github.beanmerge.Merger;

public final class MergerHelper {

  private final MergeConfiguration configuration;

  public MergerHelper(MergeConfiguration configuration) {
    this.configuration = configuration;
  }

  public <Source, Target> boolean merge(Source source, Target target) {
    return new Merger(configuration.getCustoms(), configuration.getNotifiers(),
        configuration.getIgnoreNullValue(), configuration.getCustomImmutableTypes(), source, target).merge();
  }
}
