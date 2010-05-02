package com.hermes.hermesd

import com.twitter.ostrich.{BackgroundProcess, Server, ServerInterface, Stats}
import net.lag.configgy.{Configgy, RuntimeEnvironment}
import net.lag.logging.Logger

object Main extends ServerInterface {
	val log = Logger.get(getClass.getName)

	def main(args: Array[String]) {
		val runtime = new RuntimeEnvironment(getClass)
		runtime.load(args)
		val config = Configgy.config
		Server.startAdmin(this, config, runtime)
		ConnectionListener.start(config, runtime)

		log.info("Starting hermesd!")
		BackgroundProcess.spawnDaemon("main") {
			while (true) {
				Thread.sleep(2000)
				Stats.incr("sheep")
			}
		}
	}

	def shutdown() {
		log.info("Shutting down!")
		System.exit(0)
	}

	def quiesce() {
		shutdown()
	}
}
