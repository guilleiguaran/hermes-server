package com.hermes.hermesd

import java.net.{Socket, ServerSocket}
import net.lag.configgy.{Configgy, ConfigMap, RuntimeEnvironment}
import net.lag.logging.Logger

case class Connection(client: Socket, id: Int)

object ConnectionListener {
	val log = Logger.get(getClass.getName)

	def start(config: ConfigMap, runtime: RuntimeEnvironment) = {
		val port = config.getInt("server_port", 9095)
		val socket = new ServerSocket(port)
		val dispatcher = new Dispatcher()
        dispatcher.start()
		var i = 0

		while (true){
			log.info("Waiting for connection.")
			val client = socket.accept()
			log.info("Accepted connection #"+i)
			i += 1
			dispatcher ! Connection(client, i)
		}
	}
}