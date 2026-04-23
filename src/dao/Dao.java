package src.dao;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import src.util.Registro;

public class Dao<T extends Registro> {
    private static final String path = "data/";
    private String nomeArquivo;
    private RandomAccessFile raf;

    public short ultimoId;
    public short numRegistros;
    public short numRegistrosDeletados;
    public long bytePrimeiraLapide;
    public static final short TAMANHO_CABECALHO = 16;

    private Function<byte[], T> fromBytes;
    private Function<HashMap<String, Object>, T> formToInstance;

    public Dao(
        String nomeArquivo, 
        Function<byte[], T> fromBytes,
        Function<HashMap<String, Object>, T> formToInstance
    ) throws IOException {
        this.nomeArquivo = nomeArquivo;
        this.fromBytes = fromBytes;
        this.formToInstance = formToInstance;
        inicializaArquivo();
    }

    private void inicializaArquivo() throws IOException {
        File arquivo = new File(Dao.path + this.nomeArquivo);
        boolean arquivoPresente = arquivo.exists();

        this.raf = new RandomAccessFile(Dao.path + this.nomeArquivo, "rw");
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

    private void atualizaCabecalho() throws IOException {
        long enderecoOriginal = this.raf.getFilePointer();

        this.raf.seek(0);

        this.raf.writeShort(this.ultimoId);
        this.raf.writeShort(this.numRegistros);
        this.raf.writeShort(this.numRegistrosDeletados);
        this.raf.writeLong(this.bytePrimeiraLapide);

        this.raf.seek(enderecoOriginal);
    }

    private void escreveDados(T instancia) throws IOException {
        int tamanhoRegistro = instancia.getTamanhoEmBytes();

        this.raf.writeChar(' ');

        // Caso não seja uma escrita de final de arquivo, ou seja, se
        // estivermos sobrescrevendo um registro, manter a informação do
        // tamanho original para futuras navegações
        if (this.raf.getFilePointer() != this.raf.length()) {
            long endereco = this.raf.getFilePointer();

            tamanhoRegistro = this.raf.readShort();

            this.raf.seek(endereco);
        }

        this.raf.writeShort(tamanhoRegistro);
        this.raf.write(instancia.toBytes());
    }

    private void remocaoLogica() throws IOException {
        long enderecoPonteiro = this.raf.getFilePointer();

        this.numRegistrosDeletados += 1;

        if (this.bytePrimeiraLapide > enderecoPonteiro) {
            this.bytePrimeiraLapide = enderecoPonteiro;
        }
        
        this.raf.writeChar('*');
    }

    public boolean seekRegistro(short id) {
        try {
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
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return false;
        }
    }

    public T encontraRegistro(short id) throws IOException {
        if (this.seekRegistro(id)) {
            return this.leRegistroAtual();
        }

        return null;
    }

    public T leRegistroAtual() throws IOException {
        // pula a lápide
        this.raf.seek(this.raf.getFilePointer() + 2);

        short tamRegistro = this.raf.readShort();
        byte[] buffer = new byte[tamRegistro];

        this.raf.readFully(buffer);

        return fromBytes.apply(buffer);
    }

    public T formToInstance(HashMap<String, Object> formData) {
        return this.formToInstance.apply(formData);
    }

    public void inserir(T instancia) throws IOException {
        this.seekSecaoLivre(instancia.getTamanhoEmBytes());

        this.ultimoId += 1;
        this.numRegistros += 1;

        instancia.setId(this.ultimoId);
        this.atualizaCabecalho();
        this.escreveDados(instancia);
    }

    public ArrayList<T> listar() throws IOException {
        this.raf.seek(TAMANHO_CABECALHO);

        ArrayList<T> listaInstancia = new ArrayList<T>(this.numRegistros);

        for (int i = 0; i < this.numRegistros; ++i) {
            this.avancaEnquanto('*');

            listaInstancia.add(this.leRegistroAtual());
        }

        return listaInstancia;
    }

    public void atualizar(T instancia) throws IOException {
        int tamRegistroNovo = instancia.getTamanhoEmBytes();
        long inicioRegistro = this.raf.getFilePointer();

        this.raf.seek(inicioRegistro + 2);
        short tamRegistroAntigo = this.raf.readShort();
        short idOriginal = this.raf.readShort();

        instancia.setId(idOriginal);

        this.raf.seek(inicioRegistro);

        if (tamRegistroNovo > tamRegistroAntigo) {
            this.remocaoLogica();
            this.atualizaCabecalho();
            this.seekSecaoLivre(tamRegistroNovo);
        }

        this.escreveDados(instancia);
    }

    public void deletar() throws IOException {
        this.numRegistros -= 1;

        remocaoLogica();

        this.atualizaCabecalho();
    }
}
