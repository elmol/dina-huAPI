package code

import code.model.Pago
import net.liftweb.json._
import scala.reflect.ManifestFactory.classType
import java.text.SimpleDateFormat
import java.util.Date
import java.text.ParseException
import net.liftweb.http.LiftRules
import net.liftweb.common.Empty

object Store {
  implicit val formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("dd/MM/yyyy")
    //THIS IS BECUASE THERE IS A BUG IN DATA FORMAT
    //https://groups.google.com/forum/#!topic/liftweb/pdsaXSwl41k
    override val dateFormat = new DateFormat {
      def parse(s: String) = try {
        Some(dateFormatter.parse(s))
      } catch {
        case e: ParseException => None
      }
      def format(d: Date) = dateFormatter.format(d)
    }
  }

  lazy val pagos: List[Pago] = {
    parse(Store.jsonStore).extract[List[Pago]]
  }
  private lazy val jsonStore = {
    LiftRules.loadResourceAsString("/pagos.json") match {
      case Empty =>  {println("ERROR: it was not possible to load store file pagos.json"); ""} 
      case x => x.mkString 
    }
  }
}