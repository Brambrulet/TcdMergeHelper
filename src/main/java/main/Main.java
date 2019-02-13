package main;

import utils.RcFile;
import utils.ResourceHFile;

public class Main {
    public static void main(String[] args) {
        ResourceHFile resourceH = new ResourceHFile();
        RcFile rcFile = new RcFile();

        resourceH.read();
        rcFile.read();

        rcFile.updateIds(resourceH);
        resourceH.updateTotal(rcFile);

        resourceH.save();
        rcFile.save();
    }
}
