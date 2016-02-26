package com.lab4.ecobicis.entidades;

import java.util.ArrayList;

public class Estacion {
	
	private int idEstacion;
	private String nombre, estado;
	private double lat,lon;
	private ArrayList<Bicicleta> bicicletas;
	private int disponibles;
	
	public Estacion(){
		this.bicicletas = new ArrayList<Bicicleta>();
		this.disponibles=0;
	}
	
	public int getIdEstacion() {
		return idEstacion;
	}
	public void setIdEstacion(int idEstacion) {
		this.idEstacion = idEstacion;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}

	public ArrayList<Bicicleta> getBicicletas() {
		return bicicletas;
	}

	public void setBicicletas(ArrayList<Bicicleta> bicicletas) {
		this.bicicletas = bicicletas;
	}
	
	public void agregarBici(Bicicleta b){
		this.bicicletas.add(b);
	}

	public int getDisponibles() {
		return disponibles;
	}

	public void setDisponibles(int disponibles) {
		this.disponibles = disponibles;
	}
	
}
