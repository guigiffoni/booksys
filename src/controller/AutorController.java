package src.controller;

import java.io.IOException;

import src.dao.DaoGenerico;
import src.model.Autor;

public class AutorController extends ControllerGenerico<Autor> {
    private static final String nomeArquivo = "autores.dat";

    public AutorController() {
        try {
            super.dao = new DaoGenerico<Autor>(nomeArquivo, Autor::fromBytes);
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
