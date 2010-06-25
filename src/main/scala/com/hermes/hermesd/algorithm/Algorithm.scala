package com.hermes.hermesd.algorithm

import com.nodeta.scalandra.serializer.StringSerializer
import com.nodeta.scalandra._
import scala.collection.mutable.HashMap 



object DataConnection{
    val serialization = new Serialization(
        StringSerializer,
        StringSerializer,
        StringSerializer
    )
    val cassandra = new Client(
        Connection("127.0.0.1", 9160),
        "Hermes",
        serialization,
        ConsistencyLevels.one
    )
}

class Dijkstra(){
    def getAllNodes(): String = {
	var size = DataConnection.cassandra.ColumnFamily("Intersecciones")("DB")("0").toInt
	var Salida = ""
	for(i<-1 to size){
		var id = DataConnection.cassandra.ColumnFamily("Intersecciones")("DB")(i.toString)
		Salida = Salida + ";" + DataConnection.cassandra.ColumnFamily("Coordenadas")(id)("Lat") + "_" +  DataConnection.cassandra.ColumnFamily("Coordenadas")(id)("Lon")   

	}
	Salida
    }
    
    def algorithm(start: Map[String, String], goal: Map[String, String], hora: Int): String = {
        var startId = NearestNeighbor.find(start,0.0001)
	var endId = NearestNeighbor.find(goal,0.0001)

//	println("startId:" + startId)
//	println("endId:" + endId)

	var distancia = new HashMap[String, Double]()
	var novistos = Set[String]()
	var padre = new HashMap[String, String]()
	var size = DataConnection.cassandra.ColumnFamily("Intersecciones")("DB")("0").toInt
	for(i<-1 to size){
	   var id = DataConnection.cassandra.ColumnFamily("Intersecciones")("DB")(i.toString)
	   novistos += id
	   if(DataConnection.cassandra.SuperColumnFamily("Trafico")(startId)(id).size != 0){
		padre(id) = startId
		distancia(id) = DataConnection.cassandra.SuperColumnFamily("Trafico")(startId)(id)(hora.toString).toDouble
	   }else{
	   	padre(id) = "NONE"
		distancia(id) = Math.MAX_DOUBLE
	   }	       	
	}
	distancia(startId) = 0.0
	novistos -= startId
	while(novistos.isEmpty == false){
		//Console.readLine()
//		println("novistos.size: " + novistos.size)
		var idS = novistos.toArray
		
		var idMenor = idS(0)
				
		for(i<-1 to idS.size - 1){
			if(distancia(idMenor) > distancia(idS(i))){
				idMenor = idS(i)
			}
		}
//		println("idMenor:" + idMenor)
//		println("distancia(idMenor):" + distancia(idMenor))
		novistos -= idMenor
		var size = 0	
		if(DataConnection.cassandra.ColumnFamily("Vecinos")(idMenor).size != 0){
			size = DataConnection.cassandra.ColumnFamily("Vecinos")(idMenor)("0").toInt	
		}
		for(i<-1 to size){
			var vecino = DataConnection.cassandra.ColumnFamily("Vecinos")(idMenor)(i.toString)
			var peso = DataConnection.cassandra.SuperColumnFamily("Trafico")(idMenor)(vecino)(hora.toString).toDouble
			if(distancia(vecino) > distancia(idMenor) + peso){
				distancia(vecino) = distancia(idMenor) + peso
				padre(vecino) = idMenor
			}
			
		}
		
	}
	
	var salida = ""
	//DataConnection.cassandra.ColumnFamily("Coordenadas")(dbId)("Lat") + "_" +  DataConnection.cassandra.ColumnFamily("Coordenadas")(dbId)("Lon")  
	var current = endId
	while(current != "NONE"){
	//	println(current)
		salida = salida + ";" + DataConnection.cassandra.ColumnFamily("Coordenadas")(current)("Lat") + "_" + DataConnection.cassandra.ColumnFamily("Coordenadas")(current)("Lon")  
		current = padre(current)
	}
	//println(salida)
	return salida
    } 
    

}
