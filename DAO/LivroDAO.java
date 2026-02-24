package DAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Livro;

public class LivroDAO {
    private String arq = "livros.txt";

    // ----- CREATE 
    public void salvar(Livro livro) {
        try {
            FileWriter fw = new FileWriter(arq, true);
            BufferedWriter writer = new BufferedWriter(fw);
            writer.write(livroParaString(livro));
            writer.newLine();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // ----- READ
    public List<Livro> listar(){
        List<Livro> lista = new ArrayList<>();
        
        try {
            FileReader fr = new FileReader(arq);
            BufferedReader reader = new BufferedReader(fr);
            
            String linha;
            
            while ((linha = reader.readLine()) != null) {
                lista.add(stringParaLivro(linha));
            }

            reader.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return lista;
    }

    /*// busca livros pelo ID
    public Livro buscarId(int id){
        List<Livro> lista = listar();
        for(Livro l : lista){
            if(l.getId() == id)
                return l;
        }

        return null;
    }*/

    // ----- UPDATE
    public void atualizar(Livro livroAtt) {
        List<Livro> lista = listar();

        try{
            FileWriter fr = new FileWriter(arq);
            BufferedWriter writer = new BufferedWriter(fr);

            for(Livro l : lista) {
                if(l.getId() == livroAtt.getId()) {
                    writer.write(livroParaString(livroAtt));
                } else {
                    writer.write(livroParaString(l));
                }

                writer.newLine();
                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----- DELETE 
    public void remover(int id) {
        List<Livro> lista = listar();

        try {
            FileWriter fw = new FileWriter(arq);
            BufferedWriter writer = new BufferedWriter(fw);
        
            for (Livro l : lista) {
                if (l.getId() != id) {
                    writer.write(livroParaString(l));
                    writer.newLine();
                }
            }

            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // converte os dados de um livro em uma string para ser armazenado no arquivo. separa os dados pelo tamanho de cada string
    private String livroParaString(Livro livro) {
        String categorias = String.join(",", livro.getCategorias());        // junta as categorias numa única string e as separa por vírgula
        String dados = "";
        dados += tamMetadado(String.valueOf(livro.getId()));
        dados += tamMetadado(livro.getTitulo());
        dados += tamMetadado(String.valueOf(livro.getAnoPublicacao()));
        dados += tamMetadado(livro.getISBN());
        dados += tamMetadado(categorias);
        dados += tamMetadado(String.valueOf(livro.getQuantidade()));

        int tamTotal = dados.length();  // tamanho total da string do livro 

        String tamTotalFormatado = String.format("%04d", tamTotal); 

        return tamTotalFormatado + dados;
    }

    // converte uma string para um objeto livro novamente
    private Livro stringParaLivro(String registro){
        int[] indice = {4}; 

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

    // descobrir o tamanho da string para fazer a separação dos dados
    private String tamMetadado (String valor) {
        int tam = valor.length();
        String tamFormatado = String.format("%04d", tam);       // define o tamanho do metadado como sempre com 4 casas e preenche com 0 à esquerda
        
        return tamFormatado + valor; 
    }

    // lê os campos da string separadamente, distinguindo metadado de dado
    private String separaDado (String dados, int[] indice) {
        String tamString = dados.substring(indice[0], indice[0] + 4);      // recorta a string e separa os 4 primeiros dígitos, que indicam o tamanho da string
        int tam = Integer.parseInt(tamString);

        indice[0] += 4;

        String valor = dados.substring(indice[0], indice[0] + tam);     // lê o tamanho do campo informado pelo metadado

        indice[0] += tam;       // avança o ponteiro para o proximo campo

        return valor;
    }
}