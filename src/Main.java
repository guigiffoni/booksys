package src;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
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
            Autor::formToInstance
        );
        server.createContext("/autores", autorController.orchestrator());

        Controller<Emprestimo> emprestimoController = new Controller<>(
            "emprestimos.dat",
            Emprestimo::fromBytes,
            Emprestimo::formToInstance
        );
        server.createContext("/emprestimos", emprestimoController.orchestrator());

        Controller<Livro> livroController = new Controller<Livro>(
            "livros.dat",
            Livro::fromBytes,
            Livro::formToInstance
        );
        server.createContext("/livros", livroController.orchestrator());

        Controller<Usuario> usuarioController = new Controller<Usuario>(
            "usuarios.dat",
            Usuario::fromBytes,
            Usuario::formToInstance
        );
        server.createContext("/usuarios", usuarioController.orchestrator());

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

        server.start();
    }
}
