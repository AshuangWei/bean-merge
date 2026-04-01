package io.github.beanmerge.notifier;

import io.github.beanmerge.notifier.function.FieldUpdateNotifier;
import java.util.Set;

public class UpdatedNotifier {

  public enum MatchMode {
    ALL,
    ANY
  }

  private final Set<String> paths;
  private final FieldUpdateNotifier notifier;
  private final MatchMode matchMode;

  public UpdatedNotifier(Set<String> paths, FieldUpdateNotifier notifier) {
    this(paths, notifier, MatchMode.ALL);
  }

  public UpdatedNotifier(Set<String> paths, FieldUpdateNotifier notifier, MatchMode matchMode) {
    this.paths = paths;
    this.notifier = notifier;
    this.matchMode = matchMode;
  }

  public boolean shouldNotify(Set<String> updatedPaths) {
    if (matchMode == MatchMode.ANY) {
      return paths.stream().anyMatch(updatedPaths::contains);
    }
    return updatedPaths.containsAll(paths);
  }

  public Set<String> getPaths() {
    return paths;
  }

  public FieldUpdateNotifier getNotifier() {
    return notifier;
  }
}
