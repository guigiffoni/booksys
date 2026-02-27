import java.util.Scanner;

public class LivroView {
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

    public int getOpcao() {
        while (true) {
            if (scanner.hasNextInt()) {
                return Integer.parseInt(scanner.nextLine());
            }

            System.out.println("Favor inserir um número");
        }
    }

    public short getId() {
        String id;

        while (true) {
            System.out.print("ID do livro: ");
            id = scanner.nextLine();
            short idParseado;

            try {
                idParseado = Short.parseShort(id);

                if (idParseado > 0) {
                    return idParseado;
                }

                System.out.println("IDs menores que 1 são inválidos");
            } catch (Exception e) {
                System.out.println("Digite um número inteiro maior que 0");
            }
        }
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

    public short getAnoPublicacao() {
        String anoPublicacao;

        while (true) {
            System.out.print("Ano de Publicação: ");
            anoPublicacao = scanner.nextLine();

            try {
                return Short.parseShort(anoPublicacao);
            } catch (Exception e) {
                System.out.println("Digite um número inteiro");
            }
        }
    }

    public String getIsbn() {
        String isbn;

        while (true) {
            System.out.print("ISBN: ");
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
            System.out.print("Categorias (separadas por vírgula): ");
            categorias = scanner.nextLine().split(",\s*");

            if (categorias.length == 0) {
                System.out.println("É necessário ter ao menos uma categoria");
                continue;
            }

            return categorias;
        }
    }

    public short getQuantidade() {
        String quantidade;
        short exemplares;

        while (true) {
            System.out.print("Exemplares: ");
            quantidade = scanner.nextLine();

            try {
                exemplares = Short.parseShort(quantidade);

                if (exemplares > 0) {
                    return exemplares;
                }

                System.out.println("Não é possível registrar 0 ou menos exemplares");
            } catch (Exception e) {
                System.out.println("Digite um número inteiro maior que 0");
            }
        }
    }

    /*
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
    */
}