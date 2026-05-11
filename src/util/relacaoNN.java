package util;

import dao.Dao;
import model.Autor;
import model.Livro;
import java.util.ArrayList;
import java.util.List;

public class IndiceRelacionalNN {
    private HashExtensivel autorParaLivros;
    private HashExtensivel livroParaAutores;
    
    private Dao<Autor> daoAutor;
    private Dao<Livro> daoLivro;

    public IndiceRelacionalNN(Dao<Autor> daoAutor, Dao<Livro> daoLivro) {
        this.daoAutor = daoAutor;
        this.daoLivro = daoLivro;
        // Inicializa os arquivos de índice na pasta 
        this.autorParaLivros = new HashExtensivel("data/index_autor_livro.dat");
        this.livroParaAutores = new HashExtensivel("data/index_livro_autor.dat");
    }
    //Cria a ligação N:N entre um autor e um livro.
    public void vincular(int idAutor, int idLivro) throws Exception {
        autorParaLivros.create(idAutor, idLivro);
        livroParaAutores.create(idLivro, idAutor);
    }
    //Encontra todos os livros de um autor.
    public List<Livro> listarLivrosDeUmAutor(int idAutor) throws Exception {
        List<Livro> lista = new ArrayList<>();
        int[] ids = autorParaLivros.read(idAutor);
        if (ids != null) {
            for (int id : ids) {
                Livro l = daoLivro.read(id); 
                if (l != null) lista.add(l);
            }
        }
        return lista;
    }
    //Encontra todos os autores de um livro.
    public List<Autor> listarAutoresDeUmLivro(int idLivro) throws Exception {
        List<Autor> lista = new ArrayList<>();
        int[] ids = livroParaAutores.read(idLivro);
        if (ids != null) {
            for (int id : ids) {
                Autor a = daoAutor.read(id);
                if (a != null) lista.add(a);
            }
        }
        return lista;
    }
}
