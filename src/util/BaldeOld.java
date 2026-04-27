package src.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BaldeOld {
    public static final int maxElementos = 5;
    // profundidade + numElementos + (maxElementos * (chave + valor))
    public static final int tamanhoBytes = 4 * (2 + maxElementos * 2);

    private int profundidade;
    private int numElementos = 0;

    public int[] chaves = new int[BaldeOld.maxElementos];
    public int[] valores = new int[BaldeOld.maxElementos];

    BaldeOld() {
        this(1);
    }

    BaldeOld(int profundidadeInicial) {
        this.profundidade = profundidadeInicial;
        
        for (int i = 0; i < BaldeOld.maxElementos; i++) {
            this.chaves[i] = -1;
            this.valores[i] = -1;
        }
    }

    // ideia boa! implementar nos models; substitui método estático fromBytes
    // também podemos fazer a mesma coisa para formToInstance
    BaldeOld(byte[] dados) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(dados);
        DataInputStream dis = new DataInputStream(bais);

        this.profundidade = dis.readInt();
        this.numElementos = dis.readInt();

        for (int i = 0; i < BaldeOld.maxElementos; i++) {
            this.chaves[i] = dis.readInt();
            this.valores[i] = dis.readInt();
        }
    }

    byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.profundidade);
        dos.writeInt(this.numElementos);

        for (int i = 0; i < this.numElementos; i++) {
            dos.writeInt(this.chaves[i]);
            dos.writeInt(this.valores[i]);
        }

        for (int i = this.numElementos; i < BaldeOld.maxElementos; i++) {
            dos.writeInt(-1);
            dos.writeInt(-1);
        }

        return baos.toByteArray();
    }

    boolean isCheio() {
        return this.numElementos >= BaldeOld.maxElementos;
    }

    int getProfundidade() {
        return this.profundidade;
    }

    void setProfundidade(int novaProfundidade) {
        this.profundidade = novaProfundidade;
    }

    void inserir(int chave, int valor) {
        if (!this.isCheio()) {
            this.chaves[this.numElementos] = chave;
            this.valores[this.numElementos] = valor;
            this.numElementos += 1;
        }
    }

    void remover() {
        if (this.numElementos > 0) {
            this.numElementos -= 1;
            this.chaves[this.numElementos] = 0;
        }
    }

    void removerTudo() {
        for (int i = 0; i < BaldeOld.maxElementos; i++) {
            this.chaves[i] = -1;
        }

        this.numElementos = 0;
    }

    int[] getChaves() {
        return this.chaves;
    }

    int getNumElementos() {
        return this.numElementos;
    }

    int[] buscarValores(int chave) {
        int[] temp = new int[BaldeOld.maxElementos];
        int contador = 0;

        for (int i = 0; i < this.numElementos; i++) {
            if (this.chaves[i] == chave) {
                temp[contador++] = this.valores[i];
            }
        }

        int[] chavesEncontradas = new int[contador];
        System.arraycopy(temp, 0, chavesEncontradas, 0, contador);

        return chavesEncontradas;
    }
}