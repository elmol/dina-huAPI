package code.model

import java.util.Date
import java.text.SimpleDateFormat
import net.liftweb.json.DefaultFormats
import java.text.ParseException
import net.liftweb.json.DateFormat
import net.liftweb.json.JsonAST.JValue
import PagoRichList._

case class Pago(id: Int = 0, nro: Int = 0, fecha: Option[Date] = None, tipo: String = "PPGACP", ctacte: String, expediente: String = "", neto: Double) {

  def toJSON = {
    import net.liftweb.json._
    import net.liftweb.json.JsonDSL._
    val format = new java.text.SimpleDateFormat("dd/MM/yyyy")
    ("id" -> id) ~ ("nro" -> nro) ~ ("fecha" -> fecha.map(format.format(_))) ~ ("tipo" -> tipo) ~ ("ctacte" -> ctacte) ~ ("expediente" -> expediente) ~ ("neto" -> neto)

  }
  
}

object Pago {
  import code.Store
  var store: List[Pago] = Store.pagos
  val format = new java.text.SimpleDateFormat("dd/MM/yyyy")

  def fromJSON(jsonData: JValue): Pago = {
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
    jsonData.extract[Pago]
  }

  def addPago(pago: Pago) = {
    val nextId = store.map(_.id).max + 1
    val newPago = pago.copy(id = nextId)
    store ::= newPago
    newPago
  }
  def getPago(id: Int): Option[Pago] = store.filter(_.id == id).headOption
  def deletePago(id: Int) { store = store.filterNot(_.id == id) } 
  def getPagos(): List[Pago] = store
  def searchPagosBy(params: Map[String, Option[String]], pagos: List[Pago]=Pago.store): List[Pago] = pagos.searchPagosBy(params)
  def sortPagosBy(params: Map[String, Option[String]], pagos: List[Pago]=Pago.store): List[Pago] = pagos.sortPagosBy(params)
  def sumPagos(params: Map[String, Option[String]], pagos: List[Pago]=Pago.store): Double = pagos.sumPagos(params)
}  

