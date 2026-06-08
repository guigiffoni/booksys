package src.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Lzw {

    private static final int TAMANHO_INICIAL = 256;
    private static final int MAX_DICT = 4096;
    private static final int LARGURA_INICIAL = 6;
    private static final int LARGURA_MAXIMA = 12;

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

        String nomeOriginal = nomeArquivo.endsWith(".lzw")
                ? nomeArquivo.substring(0, nomeArquivo.length() - 4)
                : nomeArquivo + ".descomprimido";

        Path destino = Paths.get(PATH + nomeOriginal);
        Files.write(destino, restaurado);
        return restaurado.length;
    }

    public static byte[] comprimirBytes(byte[] entrada) throws IOException {
        if (entrada.length == 0) {
            // arquivo vazio: apenas cabeçalho com tamanho 0
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(0);
            dos.flush();
            return baos.toByteArray();
        }

        HashMap<String, Integer> dict = new HashMap<>(MAX_DICT);
        for (int i = 0; i < TAMANHO_INICIAL; i++) {
            dict.put(String.valueOf((char) i), i);
        }

        int proximoCodigo = TAMANHO_INICIAL;
        int larguraAtual = LARGURA_INICIAL;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(entrada.length);

        BitOutputStream bos = new BitOutputStream(baos);

        String prefixo = String.valueOf((char) (entrada[0] & 0xFF));

        for (int i = 1; i < entrada.length; i++) {
            char c = (char) (entrada[i] & 0xFF);
            String prefixoMaisC = prefixo + c;

            if (dict.containsKey(prefixoMaisC)) {
                prefixo = prefixoMaisC;
            } else {
                // emite o código do prefixo com a largura atual
                bos.writeBits(dict.get(prefixo), larguraAtual);

                // adiciona ao dicionário se ainda couber
                if (proximoCodigo < MAX_DICT) {
                    dict.put(prefixoMaisC, proximoCodigo++);
                    // aumenta a largura quando o próximo código ultrapassar (1 << largura)
                    if (proximoCodigo > (1 << larguraAtual) && larguraAtual < LARGURA_MAXIMA) {
                        larguraAtual++;
                    }
                }
                prefixo = String.valueOf(c);
            }
        }

        bos.writeBits(dict.get(prefixo), larguraAtual);

        bos.close();
        dos.flush();
        return baos.toByteArray();
    }

    public static byte[] descomprimirBytes(byte[] comprimido) throws IOException {
        if (comprimido.length == 0)
            return new byte[0];

        ByteArrayInputStream bais = new ByteArrayInputStream(comprimido);
        DataInputStream dis = new DataInputStream(bais);

        int tamanhoOriginal = dis.readInt();
        if (tamanhoOriginal == 0)
            return new byte[0];

        BitInputStream bis = new BitInputStream(bais);

        // dicionário: código -> sequência de bytes
        ArrayList<byte[]> dict = new ArrayList<>(MAX_DICT);
        for (int i = 0; i < TAMANHO_INICIAL; i++) {
            dict.add(new byte[] { (byte) i });
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream(tamanhoOriginal);

        int larguraAtual = LARGURA_INICIAL;
        int proximoCodigo = TAMANHO_INICIAL;

        // primeiro código
        int codigo = bis.readBits(larguraAtual);
        if (codigo == -1)
            return new byte[0];

        byte[] entradaAtual = dict.get(codigo);
        baos.write(entradaAtual);
        byte[] anterior = entradaAtual;

        while (baos.size() < tamanhoOriginal) {
            if (proximoCodigo >= (1 << larguraAtual) && larguraAtual < LARGURA_MAXIMA) {
                larguraAtual++;
            }

            int cod = bis.readBits(larguraAtual);
            if (cod == -1)
                break;

            byte[] atual;
            if (cod < dict.size()) {
                atual = dict.get(cod);
            } else if (cod == dict.size()) {
                // caso especial: código ainda não adicionado
                atual = new byte[anterior.length + 1];
                System.arraycopy(anterior, 0, atual, 0, anterior.length);
                atual[anterior.length] = anterior[0];
            } else {
                throw new IOException("Código LZW inválido: " + cod);
            }

            baos.write(atual);

            // adiciona nova entrada ao dicionário
            if (dict.size() < MAX_DICT) {
                byte[] novaEntrada = new byte[anterior.length + 1];
                System.arraycopy(anterior, 0, novaEntrada, 0, anterior.length);
                novaEntrada[anterior.length] = atual[0];
                dict.add(novaEntrada);
                proximoCodigo++;
            }

            anterior = atual;
        }

        return baos.toByteArray();
    }

    private static class BitOutputStream implements Closeable {
        private final OutputStream out;
        private int buffer;
        private int bitsNoBuffer;

        public BitOutputStream(OutputStream out) {
            this.out = out;
            this.buffer = 0;
            this.bitsNoBuffer = 0;
        }

        // escreve os 'numBits' menos significativos de 'valor' (máx 32)
        public void writeBits(int valor, int numBits) throws IOException {
            if (numBits == 0) {
                return;
            }

            // escreve bit a bit, começando pelo mais significativo
            for (int i = numBits - 1; i >= 0; i--) {
                int bit = (valor >> i) & 1;
                buffer = (buffer << 1) | bit;
                bitsNoBuffer++;
                if (bitsNoBuffer == 8) {
                    out.write(buffer);
                    buffer = 0;
                    bitsNoBuffer = 0;
                }
            }
        }

        // preenche o último byte com zeros e escreve
        @Override
        public void close() throws IOException {
            if (bitsNoBuffer > 0) {
                buffer <<= (8 - bitsNoBuffer);
                out.write(buffer);
                buffer = 0;
                bitsNoBuffer = 0;
            }
        }
    }

    private static class BitInputStream {
        private final InputStream in;
        private int buffer;
        private int bitsRestantes;

        public BitInputStream(InputStream in) {
            this.in = in;
            this.buffer = 0;
            this.bitsRestantes = 0;
        }

        // lê 'numBits' e retorna como inteiro; -1 se EOF durante a leitura
        public int readBits(int numBits) throws IOException {
            if (numBits == 0)
                return 0;
            int resultado = 0;
            for (int i = 0; i < numBits; i++) {
                if (bitsRestantes == 0) {
                    int proximo = in.read();
                    if (proximo == -1)
                        return -1;
                    buffer = proximo;
                    bitsRestantes = 8;
                }
                // extrai o bit mais significativo
                int bit = (buffer >> (bitsRestantes - 1)) & 1;
                bitsRestantes--;
                resultado = (resultado << 1) | bit;
            }
            return resultado;
        }
    }
}