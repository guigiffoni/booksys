package src.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

import com.sun.net.httpserver.HttpHandler;

import src.model.Emprestimo;
import src.util.IndiceHash;

public class EmprestimoController extends Controller<Emprestimo> {
    private final IndiceHash indiceHash;

    public EmprestimoController(
            String nomeArquivo,
            Function<byte[], Emprestimo> fromBytes,
            Function<HashMap<String, Object>, Emprestimo> formToInstance) {
        super(nomeArquivo, fromBytes, formToInstance);

        IndiceHash hash = null;
        try {
            hash = new IndiceHash("livro_emprestimo");
        } catch (IOException e) {
            System.err.println("[EmprestimoController] Erro ao inicializar índice hash: "
                    + e.getLocalizedMessage());
            System.exit(1);
        }
        this.indiceHash = hash;
    }

    public void inserirComHash(Emprestimo emprestimo) throws IOException {
        this.dao.inserir(emprestimo);                          // define id automaticamente
        this.indiceHash.inserir(emprestimo.getIdLivro(), emprestimo.getId());

        System.out.printf("[Hash] Inserido: Livro %d → Empréstimo %d%n",
                emprestimo.getIdLivro(), emprestimo.getId());
    }

    public boolean deletarComHash(short id) throws IOException {
        if (!this.dao.seekRegistro(id)) {
            return false;
        }

        Emprestimo emp = this.dao.leRegistroAtual();
        this.dao.seekRegistro(id);
        this.dao.deletar();

        boolean removidoHash = this.indiceHash.remover(emp.getIdLivro(), emp.getId());
        System.out.printf("[Hash] Removido: Livro %d → Empréstimo %d (hash=%b)%n",
                emp.getIdLivro(), emp.getId(), removidoHash);

        return true;
    }

    public String listarPorLivro(short idLivro) throws IOException {
        ArrayList<Short> idsEmprestimo = this.indiceHash.buscar(idLivro);

        if (idsEmprestimo.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        boolean primeiro = true;

        for (short idEmp : idsEmprestimo) {
            Emprestimo emp = this.dao.encontraRegistro(idEmp);
            if (emp != null) {
                if (!primeiro) sb.append(',');
                sb.append(emp.toJson());
                primeiro = false;
            }
        }

        sb.append(']');
        return sb.toString();
    }

    @Override
    public HttpHandler orchestrator() {
        return exchange -> {
            String   method   = exchange.getRequestMethod();
            String   path     = exchange.getRequestURI().getPath();
            String[] segments = path.split("/");
            byte[]   body     = exchange.getRequestBody().readAllBytes();

            System.out.println(method + " → " + path);

            try {
                switch (method) {

                    case "POST": {
                        Emprestimo emp = this.parseFormData(body);
                        this.inserirComHash(emp);
                        this.enviaResposta(exchange, 200,
                                "{\"mensagem\":\"ok!\",\"id\":" + emp.getId() + "}");
                        break;
                    }

                    case "GET": {
                        if (segments.length == 4
                                && segments[2].equals("livro")
                                && segments[3].matches("\\d+")) {

                            short idLivro = Short.parseShort(segments[3]);
                            String json   = this.listarPorLivro(idLivro);
                            this.enviaResposta(exchange, 200, json);

                        } else if (segments.length == 3
                                && segments[2].matches("\\d+")) {

                            short      id  = Short.parseShort(segments[2]);
                            Emprestimo emp = this.dao.encontraRegistro(id);
                            if (emp != null) {
                                this.enviaResposta(exchange, 200, emp.toJson());
                            } else {
                                this.enviaResposta(exchange, 404,
                                        "{\"mensagem\":\"Empréstimo não encontrado\"}");
                            }

                        } else {
                            this.enviaResposta(exchange, 200, this.listaRegistros());
                        }
                        break;
                    }

                    case "PUT": {
                        if (segments.length == 3 && segments[2].matches("\\d+")) {
                            short id = Short.parseShort(segments[2]);
                            this.atualizaRegistro(id, this.parseFormData(body));
                        }
                        this.enviaResposta(exchange, 200, "{\"mensagem\":\"ok!\"}");
                        break;
                    }

                    case "DELETE": {
                        if (segments.length == 3 && segments[2].matches("\\d+")) {
                            short id = Short.parseShort(segments[2]);
                            if (this.deletarComHash(id)) {
                                this.enviaResposta(exchange, 200,
                                        "{\"mensagem\":\"Empréstimo removido\"}");
                            } else {
                                this.enviaResposta(exchange, 404,
                                        "{\"mensagem\":\"Empréstimo não encontrado\"}");
                            }
                        }
                        break;
                    }

                    default:
                        this.enviaRespostaErro(exchange, "Método desconhecido");
                }
            } catch (IOException e) {
                System.err.println("[EmprestimoController] " + e.getLocalizedMessage());
                this.enviaRespostaErro(exchange, "{\"erro\":\"" + e.getMessage() + "\"}");
            }

            exchange.close();
        };
    }
}
