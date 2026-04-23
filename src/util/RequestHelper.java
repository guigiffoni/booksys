package src.util;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestHelper {
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

    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> parseQueryString(String requestBody) {
        HashMap<String, Object> dados = new HashMap<String, Object>();
        String[] requests = requestBody.split("&");
        
        for (String request : requests) {
            String[] keyValue = request.split("=", 2);

            String chave = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String valor = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            Object chaveExiste = dados.get(chave);
            
            if (chaveExiste == null) {
                dados.put(chave, valor);
            } else if (chaveExiste instanceof ArrayList) {
                ((ArrayList<String>) chaveExiste).add(valor);
            } else {
                ArrayList<String> lista = new ArrayList<String>();
                lista.add((String) chaveExiste);
                lista.add((String) valor);
                dados.put(chave, lista);
            }
        }

        return dados;
    }
}
