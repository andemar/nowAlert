package mobi.prueba.nowalert;

import android.content.ContentValues;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class editarContacto extends AppCompatActivity {

    //-------------- VARIABLES FINAL ------------------//

    public final String NOMBRE_DB = "dbContactos";
    public final String NOMBRE_TABLA = "contactos";

    //--------------------VARIABLES-------------------//
    private String dato;

    private EditText etNombre;
    private EditText etNumero;
    private EditText etMinimo;
    private EditText etMaximo;
    private RadioButton rbLlamada;
    private RadioButton rbMensaje;
    private RadioButton rbNada;

    private conexionContactosDB conexion;


    //------------------------CONSTRUCTOR---------------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacto);

        //Lineas para obtner el nombre y dator de un contacto.
        dato = getIntent().getStringExtra("nombre");
        //Lineas para la inicializacion de las variables de comunicacion XML
        etNombre = (EditText) findViewById(R.id.etNombreContacto);
        etNumero = (EditText) findViewById(R.id.etNumeroContacto);
        etMinimo = (EditText) findViewById(R.id.etRCMin);
        etMaximo = (EditText) findViewById(R.id.etRCmax);
        rbLlamada = (RadioButton) findViewById(R.id.rbLlamada);
        rbNada = (RadioButton) findViewById(R.id.rbNada);
        rbMensaje = (RadioButton) findViewById(R.id.rbMensaje);
        System.out.println("Este es el put: " + dato);
        //Si el usuario selecciono un contacto, se muestran los datos de este, de la DB.
        if(dato != null){
            //Realizo conexion con la DB
            conexion = new conexionContactosDB(this, NOMBRE_DB, null, 1);
            SQLiteDatabase db = conexion.getReadableDatabase();
            //Realizo un query para extraer los datos de la DB.
            Cursor fila = db.rawQuery("select numContacto, rcMin, rcMax, avisoLlamadaContacto, avisoMensajeContacto, avisoNada from "+ NOMBRE_TABLA +" where nombreContacto ='"+dato+"'", null);

            if(fila.moveToFirst()){
                etNombre.setText(dato);
                etNumero.setText(fila.getString(0));
                etMinimo.setText(fila.getString(1));
                etMaximo.setText(fila.getString(2));
                //Condicionales que seleccionan o no, los checkbox (Les coloca la palomita si es 1)
                if(fila.getInt(3) == 1){
                    rbLlamada.setChecked(true);
                }
                if(fila.getInt(4) == 1){
                    rbMensaje.setChecked(true);
                }
                if(fila.getInt(5) == 1){
                    rbNada.setChecked(true);
                }
            }else{
                Toast.makeText(this,"El contacto no existe",Toast.LENGTH_SHORT).show();
                db.close();
            }

        }
    }
    //--------------------METODOS PARA DB-------------------//

    public void btGuardar(View view){
        //Recojo todos los valores que ingreso el usuario.
        String nombre = etNombre.getText().toString();
        String numero = etNumero.getText().toString();
        String minimo = etMinimo.getText().toString();
        String maximo = etMaximo.getText().toString();

        //Si el check box esta senalado, guarda 1, de lo contrario 0.
        int llamada = rbLlamada.isChecked() == true? 1:0;
        int mensaje = rbMensaje.isChecked() == true? 1:0;
        int nada = rbNada.isChecked() == true? 1:0;



        //Reviso si todos los campos estan deligenciados
        if(!nombre.isEmpty() && !numero.isEmpty() && !minimo.isEmpty() && !maximo.isEmpty()){

            //Realizo conexion con la DB
            conexion = new conexionContactosDB(this, NOMBRE_DB, null, 1);
            SQLiteDatabase db = conexion.getReadableDatabase();
            //Linea para guardar/modificar en la DB

            //Existe el contacto ?
            Cursor existe = db.rawQuery("select * from "+ NOMBRE_TABLA+ " where nombreContacto ='"+nombre+"'", null);
            //Si no existe, se crea el contacto con su nombre y respectivo id.
            if(existe.getCount() <= 0){
                //Guardo todos los datos en un content ya que se va a crear un nuevo contacto.
                ContentValues registro = new ContentValues();
                //Obtengo el numero de filas y le sumo 1, para crear un nuevo 1dContacto.
                registro.put("idContacto", db.rawQuery("select * from "+ NOMBRE_TABLA, null).getCount() + 1);
                registro.put("nombreContacto", nombre);
                registro.put("numContacto", numero);
                registro.put("rcMin", minimo);
                registro.put("rcMax", maximo);
                registro.put("avisoLlamadaContacto", llamada);
                registro.put("avisoMensajeContacto", mensaje);
                registro.put("avisoNada", nada);
                //Se inserta el contacto a la DB.
                db.insert(NOMBRE_TABLA,null, registro);


                //Se crea un mensaje, diciendo que se anadio.
                Toast.makeText(this, "El contacto se creo correctamente", Toast.LENGTH_SHORT).show();;
                //Se cierra conexion.
                db.close();
                //Se devuelve al anterior activity, listaContactos.
                Intent i = new Intent(this, listaContactos.class);
                startActivity(i);
            }else{
                //Se debe actualizar el contacto, ya que si existe.
                ContentValues registro = new ContentValues();
                //Valores del contacto
                registro.put("numContacto", numero);
                registro.put("rcMin", minimo);
                registro.put("rcMax", maximo);
                registro.put("avisoLlamadaContacto", llamada);
                registro.put("avisoMensajeContacto", mensaje);
                registro.put("avisoNada", nada);
                //Se actualiza la DB.
                int cantidad = db.update(NOMBRE_TABLA, registro, "nombreContacto='"+nombre+"'",null);
                db.close();

                if(cantidad >= 1){
                    Toast.makeText(this, "El contacto fue modificado.", Toast.LENGTH_SHORT).show();;
                    Intent i = new Intent(this, listaContactos.class);
                    startActivity(i);
                }else
                    Toast.makeText(this, "Hubo un error, al moficar el contacto.", Toast.LENGTH_SHORT).show();;
            }
        }else
            Toast.makeText(this, "Diligencie todos los campos, antes de guardar.", Toast.LENGTH_LONG).show();

    }

    public void btCancelar(View view){
        Intent i = new Intent(this, listaContactos.class);
        startActivity(i);
    }

    //------------------- PARTE GRAFICA -----------------------//
    //Metodo para mostrar los botones de accion (Eliminar)
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.eliminarcontacto, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        // Extraigo el id del item que se selecciono
        int id = item.getItemId();
        //Lo comparo con el id de eliminar
        if(id == R.id.elimnar){

            if(etNombre != null && !etNombre.getText().toString().isEmpty()){
                //Establesco conexion a la DB
                conexion = new conexionContactosDB(this, NOMBRE_DB, null, 1);
                SQLiteDatabase db = conexion.getReadableDatabase();
                //Realizo el delate del contacto con nombre "dato": El dato es el que se envio por PUT.EXTRA.
                int cantidad = db.delete(NOMBRE_TABLA, "nombreContacto='"+dato+"'", null);
                //Si cantidad es mayor o igual a 1, se elimino correctamente y se devuelve a la lista de contactos.
                if(cantidad >= 1){
                    Toast.makeText(this, "El contacto fue eliminado", Toast.LENGTH_SHORT).show();
                    //Intent a lista contactos
                    Intent i = new Intent(this, listaContactos.class);
                    startActivity(i);
                }else
                    Toast.makeText(this, "Hubo un error y no se puedo eliminar el contacto.", Toast.LENGTH_SHORT).show();

            }else
                Toast.makeText(this, "Tienes que seleccionar un usuario, para poder eliminarlo.", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}