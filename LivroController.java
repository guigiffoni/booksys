import java.io.IOException;
import java.util.List;

public class LivroController {
    private LivroDAO dao;
    private LivroView view;

    public LivroController() {
        this.dao = new LivroDAO();
        this.view = new LivroView();
    }

    public void run() {
        int opcao = 0;

        do {
            this.view.printaMenuInicial();
            opcao = this.view.getOpcao();

            switch (opcao) {
                case 1:
                    this.cadastrarLivro();
                    break;
                case 2:
                    this.listarLivros();
                    break;
                case 3:
                    
                    break;
                case 4:
                    
                    break;
                case 0:
                    
                    break;
                default:
                    System.out.println("Opção inválida!\n");
                    break;
            }
        } while (opcao != '0');
    }

    // cadastrar livro
    public void cadastrarLivro() {
        String titulo = this.view.getTitulo();
        int anoPublicacao = this.view.getAnoPublicacao();
        String isbn = this.view.getIsbn();
        String[] categorias = this.view.getCategorias();
        int quantidade = this.view.getQuantidade();

        Livro livro = new Livro(titulo, anoPublicacao, isbn, categorias, quantidade);

        dao.inserir(livro);
    }

    // listar todos os livros
    public void listarLivros(){
        dao.listar();
    }

    // atualizar livro já existente
    public void atualizarLivro(int id, String titulo, int ano, String ISBN, String[] categorias, int quantidade){
        // Livro livro = new Livro(titulo, ano, ISBN, categorias, quantidade);
        // livro.setId(id);
        // dao.atualizar(livro);
    }

    // remover livro pelo titulo
    /* 
    public void removerLivro(String titulo) {
        dao.remover(titulo);
    }
    */
    
    public static void main(String[] args) throws Exception {
        LivroController controller = new LivroController();
        controller.run();
    }
}