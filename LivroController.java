import java.io.IOException;

public class LivroController {
    private LivroDAO dao;
    private LivroView view;

    public LivroController() {
        try {
            this.dao = new LivroDAO();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.view = new LivroView();
    }

    public void run() {
        int opcao = 0;

        do {
            this.view.printaMenuInicial();
            opcao = this.view.getOpcao();

            switch (opcao) {
                case 1:
                    this.cadastraLivro();
                    break;
                case 2:
                    this.listaLivros();
                    break;
                case 3:
                    this.atualizaLivro();
                    break;
                case 4:
                    this.removeLivro();
                    break;
                case 0:
                    
                    break;
                default:
                    System.out.println("Opção inválida!\n");
                    break;
            }
        } while (opcao != 0);
    }

    private Livro criaLivro() {
        String titulo = this.view.getTitulo();
        short anoPublicacao = this.view.getAnoPublicacao();
        String isbn = this.view.getIsbn();
        String[] categorias = this.view.getCategorias();
        short quantidade = this.view.getQuantidade();

        return new Livro(titulo, anoPublicacao, isbn, categorias, quantidade);
    }

    public void cadastraLivro() {
        Livro livro = this.criaLivro();

        try {
            this.dao.inserir(livro);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listaLivros() {
        try {
            this.dao.listar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeLivro() {
        short id = this.view.getId();

        try {
            if (!this.dao.encontraLivro(id)) {
                // refatorar, mensagem não deve ser resposabilidade
                // do controller
                System.out.println("Livro não encontrado");
                return;
            }
            
            this.dao.deletar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void atualizaLivro() {
        short id = this.view.getId();
        
        try {
            if (!this.dao.encontraLivro(id)) {
                // refatorar, mensagem não deve ser resposabilidade
                // do controller
                System.out.println("Livro não encontrado");
                return;
            }

            Livro novoLivro = this.criaLivro();
            this.dao.atualizar(novoLivro);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception {
        LivroController controller = new LivroController();
        controller.run();
    }
}