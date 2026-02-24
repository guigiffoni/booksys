

import model.Livro;
import controller.LivroController;
import java.util.Scanner;
import java.util.List;


public class LivroView {
    private static LivroController controller = new LivroController();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args){
        int op = 0;

        do{
            System.out.println("---- MENU ---");
            System.out.println("1. Cadastrar Livro");
            System.out.println("2. Listar Livros");
            System.out.println("3. Atualizar Livro");
            System.out.println("4. Remover Livro");
            System.out.println("0. Sair");
            System.out.println("Escolha uma opcao");

            op = Integer.parseInt(scanner.nextLine());

            switch(op){
                case 1: cadastrarLivro(); break;
                case 2: listarLivros(); break;
                case 3: atualizarLivro(); break;
                case 4: removerLivro(); break;
                case 0: System.out.println("Saindo..."); break; 
                default: System.out.println("Opcao invalida!");

            }
        } while (op!=0);
    }

    public static void cadastrarLivro(){
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Ano de publicacao (AAAA): ");
        int ano = Integer.parseInt(scanner.nextLine());
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Categorias (separadas por vírgula): ");
        String[] categorias = scanner.nextLine().split(",");
        System.out.print("Quantidade de exemplares: ");
        int qtd = Integer.parseInt(scanner.nextLine());

        controller.cadastrarLivro(titulo, ano, isbn, categorias, qtd);
        System.out.println("Livro cadastrado com sucesso!");
    }

    public static void listarLivros(){
        List<Livro> livros = controller.listarLivros();
        for(Livro l : livros){
            String categorias = String.join(", ", l.getCategorias());
            System.out.println("ID: " + l.getId() + " | Titulo: " + l.getTitulo() + " | Ano de publicacao: " + l.getAnoPublicacao() + " | ISBN: " + l.getISBN() + " | Quantidade de exemplares: " + l.getQuantidade() + " | Categorias: " + categorias);
        }
    }

    public static void atualizarLivro(){
         System.out.print("ID do livro para atualizar: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Novo título: ");
        String titulo = scanner.nextLine();
        System.out.print("Novo ano de publicacao: ");
        int ano = Integer.parseInt(scanner.nextLine());
        System.out.print("Novo ISBN: ");
        String ISBN = scanner.nextLine();
        System.out.print("Novas categorias (separadas por vírgula): ");
        String[] categorias = scanner.nextLine().split(",");
        System.out.print("Nova quantidade de exemplares: ");
        int qtd = Integer.parseInt(scanner.nextLine());

        controller.atualizarLivro(id, titulo, ano, ISBN, categorias, qtd);
        System.out.println("Livro atualizado com sucesso!");
    }

    public static void removerLivro(){
        System.out.print("ID do livro para remover: ");
        int id = Integer.parseInt(scanner.nextLine());
        controller.removerLivro(id);
        System.out.println("Livro removido!");
    }
}