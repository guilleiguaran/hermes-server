package com.hermes.hermesd

import com.hermes.hermesd.Connection
import com.hermes.hermesd.Dispatcher

case class Inactive(worker: Worker)

class Worker(id: Int, dispatcher: Dispatcher) extends Actor{
	
	def act()
	{
		
		loop{
			
			react{
				
				case => Connection(socket, id)
				//handle client
				socket.close()
				dispatcher ! Inactive(this)
				
			}
			
		}
		
	}
	
	
	
}