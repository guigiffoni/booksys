package model;

public class Livro {

    // atributos
    private int id;
    private String titulo;
    private int anoPublicacao;
    private String ISBN;
    private String[] categorias;
    private int quantidade;

    // construtores
    public Livro(){}

    public Livro(String titulo, int anoPublicacao, String ISBN, String[] categorias, int quantidade){
        this.titulo = titulo;
        this.anoPublicacao = anoPublicacao;
        this.ISBN = ISBN;
        this.categorias = categorias;
        this.quantidade = quantidade;
    }

    // getters e setters
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return this.titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getAnoPublicacao() {
        return this.anoPublicacao;
    }
    public void setAnoPublicacao(int anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public String getISBN() {
        return this.ISBN;
    }
    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String[] getCategorias() {
        return this.categorias;
    }
    public void setCategorias(String[] categorias) {
        this.categorias = categorias;
    }

    public int getQuantidade() {
        return this.quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

}