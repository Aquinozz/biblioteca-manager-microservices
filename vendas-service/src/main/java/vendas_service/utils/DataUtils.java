package vendas_service.utils;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DataUtils {

    public static final String  DATA_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss";
    public static final String DATA_PATTERN = "dd/MM/yyyy";


    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATA_TIME_PATTERN);
    public static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern(DATA_PATTERN);



    public static String format (LocalDate date){
        return date != null ? date.format(DATE_PATTERN) : null;
    }

    public static String format (LocalDateTime dateTime){
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
    }

}
