//package com.hermes.hermesd.algorithm

import java.util.ArrayList
import scala.collection.mutable.ArrayBuffer

class Node(var Coordinates: List[Int]){

    var costsAdjacents = new ArrayList[Double]();
    var nodesAdjacents = new ArrayList[Node]()
    var gScore = 0.0
    var hScore = 0.0
    var fScore = 0.0
    var cameFrom = List(-1,-1)

    def aString(complet: Boolean): String = {
        if(complet == true){
            "(gScore, hScore, fScore) = (" + gScore + "," + hScore + "," + fScore + ") --- " +
            "(x,y) = (" + Coordinates(0) + "," + Coordinates(1) + ")"
        }else{
            "(" + Coordinates(0) + "," + Coordinates(1) + ")"    
        }
    }

    def addAdjacent(node: Node, cost: Double){
        costsAdjacents.add(cost)
        nodesAdjacents.add(node)
    }

}


class AStar(var minCost: Double, var nodes: ArrayBuffer[ArrayBuffer[Node]], var numStreets: Int, var numAvenues: Int){


    def heuristicStimateOfDistance(a: List[Int], b: List[Int]) = minCost * (Math.abs(a(0) - b(0)) + Math.abs(a(1) - b(1)))
   
    def buildPath(current: Node): String = {
        if (current.cameFrom != List(-1,-1)){
            //println(actual.aString(false))
            return buildPath(nodes(current.cameFrom(0))(current.cameFrom(1))) + current.aString(false)
        }else{
            //println(actual.aString(false))
            return current.aString(false)
        }   
    }
    
    def calculatePath(start: List[Int], goal: List[Int]): String = {
        var closedset = new ArrayList[Node]()
        var openset = new ArrayList[Node]()
        nodes(start(0))(start(1)).asInstanceOf[Node].gScore_=(0.0)
        nodes(start(0))(start(1)).asInstanceOf[Node].hScore_=(heuristicStimateOfDistance(start, goal))
        nodes(start(0))(start(1)).asInstanceOf[Node].fScore_=(heuristicStimateOfDistance(start, goal))
        openset.add(nodes(start(0))(start(1)))
        
        while(openset.isEmpty() == false){
            //println(openset.size)
            //println(closedset.size)
            //println("-----------------------")
            var x:Node = {
                var minNode = new Node(List(-1,-1))
                minNode.fScore_=(Double.MaxValue)
                for(i<- 0 to openset.size - 1 if openset.get(i).asInstanceOf[Node].fScore < minNode.fScore){
                    minNode = openset.get(i)
                }
                minNode
            }
            if(x.Coordinates == goal){
                return buildPath(nodes(goal(0))(goal(1)))
            }else{
                openset.remove(x)
                closedset.add(x)
                /*
                El nodo x debe tener un metodo que leyendo la informacion de la base de datos
                defina una coleccion de sus nodos adyacentes, y regrese ya sea una coleccion
                de los mismos o un iterador sobre ella.
                Todo el lio ese de la prelacion de las calles puede manejarse en el cuerpo de
                ese metodo

                Talvez sea buena idea definir una clase envoltorio que encapsule el nodo adyacente 
                y el costo para llegar a el, y devolver una coleccion (o su iterador) de instancias
                de esa clase
                */
                var i = x.nodesAdjacents.iterator();
                var j = x.costsAdjacents.iterator();
                while(i.hasNext()) {
                    var currentNode:Node = i.next()
                    var dist:Double = j.next()
                    if(closedset.contains(currentNode) == false){
                        var tgScore = x.gScore + dist
                        var tIsBetter = false
                        if(openset.contains(currentNode) == false){
                            openset.add(currentNode)
                            tIsBetter = true
                        }else{
                            if(tgScore < currentNode.gScore){
                                tIsBetter = true
                            }else{
                                tIsBetter = false
                            }
                        }
                        if(tIsBetter){
                            currentNode.cameFrom_=(List(x.Coordinates(0),x.Coordinates(1)))
                            currentNode.gScore_=(tgScore)
                            currentNode.hScore_=(heuristicStimateOfDistance(currentNode.Coordinates, goal))
                            currentNode.fScore_=(currentNode.gScore + currentNode.hScore)
                        }
                    }
                    

                }
            }
        }
        "No route"
    }
            
}
        
        
}


object Main {
    /**
     * @param args the command line arguments
     */
    def main(args: Array[String]) :Unit = {
        var numCalles = 100
        var numCarreras = 100
        var nodos = new ArrayBuffer[ArrayBuffer[Node]]()
        var minCost = 20.0
        for(x <- 0 to numCalles - 1){
            var fila = new ArrayBuffer[Node]()
            for(y <- 0 to numCarreras - 1){
                fila += new Node(List(x,y))
            }
            nodos += fila
        }
        for(x <- 0 to numCalles - 1 ; y <- 0 to numCarreras - 1){
            var a:Node = nodos(x)(y)
            if(x != 0){
                a.addAdjacent(nodos(x - 1)(y),Math.random*(100 - minCost) + minCost)
            }
            if(x != numCalles - 1){
                a.addAdjacent(nodos(x + 1)(y), Math.random*(100 - minCost) + minCost)
            }
            if(y != 0){
                a.addAdjacent(nodos(x)(y-1),Math.random*(100 - minCost) + minCost)
            }
            if(y != numCarreras - 1){
                a.addAdjacent(nodos(x)(y+1),Math.random*(100 - minCost) + minCost)
            }
        }
        var start = List((Math.random * numCalles).asInstanceOf[Int],(Math.random * numCarreras).asInstanceOf[Int] )
        var goal = List((Math.random * numCalles).asInstanceOf[Int],(Math.random * numCarreras).asInstanceOf[Int] )
        println("Inicio " +  nodos(start(0))(start(1)).aString(false))
        println("Meta " +nodos(goal(0))(goal(1)).aString(false))
        var A = new AStar(minCost, nodos, numCalles, numCarreras)
        var output = A.calculatePath(start, goal)
        println(output)
              
  
    }

}


