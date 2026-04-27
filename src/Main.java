package src;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.*;

import src.controller.Controller;
import src.model.Autor;
import src.model.Emprestimo;
import src.model.Livro;
import src.model.Usuario;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        Controller<Autor> autorController = new Controller<>(
                "autores.dat",
                Autor::fromBytes,
                Autor::formToInstance);
        server.createContext("/autores", autorController.orchestrator());

        Controller<Emprestimo> emprestimoController = new Controller<>(
                "emprestimos.dat",
                Emprestimo::fromBytes,
                Emprestimo::formToInstance);
        server.createContext("/emprestimos", emprestimoController.orchestrator());

        Controller<Livro> livroController = new Controller<Livro>(
                "livros.dat",
                Livro::fromBytes,
                Livro::formToInstance);

        Controller<Usuario> usuarioController = new Controller<Usuario>(
                "usuarios.dat",
                Usuario::fromBytes,
                Usuario::formToInstance);
        server.createContext("/usuarios", usuarioController.orchestrator());

        server.createContext("/livros", exchange -> {
            String method = exchange.getRequestMethod();
            String uriPath = exchange.getRequestURI().getPath();
            String[] segments = uriPath.split("/");

            if ("GET".equals(method)
                    && segments.length == 4
                    && segments[2].matches("\\d+")
                    && segments[3].equals("emprestimos")) {

                short idLivro = Short.parseShort(segments[2]);
                String json;
                json = emprestimoController.listarPorLivro(idLivro);
                byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
                exchange.close();
                return;
            }

            // Todas as demais rotas de Livros -> controller generico
            livroController.orchestrator().handle(exchange);
        });

        server.createContext("/index.html", exchange -> {
            try {
                Path filePath = Paths.get("src/view/index.html");
                byte[] fileContent = Files.readAllBytes(filePath);

                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, fileContent.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(fileContent);
                }
            } catch (IOException e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            }
        });

        System.out.println("http://127.0.0.1:8080/index.html");
        server.start();
    }
}
