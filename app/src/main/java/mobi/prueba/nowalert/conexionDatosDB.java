package mobi.prueba.nowalert;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class conexionDatosDB extends SQLiteOpenHelper {


    //Respectivos nombres de la DB y tabla de datos.
    public final String  NOMBRE_DB = "dbDatos";
    public final String NOMBRE_TABLA = "datos";

    public conexionDatosDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         /**
          * idDato = La llave primaria y el numero del dato.
          * rc = Ritmo Cardiaco
          * fecha = La fecha en que fue tomada el rc.
          * coor = Coordenadas del usuario cuando fue tomado el rc.
          */
        db.execSQL("create table datos(idDato int primary key, rc int, fecha String, coor String)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
