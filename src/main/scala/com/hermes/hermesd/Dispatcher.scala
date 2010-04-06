package com.hermes.hermesd

import scala.collection.mutable.{Map, ListBuffer}
import java.util.Random
import net.lag.configgy.{Configgy, RuntimeEnvironment}
import net.lag.logging.Logger
import com.hermes.hermesd.Connection

class Dispatcher extends Actor{

	val inactiveWorkers = new ListBuffer[Worker]
	val activeWorkers = Map[Int, Worker]()
	val rnd = new Random()

	for(i<-1 to 100){
		val worker = new Worker(i, this)
		worker.start()
		inactiveWorkers += worker
	}

	def act(){

		loop{

			react{

				case Inactive(worker) =>
				activeWorkers -= worker.id
				inactiveWorkers += worker

				case conn: Connection =>

				val worker = 
					if (inactiveWorkers.length == 0){
						activeWorkers.get(rnd.nextInt(activeWorkers.size)).get
					}
					else
					{
						val w = inactiveWorkers.remove(0)
						activeWorkers += w.id -> w
						w
					}
				worker ! conn

			}

		}

	}

}