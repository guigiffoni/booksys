package src.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import src.dao.DaoGenerico;
import src.util.Registro;
import src.view.ViewGenerico;

public class ControllerGenerico<T extends Registro> {
    protected String nomeArquivo;
    protected DaoGenerico<T> dao;
    protected ViewGenerico<T> view;

    public void enviaResposta(HttpExchange exchange, int status, String resposta) throws IOException {
        byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
    }

    public String listaRegistros() throws IOException {
        ArrayList<T> registros = this.dao.listar();
        StringBuilder str = new StringBuilder();

        str.append('[');
        for (T registro : registros) {
            str.append(registro.toJson());
            str.append(',');
        }
        str.setCharAt(str.length() - 1, ']');
        return str.toString();
    }

    public HttpHandler orchestrator() {
        return exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            // String[] segments = path.split(path);

            System.out.println(path);

            String resposta;

            switch (method) {
                case "POST":
                    
                    break;
                case "GET":
                    resposta = this.listaRegistros();
                    this.enviaResposta(exchange, 200, resposta);
                    break;
                case "PUT":
                    
                    break;
                case "DELETE":

                    break;
                default:

                    break;
            }
        };
    }
}
