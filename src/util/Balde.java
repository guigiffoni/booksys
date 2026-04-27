package src.util;

class Balde {
    private static final int numElementos = 5;
    final long offset;
    int profLocal;
    int numReg;
    short[] chaves = new short[numElementos];
    short[] valores = new short[numElementos];

    Balde(long offset, int profLocal) {
        this.offset = offset;
        this.profLocal = profLocal;
        this.numReg = 0;
        java.util.Arrays.fill(chaves, (short) -1);
        java.util.Arrays.fill(valores, (short) -1);
    }

    boolean cheio() {
        return numReg >= numElementos;
    }

    void adicionar(short chave, short valor) {
        chaves[numReg] = chave;
        valores[numReg] = valor;
        numReg++;
    }

    boolean remover(short chave, short valor) {
        for (int i = 0; i < numReg; i++) {
            if (chaves[i] == chave && valores[i] == valor) {
                // shift left
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