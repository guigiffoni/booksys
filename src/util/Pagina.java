package src.util;

import java.util.Arrays;
import java.util.Comparator;

public class Pagina<T extends Registro> {
    private final int maxChaves;
    private int numChaves;
    private final int indiceChavePromovida;
    public boolean folha;
    public Pagina<T> proximo = null;
    public Chave<T>[] chaves;

    @SuppressWarnings("unchecked")
    Pagina(int maxChaves) {
        this.maxChaves = maxChaves;
        this.chaves = (Chave<T>[]) new Chave[maxChaves];
        this.numChaves = 0;
        this.folha = false;
        this.indiceChavePromovida = (maxChaves + 1) / 2;
    }

    Pagina(int maxChaves, boolean folha) {
        this(maxChaves);
        this.folha = folha;
    }

    void inserirFolha(T registro) {
        if (folha && !isCheio()) {
            Chave<T> novoRegistro = new Chave<>(registro);
            chaves[numChaves] = novoRegistro;
            numChaves++;

            Arrays.sort(
                chaves, 
                0, 
                numChaves, 
                Comparator.comparingInt(c -> c.refInstancia.getId())
            );
        }
    }

    void inserirChaveInterna(Chave<T> chave, Pagina<T> esq, Pagina<T> dir) {
        if (esq != null) {
            chave.pagEsquerda = esq;
        }

        if (dir != null) {
            chave.pagDireita = dir;
        }

        // Encontra posição de inserção
        int pos = 0;
        while (
            pos < numChaves && 
            chaves[pos].refInstancia.getId() < chave.refInstancia.getId()
        ) {
            pos++;
        }

        for (int i = numChaves; i > pos; i--) {
            chaves[i] = chaves[i - 1];
        }
        chaves[pos] = chave;
        numChaves++;

        // Ajusta os ponteiros laterais
        if (pos > 0) {
            chaves[pos - 1].pagDireita = chave.pagEsquerda;
        }

        if (pos < numChaves - 1) {
            chave.pagDireita = chaves[pos + 1].pagEsquerda;
        }
    }

    // split de página folha
    Chave<T> splitFolha(T novoRegistro, Pagina<T> novaPagina) {
        int total = numChaves + 1;

        @SuppressWarnings("unchecked")
        Chave<T>[] todas = (Chave<T>[]) new Chave[total];
        System.arraycopy(chaves, 0, todas, 0, numChaves);
        todas[numChaves] = new Chave<>(novoRegistro);
        Arrays.sort(
            todas, 
            0, 
            total, 
            Comparator.comparingInt(c -> c.refInstancia.getId())
        );

        // limpa a página atual
        this.numChaves = 0;
        for (int i = 0; i < indiceChavePromovida; i++) {
            this.inserirFolha(todas[i].refInstancia);
        }
        // preenche a nova folha
        for (int i = indiceChavePromovida; i < total; i++) {
            novaPagina.inserirFolha(todas[i].refInstancia);
        }

        Chave<T> promovida = new Chave<>(novaPagina.chaves[0].refInstancia);
        promovida.pagEsquerda = this;
        promovida.pagDireita = novaPagina;

        promovida.pagEsquerda.proximo = novaPagina;

        return promovida;
    }

    // split de página interna
    Chave<T> splitInterno(Chave<T> chaveInserida) {
        int total = numChaves + 1;
        @SuppressWarnings("unchecked")
        Chave<T>[] todas = new Chave[total];
        System.arraycopy(chaves, 0, todas, 0, numChaves);
        todas[numChaves] = chaveInserida;
        Arrays.sort(
            todas, 
            0, 
            total, 
            Comparator.comparingInt(c -> c.refInstancia.getId())
        );

        // chave que sobe para o pai
        Chave<T> promovida = todas[indiceChavePromovida];
        Pagina<T> novaDireita = new Pagina<>(this.maxChaves, false);

        // Preenche página esquerda (this) com as chaves antes da promovida
        this.numChaves = 0;

        for (int i = 0; i < indiceChavePromovida; i++) {
            this.inserirChaveInterna(todas[i], null, null);
        }

        // preenche página direita com as chaves depois da promovida
        for (int i = indiceChavePromovida + 1; i < total; i++) {
            novaDireita.inserirChaveInterna(todas[i], null, null);
        }

        // ajusta os ponteiros da chave promovida
        if (this.numChaves > 0) {
            promovida.pagEsquerda = this.chaves[this.numChaves - 1].pagDireita;
        } else {
            promovida.pagEsquerda = chaveInserida.pagEsquerda;
        }

        promovida.pagDireita = novaDireita;
        if (novaDireita.numChaves > 0) {
            novaDireita.chaves[0].pagEsquerda = promovida.pagDireita;
        }

        return promovida;
    }

    public int getNumChaves() {
        return this.numChaves;
    }

    boolean isCheio() {
        return this.numChaves >= maxChaves;
    }
}
