package art.ameliah.brigadier.core.utils;

public class Utils {

  public static boolean typeIsBool(Class<?> obj) {
    return obj.equals(boolean.class) || obj.equals(Boolean.class);
  }

  public static boolean typeIsInt(Class<?> obj) {
    return obj.equals(int.class) || obj.equals(Integer.class);
  }

  public static boolean typeIsFloat(Class<?> obj) {
    return obj.equals(float.class) || obj.equals(Float.class);
  }

  public static boolean typeIsDouble(Class<?> obj) {
    return obj.equals(double.class) || obj.equals(Double.class);
  }

}
