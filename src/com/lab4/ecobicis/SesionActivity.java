package com.lab4.ecobicis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.internal.di;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lab4.ecobicis.entidades.Bicicleta;
import com.lab4.ecobicis.entidades.Cliente;
import com.lab4.ecobicis.entidades.Estacion;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SesionActivity extends FragmentActivity {
	
	private final String dburl="jdbc:mysql://192.168.1.2:3306/ecobici";
	//private final String dburl="jdbc:mysql://10.130.2.183:3306/ecobicis";
	private final String dbuser="root";
	private final String dbpass="masterkey";
	private boolean sesion = true, consulta=false;
	String stsql = "";
	public Intent intent;
	private Cliente cliente;
	private GoogleMap mapa;
	private ArrayList<Estacion> estaciones;
	private ArrayList<Bicicleta> bicicletas;
	private int tipoConsulta=0;
	private int exitoConsulta=0;
	private String msjReserva;
	private Bicicleta b;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Obtener datos del cliente
		intent=getIntent();
		cliente = new Cliente();
		cliente.setNombre(intent.getStringExtra(MainActivity.EXTRA_NOMBRE));
		cliente.setApellido(intent.getStringExtra(MainActivity.EXTRA_APELLIDO));
		cliente.setCedula(intent.getStringExtra(MainActivity.EXTRA_CEDULA));
		cliente.setTarjeta(intent.getStringExtra(MainActivity.EXTRA_TARJETA));
		cliente.setTelefono(intent.getStringExtra(MainActivity.EXTRA_TELEFONO));
		cliente.setUsuario(intent.getStringExtra(MainActivity.EXTRA_USUARIO));
		cliente.setPass(intent.getStringExtra(MainActivity.EXTRA_PASS));
		cliente.setIdcliente(intent.getIntExtra(MainActivity.EXTRA_IDCLIENTE, 0));
		cliente.setSaldo(intent.getLongExtra(MainActivity.EXTRA_SALDO, 0));
		
		setContentView(R.layout.activity_sesion);
		
		TextView msjBienvenida = (TextView) findViewById(R.id.msjBienvenido);
		msjBienvenida.setText("Bienvenido "+cliente.getNombre());
		
		sqlEstaciones.start();
		listarEstaciones();
		listarBicicletas();
		
		setPaneles();
		
		setListeners();
		
		cargarDatos();
		
		TextView textSaldo = (TextView) findViewById(R.id.saldoActual);
		textSaldo.setText("Su saldo actual es: "+cliente.getSaldo());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sesion, menu);
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
	
	public void cargarLista(){
		ArrayList<String> estacionesDisponibles = new ArrayList<String>();
		for(Estacion e : estaciones){
			if(e.getDisponibles()>0) estacionesDisponibles.add(e.getNombre());
		}
		ListView lista = (ListView) findViewById(R.id.lista);
		
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, 
				android.R.layout.simple_list_item_1,estacionesDisponibles);
		
		lista.setAdapter(adaptador);
		
		lista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				ListAdapter la = (ListAdapter) arg0.getAdapter();
				System.out.println(la.getItem(arg2).toString());
				for(Estacion e: estaciones){
					if(e.getNombre().equals(la.getItem(arg2).toString())){
						for(Bicicleta bi: e.getBicicletas()){
							if(bi.getEstado().equals("inactivo")) {
								b=bi;
								break;
							}
						}
					}
				}
				msjReserva="No se pudo reservar";
				if(b!=null){
					stsql="UPDATE BICICLETA SET ESTADO='reservada', IDCLIENTE=? WHERE IDBICICLETA=?";
					exitoConsulta=0;
					tipoConsulta=4;
					consulta=false;
					
					do{
						
					} while(!consulta);
				}
				Toast.makeText(arg1.getContext(), msjReserva, Toast.LENGTH_LONG).show();
			}
			
		});
	}
	
	public void cargarSaldo(Long s, Context c){
		cliente.setSaldo(cliente.getSaldo()+s);
		stsql="UPDATE CLIENTE SET SALDO=? WHERE IDCLIENTE=?";
		exitoConsulta=0;
		tipoConsulta=3;
		consulta=false;
		String msjSaldo="No se pudo realizar la recarga";
		//Esperar a que termine la consulta
		do{
			
		} while(!consulta);
		
		if(exitoConsulta>0) msjSaldo="Recarga realizada";
		else cliente.setSaldo(cliente.getSaldo()-s);
		
		Toast.makeText(c, msjSaldo, Toast.LENGTH_LONG).show();
		
		TextView textSaldo = (TextView) findViewById(R.id.saldoActual);
		textSaldo.setText("Su saldo actual es: "+cliente.getSaldo());
	}
	
	
	
	public void setUpMap(){	
		SupportMapFragment tmp;
		try{
			if(mapa == null){
				tmp = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
				mapa = (tmp).getMap();
				if(mapa!=null){
					mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					mapa.setMyLocationEnabled(true);
					LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
					Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					System.out.println(location.getLatitude());
					mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 14.0f));
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void listarEstaciones(){
		estaciones = new ArrayList<Estacion>();
		stsql="SELECT * FROM ESTACION";
		tipoConsulta=1;
		consulta=false;
		//if(!sqlEstaciones.isAlive()) sqlEstaciones.start();
		
		//Esperar a que termine la consulta
		do{
			
		} while(!consulta);
	}
	
	public void listarBicicletas(){
		bicicletas = new ArrayList<Bicicleta>();
		stsql="SELECT * FROM BICICLETA";
		tipoConsulta=2;
		consulta=false;
		//if(!sqlEstaciones.isAlive()) sqlEstaciones.start();
		
		//Esperar a que termine la consulta
		do{
			
		} while(!consulta);
		
		int idTmp;
		for(Bicicleta bici :  bicicletas){
			idTmp=bici.getIdEstacion();
			for(Estacion e : estaciones){
				if(e.getIdEstacion()==idTmp){
					e.agregarBici(bici);
					if(bici.getEstado().equals("inactivo")) e.setDisponibles(e.getDisponibles()+1);
				}
			}
		}
	}
	
	public void setPaneles(){
		
		Button btnMapa = (Button) findViewById(R.id.btnMapa);
		Button btnReserva = (Button) findViewById(R.id.btnReserva);
		Button btnSaldo = (Button) findViewById(R.id.btnSaldo);
		Button btnDatos = (Button) findViewById(R.id.btnDatos);
		
		View panelMapa = findViewById(R.id.panelMapa);
		panelMapa.setVisibility(View.GONE);
		
		View panelReserva = findViewById(R.id.panelReserva);
		panelReserva.setVisibility(View.GONE);
		
		View panelSaldo = findViewById(R.id.panelSaldo);
		panelSaldo.setVisibility(View.GONE);
		
		View panelDatos = findViewById(R.id.panelDatos);
		panelDatos.setVisibility(View.GONE);
		
		btnMapa.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				
				setUpMap();
				listarEstaciones();
				listarBicicletas();
				crearMarcadores();
				
				View panelMapa = findViewById(R.id.panelMapa);
				if(panelMapa.getVisibility()==View.VISIBLE) panelMapa.setVisibility(View.GONE);
				else panelMapa.setVisibility(View.VISIBLE);
				
			}
		});
		
		btnReserva.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				
				listarEstaciones();
				listarBicicletas();
				
				View panelReserva = findViewById(R.id.panelReserva);
				cargarLista();
				if(panelReserva.getVisibility()==View.VISIBLE) panelReserva.setVisibility(View.GONE);
				else panelReserva.setVisibility(View.VISIBLE);
				
			}
		});
		
		btnSaldo.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				
				View panelSaldo = findViewById(R.id.panelSaldo);
				if(panelSaldo.getVisibility()==View.VISIBLE) panelSaldo.setVisibility(View.GONE);
				else panelSaldo.setVisibility(View.VISIBLE);
				
			}
		});
		
		btnDatos.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				
				View panelDatos = findViewById(R.id.panelDatos);
				if(panelDatos.getVisibility()==View.VISIBLE) panelDatos.setVisibility(View.GONE);
				else panelDatos.setVisibility(View.VISIBLE);
				
			}
		});
	}
	
	public void cargarDatos(){
		
		EditText textoNombre = (EditText) findViewById(R.id.editNombre);
		textoNombre.setText(cliente.getNombre());
		
		EditText textoApellido = (EditText) findViewById(R.id.editApellido);
		textoApellido.setText(cliente.getApellido());
		
		EditText textoTelefono = (EditText) findViewById(R.id.editTelefono);
		textoTelefono.setText(cliente.getTelefono());
	}
	
	public void setListeners(){
		
		Button btn5K = (Button) findViewById(R.id.btn5k);
		Button btn10K = (Button) findViewById(R.id.btn10k);
		Button btn20K = (Button) findViewById(R.id.btn20k);
		Button btnEditar = (Button) findViewById(R.id.btnEditar);
		Button btnCancelar = (Button) findViewById(R.id.btnCancelar);
		Button btnEliminar = (Button) findViewById(R.id.btnEliminar);
		
		btn5K.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				long l=5000;
				cargarSaldo(l, v.getContext());
			}
		});
		
		btn10K.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				long l=10000;
				cargarSaldo(l, v.getContext());
			}
		});
		
		btn20K.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				long l=20000;
				cargarSaldo(l, v.getContext());
			}
		});
		
		btnEditar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				EditText textoNombre = (EditText) findViewById(R.id.editNombre);
				cliente.setNombre(textoNombre.getText().toString());
				
				EditText textoApellido = (EditText) findViewById(R.id.editApellido);
				cliente.setApellido(textoApellido.getText().toString());
				
				EditText textoTelefono = (EditText) findViewById(R.id.editTelefono);
				cliente.setTelefono(textoTelefono.getText().toString());
				
				stsql="UPDATE CLIENTE SET NOMBRE=?, APELLIDO=?, TELEFONO=? WHERE IDCLIENTE=?";
				exitoConsulta=0;
				tipoConsulta=5;
				consulta=false;
				String msjEditar="No se pudo editar";

				do{
					
				} while(!consulta);
				
				if(exitoConsulta>0) msjEditar="Datos cambiados";
				
				Toast.makeText(v.getContext(), msjEditar, Toast.LENGTH_LONG).show();
				
				TextView msjBienvenida = (TextView) findViewById(R.id.msjBienvenido);
				msjBienvenida.setText("Bienvenido "+cliente.getNombre());
			}
		});
		
		btnCancelar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				stsql="UPDATE BICICLETA SET ESTADO='inactivo', IDCLIENTE=? WHERE IDCLIENTE=?";
				exitoConsulta=0;
				tipoConsulta=6;
				consulta=false;
				String msjCancelar="No se pudo remover la reserva";

				do{
					
				} while(!consulta);
				
				if(exitoConsulta>0) msjCancelar="Reserva removida";
				
				Toast.makeText(v.getContext(), msjCancelar, Toast.LENGTH_LONG).show();
			}
		});
		
		btnEliminar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder1 = new AlertDialog.Builder(v.getContext());
	            builder1.setMessage("Seguro que desea cancelar su suscripcion?");
	            builder1.setCancelable(true);
	            builder1.setPositiveButton("Si",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                	exitoConsulta=0;
	    				tipoConsulta=7;
	    				consulta=false;

	    				do{  
	    					
	    				} while(!consulta);
	    				System.out.println("1");
	    				if(exitoConsulta>0){
	    					SesionActivity.this.finish();
	    				}
	    				else{
	    					//Toast.makeText(dialog.getContext(), "No se pudo cancelar tu solicitud", Toast.LENGTH_LONG).show();
	    					dialog.cancel();
	    				}
	    				

	                    
	                }
	            });
	            builder1.setNegativeButton("No",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });

	            AlertDialog alert11 = builder1.create();
	            alert11.show();
				
			}
		});
	}
	
	public void crearMarcadores(){
		mapa.clear();
		Marker marcador;
		float color;
		int numeroDisponibles=0;
		String descripcion="";
		for(Estacion e : estaciones){
			if(e.getEstado().equals("activo")) color = BitmapDescriptorFactory.HUE_GREEN;
			else color = BitmapDescriptorFactory.HUE_RED;
			descripcion="";
			descripcion="Bicicletas disponibles: "+String.valueOf(e.getDisponibles());
			marcador = mapa.addMarker(new MarkerOptions()
			.position(new LatLng(e.getLat(), e.getLon())).title(e.getNombre())
			.snippet(descripcion)
			.icon(BitmapDescriptorFactory.defaultMarker(color)));
		}
	}
	
	Thread sqlEstaciones = new Thread(){
		public void run(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// "jdbc:mysql://IP:PUERTO/DB", "USER", "PASSWORD");
			// Si estás utilizando el emulador de android y tenes el mysql en tu misma PC no utilizar 127.0.0.1 o localhost como IP, utilizar 10.0.2.2
			Connection conn = DriverManager.getConnection(dburl, dbuser, dbpass);
			//En el stsql se puede agregar cualquier consulta SQL deseada.
			Statement st;
			ResultSet rs;
			PreparedStatement pst;
			while(sesion){
				if(tipoConsulta==1){
					consulta=false;
					st = conn.createStatement();
					rs = st.executeQuery(stsql);
					Estacion estacion=new Estacion();
					while(rs.next()){
						estacion.setNombre(rs.getString("NOMBRE"));
						estacion.setEstado(rs.getString("ESTADO"));
						estacion.setIdEstacion(rs.getInt("IDESTACION"));
						estacion.setLon(rs.getDouble("LONGITUD"));
						estacion.setLat(rs.getDouble("LATITUD"));
						estaciones.add(estacion);
						estacion=new Estacion();
					}
					consulta=true;
					stsql="";
					tipoConsulta=0;
				}
				else if(tipoConsulta==2){
					consulta=false;
					st = conn.createStatement();
					rs = st.executeQuery(stsql);
					Bicicleta bici=new Bicicleta();
					System.out.println(stsql);
					while(rs.next()){
						bici.setEstado(rs.getString("ESTADO"));
						bici.setIdBicicleta(rs.getInt("IDBICICLETA"));
						bici.setIdEstacion(rs.getInt("IDESTACION"));
						bici.setIdCliente(rs.getInt("IDCLIENTE"));
						bicicletas.add(bici);
						bici=new Bicicleta();
					}
					consulta=true;
					stsql="";
					tipoConsulta=0;
				}
				else if(tipoConsulta==3){
					//Actualizar saldo
					consulta=false;
					pst = conn.prepareStatement(stsql);
					pst.setLong(1, cliente.getSaldo());
					pst.setInt(2, cliente.getIdcliente());
					exitoConsulta=pst.executeUpdate();
					consulta=true;
					stsql="";
					tipoConsulta=0;
				}
				else if(tipoConsulta==4){
					//Verifica si no tiene bicicleta o reserva
					consulta=false;
					pst = conn.prepareStatement("SELECT * FROM BICICLETA WHERE IDCLIENTE=?");
					pst.setInt(1, cliente.getIdcliente());
					rs=pst.executeQuery();
					if(rs.last()){
						msjReserva="Usted ya tiene una reserva en curso";
					}
					else{
						//Realiza reserva
						pst = conn.prepareStatement(stsql);
						pst.setInt(1, cliente.getIdcliente());
						pst.setInt(2, b.getIdBicicleta());
						exitoConsulta=pst.executeUpdate();
						if(exitoConsulta>0) msjReserva="Reserva realizada";
					}
					consulta=true;
					stsql="";
					tipoConsulta=0;
				}
				else if(tipoConsulta==5){
					//Editar los datos de usuario
					consulta=false;
					pst = conn.prepareStatement(stsql);
					pst.setString(1, cliente.getNombre());
					pst.setString(2, cliente.getApellido());
					pst.setString(3, cliente.getTelefono());
					pst.setInt(4, cliente.getIdcliente());
					exitoConsulta=pst.executeUpdate();
					consulta=true;
					stsql="";
					tipoConsulta=0;
				}
				else if(tipoConsulta==6){
					//Elimina la reserva del ususario
					consulta=false;
					pst = conn.prepareStatement(stsql);
					pst.setNull(1, java.sql.Types.INTEGER);
					pst.setInt(2, cliente.getIdcliente());
					exitoConsulta=pst.executeUpdate();
					consulta=true;
					stsql="";
					tipoConsulta=0;
				}
				else if(tipoConsulta==7){
					//Eliminar usuario (Cancelar suscribcion)
					consulta=false;
					stsql="DELETE FROM CLIENTE WHERE IDCLIENTE=?";
					pst = conn.prepareStatement(stsql);
					pst.setInt(1, cliente.getIdcliente());
					exitoConsulta=pst.executeUpdate();
					consulta=true;
					stsql="";
					tipoConsulta=0;
				}
			}
			conn.close();
			} catch (SQLException se) {
			 System.out.println("oops! No se puede conectar. Error: " + se.toString());
			 consulta=true;
			} catch (ClassNotFoundException e) {
			 System.out.println("oops! No se encuentra la clase. Error: " + e.getMessage());
			 consulta=true;
			}
	}
		
	};
}
