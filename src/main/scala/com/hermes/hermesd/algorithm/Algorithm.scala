package com.hermes.hermesd.algorithm

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap 
import java.util.ArrayList
import java.lang.Comparable
import java.util.PriorityQueue
import com.nodeta.scalandra.serializer.StringSerializer
import com.nodeta.scalandra._


object DataConnection{
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
}


class Node(var dbId: String) extends Comparable[Node]{
    var gScore = 0.0
    var hScore = 0.0
    var fScore = 0.0
    var cameFrom = "-1"

    def compareTo(other: Node): Int = { this.fScore.compare(other.fScore) }
    
    def getNeighbors(): Iterator[String] = { DataConnection.cassandra.SuperColumnFamily("Super1")(dbId).keys }
    
    def aString(complete: Boolean): String = {
        if(complete == true){
            "(gScore, hScore, fScore) = (" + gScore + "," + hScore + "," + fScore + ") --- " +
            aString(false)
        }
		else {
           DataConnection.cassandra.ColumnFamily("Standard1")(dbId)("Lat") + "_" +  DataConnection.cassandra.ColumnFamily("Standard1")(dbId)("Lon")   
        }
    }
}

/*
var t = DataConnection.cassandra.ColumnFamily("Standard1").elements
	var Salida = ""
	//while(t.hasNext){
	//	Salida = Salida + ";" + (new Node(t.next)).aString(false)
	//}
	while(t.hasNext){
		Salida = Salida + ";" +  (new Node(t.next._1)).aString(false)
	}
	Salida
*/
class AStar(var minCost: Double){
    def getAllNodes(): String = {
	var t = DataConnection.cassandra.ColumnFamily("Standard1").toArray
	var Salida = ""
	//while(t.hasNext){
	//	Salida = Salida + ";" + (new Node(t.next)).aString(false)
	//}
	for(i<-0 to t.size -1){
		Salida = Salida + ";" +  (new Node(t(i)._1)).aString(false)
	}
	Salida
	
    } 

    //def heuristicStimateOfDistance(a: List[Int], b: List[Int]) = minCost * (Math.abs(a(0) - b(0)) + Math.abs(a(1) - b(1)))
    def heuristicStimateOfDistance(aI : String, bI: String): Double = {

/*		var a = DataConnection.cassandra.ColumnFamily("Standard1")(aI)
		var b = DataConnection.cassandra.ColumnFamily("Standard1")(bI)	
		var r = minCost * (Math.abs(a("Lat").toDouble - b("Lat").toDouble) + Math.abs(a("Lon").toDouble - b("Lon").toDouble))
		r
*/
		val f = DataConnection.cassandra.ColumnFamily("Standard1")(aI)
		val t = DataConnection.cassandra.ColumnFamily("Standard1")(bI)
		
		var dlong = f("Lon").toDouble - t("Lon").toDouble

		var degtorad = 0.01745329
		var radtodeg = 57.29577951

		var dvalue = (Math.sin(f("Lat").toDouble * degtorad))*(Math.sin(t("Lat").toDouble * degtorad)) + 
		(Math.cos(f("Lat").toDouble * degtorad) * Math.cos(t("Lat").toDouble * degtorad) * Math.cos(dlong * degtorad))

		var dd = Math.acos(dvalue) * radtodeg

		var km = dd*111.302 
		var horas = km/30.0
		horas*3600
    }

    def buildPath(current: Node, closedset: HashMap[String, Node]): String = {
	println(current.dbId)	
	if (current.cameFrom != "-1"){
			
			return buildPath(closedset(current.cameFrom), closedset)  + ";" +current.aString(false)
        }
	else {
            return current.aString(false)
        }   
    }
    
    def calculatePath(start: Map[String, String], goal: Map[String, String], hora: Int): String = {
        var nodes = new HashMap[String, Node]()

	var closedset = new HashMap[String, Node]()
 
	var openset = new PriorityQueue[Node]()
        var startId = NearestNeighbor.find(start,0.0001)
	var endId = NearestNeighbor.find(goal,0.0001)

//	println("Meta :" + endId)
//	println("Inicio:" + startId)
	
	nodes(startId) = new Node(startId)
        openset.add(nodes(startId))
       
        while(openset.isEmpty() == false){

            var x:Node = openset.poll()
//	    println("En el nodo :" + x.dbId)
    	    if(x.dbId == endId){
                //return buildPath(x,closedset)
		if(closedset.contains(x.dbId)){
		 	if(closedset(x.dbId).gScore > x.gScore){
				closedset(x.dbId) = x
			}
		}else{
			 closedset(x.dbId) = x
		}
//		println("Llego, a traves de " + x.cameFrom)		
//	    	Console.readLine()
            }
	    else {

                closedset(x.dbId) = x

                var i = x.getNeighbors();
//		println("Expandiendo")
                //var j = x.costsAdjacents.iterator();
                while(i.hasNext) {
	            var currentId = i.next
		    var currentNode:Node = {
	  		    if(nodes.contains(currentId)){
				nodes(currentId)
			    }else{
				nodes(currentId) = new Node(currentId)
				nodes(currentId)
			    }
		    }

//                    var currentNode:Node = new Node(i.next)
//		    println("Hijo " + currentNode.dbId)
                    var dist:Double = DataConnection.cassandra.SuperColumnFamily("Super1")(x.dbId.toString)(currentNode.dbId.toString)(hora.toString).toDouble

                    if(closedset.contains(currentNode.dbId) == false){			
                    	var tgScore = x.gScore + dist
                        var tIsBetter = false
		        
                        if(openset.contains(currentNode) == false){
                            openset.add(currentNode)
                            tIsBetter = true
//			    println("Primera vez")
                        }
			else{
//			    println("Ya habia llegado")
                            if(tgScore < currentNode.gScore){
//				println("Este es mejor")
                                tIsBetter = true
                            }
			    else {
//				println("El anterior es mejor")
                                tIsBetter = false
                            }
//			    println(tgScore)
//			    println(currentNode.gScore)
                        }
//	    		    Console.readLine()

                        if(tIsBetter){
                            currentNode.cameFrom_=(x.dbId)
                            currentNode.gScore_=(tgScore)
                            currentNode.hScore_=(heuristicStimateOfDistance(endId, currentNode.dbId))
                            currentNode.fScore_=(currentNode.gScore + currentNode.hScore)
                        }
                    }

                }
            }
        }
	return buildPath(closedset(endId),closedset)
    }
}

