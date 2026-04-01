package src.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import src.util.Registro;

public class Usuario implements Registro {
    private short id;
    private short diasBloqueado;
    private byte nivelPermissao;
    private String nome;
    private String dataNascimento;
    private String email;
    private String senha;
    private String[] telefones;

    public Usuario() {

    }

    public Usuario(
        byte nivelPermissao,
        String nome, 
        String dataNascimento,
        String email, 
        String senha, 
        String[] telefones) {
            this.setNome(nome);
            this.setDataNascimento(dataNascimento);
            this.setEmail(email);
            this.setSenha(senha);
            this.setTelefones(telefones);
    }

    private Usuario(
        short id,
        short diasBloqueado,
        byte nivelPermissao,
        String nome, 
        String dataNascimento,
        String email, 
        String senha, 
        String[] telefones) {
            this.setId(id);
            this.setNome(nome);
            this.setDataNascimento(dataNascimento);
            this.setEmail(email);
            this.setSenha(senha);
            this.setTelefones(telefones);
    }

    @Override
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeShort(this.id);
        dos.writeShort(this.diasBloqueado);
        dos.writeByte(this.nivelPermissao);
        dos.writeUTF(this.nome);
        dos.writeUTF(this.dataNascimento);
        dos.writeUTF(this.email);
        dos.writeUTF(this.senha);
        
        dos.writeByte(telefones.length);
        for (String telefone : this.telefones) {
            dos.writeUTF(telefone);
        }

        return baos.toByteArray();
    }

    public static Usuario fromBytes(byte[] dados) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(dados);
        DataInputStream dis = new DataInputStream(bais);

        short id = dis.readShort();
        short diasBloqueado = dis.readShort();
        byte nivelPermissao = dis.readByte();
        String nome = dis.readUTF();
        String dataNascimento = dis.readUTF();
        String email = dis.readUTF();
        String senha = dis.readUTF();

        int numTelefones = dis.readShort();
        String[] telefones = new String[numTelefones];
        for (int i = 0; i < numTelefones; i++) {
            telefones[i] = dis.readUTF();
        }

        return new Usuario(
            id,
            diasBloqueado,
            nivelPermissao,
            nome,
            dataNascimento,
            email,
            senha,
            telefones
        );
    }

    @Override
    public int getTamanhoEmBytes() {
        // tamanho inicial: 
        // id + diasBloqueado + nivelPermissao + dataNascimento + numTelefones
        int tamanho = 2 * 2 + 1 + 8 + 1;

        tamanho += this.nome.getBytes(Registro.charset).length + 2;
        tamanho += this.email.getBytes(Registro.charset).length + 2;
        tamanho += this.senha.getBytes(Registro.charset).length + 2;

        for (String telefone : this.telefones) {
            // +2 bytes para tamanho da string
            tamanho += telefone.getBytes(Registro.charset).length + 2;
        }

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
    
    public short getDiasBloqueado() {
        return this.diasBloqueado;
    }

    public void set(short diasBloqueado) {
        if (diasBloqueado < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo!");
        }

        this.diasBloqueado = diasBloqueado;
    }

    public byte getNivelPermissao() {
        return this.nivelPermissao;
    }

    public void set(byte nivelPermissao) {
        if (nivelPermissao < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo!");
        }

        this.nivelPermissao = nivelPermissao;
    }

    public String getNome() {
        return this.nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio!");
        }

        this.nome = nome;
    }

    public String getDataNascimento() {
        return this.dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        if (dataNascimento.length() != 8) {
            throw new IllegalArgumentException("Data de nascimento deve seguir o formato YYYYMMDD");
        }

        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return this.senha;
    }

    public void setSenha(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Uma senha deve ser provida!");
        }

        this.senha = senha;
    }

    public String[] getTelefones() {
        return this.telefones;
    }

    public void setTelefones(String[] telefones) {
        this.telefones = telefones;
    }
}
