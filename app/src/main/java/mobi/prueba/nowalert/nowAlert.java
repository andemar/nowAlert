package mobi.prueba.nowalert;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class nowAlert extends AppCompatActivity {
    //------------------ACRONIMOS--------------------//
    /**
     * RC = Ritmo Cardiaco
     * bt = Button
     * ibt = ImageButton
     * tv = TextView
     * lv = ListView
     * DB = Data Base
     *
     *
     */
    //------------------FINAL--------------------//

    //Respectivos nombres de la DB y tabla de datos.
    public final String NOMBRE_DB_DATOS = "dbDatos";
    public final String NOMBRE_TABLA_DATOS = "datos";
    //Respectivos nombres de la DB y tabla de contactos.
    public final String NOMBRE_DB_CONTACTOS = "dbContactos";
    public final String NOMBRE_TABLA_CONTACTOS = "contactos";
    //PERMISOS

    //------------------ATRIBUTOS--------------------//
    //Pantalla
    private ImageButton ibtReload;
    private TextView tvRc;
    private ListView lvRc;
    //DB
    private conexionDatosDB conexionDatos;
    private conexionContactosDB conexionContactos;
    //GPS
    private String gpsCoor;

    //------------------CONSTRUCTOR--------------------//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_alert);

        //Permisos para el uso de componentes. (GPS, MENSAJERIA, LLAMADAS)

        int permissionCheckGps = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        //Muestra el letrero para solicitar el permiso del GPS.
        if (permissionCheckGps == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }


        int permissionCheckLlamada = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        //Muestra el letrero para solicitar el permiso de las llamadas.
        if(permissionCheckLlamada == PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        }

        int permissionCheckMsm = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        //Muestra el letrero para solicitar el permiso de las llamadas.
        if(permissionCheckMsm == PackageManager.PERMISSION_DENIED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 1);
            }
        }

        //Inicializo el GPS.
        coorGps();

        //Inicializacion de los xml's
        ibtReload = (ImageButton) findViewById(R.id.ibtReaload);
        tvRc = (TextView) findViewById(R.id.tvRc);
        lvRc = (ListView) findViewById(R.id.lvRc);
        //Este metodo extrae los datos de DB y los muestra en la lista.
        refrescarLista();
    }

    //-------------------METODOS---------------------//
    // <------------ %%%%%%%%ESTE METODO SE ENCARGA DE RECIBIR LOS DATOS DEL bluetooth %%%%%%%%%
    public void reloadRc(View view) {

        conexionDatos = new conexionDatosDB(this, NOMBRE_DB_DATOS, null, 1);
        SQLiteDatabase db = conexionDatos.getReadableDatabase();

        int idDato = db.rawQuery("select * from " + NOMBRE_TABLA_DATOS, null).getCount() + 1;
        String rc = String.valueOf((int) (Math.random() * 99) + 1);  //<---- AQUI IRIA EL METODO GetRcBlooeth PARA RECOGER EL VALOR DE RC Y GUARDARLO EN LA DB. %%%%%%%%%%%
        String fecha = getFecha();
        String coor = gpsCoor;

        ContentValues registro = new ContentValues();

        registro.put("idDato", idDato);
        registro.put("rc", rc);
        registro.put("fecha", fecha);
        registro.put("coor", coor);

        db.insert(NOMBRE_TABLA_DATOS, null, registro);
        db.close();
        refrescarLista();
    }

    private void refrescarLista() {
        //Contenedor del RC
        ArrayAdapter<String> rc = new ArrayAdapter<String>(this, R.layout.lista_contactos, getRc());
        //Ingreso los rc
        lvRc.setAdapter(rc);
    }


    private void coorGps() {
        //Creamos un gestor de locaciones.
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Creamos un listener de locaciones.
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                gpsCoor = location.getLatitude() + " " + location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        //Verificamos el permiso.
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (Exception i) {
            Toast.makeText(this, "Para ejecutar la app, se necesita el gps", Toast.LENGTH_SHORT).show();
        }

    }


    //-------------------CONEXION DB---------------------//

    private String[] getRc() {
        //Lista contenedora de los valores de RC
        ArrayList<String> listaRc = new ArrayList<String>();
        //Resultado a devolver
        String[] lista;
        //Valores del contenedor
        String rc = "-1";
        String fecha = "";
        String coor = "";
        //Contador
        int i = 0;
        //Realizo conexion con DB
        conexionDatos = new conexionDatosDB(this, NOMBRE_DB_DATOS, null, 1);
        SQLiteDatabase db = conexionDatos.getReadableDatabase();
        //Busco en la base de datos, si la tabla ya se encuentra creada. "Para no cagarla"
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + NOMBRE_TABLA_DATOS + "'", null);
        //Si el cursor es diferente de null, significa que encontro ALGO.
        if (cursor != null) {
            //Si el count del cursor es mayor a cero, significa que la tabla, si existe.
            if (cursor.getCount() > 0) {
                //Query para extraer los datos de RC. (rc, fecha, coordenadas)
                Cursor fila = db.rawQuery("select rc, fecha, coor from " + NOMBRE_TABLA_DATOS, null);
                //Extraigo los valores de la fila y los guardo en el arreglo de la forma.      Ritmo Cardiaco  | Fecha | Coordenadas
                if (fila.moveToFirst()) {
                    rc = fila.getString(0);
                    fecha = fila.getString(1);
                    coor = fila.getString(2);
                    listaRc.add(rc + "  |  " + fecha + "  |  " + coor);
                    i++;
                }
                while (fila.moveToNext()) {
                    rc = fila.getString(0);
                    fecha = fila.getString(1);
                    coor = fila.getString(2);
                    listaRc.add("  " + rc + " |   " + fecha + "   |  " + coor);
                    i++;
                }
            }
        }
        //Cierro la DB.
        db.close();
        //Inicializo la lista con el numero de datos recogidos
        lista = new String[listaRc.size()];
        //Retorno la lista vacia si no existe la tabla
        if (cursor == null) {
            return lista;
            //Retorno la lista con los datos recopilados
        } else {

            for (int j = 0; j < listaRc.size(); j++) {
                lista[j] = listaRc.get(listaRc.size() - j - 1);
            }
            //Cambia de color el tvRc segun el rc del usuario. entre 50 y 70, verde; entre 40 y 49 o 71 y 80, amarillo; todo lo demas rojo
            if (lista.length != 0) {
                tvRc.setText(rc);
                if (Integer.parseInt(rc) >= 50 && Integer.parseInt(rc) <= 70) {
                    tvRc.setBackgroundResource(R.color.rcNormal);
                } else if (Integer.parseInt(rc) >= 40 && Integer.parseInt(rc) <= 49 || Integer.parseInt(rc) >= 71 && Integer.parseInt(rc) <= 80) {
                    tvRc.setBackgroundResource(R.color.rcCuidado);
                } else
                    tvRc.setBackgroundResource(R.color.rcAlerta);
            }

            //Inicializo el GPS. Para verificar los permisos.
            coorGps();
            //Se verifica si se envia la alerta, o no.
            rcAlerta(Integer.parseInt(rc));
            //Retorna la lista
            return lista;
        }
    }

    //------------------- METODOS ----------------------//

    private String getFecha() {

        Date date = new Date();
        SimpleDateFormat hora = new SimpleDateFormat("HH-mm");
        SimpleDateFormat dia = new SimpleDateFormat("dd");
        SimpleDateFormat mes = new SimpleDateFormat("MM");
        String mesNom = "";

        switch (mes.format(date)) {
            case "1":
                mesNom = "Ene";
                break;
            case "2":
                mesNom = "Feb";
                break;
            case "3":
                mesNom = "Mar";
                break;
            case "4":
                mesNom = "Abr";
                break;
            case "5":
                mesNom = "May";
                break;
            case "6":
                mesNom = "Jun";
                break;
            case "7":
                mesNom = "Jul";
                break;
            case "8":
                mesNom = "Ago";
                break;
            case "9":
                mesNom = "Sep";
                break;
            case "10":
                mesNom = "Oct";
                break;
            case "11":
                mesNom = "Nov";
                break;
            case "12":
                mesNom = "Dic";
                break;
        }

        String fecha = mesNom + " " + dia.format(date) + ", " + hora.format(date);

        return fecha;
    }

    //Metodo que revisa los contactos que tienen limite de RC y le envia la alerta al primero que encuentre.
    private void rcAlerta(int rc) {
        //Valores rcMin, rcMax, numContacto ----> Estos valores corresponden al 2, 3, 4 para la DB correspondientemente.
        String nombreContacto = "";
        String numContacto = "";
        int rcMin = 0;
        int rcMax = 0;
        int llamada = -1;
        int mensaje = -1;
        int nada = -1;
        //Conexion con db contactos
        conexionContactos = new conexionContactosDB(this, NOMBRE_DB_CONTACTOS, null, 1);
        SQLiteDatabase db = conexionContactos.getReadableDatabase();
        //Cursor para extraer todos los contactos
        Cursor fila = db.rawQuery("select * from " + NOMBRE_TABLA_CONTACTOS, null);
        //Recorrido de los contactos
        while (fila.moveToNext()) {
            nombreContacto = fila.getString(1);
            numContacto = fila.getString(2);
            rcMin = fila.getInt(3);
            rcMax = fila.getInt(4);
            llamada = fila.getInt(5);
            mensaje = fila.getInt(6);
            nada = fila.getInt(7);

            if(rc == -1){

            }else{
                if (rc < rcMin || rc > rcMax) {
                    if (llamada == 1) {
                        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+numContacto));
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }else
                        startActivity(i);
                    }else if(mensaje == 1){

                        try {
                            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
                            if(permissionCheck == PackageManager.PERMISSION_DENIED){
                                Toast.makeText(getApplicationContext(),"No se concedieron permiso de mensajeria.", Toast.LENGTH_LONG).show();
                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},255);
                            }else{
                                SmsManager msm = SmsManager.getDefault();
                                msm.sendTextMessage("tel:"+numContacto,null, "El usuario tiene un RC de "+ rc +", su posicion actual es: "+ gpsCoor, null, null);
                                Toast.makeText(getApplicationContext(), "El mensaje fue enviado", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"El mensaje no pudo ser enviado.", Toast.LENGTH_LONG).show();
                        }

                    }else if(nada == 1){
                        Toast.makeText(this, "El contacto "+ nombreContacto +" tiene las alertas deshabilitadas.", Toast.LENGTH_LONG).show();
                    }else
                        Toast.makeText(this, "La alerta no pudo ser enviada.", Toast.LENGTH_SHORT).show();
                }
            }

            db.close();

        }
    }

    //------------------- PARTE GRAFICA -----------------------//
    //Metodo para mostrar los botones de accion (Configuracion)
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.configuracion, menu);
        return true;
    }

    //Metodo para agregar las acciones a los botones de accion
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.conf){
            Intent i = new Intent(this, listaContactos.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}