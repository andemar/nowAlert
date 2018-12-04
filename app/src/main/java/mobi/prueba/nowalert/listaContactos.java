package mobi.prueba.nowalert;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class listaContactos extends AppCompatActivity {

    //-------------- VARIABLES FINAL ------------------//

    public final String  NOMBRE_DB = "dbContactos";
    public final String NOMBRE_TABLA = "contactos";

    //--------------------VARIABLES-------------------//

    //Lista de contactos
    private ListView lvContactos;
    //Conexion DB
    private conexionContactosDB conexion;


    //------------------------CONSTRUCTOR---------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contactos);

        //Comunico el atributo lvContactos con el ListView del xlm
        lvContactos = (ListView) findViewById(R.id.lvContactos);

        //Contenedor de los nombres
        ArrayAdapter<String> nombres = new ArrayAdapter<String>(this, R.layout.lista_contactos, getNombreContactos());
        //Ingreso los nombres a la lista del xml
        lvContactos.setAdapter(nombres);
        //Codigo para las acciones cuando se pulsa sobre un nombre
        lvContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intentEditarContactos(position);
            }
        });

    }


    //------------------- METODOS PARA LA BASE DE DATOS -----------------------//

    //Metodo para extraer de la base de datos, los nombres de los contactos
    private String[] getNombreContactos(){
        //Creo variable contenedora de los nombres de la DB.
        String[] nombres;
        //Contador
        int i = 0;
        //Realizo conexion con la DB
        conexion = new conexionContactosDB(this, NOMBRE_DB, null, 1);
        SQLiteDatabase db = conexion.getReadableDatabase();
        //Query para extraer los nombres de la DB.
        Cursor fila = db.rawQuery("select nombreContacto from "+ NOMBRE_TABLA, null);
        //Inicializa el arreglo con el numero de nombres a agregar.
        nombres = new String[fila.getCount()];
        //Extraigo los valores de la fila, y los guardo en el arreglo.
        if(fila.moveToFirst()){
            nombres[i] = fila.getString(0);
            i++;
        }
        while(fila.moveToNext()){
            nombres[i] = fila.getString(0);
            i++;
        }
        db.close();
        return nombres;
    }

    //------------------- METODOS PARA CAMBIAR DE ACTIVITY -----------------------//

    //Metodo que nos permite ir a la clase editar contacto, del contacto que senalo el usuario en la lista.
    private void intentEditarContactos(int i){
        Intent acEditarContacto = new Intent(this, editarContacto.class);
        //Obtengo la posicion del valor del nombre, de la lista, y lo envio como extra.
        acEditarContacto.putExtra("nombre", lvContactos.getItemAtPosition(i).toString());
        startActivity(acEditarContacto);
    }


    public void intentHome(View view){
        Intent acNowAlert = new Intent(this, nowAlert.class);
        startActivity(acNowAlert);
    }

    //------------------- PARTE GRAFICA -----------------------//
    //Metodo para mostrar los botones de accion (Configuracion)
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.anadircontacto, menu);
        return true;
    }

    //Metodo para agregar las acciones a los botones de accion
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.anadir){
            Intent i = new Intent(this, editarContacto.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
