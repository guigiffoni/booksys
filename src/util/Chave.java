package src.util;

public class Chave<T extends Registro> {
    public T refInstancia;
    Pagina<T> pagEsquerda;
    Pagina<T> pagDireita;

    public Chave(T refInstancia) {
        this.refInstancia = refInstancia;
    }

    public int compareTo(Chave<T> outra) {
        if (outra == null) {
            return 1;
        }

        return Short.compare(
            this.refInstancia.getId(), 
            outra.refInstancia.getId()
        );
    }
}