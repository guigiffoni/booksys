package src.util;

import java.util.ArrayList;
import java.util.List;

public class BuscaKMP {
    public static List<Integer> buscar(String texto, String padrao) {
        List<Integer> ocorrencias = new ArrayList<>();

        if (texto == null || padrao == null || padrao.isEmpty() || texto.length() < padrao.length()) {
            return ocorrencias;
        }
        String textoMinusculo = texto.toLowerCase();
        String padraoMinusculo = padrao.toLowerCase();
        int[] lps = calcularLPS(padraoMinusculo);
        int i = 0; 
        int j = 0; 
        while (i < textoMinusculo.length()) {
            if (padraoMinusculo.charAt(j) == textoMinusculo.charAt(i)) {
                i++;
                j++;
            }
            if (j == padraoMinusculo.length()) {
                // Padrão encontrado! Adiciona o índice inicial à lista
                ocorrencias.add(i - j);
                // Ajusta o índice do padrão usando o LPS para continuar buscando outras ocorrências
                j = lps[j - 1]; 
            } else if (i < textoMinusculo.length() && padraoMinusculo.charAt(j) != textoMinusculo.charAt(i)) {
                // Mismatch após j correspondências
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return ocorrencias;
    }
    public static boolean contem(String texto, String padrao) {
        if (padrao == null || padrao.isEmpty()) return true;
        return !buscar(texto, padrao).isEmpty();
    }
    private static int[] calcularLPS(String padrao) {
        int tamanho = padrao.length();
        int[] lps = new int[tamanho];
        int len = 0; 
        int i = 1;

        lps[0] = 0; 

        while (i < tamanho) {
            if (padrao.charAt(i) == padrao.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }
}