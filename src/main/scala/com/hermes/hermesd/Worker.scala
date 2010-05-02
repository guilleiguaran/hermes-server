package com.hermes.hermesd

import scala.actors.Actor
import scala.actors.Actor._
import java.util.ArrayList
import java.net.Socket
import java.io.DataOutputStream
import java.io.DataInputStream
import net.lag.logging.Logger

case class Inactive(worker: Worker)

class Worker(val id: Int, val dispatcher: Dispatcher) extends Actor{
	
	val log = Logger.get(getClass.getName)
	
    def act()
    {
		
        loop{
			
            react{
				
                case Connection(socket, id) =>
				
                    handle(socket)
                    socket.close()
                    dispatcher ! Inactive(this)
				
            }
			
        }
		
    }

    
    def handle(socket: Socket): Unit =
    {
        val os = socket.getOutputStream
        val writer = new DataOutputStream(os)

        val is = socket.getInputStream
        val reader = new DataInputStream(is)

        val input = reader.readLine()
        val msg = input.split(" ")(1).split("/")
        val action = msg(1)
        if(action == "ruta"){
            log.info("Action: ruta")
            val start = msg(2)
            val end = msg(3)
			/*
				Modify this segment to execute algorithm and get coordLats and coordLons for the algorithm
			*/
			var coordLats = new ArrayList[String]
			var coordLons = new ArrayList[String]
			coordLats.add(start.split("_")(0))
			coordLats.add(end.split("_")(0))
			coordLons.add(start.split("_")(1))
			coordLons.add(end.split("_")(1))			
			/*
				End Segment
			*/
			
			var response = "{\"coordinates\": ["
			
			for(i<-0 to coordLats.size()-2){
				response = response + "{\"lat\": \""+ coordLats.get(i) +"\", \"lon\": \""+ coordLons.get(i) +"\"},"
			}
			response = response + "{\"lat\": \""+ coordLats.get(coordLats.size()-1) +"\", \"lon\": \""+ coordLons.get(coordLats.size()-1) +"\"}"
			response = response + "]}"
			
            val httpresponse = composeHTTPResponse(response)
            writer.write(httpresponse.getBytes())
            writer.flush()
        }else{
            log.info("Unsupported action")
            
        }
    }
    def composeHTTPResponse(message: String): String = {
        var response = "HTTP/1.1 200 OK\n"
        response = response + "Content-type: text/html\n"
        response = response + "\r\n" + message
		log.info(response)
        response
    }	

}