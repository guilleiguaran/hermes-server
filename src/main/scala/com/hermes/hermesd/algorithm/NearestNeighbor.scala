package com.hermes.hermesd.algorithm

import scala.collection.mutable.ArrayBuffer
import com.nodeta.scalandra.serializer.StringSerializer
import com.nodeta.scalandra.map.StandardRecord
import com.nodeta.scalandra._


object NearestNeighbor{
	
	val serialization = new Serialization(
		StringSerializer,
		StringSerializer,
		StringSerializer
	)

    val cassandra = new Client(
        Connection("127.0.0.1", 9160),
        "Keyspace1",
        serialization,
        ConsistencyLevels.one
     )

     //NearestNeighbor.find(Map("Lat"->"11.00498","Lon"->"-74.80806"), 0.000001)
     //Regresa los nodos de la bd que esten a una distancia menor de 0.000001 del entryPoint
     //Si no existe tal metodo, el metodo se llamara a si mismo con un error mayor 
     //Entre mayor sea el valor del error, menos nodos regresara el metodo
     //Entre mayor sea el valor del error, mas tardara el metodo en ejecutarse	
     //Se regresa un ArrayBuffer de Tuplas (Indice del nodo en la base de datos, Map con Lat y Lon del nodo)

	def find(entryPoint: Map[String,String], error: Double): ArrayBuffer[(String, StandardRecord[String,String,String])] = {

		var Iterator = cassandra.ColumnFamily("Standard1").filter(s => Math.sqrt(Math.pow(s._2("Lat").toDouble - entryPoint("Lat").toDouble ,2) + 
        		Math.pow(s._2("Lon").toDouble - entryPoint("Lon").toDouble ,2)) < error).asInstanceOf[ArrayBuffer[(String,StandardRecord[String,String,String])]]	
		var size = Iterator.size
		if(size > 0){
			Iterator	
		}
		else{
			find(entryPoint, error*2)	
		}
  	}
}
