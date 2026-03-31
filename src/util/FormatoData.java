package src.util;

import java.time.format.DateTimeFormatter;

public interface FormatoData {
    public static final DateTimeFormatter ANO_MES_DIA = 
            DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter DIA_MES_ANO = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DIA_MES_ANO_HORA = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
}
