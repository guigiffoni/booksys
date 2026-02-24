import java.io.Serializable;

public class Livro implements Serializable {

    // atributos
    private int id;
    private String titulo;
    private int anoPublicacao;
    private String ISBN;
    private String[] categorias;
    private int quantidade;

    public Livro(
        String titulo, 
        int anoPublicacao, 
        String ISBN, 
        String[] categorias, 
        int quantidade
    ) throws Exception {
        this.setTitulo(titulo);
        this.setAnoPublicacao(anoPublicacao);
        this.setISBN(ISBN);
        this.setCategorias(categorias);
        this.setQuantidade(quantidade);
    }

    // getters e setters
    public int getId() {
        return this.id;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }

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
        if (ISBN == null || ISBN.length() != 13) {
            throw new IllegalArgumentException("ISBN deve conter 13 dígitos");
        }

        this.ISBN = ISBN;
    }

    public String[] getCategorias() {
        return this.categorias;
    }

    public void setCategorias(String[] categorias) throws Exception {
        if (categorias == null || categorias.length < 1) {
            throw new Exception("Deve haver ao menos uma categoria");
        }
        
        this.categorias = categorias;
    }

    public int getQuantidade() {
        return this.quantidade;
    }

    public void setQuantidade(int quantidade) throws Exception {
        if (quantidade < 0) {
            throw new Exception("Quantidade não pode ser negativa");
        }

        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return String.format(
            "'%s' - %d\tDisponíveis: %d", 
            this.getTitulo(), 
            this.getAnoPublicacao(),
            this.getQuantidade()
        );
    }
}