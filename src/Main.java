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
import src.dao.Dao;
import src.model.Autor;
import src.model.Emprestimo;
import src.model.Livro;
import src.model.Usuario;
import src.util.IndiceRelacionalNN;
import src.util.Huffman;
import src.util.Lzw;
import src.util.BoyerMoore;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // DAOs compartilhados para Autor e Livro
        Dao<Autor> autorDao = new Dao<>(
            "autores.dat",
            Autor::fromBytes,
            Autor::formToInstance
        );
        Dao<Livro> livroDao = new Dao<>(
            "livros.dat",
            Livro::fromBytes,
            Livro::formToInstance
        );

        // Índice N:N
        IndiceRelacionalNN indiceAssociacao = new IndiceRelacionalNN(autorDao, livroDao);

        // Controllers
        Controller<Autor> autorController = new Controller<>(autorDao);
        Controller<Livro> livroController = new Controller<>(livroDao);
        Controller<Emprestimo> emprestimoController = new Controller<>(
                "emprestimos.dat", Emprestimo::fromBytes, Emprestimo::formToInstance);
        Controller<Usuario> usuarioController = new Controller<>(
                "usuarios.dat", Usuario::fromBytes, Usuario::formToInstance);

        // --- Contexto unificado para /autores ---
        server.createContext("/autores", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] segments = path.split("/");

            // POST /autores/{idAutor}/livros
            if ("POST".equals(method) && segments.length == 4 && "livros".equals(segments[3])) {
                try {
                    int idAutor = Integer.parseInt(segments[2]);
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    String[] params = body.split("&");
                    int idLivro = -1;
                    for (String p : params) {
                        String[] kv = p.split("=");
                        if (kv.length == 2 && "idLivro".equals(kv[0])) {
                            idLivro = Integer.parseInt(kv[1]);
                            break;
                        }
                    }
                    if (idLivro == -1) throw new IllegalArgumentException("idLivro não informado");
                    indiceAssociacao.vincular(idAutor, idLivro);
                    exchange.sendResponseHeaders(200, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(400, -1);
                } finally {
                    exchange.close();
                }
                return;
            }

            // DELETE /autores/{idAutor}/livros/{idLivro}
            if ("DELETE".equals(method) && segments.length == 5 && "livros".equals(segments[3])) {
                try {
                    int idAutor = Integer.parseInt(segments[2]);
                    int idLivro = Integer.parseInt(segments[4]);
                    indiceAssociacao.desvincular(idAutor, idLivro);
                    exchange.sendResponseHeaders(200, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(404, -1);
                } finally {
                    exchange.close();
                }
                return;
            }

            // GET /autores/{idAutor}/livros
            if ("GET".equals(method) && segments.length == 4 && "livros".equals(segments[3])) {
                try {
                    int idAutor = Integer.parseInt(segments[2]);
                    var livros = indiceAssociacao.listarLivrosDoAutor(idAutor);
                    String json = livrosToJson(livros);
                    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                } finally {
                    exchange.close();
                }
                return;
            }

            // Demais rotas: delegar ao controller original
            autorController.orchestrator().handle(exchange);
        });

        // --- Contexto unificado para /livros ---
        server.createContext("/livros", exchange -> {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] segments = path.split("/");

            // GET /livros/{id}/autores (nova rota)
            if ("GET".equals(method) && segments.length == 4 && "autores".equals(segments[3])) {
                try {
                    int idLivro = Integer.parseInt(segments[2]);
                    var autores = indiceAssociacao.listarAutoresDoLivro(idLivro);
                    String json = autoresToJson(autores);
                    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                } finally {
                    exchange.close();
                }
                return;
            }

            // GET /livros/{id}/emprestimos (rota original)
            if ("GET".equals(method) && segments.length == 4 && "emprestimos".equals(segments[3])) {
                try {
                    short idLivro = Short.parseShort(segments[2]);
                    String json = emprestimoController.listarPorLivro(idLivro);
                    byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                } finally {
                    exchange.close();
                }
                return;
            }

            // Demais rotas: delegar ao controller original de livros
            livroController.orchestrator().handle(exchange);
        });

        // Contextos simples para as demais entidades
        server.createContext("/emprestimos", emprestimoController.orchestrator());
        server.createContext("/usuarios", usuarioController.orchestrator());

        // --- Huffman - compactação e descompactação ---
        server.createContext("/huffman", exchange -> {
            String method = exchange.getRequestMethod();
            String[] segments = exchange.getRequestURI().getPath().split("/");
            byte[] body = exchange.getRequestBody().readAllBytes();
            String nomeArquivo = new String(body, StandardCharsets.UTF_8).replace("arquivo=", "").trim();
            String caminhoOriginal = "data/" + nomeArquivo;

            // POST /huffman/comprimir
            if ("POST".equals(method) && segments.length == 3 && "comprimir".equals(segments[2])) {
                try {
                    byte[] dados = Files.readAllBytes(Paths.get(caminhoOriginal));
                    byte[] comprimido = Huffman.comprimir(dados);
                    Files.write(Paths.get(caminhoOriginal + ".huff"), comprimido);
                    String resposta = "{\"mensagem\":\"Arquivo comprimido com sucesso\",\"original\":" + dados.length + ",\"comprimido\":" + comprimido.length + "}";
                    byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, bytes.length);
                    exchange.getResponseBody().write(bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                } finally {
                    exchange.close();
                }
                return;
            }

            // POST /huffman/descomprimir
            if ("POST".equals(method) && segments.length == 3 && "descomprimir".equals(segments[2])) {
                try {
                    byte[] dados = Files.readAllBytes(Paths.get(caminhoOriginal + ".huff"));
                    byte[] descomprimido = Huffman.descomprimir(dados);
                    Files.write(Paths.get(caminhoOriginal + ".dec"), descomprimido);
                    String resposta = "{\"mensagem\":\"Arquivo descomprimido com sucesso\"}";
                    byte[] bytes = resposta.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, bytes.length);
                    exchange.getResponseBody().write(bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                } finally {
                    exchange.close();
                }
                return;
            }
        });

        // Servir o arquivo HTML
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

        server.createContext("/compactar", exchange -> {
            if (!"POST".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }
            try {
                String body = new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8);

                String arquivo = null;
                String acao    = null;

                for (String par : body.split("&")) {
                    String[] kv = par.split("=", 2);
                    if (kv.length == 2) {
                        switch (kv[0]) {
                            case "arquivo" -> arquivo = java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                            case "acao"    -> acao    = kv[1];
                        }
                    }
                }

                if (arquivo == null || acao == null) {
                    exchange.sendResponseHeaders(400, -1);
                    exchange.close();
                    return;
                }

                long bytes;
                String nomeResultado;

                switch (acao) {
                    case "comprimir" -> {
                        bytes = Lzw.comprimir(arquivo);
                        nomeResultado = arquivo + ".lzw";
                    }
                    case "descomprimir" -> {
                        bytes = Lzw.descomprimir(arquivo);
                        nomeResultado = arquivo.endsWith(".lzw")
                                ? arquivo.substring(0, arquivo.length() - 4)
                                : arquivo + ".descomprimido";
                    }
                    default -> {
                        exchange.sendResponseHeaders(400, -1);
                        exchange.close();
                        return;
                    }
                }

                String json = String.format(
                        "{\"arquivo\":\"%s\",\"bytes\":%d}",
                        nomeResultado, bytes);
                byte[] respBytes = json.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type",
                        "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, respBytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(respBytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, -1);
            } finally {
                exchange.close();
            }
        });

        System.out.println("Servidor rodando em http://127.0.0.1:8080/index.html");
        server.start();

        // exemplo de uso do BoyceMoore
        BoyerMoore.buscaPadrao("livros.dat", "Dom Casmurro");
        // buscaPadrao retorna (em long) o endereço do padrão encontrado (ou não)
    }

    // Métodos auxiliares para conversão JSON
    private static String livrosToJson(java.util.List<Livro> livros) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < livros.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(livros.get(i).toJson());
        }
        sb.append("]");
        return sb.toString();
    }

    private static String autoresToJson(java.util.List<Autor> autores) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < autores.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(autores.get(i).toJson());
        }
        sb.append("]");
        return sb.toString();
    }
}
