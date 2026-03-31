package src.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import src.util.FormatoData;
import src.util.ModelInterface;

public class Emprestimo implements ModelInterface<Emprestimo> {
    private short id;
    private short idLivro;
    private short idUsuario;
    // false: não foi devolvido | true: devolvido
    private boolean statusDevolucao;
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
        this.statusDevolucao = false;

        LocalDate dataAtual = LocalDate.now();
        this.dataEmprestimo = dataAtual.format(FormatoData.ANO_MES_DIA);
        LocalDate diaDevolucao = dataAtual.plusDays(DIAS_EMPRESTIMO);
        this.dataDevolucao = diaDevolucao.format(FormatoData.ANO_MES_DIA);
    }

    public Emprestimo(
            short id,
            short idLivro,
            short idUsuario,
            boolean statusDevolucao,
            String dataEmprestimo,
            String dataDevolucao) {
        this.setId(id);
        this.setIdLivro(idLivro);
        this.setIdUsuario(idUsuario);
        this.setStatusDevolucao(statusDevolucao);
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
        dos.writeBoolean(statusDevolucao);
        dos.writeUTF(dataEmprestimo);
        dos.writeUTF(dataDevolucao);

        return baos.toByteArray();
    }

    public static Emprestimo fromBytes(byte[] dados) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(dados);
        DataInputStream dis = new DataInputStream(bais);

        short id = dis.readShort();
        short idLivro = dis.readShort();
        short idUsuario = dis.readShort();
        boolean statusDevolucao = dis.readBoolean();
        String dataEmprestimo = dis.readUTF();
        String dataDevolucao = dis.readUTF();

        return new Emprestimo(
                id,
                idLivro,
                idUsuario,
                statusDevolucao,
                dataEmprestimo,
                dataDevolucao);
    }

    @Override
    public int getTamanhoEmBytes() {
        // tamanho inicial e fixo é definido por:
        // id + idLivro + idUsuario + statusDevolucao + dataEmprestimo + dataDevolucao
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

    public boolean getStatusDevolucao() {
        return this.statusDevolucao;
    }

    public void setStatusDevolucao(boolean statusDevolucao) {
        this.statusDevolucao = statusDevolucao;
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
                this.getStatusDevolucao(),
                this.getDataEmprestimo(),
                this.getDataDevolucao());
    }
}
