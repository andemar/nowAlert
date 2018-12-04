package mobi.prueba.nowalert;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class conexionContactosDB extends SQLiteOpenHelper {

    //Respectivos nombres de la DB y tabla de contactos.
    public final String  NOMBRE_DB = "dbContactos";
    public final String NOMBRE_TABLA = "contactos";


    public conexionContactosDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * idContacto: El id del contacto
         * nombreContacto: El nombre del contacto
         * telContacto: El numero celular del contacto
         * rcMin: El rc minimo.
         * rcMax: El rc maximo.
         * avisoLlamadaContacto: Se llama al contacto.              <---- valor 1, selecciono, valor 0, no selecciono.
         * aviensajeContacto: Se envia mensaje al contacto.         <---- valor 1, selecciono, valor 0, no selecciono.
         */
        db.execSQL("create table contactos(idContacto int primary key, nombreContacto String, numContacto String, rcMin String, rcMax String, avisoLlamadaContacto int, avisoMensajeContacto int, avisoNada int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
