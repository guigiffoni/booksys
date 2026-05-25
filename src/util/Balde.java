package src.util;

class Balde {
    // Usa a mesma constante definida em IndiceHash
    static final int NUM_ELEMENTOS = IndiceHash.SLOTS_POR_Balde;
    
    final long offset;
    short profLocal;      // usar short para alinhar com IndiceHash
    short numReg;         // usar short
    short[] chaves = new short[NUM_ELEMENTOS];
    short[] valores = new short[NUM_ELEMENTOS];

    Balde(long offset, int profLocal) {
        this.offset = offset;
        this.profLocal = (short) profLocal;
        this.numReg = 0;
        java.util.Arrays.fill(chaves, (short) -1);
        java.util.Arrays.fill(valores, (short) -1);
    }

    boolean cheio() {
        return numReg >= NUM_ELEMENTOS;
    }

    void adicionar(short chave, short valor) {
        if (cheio()) throw new IllegalStateException("Balde cheio");
        chaves[numReg] = chave;
        valores[numReg] = valor;
        numReg++;
    }

    boolean remover(short chave, short valor) {
        for (int i = 0; i < numReg; i++) {
            if (chaves[i] == chave && valores[i] == valor) {
                // desloca elementos à esquerda
                for (int j = i; j < numReg - 1; j++) {
                    chaves[j] = chaves[j + 1];
                    valores[j] = valores[j + 1];
                }
                numReg--;
                chaves[numReg] = -1;
                valores[numReg] = -1;
                return true;
            }
        }
        return false;
    }
}