package src.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import src.util.FormatoData;
import src.util.Registro;

public class Emprestimo implements Registro {
    private short id;
    private short idLivro;
    private short idUsuario;
    // false: não foi devolvido | true: devolvido
    private boolean devolvido;
    // formato: YYYYMMDD
    private String dataEmprestimo;
    // formato: YYYYMMDD
    private String dataDevolucao;

    private static final long DIAS_EMPRESTIMO = 7;

    public Emprestimo() {

    }

    public Emprestimo(short idLivro, short idUsuario) {
        this.setIdLivro(idLivro);
        this.setIdUsuario(idUsuario);
        this.devolvido = false;

        LocalDate dataAtual = LocalDate.now();
        this.dataEmprestimo = dataAtual.format(FormatoData.ANO_MES_DIA);
        LocalDate diaDevolucao = dataAtual.plusDays(DIAS_EMPRESTIMO);
        this.dataDevolucao = diaDevolucao.format(FormatoData.ANO_MES_DIA);
    }

    public Emprestimo(
            short id,
            short idLivro,
            short idUsuario,
            boolean devolvido,
            String dataEmprestimo,
            String dataDevolucao) {
        this.setId(id);
        this.setIdLivro(idLivro);
        this.setIdUsuario(idUsuario);
        this.setStatusDevolucao(devolvido);
        this.setDataEmprestimo(dataEmprestimo);
        this.setDataDevolucao(dataDevolucao);
    }

    @Override
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeShort(id);
        dos.writeShort(idLivro);
        dos.writeShort(idUsuario);
        dos.writeBoolean(devolvido);
        dos.writeUTF(dataEmprestimo);
        dos.writeUTF(dataDevolucao);

        return baos.toByteArray();
    }

    public static Emprestimo fromBytes(byte[] dados) {
        ByteArrayInputStream bais = new ByteArrayInputStream(dados);
        DataInputStream dis = new DataInputStream(bais);

        try {
            short id = dis.readShort();
            short idLivro = dis.readShort();
            short idUsuario = dis.readShort();
            boolean devolvido = dis.readBoolean();
            String dataEmprestimo = dis.readUTF();
            String dataDevolucao = dis.readUTF();

            return new Emprestimo(
                id,
                idLivro,
                idUsuario,
                devolvido,
                dataEmprestimo,
                dataDevolucao
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getTamanhoEmBytes() {
        // tamanho inicial e fixo é definido por:
        // id + idLivro + idUsuario + devolvido + dataEmprestimo + dataDevolucao
        return 2 * 3 + 1 + 8 * 2;
    }

    @Override
    public short getId() {
        return this.id;
    }

    @Override
    public void setId(short id) {
        if (id < 1) {
            throw new IllegalArgumentException("Valor não pode ser menor que 1");
        }

        this.id = id;
    }

    public short getIdLivro() {
        return this.idLivro;
    }

    public void setIdLivro(short idLivro) {
        if (idLivro < 1) {
            throw new IllegalArgumentException("Valor não pode ser menor que 1");
        }

        this.idLivro = idLivro;
    }

    public short getIdUsuario() {
        return this.idUsuario;
    }

    public void setIdUsuario(short idUsuario) {
        if (idUsuario < 1) {
            throw new IllegalArgumentException("ID não pode ser menor que 1");
        }

        this.idUsuario = idUsuario;
    }

    public boolean getDevolvido() {
        return this.devolvido;
    }

    public void setStatusDevolucao(boolean devolvido) {
        this.devolvido = devolvido;
    }

    public String getDataEmprestimo() {
        return this.dataEmprestimo;
    }

    public void setDataEmprestimo(String dataEmprestimo) {
        if (dataEmprestimo.length() != 8) {
            throw new IllegalArgumentException("Data de empréstimo deve seguir o formato YYYYMMDD");
        }

        this.dataEmprestimo = dataEmprestimo;
    }

    public String getDataDevolucao() {
        return this.dataDevolucao;
    }

    public void setDataDevolucao(String dataDevolucao) {
        if (dataEmprestimo.length() != 8) {
            throw new IllegalArgumentException("Data de devolução deve seguir o formato YYYYMMDD");
        }

        this.dataDevolucao = dataDevolucao;
    }

    @Override
    public String toString() {
        return String.format(
            "\t%d |\t%s |\t%d |\t%s |\t%s |\t%d",
            this.getId(),
            this.getIdLivro(),
            this.getIdUsuario(),
            this.getDevolvido(),
            this.getDataEmprestimo(),
            this.getDataDevolucao()
        );
    }

    public String toJson() {
        return String.format(
            "{\"id\":%d,\"idLivro\":%d,\"idUsuario\":%d,\"devolvido\":\"%s\",\"dataEmprestimo\":\"%s\",\"dataDevolucao\":\"%s\"}",
            this.getId(),
            this.getIdLivro(),
            this.getIdUsuario(),
            this.getDevolvido(),
            this.getDataEmprestimo(),
            this.getDataDevolucao()
        );
    }
}
