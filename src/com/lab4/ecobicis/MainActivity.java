package com.lab4.ecobicis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lab4.ecobicis.entidades.Cliente;
import com.mysql.jdbc.Driver;

public class MainActivity extends FragmentActivity {
	
	//private final String dburl="jdbc:mysql://10.130.2.183:3306/ecobicis";
	private final String dburl="jdbc:mysql://192.168.1.2:3306/ecobici";
	private final String dbuser="root";
	private final String dbpass="masterkey";
	String stsql = "";
	private Cliente cliente;
	private boolean sesion = true, consulta=false;
	public final static String EXTRA_NOMBRE="com.lab4.ecobicis.NOMBRE";
	public final static String EXTRA_APELLIDO="com.lab4.ecobicis.APELLIDO";
	public final static String EXTRA_CEDULA="com.lab4.ecobicis.CEDULA";
	public final static String EXTRA_TARJETA="com.lab4.ecobicis.TARJETA";
	public final static String EXTRA_USUARIO="com.lab4.ecobicis.USUARIO";
	public final static String EXTRA_PASS="com.lab4.ecobicis.PASS";
	public final static String EXTRA_TELEFONO="com.lab4.ecobicis.TELEFONO";
	public final static String EXTRA_IDCLIENTE="com.lab4.ecobicis.IDCLIENTE";
	public final static String EXTRA_SALDO="com.lab4.ecobicis.SALDO";
	public final static String EXTRA_MENSAJEERROR="com.lab4.ecobicis.MENSAJEERROR";
	private boolean errorBD = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void login(View v){
		//ProgressDialog dialogo = ProgressDialog.show(MainActivity.this, 
		//		"Porfavor espere...","Ingresando al sistema...", true, true);
		EditText usufield = (EditText) findViewById(R.id.usernameField);
		EditText passfield = (EditText) findViewById(R.id.passField);
		
		stsql="SELECT * FROM CLIENTE WHERE USUARIO='"+usufield.getText()+"'";
		cliente = new Cliente();
		
		//Iniciar el hilo de consultas en caso de ser la primera vez
		if(!sqlThread.isAlive()) sqlThread.start();
		
		//Esperar a que termine la consulta
		
		do{
			
		} while(!consulta);
		
		if(cliente.getPass().equals(passfield.getText().toString())){
			System.out.println("Inicio de sesion correcto");
			
			//Intent que inicia la proxima Activity
			Intent intent = new Intent(this, SesionActivity.class);
			
			//Pasar los datos del cliente a la prox activity
			intent.putExtra(EXTRA_NOMBRE, cliente.getNombre());
			intent.putExtra(EXTRA_APELLIDO, cliente.getApellido());
			intent.putExtra(EXTRA_CEDULA, cliente.getCedula());
			intent.putExtra(EXTRA_TARJETA, cliente.getTarjeta());
			intent.putExtra(EXTRA_USUARIO, cliente.getUsuario());
			intent.putExtra(EXTRA_PASS, cliente.getPass());
			intent.putExtra(EXTRA_TELEFONO, cliente.getTelefono());
			intent.putExtra(EXTRA_IDCLIENTE, cliente.getIdcliente());
			intent.putExtra(EXTRA_SALDO, cliente.getSaldo());
			startActivity(intent);
		}
		else{
			System.out.println("Clave erronea");
			String msjError="Verifica tus datos";
			if(errorBD){
				msjError="No te puedes conectar ahora con el servidor. Intenta mas tarde";
			}
			Toast.makeText(v.getContext(), msjError, Toast.LENGTH_LONG).show();
		}
	}
	
	Thread sqlThread = new Thread(){
	public void run(){
	try {
		errorBD=false;
		Class.forName("com.mysql.jdbc.Driver");
		// "jdbc:mysql://IP:PUERTO/DB", "USER", "PASSWORD");
		// Si estás utilizando el emulador de android y tenes el mysql en tu misma PC no utilizar 127.0.0.1 o localhost como IP, utilizar 10.0.2.2
		Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass);
		//En el stsql se puede agregar cualquier consulta SQL deseada.
		Statement st;
		ResultSet rs;
		while(sesion){
			if(stsql!=""){
				errorBD = false;
				consulta=false;
				st = conn.createStatement();
				rs = st.executeQuery(stsql);
				cliente = new Cliente();
				while(rs.next()){
					cliente.setNombre(rs.getString("NOMBRE"));
					cliente.setApellido(rs.getString("APELLIDO"));
					cliente.setCedula(rs.getString("CEDULA"));
					cliente.setTarjeta(rs.getString("TARJETA"));
					cliente.setUsuario(rs.getString("USUARIO"));
					cliente.setPass(rs.getString("PASS"));
					cliente.setTelefono(rs.getString("TELEFONO"));
					cliente.setIdcliente(rs.getInt("IDCLIENTE"));
					cliente.setSaldo(rs.getLong("SALDO"));
					System.out.println(cliente.getApellido());
				}
				consulta=true;
				stsql="";
			}
		}
		conn.close();
		} catch (SQLException se) {
		 System.out.println("oops! No se puede conectar. Error: " + se.toString());
		 consulta=true;
		 errorBD = true;
		} catch (ClassNotFoundException e) {
		 System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
		 consulta=true;
		 errorBD = true;
		}
}
	
};
}

