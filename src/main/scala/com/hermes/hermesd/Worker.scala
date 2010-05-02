package com.hermes.hermesd

import scala.actors.Actor
import scala.actors.Actor._
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
            val httpresponse = composeHTTPResponse(start + " " + end)
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