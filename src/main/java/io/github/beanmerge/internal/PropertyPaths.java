package io.github.beanmerge.internal;

/**
 * Builds dot-paths and collection index paths used for merge notifications.
 */
public final class PropertyPaths {

  private PropertyPaths() {
  }

  public static String childPath(String parentPath, String fieldName) {
    return "".equals(parentPath) ? fieldName : String.format("%s.%s", parentPath, fieldName);
  }

  public static String collectionElementPath(String parentPath, String index) {
    return "".equals(parentPath) ? String.format("{%s}", index) : String.format("%s{%s}", parentPath, index);
  }
}
