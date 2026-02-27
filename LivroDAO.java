import java.io.*;

public class LivroDAO {
    private String nomeArquivo = "livros.dat";
    private RandomAccessFile raf;

    private short ultimoId;
    private short numRegistros;
    private short numRegistrosDeletados;
    private long bytePrimeiraLapide;
    private static short TAMANHO_CABECALHO = 16;

    LivroDAO() throws IOException {
        this.inicializaArquivo();
    }

    private void inicializaArquivo() throws IOException {
        File arquivo = new File(this.nomeArquivo);
        boolean arquivoPresente = arquivo.exists();

        this.raf = new RandomAccessFile(this.nomeArquivo, "rw");

        if (arquivoPresente) {
            raf.seek(0);
            this.ultimoId = raf.readShort();
            this.numRegistros = raf.readShort();
            this.numRegistrosDeletados = raf.readShort();
            this.bytePrimeiraLapide = raf.readShort();
        } else {
            this.numRegistros = 0;
            this.ultimoId = 0;
            this.numRegistrosDeletados = 0;
            this.bytePrimeiraLapide = -1;

            this.raf.setLength(TAMANHO_CABECALHO);
            this.raf.seek(0);
            this.raf.writeShort(this.ultimoId);
            this.raf.writeShort(this.numRegistros);
            this.raf.writeShort(this.numRegistrosDeletados);
            this.raf.writeLong(this.bytePrimeiraLapide);
            // define onde começam os dados
            this.raf.writeShort(TAMANHO_CABECALHO);
        }
    }

    public void inserir(Livro livro) throws IOException {
        long secao = this.buscaSecaoLivre(livro.getTamanhoEmBytes());
        this.ultimoId += 1;
        this.numRegistros += 1;

        livro.setId(this.ultimoId);
        
        this.raf.seek(secao);

        if (secao != this.raf.length()) {
            this.setLapide(true);
        }

        this.escreveBytes(livro);
    }

    // refatorar, DAO não deve conter printagem, retornar lista de livros
    public void listar() throws IOException {
        this.raf.seek(TAMANHO_CABECALHO);

        Livro livro;
        short tamRegistro;

        for (int i = 0; i < this.numRegistros; ++i) {
            this.avancaEnquanto('*');

            tamRegistro = this.raf.readShort();
            // subtraindo para desconsiderar o cabeçalho
            byte[] buffer = new byte[tamRegistro];
            this.raf.readFully(buffer);
            livro = Livro.fromBytes(buffer);

            System.out.println(livro.toString());
        }

    }

    public void atualizar(short id, Livro livro) throws IOException {
        if (!this.encontraLivro(id)) {
            // implementar exceção, id não encontrado
            System.out.println("ID não encontrado");
            return;
        }

        short tamRegistroOriginal = this.raf.readShort();
        int tamRegistroNovo = livro.getTamanhoEmBytes();
        long enderecoEscrita = this.raf.getFilePointer();

        if (tamRegistroNovo > tamRegistroOriginal) {
            this.setLapide(true);

            enderecoEscrita = this.buscaSecaoLivre(tamRegistroNovo);
        }

        this.raf.seek(enderecoEscrita);
        this.escreveBytes(livro);
    }

    public void deletar(short id) throws IOException {
        if (this.encontraLivro(id)) {
            this.setLapide(true);

            this.numRegistros -= 1;

            this.atualizaCabecalho();
        } else {
            System.out.println("Não foi possível encontrar o livro");
        }
    }

    private boolean encontraLivro(short id) throws IOException {
        long tamArquivo = this.raf.length();
        long inicioRegistro;

        short tamRegistro;
        short idRegistro;

        this.raf.seek(TAMANHO_CABECALHO);

        while (this.raf.getFilePointer() < tamArquivo) {
            this.avancaEnquanto('*');

            tamRegistro = this.raf.readShort();
            inicioRegistro = this.raf.getFilePointer();
            idRegistro = this.raf.readShort();

            if (idRegistro == id) {
                this.raf.seek(inicioRegistro);
                return true;
            }

            this.raf.seek(inicioRegistro + tamRegistro);
        }

        return false;
    }

    private void atualizaCabecalho() throws IOException {
        long enderecoOriginal = this.raf.getFilePointer();

        this.raf.seek(0);

        this.raf.writeShort(this.ultimoId);
        this.raf.writeShort(this.numRegistros);
        this.raf.writeShort(this.numRegistrosDeletados);
        this.raf.writeLong(this.bytePrimeiraLapide);

        this.raf.seek(enderecoOriginal);
    }

    private void avancaEnquanto(char lapide) throws IOException {
        long tamRegistro = 0;

        while (this.raf.readChar() == lapide) {
            long posInicial = this.raf.getFilePointer();
            tamRegistro = (long) this.raf.readShort();
            long posProxima = posInicial + tamRegistro;

            this.raf.seek(posProxima);
        }
    }

    private long buscaSecaoLivre(int tamanhoSecao) throws IOException {
        short tamRegistroDeletado;

        this.raf.seek(TAMANHO_CABECALHO);

        if (this.bytePrimeiraLapide > TAMANHO_CABECALHO) {
            this.raf.seek(this.bytePrimeiraLapide);
        }

        for (int i = 0; i < this.numRegistrosDeletados; ++i) {
            tamRegistroDeletado = this.raf.readShort();

            if (tamRegistroDeletado >= tamanhoSecao) {
                return this.raf.getFilePointer();
            }

            this.avancaEnquanto(' ');
        }

        return this.raf.length();
    }

    private void escreveBytes(Livro livro) throws IOException {
        this.raf.write(livro.toBytes());
    }

    private void setLapide(boolean exclusao) throws IOException {
        long enderecoLapide = this.raf.getFilePointer() - 4;
        char lapide = exclusao ? '*' : ' ';

        this.raf.seek(enderecoLapide);
        this.raf.writeChar(lapide);

        if (exclusao) {
            this.numRegistrosDeletados += 1;
            
            if (this.bytePrimeiraLapide > enderecoLapide) {
                this.bytePrimeiraLapide = enderecoLapide;
            }
        } else {
            this.numRegistrosDeletados -= 1;

            if (this.bytePrimeiraLapide == enderecoLapide) {
                this.bytePrimeiraLapide = TAMANHO_CABECALHO;
            }
        }

        this.raf.seek(enderecoLapide + 4);
    }
}