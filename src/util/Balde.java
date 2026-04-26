package src.util;

public class Balde {
    private int profundidade;
    private int numChaves = 0;
    private static final int maxChaves = 3;
    public int[] chaves = new int[Balde.maxChaves];

    Balde() {
        this(1);
    }

    Balde(int profundidadeInicial) {
        this.profundidade = profundidadeInicial;
        
        for (int i = 0; i < Balde.maxChaves; i++) {
            this.chaves[i] = -1;
        }
    }

    boolean isCheio() {
        return this.numChaves >= Balde.maxChaves;
    }

    int getProfundidade() {
        return this.profundidade;
    }

    void setProfundidade(int novaProfundidade) {
        this.profundidade = novaProfundidade;
    }

    void inserir(int elemento) {
        if (!this.isCheio()) {
            this.chaves[this.numChaves] = elemento;
            this.numChaves += 1;
        }
    }

    int remover() {
        if (this.numChaves > 0) {
            this.numChaves -= 1;
            int numeroRemovido = this.chaves[this.numChaves];
            this.chaves[this.numChaves] = 0;

            return numeroRemovido;
        }

        return -1;
    }

    void removerTudo() {
        for (int i = 0; i < Balde.maxChaves; i++) {
            this.chaves[i] = -1;
        }

        this.numChaves = 0;
    }

    int[] getChaves() {
        return this.chaves;
    }
}