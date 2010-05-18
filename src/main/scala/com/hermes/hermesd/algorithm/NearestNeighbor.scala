package com.hermes.hermesd.algorithm

import com.nodeta.scalandra.serializer.StringSerializer
import nodeta.scalandra._
import scala.collection.mutable.ArrayBuffer

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

      def find(entryPoint: Map[String,String], error: Double): String = {
	var Iterator = cassandra.ColumnFamily("Standard1").filter(s => Math.sqrt(Math.pow(s._2("Lat").toDouble - entryPoint("Lat").toDouble ,2) + 
        Math.pow(s._2("Lon").toDouble - entryPoint("Lon").toDouble ,2)) < error).asInstanceOf[ArrayBuffer[(String,
	com.nodeta.scalandra.map.StandardRecord[String,String,String])]]	
	var size = Iterator.size
	if(size == 1){
		Iterator(0)._1		
	}else{
		if(size == 0){
			find(entryPoint, error*1.3)	
		}else{
			var s = Iterator(0)
			var valorMenor = Math.sqrt(Math.pow(s._2("Lat").toDouble - entryPoint("Lat").toDouble ,2) + Math.pow(s._2("Lon").toDouble - entryPoint("Lon").toDouble ,2))
			for(i <- 1 to Iterator.size - 1){
				var current = Iterator(i)
				var currentVal = Math.sqrt(Math.pow(current._2("Lat").toDouble - entryPoint("Lat").toDouble ,2) + Math.pow(current._2("Lon").toDouble - entryPoint("Lon").toDouble ,2))
				if(currentVal < valorMenor){
					s = Iterator(i)
					valorMenor = currentVal
				}
			}
			return s._1
		}
	}
      }
}
