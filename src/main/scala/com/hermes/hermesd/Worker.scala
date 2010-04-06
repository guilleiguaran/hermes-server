package com.hermes.hermesd

import scala.actors.Actor
import scala.actors.Actor._
import com.hermes.hermesd.Connection
import com.hermes.hermesd.Dispatcher

case class Inactive(worker: Worker)

class Worker(val id: Int, val dispatcher: Dispatcher) extends Actor{
	
	def act()
	{
		
		loop{
			
			react{
				
				case Connection(socket, id) =>
				//handle client
				socket.close()
				dispatcher ! Inactive(this)
				
			}
			
		}
		
	}
	
	
	
}