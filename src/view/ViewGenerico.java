package src.view;

import java.util.ArrayList;
import java.util.Scanner;

public class ViewGenerico<T> {
    protected Scanner scanner = new Scanner(System.in);

    public int getOpcao() {
        while (true) {
            if (scanner.hasNextInt()) {
                return Integer.parseInt(scanner.nextLine());
            }

            System.out.println("Favor inserir um número inteiro");
        }
    }

    public void exibeMensagem(String mensagem) {
        System.out.println(mensagem);
    }

    public void exibeMensagemErro(String mensagem) {
        System.out.println(mensagem);
    }

    public void exibeLista(ArrayList<T> entidades) {
        for (T entidade : entidades) {
            System.out.println(entidade.toString());
        }
    }

    public void printaMenuInicial() {}
}
