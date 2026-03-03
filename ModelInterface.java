import java.io.IOException;

public interface ModelInterface<T extends ModelInterface<T>> {
    short id = 0;

    byte[] toBytes() throws IOException;
    int getTamanhoEmBytes();
    String toString();
    short getId();
    void setId(short id);
}
