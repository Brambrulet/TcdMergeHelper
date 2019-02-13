package utils;

import consts.RcItemType;
import dto.RcItem;

import java.util.List;

public class ResourceHFile extends AbstractFile {
    public static final String HEADER_FILE_NAME = "resource.h";

    public ResourceHFile() {
        super(HEADER_FILE_NAME);
        clear();
    }

    protected void onReadLine(String line, String[] words) {
        boolean isControl = isLineControl(line, words);

        if ((typeOfLastReadItem == RcItemType.CONTROL || lines.isEmpty()) && footerLines.isEmpty() && isControl) {
            lines.add(line);
            addItem(line, words);
            typeOfLastReadItem = RcItemType.CONTROL;
        } else if (typeOfLastReadItem == RcItemType.UNKNOWN && lines.isEmpty() && !isControl) {
            headerLines.add(line);
        } else {
            footerLines.add(line);
            typeOfLastReadItem = RcItemType.UNKNOWN;
        }
    }

    private void addItem(String line, String[] words) {
        String id = words[1];
        int value = Integer.parseInt(words[2]);
        RcItem item = new RcItem(id, value);

        items.add(item);
    }

    private boolean isLineControl(String line, String[] words) {
        return !line.isEmpty() && line.startsWith("#") && words.length > 2 && DEFINE_MACRO.equals(words[0]);
    }

    public void updateTotal(RcFile rcFile) {
        List<RcItem> oldItems = copyOf(items);
        items.clear();
        lines.clear();

//        items.addAll(rcFile.getMenus());
//        items.addAll(rcFile.getDialogs());
//        items.addAll(rcFile.getIcons());
//        items.addAll(rcFile.getCursors());
//        items.addAll(rcFile.getImages());
//        items.addAll(rcFile.getBitmaps());
//        items.addAll(rcFile.getMenuItems());
//        items.addAll(rcFile.getControls());
//        items.addAll(rcFile.getStrings());

        for (RcItem item : oldItems) {
            if (!isControlItem(item))
                items.add(item);
        }

        List<RcItem> newControlItems = rcFile.getControls();

        int newNumber = 1000;
        for (RcItem item : newControlItems) {
            item.value = ++newNumber;
            items.add(item);
        }

        for (RcItem item : items) {
            lines.add(DEFINE_MACRO + " " + item.id + " " + item.value);
        }
    }
}
