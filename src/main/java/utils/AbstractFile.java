package utils;

import consts.RcItemType;
import dto.RcItem;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class AbstractFile {
    public static final String DEFINE_MACRO = "#define";
    public static final String ENDIF_MACRO = "#endif";


    protected boolean wasRead = false;
    protected List<RcItem> items;
    protected List<String> lines;
    protected List<String> headerLines;
    protected List<String> footerLines;
    protected final String fileName;
    protected RcItemType typeOfLastReadItem;

    public AbstractFile(String fileName) {
        this.fileName = fileName;
    }

    public void clear() {
        wasRead = false;
        typeOfLastReadItem = RcItemType.UNKNOWN;
        lines = new ArrayList<>();
        headerLines = new ArrayList<>();
        footerLines = new ArrayList<>();
        items = new ArrayList<>();
    }

    public void read() {
        if (wasRead) return;

        clear();
        File rcFile;
        try {
            rcFile = new File(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String line = "";
        try (FileInputStream inputStream = new FileInputStream(fileName);
             InputStreamReader reader = new InputStreamReader(inputStream, "windows-1251");
             Scanner scanner = new Scanner(reader);) {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                onReadLine(line, line.trim().split("\\s+"));
            }

            wasRead = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected List<RcItem> copyOf(List<RcItem> src) {
        List<RcItem> result = new ArrayList<>();
        for (RcItem item : src) {
            result.add(new RcItem(item));
        }

        return result;
    }

    protected abstract void onReadLine(String line, String[] words);

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName + ".new", false));) {
            writeHeader(writer);

            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }

            writeFooter(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void writeFooter(BufferedWriter writer) throws IOException {
        for (String line : footerLines) {
            writer.write(line);
            writer.newLine();
        }
    }

    protected void writeHeader(BufferedWriter writer) throws IOException {
        for (String line : headerLines) {
            writer.write(line);
            writer.newLine();
        }
    }

    public static boolean isControlItem(RcItem item) {
        return item.type == RcItemType.CONTROL ||
                (item.type == RcItemType.UNKNOWN && item.id.startsWith("IDC_"));
    }

    public static boolean isBitmapItem(RcItem item) {
        return item.type == RcItemType.BITMAP ||
                (item.type == RcItemType.UNKNOWN && item.id.startsWith("IDB_"));
    }

    public List<RcItem> getItems() {
        return copyOf(items);
    }
}
