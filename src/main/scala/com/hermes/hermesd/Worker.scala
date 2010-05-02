package com.hermes.hermesd

import scala.actors.Actor
import scala.actors.Actor._
import com.hermes.hermesd.Connection
import com.hermes.hermesd.Dispatcher
import java.net.Socket
import java.io.DataOutputStream
import java.io.DataInputStream

case class Inactive(worker: Worker)

class Worker(val id: Int, val dispatcher: Dispatcher) extends Actor{
	
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

        val entrada = reader.readLine()
        val msj = entrada.split(" ")(1).split("/")
        val accion = msj(1)
        if(accion == "ruta"){
            println("Esta pidiendo una ruta")
            val inicio = msj(2)
            val fin = msj(3)
            val httpresponse = composeHTTPResponse(inicio + " " + fin)
            writer.write(httpresponse.getBytes())
            writer.flush()
        }else{
            println("Accion no soportada")
            
        }
    }
    def composeHTTPResponse(mensaje: String): String = {
        var response = "HTTP/1.1 200 OK\n"
        response = response + "Content-type: text/html\n"
        response = response + "\r\n" + mensaje
        response
    }	

}