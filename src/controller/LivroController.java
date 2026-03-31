package src.controller;
import src.dao.LivroDAO;
import src.view.LivroView;
import src.model.Livro;

import java.io.IOException;
import java.util.ArrayList;

public class LivroController {
    private LivroDAO dao;
    private LivroView view;

    public LivroController() {
        try {
            this.dao = new LivroDAO();
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
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
        ArrayList<Livro> livros;

        try {
            livros = this.dao.listar();

            if (livros.isEmpty()) {
                this.view.exibeMensagem("Lista de livros é vazia!");
            } else {
                this.view.exibeListaLivros(livros);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void atualizaLivro() {
        short id = this.view.getId();
        
        try {
            if (!this.dao.encontraLivro(id)) {
                throw new RuntimeException("Livro não encontrado");
            }

            Livro novoLivro = this.criaLivro();
            this.dao.atualizar(novoLivro);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void removeLivro() {
        short id = this.view.getId();

        try {
            if (!this.dao.encontraLivro(id)) {
                throw new RuntimeException("Livro não encontrado");
            }
            
            this.dao.deletar();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    public static void main(String[] args) throws Exception {
        LivroController controller = new LivroController();
        controller.run();
    }
}