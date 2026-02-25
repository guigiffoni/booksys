import java.io.*;

public class LivroDAO {
    private String nomeArquivo = "livros.dat";
    private static int TAMANHO_CABECALHO = 12;

    private int ultimoId;
    private int numRegistros;

    LivroDAO() {
        this.inicializaArquivo();
    }

    private void inicializaArquivo() {
        File arquivo = new File(this.nomeArquivo);

        try {
            if (arquivo.exists()) {
                RandomAccessFile raf = new RandomAccessFile(arquivo, "r");

                raf.seek(0);
                this.ultimoId = raf.readInt();
                this.numRegistros = raf.readInt();

                raf.close();

            } else {
                RandomAccessFile raf = new RandomAccessFile(arquivo, "rw");
                this.numRegistros = 0;
                this.ultimoId = 0;

                raf.setLength(TAMANHO_CABECALHO);
                raf.seek(0);
                raf.writeInt(this.ultimoId);
                raf.writeInt(this.numRegistros);
                // define onde começam os dados
                raf.writeInt(TAMANHO_CABECALHO);

                raf.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void atualizaCabecalho(RandomAccessFile raf) throws IOException {
        raf.seek(0);
        raf.writeInt(this.ultimoId);
        raf.writeInt(this.numRegistros);
    }

    public void escreveBytes(Livro livro) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.nomeArquivo, "rw");
        byte[] dadosRegistro = livro.toBytes();

        this.atualizaCabecalho(raf);
        raf.seek(raf.length());
        raf.write(dadosRegistro);
    }

    // ----- CREATE
    public void inserir(Livro livro) {
        try {
            this.ultimoId += 1;
            this.numRegistros += 1;

            livro.setId(this.ultimoId);

            this.escreveBytes(livro);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----- READ
    public void listar() {
        try {
            RandomAccessFile raf = new RandomAccessFile(this.nomeArquivo, "r");
            raf.seek(TAMANHO_CABECALHO);

            Livro livro = new Livro();

            short tamRegistro;

            int numCategorias;
            String[] categorias;

            for (int i = 0; i < this.numRegistros; ++i) {
                while (raf.readChar() == '*') {
                    long posInicial = raf.getFilePointer();
                    tamRegistro = raf.readShort();
                    long posProxima = posInicial + tamRegistro;
    
                    raf.seek(posProxima);
                }

                tamRegistro = raf.readShort();

                livro.setId(raf.readInt());
                livro.setTitulo(raf.readUTF());
                livro.setAnoPublicacao(raf.readInt());
                livro.setISBN(raf.readUTF());

                numCategorias = raf.readInt();
                categorias = new String[numCategorias];
                for (int j = 0; j < numCategorias; ++j) {
                    categorias[j] = raf.readUTF();
                }
                livro.setCategorias(categorias);
                livro.setQuantidade(raf.readInt());

                System.out.println(livro.toString());
            }

            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*

    // BUSCA
    // busca livros pelo titulo
    public Livro buscarTitulo(String titulo){
        List<Livro> lista = listar();
        for(Livro l : lista){
            if(l.getTitulo().equalsIgnoreCase(titulo))
                return l;
    }
    
        return null;
    }

    // busca livros pelo ISBN
    public Livro buscarISBN(String ISBN){
        List<Livro> lista = listar();
        for(Livro l : lista){
            if(l.getISBN().equalsIgnoreCase(ISBN))
                return l;
        }

        return null;
    }

    // busca livro pela categoria 
    public Livro buscaLivroCategoria(String categoria){
        List<Livro> lista = listar();
        
    }
     

    // ----- UPDATE
    public void atualizar(Livro livroAtt){
        List<Livro> lista = listar();

        try {
            FileOutputStream fos = new FileOutputStream(nomeArquivo);
            ObjectOutputStream writer = new ObjectOutputStream(fos);

            for (Livro l : lista) {
                if (l.getId() == livroAtt.getId()) {
                    writer.writeObject(livroParaString(livroAtt));
                } else {
                    writer.writeObject(livroParaString(l));
                }

                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----- DELETE
    public void remover(String titulo){
        List<Livro> lista = listar();

        try {
            FileOutputStream fos = new FileOutputStream(nomeArquivo);
            ObjectOutputStream writer = new ObjectOutputStream(fos);

            for (Livro l : lista) {
                if (!l.getTitulo().equalsIgnoreCase(titulo)){
                    writer.writeObject(livroParaString(l));
                }
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    // converte os dados de um livro em uma string para ser armazenado no arquivo.
    // separa os dados pelo tamanho de cada string
    private String livroParaString(Livro livro){
        // junta as categorias numa única string e as separa por vírgula
        String categorias = String.join(",", livro.getCategorias());
        String dados = "";
        dados += tamMetadado(String.valueOf(livro.getId()));
        dados += tamMetadado(livro.getTitulo());
        dados += tamMetadado(String.valueOf(livro.getAnoPublicacao()));
        dados += tamMetadado(livro.getISBN());
        dados += tamMetadado(categorias);
        dados += tamMetadado(String.valueOf(livro.getQuantidade()));

        // tamanho total da string do livro
        int tamTotal = dados.length();

        String tamTotalFormatado = String.format("%04d", tamTotal);

        return tamTotalFormatado + dados;
    }

    // converte uma string para um objeto livro novamente
    /*
    private Livro stringParaLivro(String registro){
        int[] indice = { 4 };

        String idStr = separaDado(registro, indice);
        String titulo = separaDado(registro, indice);
        String anoStr = separaDado(registro, indice);
        String isbn = separaDado(registro, indice);
        String categoriasStr = separaDado(registro, indice);
        String quantidadeStr = separaDado(registro, indice);

        Livro livro = new Livro();

        livro.setId(Integer.parseInt(idStr));
        livro.setTitulo(titulo);
        livro.setAnoPublicacao(Integer.parseInt(anoStr));
        livro.setISBN(isbn);
        livro.setCategorias(categoriasStr.split(","));
        livro.setQuantidade(Integer.parseInt(quantidadeStr));

        return livro;
    }
    */

    // descobrir o tamanho da string para fazer a separação dos dados
    private String tamMetadado(String valor){
        int tam = valor.length();
        // define o tamanho do metadado como sempre com 4 casas e preenche com 0 à esquerda
        String tamFormatado = String.format("%04d", tam);

        return tamFormatado + valor;
    }

    // lê os campos da string separadamente, distinguindo metadado de dado
    private String separaDado(String dados, int[] indice){
        // recorta a string e separa os 4 primeiros dígitos, que indicam o tamanho da string
        String tamString = dados.substring(indice[0], indice[0] + 4);
        int tam = Integer.parseInt(tamString);

        indice[0] += 4;
        // lê o tamanho do campo informado pelo metadado
        String valor = dados.substring(indice[0], indice[0] + tam);
        // avança o ponteiro para o proximo campo
        indice[0] += tam;

        return valor;
    }
}