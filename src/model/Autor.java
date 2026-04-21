package src.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import src.util.Registro;

public class Autor implements Registro {
    private short id;
    private String nome;
    // formato: YYYYMMDD
    private String dataNascimento;
    private String nacionalidade;

    public Autor() {

    }

    public Autor(String nome, String dataNascimento, String nacionalidade) {
        this.setNome(nome);
        this.setDataNascimento(dataNascimento);
        this.setNacionalidade(nacionalidade);
    }

    public Autor(
            short id,
            String nome,
            String dataNascimento,
            String nacionalidade) {
        this.setId(id);
        this.setNome(nome);
        this.setDataNascimento(dataNascimento);
        this.setNacionalidade(nacionalidade);
    }

    @Override
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeShort(this.id);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.dataNascimento);
        dos.writeUTF(this.nacionalidade);

        return baos.toByteArray();
    }

    public static Autor fromBytes(byte[] dados) {
        ByteArrayInputStream bais = new ByteArrayInputStream(dados);
        DataInputStream dis = new DataInputStream(bais);

        try {
            short id = dis.readShort();
            String nome = dis.readUTF();
            String dataNascimento = dis.readUTF();
            String nacionalidade = dis.readUTF();
    
            return new Autor(id, nome, dataNascimento, nacionalidade);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getTamanhoEmBytes() {
        // tamanho inicial: 2 bytes para ID
        int tamanho = 2;

        tamanho += this.nome.getBytes(Registro.charset).length + 2;
        tamanho += this.dataNascimento.getBytes(Registro.charset).length + 2;
        tamanho += this.nacionalidade.getBytes(Registro.charset).length + 2;

        return tamanho;
    }

    @Override
    public short getId() {
        return this.id;
    }

    @Override
    public void setId(short id) {
        if (id < 1) {
            throw new IllegalArgumentException("Valor não pode ser menor que 1");
        }

        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        if (nome == null) {
            throw new IllegalArgumentException("Nome não pode ser nulo");
        }

        this.nome = nome;
    }

    public String getDataNascimento() {
        return this.dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        if (dataNascimento.length() != 8) {
            throw new IllegalArgumentException("Data de empréstimo deve seguir o formato YYYYMMDD");
        }

        this.dataNascimento = dataNascimento;
    }

    public String getNacionalidade() {
        return this.nacionalidade;
    }

    public void setNacionalidade(String nacionalidade) {
        if (nacionalidade == null) {
            throw new IllegalArgumentException("Nacionalidade não pode ser nula");
        }

        this.nacionalidade = nacionalidade;
    }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"nome\":\"%s\",\"dataNascimento\":\"%s\",\"nacionalidade\":\"%s\"}"
        );
    }
}
