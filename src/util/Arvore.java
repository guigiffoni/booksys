package src.util;

import java.util.ArrayList;

public class Arvore<T extends Registro> {
    final int maxChaves = 3;
    final int indiceChavePromovida = (maxChaves + 1) / 2;
    Pagina<T> raiz;

    public Arvore() {
        this.raiz = new Pagina<>(this.maxChaves, true);
    }

    Pagina<T> encontraPaginaAlvo(Pagina<T> pag, int valor) {
        for (int i = 0; i < pag.getNumChaves(); i++) {
            if (valor < pag.chaves[i].refInstancia.getId()) {
                return pag.chaves[i].pagEsquerda;
            }
        }

        return pag.chaves[pag.getNumChaves() - 1].pagDireita;
    }

    public Pagina<T> getPrimeiraFolha() {
        Pagina<T> ref = this.raiz;

        while (!ref.folha) {
            ref = ref.chaves[0].pagEsquerda;
        }

        return ref;
    }

    public void inserir(T registro) {
        short id = registro.getId();
        ArrayList<Pagina<T>> pilha = new ArrayList<>();
        Pagina<T> atual = this.raiz;

        // desce até a folha
        while (!atual.folha) {
            pilha.add(atual);
            atual = encontraPaginaAlvo(atual, id);
        }

        // caso a folha não esteja cheia
        if (!atual.isCheio()) {
            atual.inserirFolha(registro);
            return;
        }

        // folha cheia: split
        Pagina<T> novaFolha = new Pagina<>(this.maxChaves, true);
        Chave<T> chavePromovida = atual.splitFolha(registro, novaFolha);

        // se a folha era a raiz, cria nova raiz
        if (atual == this.raiz) {
            Pagina<T> novaRaiz = new Pagina<>(this.maxChaves, false);
            novaRaiz.inserirChaveInterna(chavePromovida, atual, novaFolha);
            this.raiz = novaRaiz;

            return;
        }

        // ajusta ponteiros da chave promovida
        chavePromovida.pagEsquerda = atual;
        chavePromovida.pagDireita = novaFolha;

        // sobe a pilha inserindo nos pais
        while (!pilha.isEmpty()) {
            Pagina<T> pai = pilha.remove(pilha.size() - 1);

            if (!pai.isCheio()) {
                pai.inserirChaveInterna(chavePromovida, null, null);

                return;
            }

            // pai cheio: split interno
            Chave<T> novaChavePromovida = pai.splitInterno(chavePromovida);
            
            if (pai == this.raiz) {
                Pagina<T> novaRaiz = new Pagina<>(this.maxChaves, false);
                novaRaiz.inserirChaveInterna(novaChavePromovida, pai, novaChavePromovida.pagDireita);
                this.raiz = novaRaiz;

                return;
            }

            chavePromovida = novaChavePromovida;
        }
    }
}