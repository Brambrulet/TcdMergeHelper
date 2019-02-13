package utils;

import consts.RcItemType;
import dto.RcItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RcFile extends AbstractFile {
    public static final String RC_FILE_NAME = "SystemTest.rc";
    public static final String MENU = "MENU";
    public static final String MENUITEM = "MENUITEM";
    public static final String DIALOG = "DIALOG";
    public static final String DIALOGEX = "DIALOGEX";
    public static final String STRINGTABLE = "STRINGTABLE";
    public static final String BEGIN = "BEGIN";
    public static final String END = "END";
    public static final String ICON = "ICON";
    public static final String IMAGE = "IMAGE";
    public static final String BITMAP = "BITMAP";
    public static final String CURSOR = "CURSOR";
    public static final String WAVE = "WAVE";
    public static final String CONTROL = "CONTROL";

    private boolean isHeader = false;
    private boolean isFooter = false;

    private boolean waitBegin = false;
    private boolean waitEnd = false;
    private RcItemType section;
    private int nesting = 0;

    private List<RcItem> menus;
    private List<RcItem> menuItems;
    private List<RcItem> dialogs;
    private List<RcItem> strings;
    private List<RcItem> icons;
    private List<RcItem> images;
    private List<RcItem> bitmaps;
    private List<RcItem> cursors;
    private List<RcItem> controls;
    private List<RcItem> waves;

    public RcFile() {
        super(RC_FILE_NAME);
        clear();
    }

    @Override
    public void clear() {
        super.clear();

        isHeader = true;
        isFooter = false;

        waitBegin = false;
        waitEnd = false;
        section = RcItemType.UNKNOWN;
        nesting = 0;

        menus = new ArrayList<>();
        menuItems = new ArrayList<>();
        dialogs = new ArrayList<>();
        strings = new ArrayList<>();
        icons = new ArrayList<>();
        images = new ArrayList<>();
        bitmaps = new ArrayList<>();
        cursors = new ArrayList<>();
        controls = new ArrayList<>();
        waves = new ArrayList<>();
    }

    protected void onReadLine(String line, String[] words) {
        if (isFooter || isBeginFooter(words)) {
            isFooter = true;
            footerLines.add(line);
        } else if (words.length == 0 || words[0].startsWith("/")) {
            if (isHeader) {
                headerLines.add(line);
            } else {
                lines.add(line);
            }
        } else if (waitBegin && BEGIN.equals(words[0])) {
            lines.add(line);
            waitBegin = false;
            waitEnd = true;
            ++nesting;
        } else if (waitEnd && END.equals(words[0])) {
            lines.add(line);
            waitEnd = false;
            section = RcItemType.UNKNOWN;
            --nesting;
        } else if (readWave(line, words) ||
                readBitmap(line, words) ||
//                readMenu(line, words) ||
//                readMenuItem(line, words) ||
//                readCursor(line, words) ||
//                readImage(line, words) ||
//                readString(line, words) ||
//                readIcon(line, words) ||
                readDialog(line, words) ||
                readControl(line, words)) {
            isHeader = false;
            lines.add(line);
        } else if (isHeader) {
            headerLines.add(line);
        } else {
            lines.add(line);
        }
    }

    private boolean isBeginFooter(String[] words) {
        //#endif    // Russian resources
        return words.length > 2 && ENDIF_MACRO.equals(words[0]) && "resources".equals(words[words.length - 1]) && words[words.length - 2].endsWith("Russian");
    }

    private boolean readWave(String line, String[] words) {
        if (!isHeader &&
                words.length > 2 &&
                WAVE.equals(words[1]) &&
                words[1].startsWith("IDR_")) {
            RcItem item = new RcItem();

            item.id = words[1];
            item.type = RcItemType.WAVE;

            waves.add(item);
            items.add(item);

            return true;
        } else
            return false;
    }

    private boolean readBitmap(String line, String[] words) {
        if (!isHeader &&
                words.length > 2 &&
                BITMAP.equals(words[1]) &&
                words[1].startsWith("IDB_")) {
            RcItem item = new RcItem();

            item.id = words[1];
            item.type = RcItemType.BITMAP;

            bitmaps.add(item);
            items.add(item);

            return true;
        }

        return false;
    }

    private boolean readCursor(String line, String[] words) {
        //TODO write code if necessary
        return false;
    }

    private boolean readImage(String line, String[] words) {
        //TODO write code if necessary
        return false;
    }

    private boolean readIcon(String line, String[] words) {
        //TODO write code if necessary
        return false;
    }

    private boolean readString(String line, String[] words) {
        //TODO write code if necessary
        return false;
    }

    private boolean readControl(String line, String[] words) {
        if (!isHeader &&
                nesting == 1 &&
                section == RcItemType.DIALOG &&
                waitEnd &&
                (words[1].startsWith("IDC_") || words[1].indexOf(",IDC_") > -1)) {

            RcItem item = new RcItem();

            item.id = extracControlId(words[1]);
            item.type = RcItemType.CONTROL;

            if (item.id.isEmpty()) {
                return false;
            } else {
                controls.add(item);
                items.add(item);

                return true;
            }
        } else
            return false;
    }

    private String extracControlId(String src) {
        String[] words = src.split(",");

        for (String word : words) {
            if (word.startsWith("IDC_"))
                return word;
        }

        return "";
    }

    private boolean readDialog(String line, String[] words) {
        if (nesting == 0 &&
                words.length > 2 &&
                (DIALOG.equals(words[1]) || DIALOGEX.equals(words[1])) &&
                words[0].startsWith("IDD_")) {
            waitBegin = true;
            waitEnd = false;
            section = RcItemType.DIALOG;
            isHeader = false;

            RcItem item = new RcItem();

            item.id = words[1];
            item.type = RcItemType.DIALOG;

            dialogs.add(item);
            items.add(item);

            return true;
        } else
            return false;
    }

    private boolean readMenuItem(String line, String[] words) {
        //TODO write code if necessary
        return false;
    }

    private boolean readMenu(String line, String[] words) {
        //TODO write code if necessary
        return false;
    }

    public List<RcItem> getMenus() {
        return copyOf(menus);
    }

    public List<RcItem> getMenusItems() {
        return copyOf(menuItems);
    }

    public List<RcItem> getDialogs() {
        return copyOf(dialogs);
    }

    public List<RcItem> getStrings() {
        return copyOf(strings);
    }

    public List<RcItem> getIcons() {
        return copyOf(icons);
    }

    public List<RcItem> getImages() {
        return copyOf(images);
    }

    public List<RcItem> getBitmaps() {
        return copyOf(bitmaps);
    }

    public List<RcItem> getCursors() {
        return copyOf(cursors);
    }

    public List<RcItem> getMenuItems() {
        return copyOf(menuItems);
    }

    public List<RcItem> getControls() {
        return copyOf(controls);
    }

    public void updateIds(ResourceHFile resourceH) {
        Map<String, String> index = new HashMap<>();

        List<RcItem> hItems = resourceH.getItems();

        for (RcItem item: hItems) {
            if(isBitmapItem(item)){
                index.put(Integer.toString(item.value), item.id);
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] words = line.trim().split("\\s+");

            if(words.length > 1 && CONTROL.equals(words[0]) && words[1].indexOf(",\"Static\",SS_BITMAP") > -1){
                String id = words[1].split(",")[0];

                if(index.containsKey(id)){
                    line = line.replaceFirst("\\s+CONTROL\\s+" + id, "    CONTROL         " + index.get(id));
                    lines.set(i, line);
                }
            }
        }
    }
}
