package Code;

import Code.database.DataBaseManager;

public class Main {
    public static void main(String[] args) {
        DataBaseManager dbManager = new DataBaseManager();
        new Login(dbManager);
    }
}
