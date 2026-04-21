package src.controller;

import java.io.IOException;

import src.dao.DaoGenerico;
import src.model.Usuario;

public class UsuarioController extends ControllerGenerico<Usuario> {
    private static final String nomeArquivo = "livros.dat";

    public UsuarioController() {
        try {
            super.dao = new DaoGenerico<Usuario>(nomeArquivo, Usuario::fromBytes);
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
