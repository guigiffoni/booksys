import java.io.IOException;
import java.io.RandomAccessFile;

public interface DaoInterface<T extends DaoInterface<T>> {
    String nomeArquivo = "";
    RandomAccessFile raf = null;

    short ultimoId = 0;
    short numRegistros = 0;
    short numRegistrosDeletados = 0;
    long bytePrimeiraLapide = 0;
    short TAMANHO_CABECALHO = 16;
    
    void inicializaArquivo() throws IOException;
    void inserir(T instancia) throws IOException;
    void listar() throws IOException;
    void atualizar(T instancia) throws IOException;
    void deletar() throws IOException;
}
