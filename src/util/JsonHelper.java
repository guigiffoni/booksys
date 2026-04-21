package src.util;

public class JsonHelper {
    public static String arrayToJson(String[] strings) {
        StringBuilder str = new StringBuilder();

        str.append('[');
        for (String string : strings) {
            str.append("\"");
            str.append(string);
            str.append("\",");
        }

        str.setCharAt(str.length() - 1, ']');
        
        return str.toString();
    }
}
