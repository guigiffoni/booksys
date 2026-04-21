package src;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

import src.controller.LivroController;

public class Main {
    public static void main(String[] args) throws IOException {
        LivroController livroController = new LivroController();

        // System.out.println(livroController.listaRegistros());

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/livros", livroController.orchestrator());
        server.start();
    }
}
