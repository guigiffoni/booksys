package src.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface Registro {
    short id = 0;
    Charset charset = StandardCharsets.UTF_8;

    byte[] toBytes() throws IOException;
    int getTamanhoEmBytes();
    String toString();
    short getId();
    void setId(short id);
}
