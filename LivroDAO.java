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
        this.raf.seek(0);

        if (arquivoPresente) {
            this.ultimoId = raf.readShort();
            this.numRegistros = raf.readShort();
            this.numRegistrosDeletados = raf.readShort();
            this.bytePrimeiraLapide = raf.readLong();
        } else {
            this.numRegistros = 0;
            this.ultimoId = 0;
            this.numRegistrosDeletados = 0;
            this.bytePrimeiraLapide = Long.MAX_VALUE;

            this.raf.setLength(TAMANHO_CABECALHO);
            this.raf.writeShort(this.ultimoId);
            this.raf.writeShort(this.numRegistros);
            this.raf.writeShort(this.numRegistrosDeletados);
            this.raf.writeLong(this.bytePrimeiraLapide);
            // define onde começam os dados
            this.raf.writeShort(TAMANHO_CABECALHO);
        }
    }

    public void inserir(Livro livro) throws IOException {
        this.seekSecaoLivre(livro.getTamanhoEmBytes());

        this.ultimoId += 1;
        this.numRegistros += 1;

        livro.setId(this.ultimoId);
        this.atualizaCabecalho();
        this.escreveDadosLivro(livro);
    }

    // refatorar, DAO não deve conter printagem, retornar lista de livros
    public void listar() throws IOException {
        this.raf.seek(TAMANHO_CABECALHO);

        Livro livro;
        short tamRegistro;

        for (int i = 0; i < this.numRegistros; ++i) {
            this.avancaEnquanto('*');
            // +2 para desconsiderar a lápide
            this.raf.seek(this.raf.getFilePointer() + 2);

            tamRegistro = this.raf.readShort();
            byte[] buffer = new byte[tamRegistro];
            this.raf.readFully(buffer);
            livro = Livro.fromBytes(buffer);

            System.out.println(livro.toString());
        }
    }

    public void atualizar(Livro livro) throws IOException {
        int tamRegistroNovo = livro.getTamanhoEmBytes();
        long inicioRegistro = this.raf.getFilePointer();

        this.raf.seek(inicioRegistro + 2);
        short tamRegistroAntigo = this.raf.readShort();
        short idOriginal = this.raf.readShort();

        livro.setId(idOriginal);

        this.raf.seek(inicioRegistro);

        if (tamRegistroNovo > tamRegistroAntigo) {
            this.remocaoLogica();
            this.atualizaCabecalho();
            this.seekSecaoLivre(tamRegistroNovo);
        }

        this.escreveDadosLivro(livro);
    }

    public void deletar() throws IOException {
        this.numRegistros -= 1;

        remocaoLogica();

        this.atualizaCabecalho();
    }

    private void remocaoLogica() throws IOException {
        long enderecoPonteiro = this.raf.getFilePointer();

        this.numRegistrosDeletados += 1;

        if (this.bytePrimeiraLapide > enderecoPonteiro) {
            this.bytePrimeiraLapide = enderecoPonteiro;
        }
        
        this.raf.writeChar('*');
    }

    public boolean encontraLivro(short id) throws IOException {
        this.raf.seek(TAMANHO_CABECALHO);

        long tamArquivo = this.raf.length();
        long inicioRegistro = this.raf.getFilePointer();

        short tamRegistro;
        short idRegistro;

        while (true) {
            this.avancaEnquanto('*');

            inicioRegistro = this.raf.getFilePointer();

            if (inicioRegistro == tamArquivo) {
                break;
            }
            
            this.raf.readChar();
            tamRegistro = this.raf.readShort();
            idRegistro = this.raf.readShort();

            if (idRegistro == id) {
                this.raf.seek(inicioRegistro);
                return true;
            }

            this.raf.seek(inicioRegistro + tamRegistro + 4);
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
        long posInicio = this.raf.getFilePointer();
        long posProxima;
        long posFinalArquivo = this.raf.length();

        while (posInicio < posFinalArquivo) {
            posInicio = this.raf.getFilePointer();

            if (this.raf.readChar() != lapide) {
                this.raf.seek(posInicio);
                break;
            }

            tamRegistro = (long) this.raf.readShort();
            posProxima = posInicio + tamRegistro + 4;
            this.raf.seek(posProxima);
        }
    }

    private void seekSecaoLivre(int tamanhoSecao) throws IOException {
        short tamRegistroDeletado;
        long enderecoRegistro;

        this.raf.seek(TAMANHO_CABECALHO);

        if (this.bytePrimeiraLapide < Long.MAX_VALUE) {
            this.raf.seek(this.bytePrimeiraLapide);
        }

        // encontra seção disponível em registros deletados
        for (int i = 0; i < this.numRegistrosDeletados; ++i) {
            enderecoRegistro = this.raf.getFilePointer();
            this.raf.readChar();
            tamRegistroDeletado = this.raf.readShort();

            if (tamRegistroDeletado >= tamanhoSecao) {
                this.raf.seek(enderecoRegistro);
                return;
            }

            this.avancaEnquanto(' ');
        }

        // seek para o final do arquivo
        this.raf.seek(this.raf.length());
    }

    private void escreveDadosLivro(Livro livro) throws IOException {
        int tamanhoRegistro = livro.getTamanhoEmBytes();

        this.raf.writeChar(' ');

        // Caso não seja uma escrita de final de arquivo, ou seja, se
        // estivermos sobrescrevendo um registro, manter o a informação do
        // tamanho original para futuras navegações
        if (this.raf.getFilePointer() != this.raf.length()) {
            long endereco = this.raf.getFilePointer();

            tamanhoRegistro = this.raf.readShort();

            this.raf.seek(endereco);
        }

        this.raf.writeShort(tamanhoRegistro);
        this.raf.write(livro.toBytes());
    }
}