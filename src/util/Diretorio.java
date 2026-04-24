public class Diretorio {
    private int profundidade;
    private Balde[] baldes;

    Diretorio() {
        this(1);
    }

    Diretorio(int profundidadeInicial) {
        this.profundidade = profundidadeInicial;
        this.baldes = new Balde[this.getQtdBaldes()];
    }

    private int getQtdBaldes() {
        return (int) Math.pow(2, this.profundidade);
    }

    private int hash(int k) {
        return k % this.getQtdBaldes();
    }

    private void aumentaDiretorio() {
        int qtdBaldes = this.getQtdBaldes();
        Balde[] novosBaldes = new Balde[qtdBaldes * 2];
        
        for (int i = 0; i < qtdBaldes; i++) {
            novosBaldes[i] = this.baldes[i];
            novosBaldes[i + qtdBaldes] = this.baldes[i];
        }

        this.baldes = novosBaldes;
        this.profundidade += 1;
    }

    void inserir(int elemento) {
        int indice = this.hash(elemento);
        Balde baldeRef = this.baldes[indice];

        if (baldeRef == null) {
            baldeRef = new Balde(this.profundidade);
        }

        if (baldeRef.isCheio() && baldeRef.getProfundidade() == this.profundidade) {
            this.aumentaDiretorio();
            baldeRef.aumentaProfundidade();

            int elementoFoo = baldeRef.remover();
            int indiceFoo = this.hash(elementoFoo);

            while (elementoFoo != -1) {
                this.baldes[indiceFoo].inserir(elementoFoo);

                elementoFoo = baldeRef.remover();
            }

            indice = this.hash(elemento);
            baldeRef = this.baldes[indice];
        }

        baldeRef.inserir(elemento);
    }

    void test() {
        System.out.println(this.baldes.length);
        System.out.println(this.baldes[0]);
    }
}
