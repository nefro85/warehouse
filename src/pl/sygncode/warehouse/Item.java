package pl.sygncode.warehouse;


public interface Item {

    String TABLE_NAME = "STORAGE_ITEM";
    String ID = "_id";
    String STORAGE_ID = "STORAGE_ID";
    String NAME = "ITEM_NAME";
    String TYPE_NAME = "ITEM_TYPE_NAME";


    String[]  PROJ = {ID,STORAGE_ID,NAME};

}