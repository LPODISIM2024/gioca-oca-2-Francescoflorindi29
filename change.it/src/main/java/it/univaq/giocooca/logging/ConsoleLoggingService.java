package it.univaq.giocooca.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConsoleLoggingService implements ILoggingService {

    @Override
    public void logAction(String action) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + ts + "] " + action);
    }
}

