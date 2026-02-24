package controller;

import model.Livro;
import DAO.LivroDAO;
import java.util.List;

public class LivroController {
    private LivroDAO dao;

    public LivroController(){
        this.dao = new LivroDAO();
    }

    // cadastrar livro
    public void cadastrarLivro(String titulo, int ano, String ISBN, String[] categorias, int quantidade){
        Livro livro = new Livro(titulo, ano, ISBN, categorias, quantidade);
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

    // remover livro pelo ID
    public void removerLivro(int id){
        dao.remover(id);
    }

}