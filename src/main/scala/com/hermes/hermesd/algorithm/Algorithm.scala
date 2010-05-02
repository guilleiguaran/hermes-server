package com.hermes.hermesd.algorithm

import java.util.ArrayList

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