package src.util;

public class HashExtensivel {
    private int profundidade;
    private Balde[] diretorio;

    HashExtensivel() {
        this(1);
    }

    HashExtensivel(int profundidadeInicial) {
        this.profundidade = profundidadeInicial;
        this.diretorio = new Balde[this.getCapacidade()];
    }

    private int getCapacidade() {
        return (int) Math.pow(2, this.profundidade);
    }

    private int hash(int k) {
        return k % this.getCapacidade();
    }

    private void aumentaDiretorio() {
        int qtdBaldes = this.getCapacidade();
        Balde[] novosBaldes = new Balde[qtdBaldes * 2];

        for (int i = 0; i < qtdBaldes; i++) {
            novosBaldes[i] = this.diretorio[i];
            novosBaldes[i + qtdBaldes] = this.diretorio[i];
        }

        this.diretorio = novosBaldes;
        this.profundidade += 1;
    }

    private void recalculaHash(Balde baldeRef) {
        int[] copiaChaves = baldeRef.chaves.clone();
        baldeRef.removerTudo();

        for (int numero : copiaChaves) {
            this.inserir(numero);
        }
    }

    void inserir(int elemento) {
        int indice = this.hash(elemento);
        Balde baldeRef = this.diretorio[indice];

        if (baldeRef == null) {
            baldeRef = new Balde(this.profundidade);
            this.diretorio[indice] = baldeRef;
        }

        if (baldeRef.isCheio()) {
            if (baldeRef.getProfundidade() == this.profundidade) {
                this.aumentaDiretorio();
                indice = this.hash(elemento);
            }

            baldeRef.setProfundidade(this.profundidade);
            this.diretorio[indice] = new Balde(this.profundidade);

            this.recalculaHash(baldeRef);
            baldeRef = this.diretorio[indice];
        }

        baldeRef.inserir(elemento);
    }
}
