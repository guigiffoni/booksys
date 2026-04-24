public class Balde {
    private int profundidade;
    private int numRegistros = 0;
    private final int numElementos = 3;
    private final int[] chaves = new int[this.numElementos];

    Balde() {
        this(1);
    }

    Balde(int profundidadeInicial) {
        this.profundidade = profundidadeInicial;
        
        for (int i = 0; i < numElementos; i++) {
            this.chaves[i] = -1;
        }
    }

    boolean isCheio() {
        if (this.numElementos != this.numRegistros) {
            return false;
        }

        return true;
    }

    int getProfundidade() {
        return this.profundidade;
    }

    void aumentaProfundidade() {
        this.profundidade++;
    }

    void inserir(int elemento) {
        if (!this.isCheio()) {
            this.chaves[this.numRegistros] = elemento;
            this.numRegistros += 1;
        }
    }

    int remover() {
        if (this.numRegistros > 0) {
            this.numRegistros -= 1;
            int numeroRemovido = this.chaves[this.numRegistros];
            this.chaves[this.numRegistros] = 0;

            return numeroRemovido;
        }

        return -1;
    }
}