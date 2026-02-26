import java.io.*;

public class LivroDAO {
    private String nomeArquivo = "livros.dat";
    
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

        if (arquivo.exists()) {
            RandomAccessFile raf = new RandomAccessFile(arquivo, "r");

            raf.seek(0);
            this.ultimoId = raf.readShort();
            this.numRegistros = raf.readShort();
            this.numRegistrosDeletados = raf.readShort();
            this.bytePrimeiraLapide = raf.readShort();

            raf.close();
        } else {
            RandomAccessFile raf = new RandomAccessFile(arquivo, "rw");
            this.numRegistros = 0;
            this.ultimoId = 0;
            this.numRegistrosDeletados = 0;
            this.bytePrimeiraLapide = 0;

            raf.setLength(TAMANHO_CABECALHO);
            raf.seek(0);
            raf.writeShort(this.ultimoId);
            raf.writeShort(this.numRegistros);
            raf.writeShort(this.numRegistrosDeletados);
            raf.writeLong(this.bytePrimeiraLapide);
            // define onde começam os dados
            raf.writeShort(TAMANHO_CABECALHO);

            raf.close();
        }
    }

    public void inserir(Livro livro) throws IOException {
        this.ultimoId += 1;
        this.numRegistros += 1;

        livro.setId(this.ultimoId);

        long secao = this.buscaSecaoLivre(livro.getTamanhoEmBytes());

        this.escreveBytes(livro, secao);
    }

    // refatorar, DAO não deve conter printagem, retornar lista de livros
    public void listar() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.nomeArquivo, "r");
        raf.seek(TAMANHO_CABECALHO);

        Livro livro;
        short tamRegistro;

        for (int i = 0; i < this.numRegistros; ++i) {
            this.seekUntil(raf, '*');
            System.out.println(raf.getFilePointer());

            tamRegistro = raf.readShort();
            byte[] buffer = new byte[tamRegistro];
            raf.readFully(buffer);
            livro = Livro.fromBytes(buffer);

            System.out.println(livro.toString());
        }

        raf.close();
    }

    public void atualizar(Livro livro) throws IOException {
        int tamRegistro = livro.getTamanhoEmBytes();
        long secaoLivre = this.buscaSecaoLivre(tamRegistro);
        
        this.escreveBytes(livro, secaoLivre);
    }

    public void deletar(short id) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.nomeArquivo, "rw");
        long ponteiroLapide;

        raf.seek(TAMANHO_CABECALHO);
        if (this.encontraLivro(raf, id)) {
            ponteiroLapide = raf.getFilePointer() - 4;

            raf.seek(ponteiroLapide);
            raf.writeChar('*');

            this.numRegistros -= 1;
            this.numRegistrosDeletados += 1;

            if (this.bytePrimeiraLapide > ponteiroLapide) {
                this.bytePrimeiraLapide = ponteiroLapide;
            }

            this.atualizaCabecalho(raf);
        } else {
            System.out.println("Não foi possível encontrar o livro");
        }

        raf.close();
    }

    public boolean encontraLivro(RandomAccessFile raf, short id) throws IOException {
        long tamArquivo = raf.length();
        long inicioRegistro;

        short tamRegistro;
        short idRegistro;

        while (raf.getFilePointer() < tamArquivo) {
            this.seekUntil(raf, ' ');

            tamRegistro = raf.readShort();
            inicioRegistro = raf.getFilePointer();
            idRegistro = raf.readShort();

            if (idRegistro == id) {
                raf.seek(inicioRegistro);
                return true;
            }
            
            raf.seek(inicioRegistro + tamRegistro);
        }

        return false;
    }

    private void atualizaCabecalho(RandomAccessFile raf) throws IOException {
        raf.seek(0);

        raf.writeShort(this.ultimoId);
        raf.writeShort(this.numRegistros);
        raf.writeShort(this.numRegistrosDeletados);
        raf.writeLong(this.bytePrimeiraLapide);
    }

    private void seekUntil(RandomAccessFile raf, char caractere) throws IOException {
        long tamRegistro = 0;

        while (raf.readChar() == caractere) {
            long posInicial = raf.getFilePointer();
            tamRegistro = (long) raf.readShort();
            long posProxima = posInicial + tamRegistro;
    
            raf.seek(posProxima);
        }
    }

    private long buscaSecaoLivre(int tamanhoSecao) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.nomeArquivo, "r");
        long byteSecaoLivre = TAMANHO_CABECALHO;
        short tamRegistroDeletado;

        raf.seek(byteSecaoLivre);

        if (this.bytePrimeiraLapide > TAMANHO_CABECALHO) {
            raf.seek(bytePrimeiraLapide);
        }

        for (int i = 0; i < this.numRegistrosDeletados; ++i) {
            tamRegistroDeletado = raf.readShort();

            if (tamRegistroDeletado >= tamanhoSecao) {
                return raf.getFilePointer();
            }

            seekUntil(raf, '*');
        }

        byteSecaoLivre = raf.getFilePointer();

        raf.close();
        return byteSecaoLivre;
    }

    public void escreveBytes(Livro livro, long byteAddress) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.nomeArquivo, "rw");
        this.atualizaCabecalho(raf);

        System.out.println(byteAddress);
        System.out.println(raf.length());

        if (byteAddress != raf.length()) {
            this.numRegistrosDeletados -= 1;
            this.bytePrimeiraLapide = 0;
        }

        raf.seek(byteAddress);
        raf.write(livro.toBytes());
        raf.close();
    }
}