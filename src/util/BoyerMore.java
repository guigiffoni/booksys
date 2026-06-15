package src.util;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BoyerMore {
    // retorna o índice do caractere, se presente na string haystack, caso contrário: -1
    public static int indexOfChar(char caracter, String haystack) {
        for (int i = haystack.length() - 1; i >= 0; i--) {
            if (caracter == haystack.charAt(i)) {
                return i;
            }
        }

        return -1;
    }

    public static long buscaPadrao(String nomeArquivo, String padrao) throws IOException {
        RandomAccessFile raf = new RandomAccessFile("data/" + nomeArquivo, "r");
        byte[] texto = new byte[padrao.length()];
        raf.read(texto);

        int indice;
        long filePointerPadrao = -1;

        // enquanto não estivermos no final do arquivo
        while (raf.getFilePointer() < raf.length()) {
            for (int i = padrao.length() - 1; i >= 0; i--) {
                if (padrao.charAt(i) != texto[i]) {
                    // caso o caractere no texto está presente no padrão, retorna índice
                    indice = indexOfChar((char) texto[i], padrao);

                    if (indice != -1) {
                        raf.seek(raf.getFilePointer() - indice - 1);
                    }
                    
                    raf.read(texto);
                    break;
                }

                if (i == 0) {
                    filePointerPadrao = raf.getFilePointer() - padrao.length();
                    break;
                }
            }

            if (filePointerPadrao > -1) {
                break;
            }
        }

        raf.close();
        return filePointerPadrao;
    }
}
