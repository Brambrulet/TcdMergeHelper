package dto;

import consts.RcItemFlags;
import consts.RcItemType;

public class RcItem {
    public RcItemType type;
    public String id;
    public int flags;
    public int value;

    public RcItem(){
        this.type = RcItemType.UNKNOWN;
        this.id = new String();
        this.flags = RcItemFlags.NO_FLAGS;
        this.value = -1;
    }

    public RcItem(String id){
        this.type = RcItemType.UNKNOWN;
        this.id = id;
        this.flags = RcItemFlags.NO_FLAGS;
        this.value = -1;
    }

    public RcItem(String id, RcItemType type){
        this.type = type;
        this.id = id;
        this.flags = RcItemFlags.NO_FLAGS;
        this.value = -1;
    }

    public RcItem(String id, RcItemType type, int flags){
        this.type = type;
        this.id = id;
        this.flags = flags;
        this.value = -1;
    }

    public RcItem(String id, int value){
        this.type = RcItemType.UNKNOWN;
        this.id = id;
        this.flags = RcItemFlags.NO_FLAGS;
        this.value = value;
    }

    public RcItem(RcItem src){
        this.type = src.type;
        this.id = src.id;
        this.flags = src.flags;
        this.value = src.value;
    }
}
