package src.controller;

import src.dao.DaoGenerico;
import src.model.Emprestimo;

import java.io.IOException;

public class EmprestimoController extends ControllerGenerico<Emprestimo> {
    private static final String nomeArquivo = "emprestimos.dat";

    public EmprestimoController() {
        try {
            super.dao = new DaoGenerico<Emprestimo>(nomeArquivo, Emprestimo::fromBytes);
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
