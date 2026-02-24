import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.io.IOException;
import java.util.List;

public class LivroView {
    private static LivroController controller = new LivroController();
    private static Scanner scanner = new Scanner(System.in);

    public void printaMenuInicial() {
        System.out.println("---- MENU ---");
        System.out.println("1. Cadastrar Livro");
        System.out.println("2. Listar Livros");
        System.out.println("3. Atualizar Livro");
        System.out.println("4. Remover Livro");
        System.out.println("0. Sair");

        System.out.print("Escolha uma opcão: ");
    }

    public String getTitulo() {
        String titulo;
        
        while (true) {
            System.out.print("Título: ");
            titulo = scanner.nextLine();

            if (titulo.isBlank()) {
                System.out.println("Título não pode ser vazio!");
                continue;
            }

            return titulo;
        }
    }

    public int getAnoPublicacao() {
        String anoPublicacao;

        while (true) {
            System.out.println("Ano de Publicação: ");
            anoPublicacao = scanner.nextLine();

            try {
                return Integer.parseInt(anoPublicacao);
            } catch (Exception e) {
                System.out.println("Digite um número inteiro");
            }
        }
    }

    public String getIsbn() {
        String isbn;

        while (true) {
            System.out.println("ISBN: ");
            isbn = scanner.nextLine();

            if (isbn.length() < 13) {
                System.out.println("ISBN deve conter 13 dígitos");
                continue;
            }

            return isbn;
        }
    }

    public String[] getCategorias() {
        String[] categorias;

        while (true) {
            System.out.println("Categorias (separadas por vírgula): ");
            categorias = scanner.nextLine().split(",\s*");

            if (categorias.length == 0) {
                System.out.println("É necessário ter ao menos uma categoria");
                continue;
            }

            return categorias;
        }
    }

    public int getQuantidade() {
        String quantidade;
        int exemplares;

        while (true) {
            System.out.println("Exemplares: ");
            quantidade = scanner.nextLine();

            try {
                exemplares = Integer.parseInt(quantidade);

                if (exemplares > 0) {
                    return exemplares;
                }

                System.out.println("Não é possível registrar 0 ou menos exemplares");
            } catch (Exception e) {
                System.out.println("Digite um número inteiro maior que 0");
            }
        }
    }

    public static void listarLivros() {
        List<Livro> livros = controller.listarLivros();

        for (Livro l : livros) {
            String categorias = String.join(", ", l.getCategorias());

            System.out.println(
                "ID: " + l.getId() +
                " | Titulo: " + l.getTitulo() +
                " | Ano de publicacao: " + l.getAnoPublicacao() +
                " | ISBN: " + l.getISBN() +
                " | Quantidade de exemplares: " + l.getQuantidade() +
                " | Categorias: " + categorias
            );
        }
    }

    public static void atualizarLivro() {
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

    public static void removerLivro() {
        System.out.print("ID do livro para remover: ");

        int id = Integer.parseInt(scanner.nextLine());
        controller.removerLivro(id);

        System.out.println("Livro removido!");
    }
}