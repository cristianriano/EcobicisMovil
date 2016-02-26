package com.lab4.ecobicis.entidades;

public class Bicicleta {
	
	private int idBicicleta, idCliente, idEstacion;
	private String estado;
	
	public int getIdBicicleta() {
		return idBicicleta;
	}
	public void setIdBicicleta(int idBicicleta) {
		this.idBicicleta = idBicicleta;
	}
	public int getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}
	public int getIdEstacion() {
		return idEstacion;
	}
	public void setIdEstacion(int idEstacion) {
		this.idEstacion = idEstacion;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
}
