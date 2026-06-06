package src.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Lzw {

    private static final int TAMANHO_INICIAL = 256;
    private static final int MAX_DICT = 4096; // 12 bits

    private static final String PATH = "data/";

    public static long comprimir(String nomeArquivo) throws IOException {
        byte[] dados = Files.readAllBytes(Paths.get(PATH + nomeArquivo));
        byte[] comprimido = comprimirBytes(dados);
        Path destino = Paths.get(PATH + nomeArquivo + ".lzw");
        Files.write(destino, comprimido);
        return comprimido.length;
    }

    public static long descomprimir(String nomeArquivo) throws IOException {
        byte[] comprimido = Files.readAllBytes(Paths.get(PATH + nomeArquivo));
        byte[] restaurado = descomprimirBytes(comprimido);

        // Remove extensão .lzw para obter o nome original
        String nomeOriginal = nomeArquivo.endsWith(".lzw")
            ? nomeArquivo.substring(0, nomeArquivo.length() - 4)
            : nomeArquivo + ".descomprimido";

        Path destino = Paths.get(PATH + nomeOriginal);
        Files.write(destino, restaurado);
        return restaurado.length;
    }

    // comprime usando lzw e retorna os bytes do arquivo (cabeçalho + sódigos)
    public static byte[] comprimirBytes(byte[] entrada) throws IOException {
        // dicionário: sequência de bytes → código
        HashMap<String, Integer> dict = new HashMap<>(MAX_DICT);
        for (int i = 0; i < TAMANHO_INICIAL; i++) {
            dict.put(String.valueOf((char) i), i);
        }
        int proximoCodigo = TAMANHO_INICIAL;

        ArrayList<Integer> codigos = new ArrayList<>();

        if (entrada.length == 0) {
            return montarArquivo(0, codigos);
        }

        String prefixo = String.valueOf((char) (entrada[0] & 0xFF));

        for (int i = 1; i < entrada.length; i++) {
            char c = (char) (entrada[i] & 0xFF);
            String prefixoMaisC = prefixo + c;

            if (dict.containsKey(prefixoMaisC)) {
                prefixo = prefixoMaisC;
            } else {
                codigos.add(dict.get(prefixo));

                if (proximoCodigo < MAX_DICT) {
                    dict.put(prefixoMaisC, proximoCodigo++);
                }
                prefixo = String.valueOf(c);
            }
        }

        // Emite o último prefixo
        codigos.add(dict.get(prefixo));

        return montarArquivo(entrada.length, codigos);
    }

    // descomprime os nytes produzidos
    public static byte[] descomprimirBytes(byte[] comprimido)
        throws IOException {
        DataInputStream dis = new DataInputStream(
            new ByteArrayInputStream(comprimido)
        );

        int tamanhoOriginal = dis.readInt();

        // Lê os códigos
        int numCodigos = (comprimido.length - 4) / 2;
        int[] codigos = new int[numCodigos];
        for (int i = 0; i < numCodigos; i++) {
            codigos[i] = dis.readShort() & 0xFFFF;
        }

        if (numCodigos == 0) {
            return new byte[0];
        }

        // Dicionário: código → sequência de bytes
        ArrayList<byte[]> dict = new ArrayList<>(MAX_DICT);
        for (int i = 0; i < TAMANHO_INICIAL; i++) {
            dict.add(new byte[] { (byte) i });
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(tamanhoOriginal);

        // primeiro código
        byte[] entrada = dict.get(codigos[0]);
        baos.write(entrada);
        byte[] anterior = entrada;

        for (int i = 1; i < numCodigos; i++) {
            int codigo = codigos[i];
            byte[] atual;

            if (codigo < dict.size()) {
                atual = dict.get(codigo);
            } else if (codigo == dict.size()) {
                // caso código ainda não estja no dicionário
                atual = new byte[anterior.length + 1];
                System.arraycopy(anterior, 0, atual, 0, anterior.length);
                atual[anterior.length] = anterior[0];
            } else {
                throw new IOException("Código LZW inválido: " + codigo);
            }

            baos.write(atual);

            if (dict.size() < MAX_DICT) {
                byte[] novaEntrada = new byte[anterior.length + 1];
                System.arraycopy(anterior, 0, novaEntrada, 0, anterior.length);
                novaEntrada[anterior.length] = atual[0];
                dict.add(novaEntrada);
            }

            anterior = atual;
        }

        return baos.toByteArray();
    }

    // monta o arquivo comprimido: 4 bytes de cabeçalho (tam original)
    // seguidos dos códigos como shorts (2 bytes cada)
    private static byte[] montarArquivo(
        int tamanhoOriginal,
        ArrayList<Integer> codigos
    ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
            4 + codigos.size() * 2
        );
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(tamanhoOriginal);
        for (int codigo : codigos) {
            dos.writeShort(codigo);
        }

        return baos.toByteArray();
    }
}
