package org.apache.ibatis.executor.loader;

import org.apache.ibatis.reflection.MetaObject;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResultLoaderMap implements Serializable {

  private final Map<String, LoadPair> loaderMap = new HashMap<String, LoadPair>();

  public void addLoader(String property, MetaObject metaResultObject, ResultLoader resultLoader) {
    String upperFirst = getUppercaseFirstProperty(property);
    loaderMap.put(upperFirst, new LoadPair(property, metaResultObject, resultLoader));
  }

  public int size() throws SQLException {
    return loaderMap.size();
  }

  public boolean hasLoader(String methodName) throws SQLException {
    return loaderMap.containsKey(methodName.toUpperCase());
  }

  public boolean load(String property) throws SQLException {
    LoadPair pair = loaderMap.remove(property.toUpperCase());
    if (pair != null) {
      pair.load();
      return true;
    }
    return false;
  }

  public void loadAll() throws SQLException {
    final Set<String> methodNameSet = loaderMap.keySet();
    String[] methodNames = methodNameSet.toArray(new String[methodNameSet.size()]);
    for (String methodName : methodNames) {
      load(methodName);
    }
  }

  private static String getUppercaseFirstProperty(String property) {
    String[] parts = property.split("\\.");
    return parts[0].toUpperCase();
  }

  private static class LoadPair {
    private String property;
    private MetaObject metaResultObject;
    private ResultLoader resultLoader;

    private LoadPair(String property, MetaObject metaResultObject, ResultLoader resultLoader) {
      this.property = property;
      this.metaResultObject = metaResultObject;
      this.resultLoader = resultLoader;
    }

    public void load() throws SQLException {
      metaResultObject.setValue(property, resultLoader.loadResult());
    }
  }
}
