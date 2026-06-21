package src.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Xor {

    // chave fixa usada para cifrar a senha dos usuários.
    private static final byte[] CHAVE =
            "AED3-2026-CHAVE-SECRETA".getBytes(StandardCharsets.UTF_8);

    // aplica XOR byte a byte entre {@code dados} e {@code chave}, repetindo a chave 
    // ciclicamente quando ela é menor que os dados.
    public static byte[] aplicar(byte[] dados, byte[] chave) {
        byte[] resultado = new byte[dados.length];

        for (int i = 0; i < dados.length; i++) {
            resultado[i] = (byte) (dados[i] ^ chave[i % chave.length]);
        }

        return resultado;
    }

    // criptografa um texto plano usando a chave padrão e retorna o resultado em base64.
    public static String criptografar(String textoPlano) {
        byte[] dados = textoPlano.getBytes(StandardCharsets.UTF_8);
        byte[] cifrado = aplicar(dados, CHAVE);
        return Base64.getEncoder().encodeToString(cifrado);
    }

    // reverte {@link #criptografar(String)}: decodifica o base64 e 
    // aplica XOR novamente (operação simétrica) para obter o texto original.
    public static String descriptografar(String textoCriptografado) {
        byte[] cifrado = Base64.getDecoder().decode(textoCriptografado);
        byte[] dados = aplicar(cifrado, CHAVE);
        return new String(dados, StandardCharsets.UTF_8);
    }
}