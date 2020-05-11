package DatabaseManager;
import android.content.Context;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


class DataBaseHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME="MonitorAppDatabaseNew17.db";
    private static final int DATABASE_VERSION=1;

    //Constructor

    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
