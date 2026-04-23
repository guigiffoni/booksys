package src.model;
import src.util.RequestHelper;
import src.util.Registro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap; 

public class Livro implements Registro {
    // atributos
    private short id;
    private String titulo;
    private short anoPublicacao;
    private String ISBN;
    private String[] categorias;
    private short quantidade;

    public Livro() {

    }

    public Livro(
        String titulo, 
        short anoPublicacao, 
        String ISBN, 
        String[] categorias, 
        short quantidade
    ) {
        this.setTitulo(titulo);
        this.setAnoPublicacao(anoPublicacao);
        this.setISBN(ISBN);
        this.setCategorias(categorias);
        this.setQuantidade(quantidade);
    }

    private Livro(
        short id,
        String titulo, 
        short anoPublicacao, 
        String ISBN, 
        String[] categorias, 
        short quantidade
    ) {
        this.setId(id);
        this.setTitulo(titulo);
        this.setAnoPublicacao(anoPublicacao);
        this.setISBN(ISBN);
        this.setCategorias(categorias);
        this.setQuantidade(quantidade);
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeShort(id);
        dos.writeUTF(titulo);
        dos.writeShort(anoPublicacao);
        dos.writeUTF(ISBN);
        dos.write(categorias.length);

        for (String categoria : categorias) {
            dos.writeUTF(categoria);
        }
        dos.writeShort(quantidade);
        
        return baos.toByteArray();
    }

    public static Livro fromBytes(byte[] dados) {
        ByteArrayInputStream bais = new ByteArrayInputStream(dados);
        DataInputStream dis = new DataInputStream(bais);

        try {
            short id = dis.readShort();
            String titulo = dis.readUTF();
            short anoPublicacao = dis.readShort();
            String isbn = dis.readUTF();
    
            int numCategorias = dis.read();
            String[] categorias = new String[numCategorias];
            for (short i = 0; i < numCategorias; ++i) {
                categorias[i] = dis.readUTF();
            }
    
            short quantidade = dis.readShort();

            return new Livro(
                id, 
                titulo, 
                anoPublicacao, 
                isbn, 
                categorias, 
                quantidade
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Livro formToInstance(HashMap<String, Object> formData) {
        @SuppressWarnings("unchecked")
        ArrayList<String> categorias = (ArrayList<String>) formData.get("categorias");
        
        return new Livro(
            (String) formData.get("titulo"),
            Short.parseShort((String) formData.get("anoPublicacao")),
            (String) formData.get("ISBN"),
            categorias.toArray(new String[0]),
            Short.parseShort((String) formData.get("quantidade"))
        );
    }

    public int getTamanhoEmBytes() {
        // tamanho inicial:
        // id + anoPublicacao + quantidade + numCategorias + ISBN
        int tamanho = 2 * 3 + 1 + 15;

        // +2 bytes para tamanho da string
        tamanho += titulo.getBytes(Registro.charset).length + 2;
        for (String categoria : this.categorias) {
            // +2 bytes para tamanho da string
            tamanho += categoria.getBytes(Registro.charset).length + 2;
        }

        return tamanho;
    }

    // getters e setters
    public short getId() {
        return this.id;
    }

    public void setId(short id) {
        if (id < 1) {
            throw new IllegalArgumentException("ID não pode ser menor que 1");
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

    public short getAnoPublicacao() {
        return this.anoPublicacao;
    }

    public void setAnoPublicacao(short anoPublicacao) {
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
            throw new IllegalArgumentException("Deve haver ao menos uma categoria");
        }
        
        this.categorias = categorias;
    }

    public int getQuantidade() {
        return this.quantidade;
    }

    public void setQuantidade(short quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }

        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return String.format(
            "\t%d |\t%s |\t%d |\t%s |\t%s |\t%d",
            this.getId(),
            this.getTitulo(),
            this.getAnoPublicacao(),
            this.getISBN(),
            String.join(", ", this.getCategorias()),
            this.getQuantidade()
        );
    }

    public String toJson() {
        StringBuilder json = new StringBuilder(
            String.format(
                "{\"id\":%d,\"titulo\":\"%s\",\"ano_publicacao\":%d,\"isbn\":\"%s\",\"quantidade\":%d,\"categorias\":",
                this.getId(),
                this.getTitulo(),
                this.getAnoPublicacao(),
                this.getISBN(),
                this.getQuantidade()
            )
        );

        json.append(RequestHelper.arrayToJson(this.getCategorias()));
        json.append('}');

        return json.toString();
    }
}