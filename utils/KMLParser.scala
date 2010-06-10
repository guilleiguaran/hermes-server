import java.io._
import scala.util.matching.Regex

import com.nodeta.scalandra.serializer.StringSerializer
import com.nodeta.scalandra._

// scala -classpath "hermes-server/libs/*"
val serialization = new Serialization(
        StringSerializer,
        StringSerializer,
        StringSerializer
)

val cassandra = new Client(Connection("127.0.0.1", 9160),"Keyspace1", serialization, ConsistencyLevels.one)


if (args.length == 0) println("Se necesita enviar el nombre del archivo como argumento")
else{
	val fileName = args(0)
	try{
		var fr = new FileReader(fileName)
		var br = new BufferedReader(fr)

		var record = br.readLine()
		if(record == "<Document>"){
			record = br.readLine()
			if(record == "<Nodes>"){
				 readNodes(br)
				 record = br.readLine()
				 if(record == "<Links>"){
				 	readLinks(br)
				 }else{
					println("El archivo \"" + fileName + "\" no tiene la cabacera links")			
				 }
			}			
			else{
				println("El archivo \"" + fileName + "\" no tiene la cabacera nodes")			
			}
		}else{
			println("El archivo \"" + fileName + "\" no tiene la cabacera document")	
		}
		

	} catch {
		case e: FileNotFoundException => println("Archivo \"" + fileName + "\" no encontrado")
		case e: IOException => 
		{
				println("Error de lectura")
				e.printStackTrace()
		}
		case e: Exception => e.printStackTrace()
	}
}





def readNodes(br: BufferedReader){
	println("Son nodos")
	var inPlaceMark = false
	var line = br.readLine()
	var placeMark = ""
	var regex = new Regex("""<name>(.*)</name>[\w|\s|<|>|-|\W]+<coordinates>(.*)</coordinates>""")
	while(line != null && line != """</Nodes>"""){
		if(line == """<Placemark>""") {
			inPlaceMark = true
			line = br.readLine()	
		}
		if(line == """</Placemark>""") {
			var iter = regex.findAllIn(placeMark)
			if(iter.toString == "non-empty iterator" && iter.groupCount == 2){
				var id = placeMark.substring(iter.start(1),iter.end(1)) //id
				var coordinates = placeMark.substring(iter.start(2),iter.end(2)).split(",") //long,lat,0
				println(id.toString)
				println(coordinates.toString)
				cassandra.ColumnFamily("Standard1")(id.toString)("Lon") = coordinates(0)
				cassandra.ColumnFamily("Standard1")(id.toString)("Lat") = coordinates(1)
				println("--------------------------------------------------------------------")
			}
			placeMark = ""
			inPlaceMark = false

		}
		if(inPlaceMark) placeMark += line + "\n"
		line = br.readLine()
	}

}


def readLinks(br: BufferedReader){
	println("Son conexiones")
	var inPlaceMark = false
	var line = br.readLine()
	var placeMark = ""
	var regex = new Regex("""<name>(.*)<\/name>""")
	while(line != null && line != """</Links>"""){
		if(line == """<Placemark>""") {
			inPlaceMark = true
			line = br.readLine()	
		}
		if(line == """</Placemark>""") {
			var iter = regex.findAllIn(placeMark)
			if(iter.toString == "non-empty iterator" && iter.groupCount == 1){
				var links = placeMark.substring(iter.start(1),iter.end(1)).split(",") // ida-idb[,idb2-idb2]
				//println(links.toString)				
				manageLinks(links)				
				println("--------------------------------------------------------------------")
			}
			placeMark = ""
			inPlaceMark = false

		}
		if(inPlaceMark) placeMark += line + "\n"
		line = br.readLine()
	}
}

def manageLinks(l: Array[String]){
	println(l.toString)
	for (i<-0 to l.size -1){
		val element = l(i).split("-")
		val f = cassandra.ColumnFamily("Standard1")(element(0))
		val t = cassandra.ColumnFamily("Standard1")(element(1))
		
		var dlong = f("Lon").toDouble - t("Lon").toDouble

		var degtorad = 0.01745329
		var radtodeg = 57.29577951

		var dvalue = (Math.sin(f("Lat").toDouble * degtorad))*(Math.sin(t("Lat").toDouble * degtorad)) + 
		(Math.cos(f("Lat").toDouble * degtorad) * Math.cos(t("Lat").toDouble * degtorad) * Math.cos(dlong * degtorad))

		var dd = Math.acos(dvalue) * radtodeg

		var km = dd*111.302 

		var horas = km/30

		cassandra.SuperColumnFamily("Super1")(element(0))(element(1)) = Map("1" -> (horas*3600).toString)
		println(element.toString + " = " + cassandra.SuperColumnFamily("Super1")(element(0))(element(1)).toString)		
	}
}
