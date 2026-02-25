import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Livro implements Serializable {

    // atributos
    private int id;
    private String titulo;
    private int anoPublicacao;
    private String ISBN;
    private String[] categorias;
    private int quantidade;

    public Livro() {

    }

    public Livro(
        String titulo, 
        int anoPublicacao, 
        String ISBN, 
        String[] categorias, 
        int quantidade
    ) {
        this.setTitulo(titulo);
        this.setAnoPublicacao(anoPublicacao);
        this.setISBN(ISBN);
        this.setCategorias(categorias);
        this.setQuantidade(quantidade);
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        // lapide
        dos.writeChar(' ');
        dos.writeShort(this.getTamanhoEmBytes());

        dos.writeInt(id);
        dos.writeUTF(titulo);
        dos.writeInt(anoPublicacao);
        dos.writeUTF(ISBN);
        dos.writeInt(categorias.length);
        for (String categoria : categorias) {
            dos.writeUTF(categoria);
        }
        dos.writeInt(quantidade);
        
        return baos.toByteArray();
    }

    // public static Livro fromBytes(byte[] dados) throws IOException {
    //     ByteArrayInputStream bais = new ByteArrayInputStream(dados);
    //     DataInputStream dis = new DataInputStream(bais);


    // }

    public int getTamanhoEmBytes() {
        // id + anoPublicacao + numCategorias + quantidade + ISBN
        int tamanho = 4 * 4 + 15;

        // +2 bytes para tamanho da string
        tamanho += titulo.getBytes(StandardCharsets.UTF_8).length + 2;
        for (String categoria : this.categorias) {
            // +2 bytes para tamanho da string
            tamanho += categoria.getBytes(StandardCharsets.UTF_8).length + 2;
        }

        return tamanho;
    }

    // getters e setters
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new RuntimeException("ID não pode ser menor que 1");
        }

        this.id = id;
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

    public void setCategorias(String[] categorias) {
        if (categorias == null || categorias.length < 1) {
            throw new java.lang.RuntimeException("Deve haver ao menos uma categoria");
        }
        
        this.categorias = categorias;
    }

    public int getQuantidade() {
        return this.quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new RuntimeException("Quantidade não pode ser negativa");
        }

        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return String.format(
            "%d\t|%s\t|%d\t|%s\t|%s\t|%d",
            this.getId(),
            this.getTitulo(),
            this.getAnoPublicacao(),
            this.getISBN(),
            String.join(", ", this.getCategorias()),
            this.getQuantidade()
        );
    }
}