package com.hermes.hermesd

import scala.actors.Actor
import scala.actors.Actor._
import com.hermes.hermesd.Connection
import com.hermes.hermesd.Dispatcher
import java.net.Socket

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
        //val writer = new OutputStreamWriter(os)

        val is = socket.getInputStream
        //val reader = new LineNumberReader(new InputStreamReader(is))
		()
    }
	
	
	
}