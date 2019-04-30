package com.example.karla.contactos;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class ActivityContactos extends AppCompatActivity {

    EditText mNombre, mNumero;
    Button btnGuardar;
    ListView lvDatos;
    AsyncHttpClient cliente;
    static  final int PICK_CONTACT_REQUEST=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitycontactos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Instancias de las vistas

        mNombre = findViewById(R.id.nombreId);
        mNumero = findViewById(R.id.numeroId);
        btnGuardar = findViewById(R.id.btnGuardar);
        lvDatos = findViewById(R.id.lvDatos);
        cliente = new AsyncHttpClient();

        botonGuardar();
        obtenerClientes();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarContacto();
            }
        });

    }


    private  void botonGuardar(){
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNombre.getText().toString().isEmpty()|| mNumero.getText().toString().isEmpty()){
                    Toast.makeText(ActivityContactos.this, "Hay campos vacios", Toast.LENGTH_SHORT).show();
                }else {
                    Contactos c = new Contactos();
                    c.setNombre(mNombre.getText().toString().replaceAll(" ","%20"));
                    c.setTelefono(mNumero.getText().toString());
                    agregarContacto(c);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    obtenerClientes();
                }
            }
        });
    }

    private void agregarContacto(Contactos c){
        String url = "https://thermal-profile.000webhostapp.com/agregar.php?";
        String parametros = "nombre="+c.getNombre()+"&telefono="+c.getTelefono();
        cliente.post(url + parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if(statusCode == 200) {
                    Toast.makeText(ActivityContactos.this, "Cliente agregado correctamente", Toast.LENGTH_SHORT).show();
                    mNombre.setText("");
                    mNumero.setText("");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    private void obtenerClientes(){
        String url ="https://thermal-profile.000webhostapp.com/obtenerDatos.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    listarClientes(new String (responseBody));
                }


            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void listarClientes(String respuesta){
        final ArrayList<Contactos> lista = new ArrayList<Contactos>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for(int i=0; i<jsonArreglo.length();i++){
                Contactos c = new Contactos();
                c.setId_contacto(jsonArreglo.getJSONObject(i).getInt("id_contacto"));
                c.setNombre(jsonArreglo.getJSONObject(i).getString("nombre"));
                c.setTelefono(jsonArreglo.getJSONObject(i).getString("telefono"));
                lista.add(c);
            }
            ArrayAdapter <Contactos> a = new ArrayAdapter(this,android.R.layout.simple_list_item_1,lista);
            lvDatos.setAdapter(a);

            lvDatos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                   Contactos c = lista.get(position);
                   String url = "https://thermal-profile.000webhostapp.com/eliminar.php?id_contacto="+c.getId_contacto();
                   cliente.post(url, new AsyncHttpResponseHandler() {
                       @Override
                       public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                           if(statusCode == 200){
                               Toast.makeText(ActivityContactos.this, "Contacto eliminado", Toast.LENGTH_SHORT).show();
                               try {
                                   Thread.sleep(2000);
                               } catch (InterruptedException e) {
                                   e.printStackTrace();
                               }
                               obtenerClientes();
                           }

                       }

                       @Override
                       public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                       }
                   });

                    return true;
                }
            });

            lvDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Contactos c = lista.get(position);
                    StringBuffer b = new StringBuffer();
                    b.append("ID: " + c.getId_contacto() + "\n");
                    b.append("nombre: " + c.getNombre() + "\n");
                    b.append("numero: " + c.getTelefono());
                    AlertDialog.Builder a = new AlertDialog.Builder(ActivityContactos.this);
                    a.setCancelable(true);
                    a.setTitle("Detalle");
                    a.setMessage(b.toString());
                    a.show();
                }
            });


        }catch (Exception e1){
            e1.printStackTrace();
        }

    }







    private void seleccionarContacto(){
        Intent seleccContactoIntent=new Intent(Intent.ACTION_PICK, Uri.parse("contacto://contacts"));
        seleccContactoIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(seleccContactoIntent,PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       if (requestCode==PICK_CONTACT_REQUEST){
           if (resultCode==RESULT_OK){
               Uri uri=data.getData();

               Cursor cursor = getContentResolver().query(uri,null,null,null,null);
               if (cursor.moveToFirst()){
                   int columnaNombre=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                   int columnaNumero=cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                   String nombre = cursor.getString(columnaNombre);
                   String numero = cursor.getString(columnaNumero);
                   mNombre.setText(nombre);
                   mNumero.setText(numero);
               }
           }
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
