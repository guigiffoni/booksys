import java.io.IOException;
import java.util.List;

public class LivroController {
    private LivroDAO dao;
    private LivroView view;

    public LivroController() throws IOException{
        this.dao = new LivroDAO();
        this.view = new LivroView();
    }

    public void run() throws Exception {
        char opcao = '0';

        do {
            this.view.printaMenuInicial();

            switch (opcao) {
                case '1':
                    this.cadastrarLivro();
                    break;
                case '2':
                    
                    break;
                case '3':
                    
                    break;
                case '4':
                    
                    break;
                case '0':
                    
                    break;
                default:
                    System.out.println("Opção inválida!\n");
                    break;
            }
        } while (opcao != '0');
    }

    // cadastrar livro
    public void cadastrarLivro() throws Exception {
        String titulo = this.view.getTitulo();
        int anoPublicacao = this.view.getAnoPublicacao();
        String isbn = this.view.getIsbn();
        String[] categorias = this.view.getCategorias();
        int quantidade = this.view.getQuantidade();

        Livro livro = new Livro(titulo, anoPublicacao, isbn, categorias, quantidade);

        dao.salvar(livro);
    }

    // listar todos os livros
    public List<Livro> listarLivros(){
        return dao.listar();
    }

    // atualizar livro já existente
    public void atualizarLivro(int id, String titulo, int ano, String ISBN, String[] categorias, int quantidade){
        Livro livro = new Livro(titulo, ano, ISBN, categorias, quantidade);
        livro.setId(id);
        dao.atualizar(livro);
    }

    // remover livro pelo titulo
    public void removerLivro(String titulo) {
        dao.remover(titulo);
    }
    
    public static void main(String[] args) throws Exception {
        LivroController controller = new LivroController();
        controller.run();
    }
}