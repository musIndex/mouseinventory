package edu.ucsf.mousedatabase;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;

@SuppressWarnings("rawtypes")
public class ServletUtils {
  
  @SuppressWarnings("unchecked")
  public static <T> T PopulateFromRequest(HttpServletRequest request, Class<T> klass) throws Exception{
    HashMap map = new HashMap();
    T obj = klass.newInstance();
    Enumeration names = request.getParameterNames();
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      map.put(name, request.getParameterValues(name));
    }
    BeanUtils.populate(obj, map);
    return obj;
  }
}
