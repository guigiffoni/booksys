package src.util;

import java.io.*;
import java.util.ArrayList;

public class IndiceHash {
    private static final String PATH = "data/";
    public static final int SLOTS_POR_Balde = 8;
    public static final int TAMANHO_Balde = 4 + SLOTS_POR_Balde * 4;
    private static final int PROF_MAX = 16;

    private final RandomAccessFile rafDir;
    private final RandomAccessFile rafBkt;
    private int profundidade;
    private long[] diretorio;

    public IndiceHash(String nomeBase) throws IOException {
        new File(PATH).mkdirs();
        boolean existe = new File(PATH + nomeBase + ".dir").exists()
                      && new File(PATH + nomeBase + ".bal").exists();

        rafDir = new RandomAccessFile(PATH + nomeBase + ".dir", "rw");
        rafBkt = new RandomAccessFile(PATH + nomeBase + ".bal", "rw");

        if (existe) {
            lerDiretorio();
        } else {
            inicializar();
        }
    }

    private void inicializar() throws IOException {
        profundidade = 1;
        diretorio = new long[2];
        diretorio[0] = alocarBalde(1);
        diretorio[1] = alocarBalde(1);
        gravarDiretorio();
    }

    private void lerDiretorio() throws IOException {
        rafDir.seek(0);
        profundidade = rafDir.readInt();
        int n = 1 << profundidade;
        diretorio = new long[n];
        for (int i = 0; i < n; i++) {
            diretorio[i] = rafDir.readLong();
        }
    }

    private void gravarDiretorio() throws IOException {
        rafDir.seek(0);
        rafDir.writeInt(profundidade);
        for (long off : diretorio) {
            rafDir.writeLong(off);
        }
        rafDir.setLength(4L + (long) diretorio.length * 8);
    }

    private void dobrarDiretorio() {
        int n = diretorio.length;
        long[] novo = new long[n * 2];
        for (int i = 0; i < n; i++) {
            novo[i] = diretorio[i];
            novo[i + n] = diretorio[i];
        }
        diretorio = novo;
        profundidade++;
    }

    private long alocarBalde(int profundidade) throws IOException {
        long offset = rafBkt.length();
        gravarBalde(new Balde(offset, profundidade));
        return offset;
    }

    private Balde lerBalde(long offset) throws IOException {
        rafBkt.seek(offset);
        Balde b = new Balde(offset, 0);
        b.profLocal = rafBkt.readShort();
        b.numReg    = rafBkt.readShort();
        for (int i = 0; i < SLOTS_POR_Balde; i++) {
            b.chaves[i]  = rafBkt.readShort();
            b.valores[i] = rafBkt.readShort();
        }
        return b;
    }

    private void gravarBalde(Balde b) throws IOException {
        rafBkt.seek(b.offset);
        rafBkt.writeShort(b.profLocal);
        rafBkt.writeShort(b.numReg);
        for (int i = 0; i < SLOTS_POR_Balde; i++) {
            rafBkt.writeShort(i < b.numReg ? b.chaves[i] : -1);
            rafBkt.writeShort(i < b.numReg ? b.valores[i] : -1);
        }
    }

    private int hash(int chave, int prof) {
        return chave & ((1 << prof) - 1);
    }

    private boolean tentarDividir(int idx) throws IOException {
        long offsetAntigo = diretorio[idx];
        Balde bAntigo = lerBalde(offsetAntigo);
        int d = bAntigo.profLocal;
        int novaProf = d + 1;
        if (novaProf > PROF_MAX) return false;


        int cnt0 = 0, cnt1 = 0;
        for (int i = 0; i < bAntigo.numReg; i++) {
            int h = hash(bAntigo.chaves[i], novaProf);
            if (((h >> d) & 1) == 0) cnt0++; else cnt1++;
        }
        if (cnt0 == 0 || cnt1 == 0) return false;


        if (d == profundidade) {
            dobrarDiretorio();
        }


        long offsetNovo = alocarBalde(novaProf);

        Balde bVelho = new Balde(offsetAntigo, novaProf);
        Balde bNovo  = lerBalde(offsetNovo);

        for (int i = 0; i < bAntigo.numReg; i++) {
            int h = hash(bAntigo.chaves[i], novaProf);
            if (((h >> d) & 1) == 0) {
                bVelho.adicionar(bAntigo.chaves[i], bAntigo.valores[i]);
            } else {
                bNovo.adicionar(bAntigo.chaves[i], bAntigo.valores[i]);
            }
        }

        gravarBalde(bVelho);
        gravarBalde(bNovo);


        for (int i = 0; i < diretorio.length; i++) {
            if (diretorio[i] == offsetAntigo) {
                if (((i >> d) & 1) == 1) {
                    diretorio[i] = offsetNovo;
                } else {
                    diretorio[i] = offsetAntigo;
                }
            }
        }
        return true;
    }

    public boolean inserir(short chave, short valor) throws IOException {
        while (true) {
            int idx = hash(chave, profundidade);
            long offset = diretorio[idx];
            Balde b = lerBalde(offset);

            if (!b.cheio()) {
                b.adicionar(chave, valor);
                gravarBalde(b);
                gravarDiretorio();
                return true;
            }


            if (!tentarDividir(idx)) {
                return false;
            }

        }
    }

    public ArrayList<Short> buscar(short chave) throws IOException {
        int idx = hash(chave, profundidade);
        long offset = diretorio[idx];
        Balde b = lerBalde(offset);
        ArrayList<Short> resultado = new ArrayList<>();
        for (int i = 0; i < b.numReg; i++) {
            if (b.chaves[i] == chave) {
                resultado.add(b.valores[i]);
            }
        }
        return resultado;
    }

    public boolean remover(short chave, short valor) throws IOException {
        int idx = hash(chave, profundidade);
        long offset = diretorio[idx];
        Balde b = lerBalde(offset);
        if (b.remover(chave, valor)) {
            gravarBalde(b);
            gravarDiretorio();
            return true;
        }
        return false;
    }

    public void fechar() throws IOException {
        gravarDiretorio();
        rafDir.close();
        rafBkt.close();
    }
}