package src.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import src.dao.Dao;
import src.util.Arvore;
import src.util.Pagina;
import src.util.Registro;
import src.util.RequestHelper;

public class Controller<T extends Registro> {
    protected String nomeArquivo;
    protected Dao<T> dao;

    public Controller(Dao<T> dao) {
        this.dao = dao;
    }

    public Controller(
        String nomeArquivo,
        Function<byte[], T> fromBytes,
        Function<HashMap<String, Object>, T> formToInstance
    ) {
        try {
            this.dao = new Dao<T>(nomeArquivo, fromBytes, formToInstance);
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }

    public void enviaResposta(HttpExchange exchange, int status, String resposta) throws IOException {
        System.out.println(resposta);
        byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);

        exchange.close();
    }

    public void enviaRespostaErro(HttpExchange exchange, String resposta) throws IOException {
        this.enviaResposta(exchange, 500, resposta);
    }

    public String listaRegistros() throws IOException {
        ArrayList<T> registros = this.dao.listar();

        Arvore<T> arvore = new Arvore<>();
        for (T registro : registros) {
            arvore.inserir(registro);
        }

        if (registros.size() == 0) {
            return "[]";
        }

        StringBuilder str = new StringBuilder();

        str.append('[');
        Pagina<T> paginaRef = arvore.getPrimeiraFolha();
        System.out.println(paginaRef);

        while (paginaRef != null) {
            for (int i = 0; i < paginaRef.getNumChaves(); i++) {
                str.append(paginaRef.chaves[i].refInstancia.toJson());
                str.append(',');
            }

            paginaRef = paginaRef.proximo;
        }

        str.setCharAt(str.length() - 1, ']');

        return str.toString();
    }

    public boolean deletaRegistro(short id) throws IOException {
        if (this.dao.seekRegistro(id)) {
            this.dao.deletar();

            return true;
        }

        return false;
    }

    public boolean atualizaRegistro(short id, T instancia) throws IOException {
        if (this.dao.seekRegistro(id)) {
            this.dao.atualizar(instancia);

            return true;
        }

        return false;
    }

    public T parseFormData(byte[] bytesQueryString) {
        String queryString = new String(bytesQueryString);
        HashMap<String, Object> dadosForm = RequestHelper.parseQueryString(queryString);
        
        return this.dao.formToInstance(dadosForm);
    }

    public HttpHandler orchestrator() {
        return exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] segments = path.split("\\/");
            byte[] bytesBody = exchange.getRequestBody().readAllBytes();

            System.out.println(method + " -> " + path);

            switch (method) {
                case "POST":
                    this.dao.inserir(this.parseFormData(bytesBody));

                    this.enviaResposta(
                        exchange, 
                        200, 
                        "{\"mensagem\":\"ok!\"}"
                    );
                    break;
                case "GET":
                    if (segments.length == 3) {
                        if (segments[2].matches("\\d+")) {
                            short id = Short.parseShort(segments[2]);
                            T registro = this.dao.encontraRegistro(id);

                            this.enviaResposta(
                                exchange, 
                                200, 
                                registro.toJson()
                            );
                            break;
                        }
                    } else {
                        this.enviaResposta(
                            exchange, 
                            200, 
                            this.listaRegistros()
                        );
                        break;
                    }
                case "PUT":
                    if (segments.length == 3) {
                        if (segments[2].matches("\\d+")) {
                            short id = Short.parseShort(segments[2]);

                            this.atualizaRegistro(id, this.parseFormData(bytesBody));
                        }
                    }
                    this.enviaResposta(
                        exchange, 
                        200, 
                        "{\"mensagem\":\"ok!\"}"
                    );
                    
                    break;
                case "DELETE":
                    if (segments.length == 3 && segments[2].matches("\\d+")) {
                        if (this.deletaRegistro(Short.parseShort(segments[2]))) {
                            this.enviaResposta(
                                exchange, 
                                200, 
                                "{\"mensagem\": \"Registro deletado\"}"
                            );
                        } else {
                            this.enviaResposta(
                                exchange, 
                                404, 
                                "{\"mensagem\": \"Registro não encontrado\"}"
                            );
                        }
                    }
                    break;
                default:
                    this.enviaRespostaErro(exchange, "MÉTODO DESCONHECIDO");
                    break;
            }

            exchange.close();
        };
    }

    public String listarPorLivro(short idLivro) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listarPorLivro'");
    }
}
