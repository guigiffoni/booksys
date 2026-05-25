package src.util;

import src.dao.Dao;
import src.model.Autor;
import src.model.Livro;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndiceRelacionalNN {
    private IndiceHash autorParaLivros;
    private IndiceHash livroParaAutores;
    private Dao<Autor> daoAutor;
    private Dao<Livro> daoLivro;

    public IndiceRelacionalNN(Dao<Autor> daoAutor, Dao<Livro> daoLivro) throws IOException {
        this.daoAutor = daoAutor;
        this.daoLivro = daoLivro;
        this.autorParaLivros = new IndiceHash("index_autor_livro");
        this.livroParaAutores = new IndiceHash("index_livro_autor");
    }

    public void vincular(int idAutor, int idLivro) throws IOException {
        // Verifica se ambos existem (opcional, mas recomendado)
        if (daoAutor.encontraRegistro((short) idAutor) == null)
            throw new IllegalArgumentException("Autor não encontrado");
        if (daoLivro.encontraRegistro((short) idLivro) == null)
            throw new IllegalArgumentException("Livro não encontrado");

        autorParaLivros.inserir((short) idAutor, (short) idLivro);
        livroParaAutores.inserir((short) idLivro, (short) idAutor);
    }

    public void desvincular(int idAutor, int idLivro) throws IOException {
        boolean removed1 = autorParaLivros.remover((short) idAutor, (short) idLivro);
        boolean removed2 = livroParaAutores.remover((short) idLivro, (short) idAutor);
        if (!removed1 && !removed2)
            throw new IllegalArgumentException("Vínculo não encontrado");
    }

    public List<Livro> listarLivrosDoAutor(int idAutor) throws IOException {
        List<Livro> livros = new ArrayList<>();
        ArrayList<Short> ids = autorParaLivros.buscar((short) idAutor);
        for (short id : ids) {
            Livro l = daoLivro.encontraRegistro(id);
            if (l != null) livros.add(l);
        }
        return livros;
    }

    public List<Autor> listarAutoresDoLivro(int idLivro) throws IOException {
        List<Autor> autores = new ArrayList<>();
        ArrayList<Short> ids = livroParaAutores.buscar((short) idLivro);
        for (short id : ids) {
            Autor a = daoAutor.encontraRegistro(id);
            if (a != null) autores.add(a);
        }
        return autores;
    }

    public void fechar() throws IOException {
        autorParaLivros.fechar();
        livroParaAutores.fechar();
    }
}