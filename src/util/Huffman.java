package src.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;

public class Huffman {

    private static class No implements Comparable<No> {
        int frequencia;
        int valor;
        No esquerdo;
        No direito;

        No(int valor, int frequencia) {
            this.valor = valor;
            this.frequencia = frequencia;
        }

        No(No esquerdo, No direito) {
            this.valor = -1;
            this.frequencia = esquerdo.frequencia + direito.frequencia;
            this.esquerdo = esquerdo;
            this.direito = direito;
        }

        boolean isFolha() {
            return esquerdo == null && direito == null;
        }

        @Override
        public int compareTo(No outro) {
            return Integer.compare(this.frequencia, outro.frequencia);
        }
    }

    private static int[] contarFrequencias(byte[] dados) {
        int[] freq = new int[256];
        for (byte b : dados) {
            freq[b & 0xFF]++;
        }
        return freq;
    }

    private static No construirArvore(int[] frequencias) {
        PriorityQueue<No> fila = new PriorityQueue<>();
        for (int i = 0; i < 256; i++) {
            if (frequencias[i] > 0) {
                fila.add(new No(i, frequencias[i]));
            }
        }
        if (fila.size() == 1) {
            No unico = fila.poll();
            return new No(unico, new No(unico.valor, 0));
        }
        while (fila.size() > 1) {
            No esq = fila.poll();
            No dir = fila.poll();
            fila.add(new No(esq, dir));
        }
        return fila.poll();
    }

    private static void gerarCodigos(No no, String codigo, String[] tabela) {
        if (no == null) return;
        if (no.isFolha()) {
            tabela[no.valor] = codigo.isEmpty() ? "0" : codigo;
            return;
        }
        gerarCodigos(no.esquerdo, codigo + "0", tabela);
        gerarCodigos(no.direito, codigo + "1", tabela);
    }

    public static byte[] comprimir(byte[] dados) throws IOException {
        int[] frequencias = contarFrequencias(dados);
        No raiz = construirArvore(frequencias);
        String[] tabela = new String[256];
        gerarCodigos(raiz, "", tabela);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        for (int freq : frequencias) {
            dos.writeInt(freq);
        }

        StringBuilder bits = new StringBuilder();
        for (byte b : dados) {
            bits.append(tabela[b & 0xFF]);
        }

        int tamanhoOriginal = bits.length();
        dos.writeInt(tamanhoOriginal);

        int i = 0;
        while (i < bits.length()) {
            int fim = Math.min(i + 8, bits.length());
            String trecho = bits.substring(i, fim);
            while (trecho.length() < 8) {
                trecho += "0";
            }
            dos.writeByte((byte) Integer.parseInt(trecho, 2));
            i += 8;
        }

        dos.flush();
        return baos.toByteArray();
    }

    public static byte[] descomprimir(byte[] dados) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(dados);
        DataInputStream dis = new DataInputStream(bais);

        int[] frequencias = new int[256];
        for (int i = 0; i < 256; i++) {
            frequencias[i] = dis.readInt();
        }

        No raiz = construirArvore(frequencias);

        int tamanhoOriginal = dis.readInt();

        ByteArrayOutputStream resultado = new ByteArrayOutputStream();
        No atual = raiz;
        int decodificados = 0;

        while (dis.available() > 0 && decodificados < tamanhoOriginal) {
            int byteAtual = dis.readUnsignedByte();
            for (int bit = 7; bit >= 0 && decodificados < tamanhoOriginal; bit--) {
                int valorBit = (byteAtual >> bit) & 1;
                atual = valorBit == 0 ? atual.esquerdo : atual.direito;
                if (atual.isFolha()) {
                    resultado.write(atual.valor);
                    decodificados++;
                    atual = raiz;
                }
            }
        }

        return resultado.toByteArray();
    }
}