package vn.codegym.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVUtils {
    private static final String CSV_PATH = "D:\\Thi-module-2\\BankAccountManagement\\src\\main\\resources\\data\\bank_accounts.csv";

    static {
        File csvFile = new File(CSV_PATH);
        if (!csvFile.exists()) {
            try {
                File parent = csvFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                csvFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Không thể tạo file dữ liệu: " + e.getMessage());
            }
        }
    }

    public static void writeLine(String path, String line, boolean append) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, append))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeAll(String path, List<?> objects) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (Object obj : objects) {
                if (obj != null) {
                    try {
                        // Always use toCSV() for BankAccount objects
                        bw.write((String) obj.getClass().getMethod("toCSV").invoke(obj));
                    } catch (Exception e) {
                        // If toCSV() is not available, skip writing
                        // Optionally, log or handle differently if needed
                    }
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readAll(String path) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}