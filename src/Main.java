package src;
import src.controller.*;

public class Main {
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        LivroController controller = new LivroController();

        controller.run();
    }
}
