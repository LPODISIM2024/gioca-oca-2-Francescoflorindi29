package it.univaq.giocooca.logging;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLoggingService implements ILoggingService {

    private BufferedWriter writer;

    public FileLoggingService(String filePath) throws IOException {
        FileWriter fw = new FileWriter(filePath, true); 
        this.writer = new BufferedWriter(fw);
    }

    @Override
    public void logAction(String action) {
        try {
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write("[" + timestamp + "] " + action);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
