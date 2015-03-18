package pl.sygncode.warehouse;


public interface Storage {

    String TABLE_NAME = "STORAGE";
    String ID = "_id";
    String SUPER_ID = "SUPER_STORAGE_ID";
    String NAME = "STORAGE_NAME";
    String FLAG = "STORAGE_FLAG";
    String SEQUENCE = "STORAGE_SEQUENCE";


    String[] PROJ = {ID, SUPER_ID, NAME, FLAG};
}
