package code

import net.liftweb.http.rest.RestHelper
import net.liftweb.util.Helpers.AsInt
import net.liftweb.json.JsonAST.JValue
import net.liftweb.http.OkResponse
import code.model.Pago
import net.liftweb.json.JsonAST.JString
import net.liftweb.json.DefaultFormats
import java.text.SimpleDateFormat
import java.util.Date
import com.sun.javafx.css.parser.CSSParser.ParseException
import net.liftweb.json.DateFormat
import java.text.ParseException
import net.liftweb.json.JsonAST._
import net.liftweb.http.S
import net.liftweb.http.Req
import net.liftweb.http.LiftRules
import net.liftweb.common.Box
import net.liftweb.http.BadResponse
import net.liftweb.http.ResponseWithReason
import net.liftweb.http.BadRequestResponse
import scala.util.Try
import scala.util.Success
import scala.util.Failure

object DinaAPI extends RestHelper {

  def getPagoJSON(postID: Int): Option[JValue] = {
    Pago.getPago(postID).map(_.toJSON)
  }

  def deletePago(postID: Int) = {
    Pago.deletePago(postID)
    new OkResponse
  }

  def postPago(jsonData: JValue): JValue = {
    Pago.addPago(Pago.fromJSON(jsonData)).toJSON
  }

  def getPagos(): Option[JObject] = {
    val params = S.request.map(_.params).getOrElse(Map.empty)
    //if a param contains an empty attribute it's converted to a None option
    val map = params.mapValues(l => l.map(Option(_).filter(_.trim.nonEmpty)).head)
    val pagos = Pago.sortPagosBy(map, Pago.searchPagosBy(map))
    val dataJson = List(JField("data", JArray(pagos.map(_.toJSON))))
    val jsonList = Pago.sumPagos(map, pagos) match {
      case 0.0 => dataJson
      case sum => dataJson ::: List(JField("sum", JDouble(sum)))
    }
    Some(JObject(jsonList))
  }

  serve {
    //api/pago/$postId
    case "api" :: "pagos" :: AsInt(postID) :: Nil JsonGet req    => getPagoJSON(postID)
    case "api" :: "pagos" :: AsInt(postID) :: Nil JsonDelete req => deletePago(postID)
    case "api" :: "pagos" :: Nil JsonPost ((jsonData, req))      => postPago(jsonData)
    //api/pagos?
    // ctacte=${term}, tipo=${term}, from=$dd/mm/yy , to=$dd/mm/yy -> to filter
    // -neto or neto -> to sort
    // sum ->to sum    
    case "api" :: "pagos" :: _ JsonGet _ =>
      Try(getPagos()) match {
        case Success(pagos) => pagos
        case Failure(_)     => BadRequestResponse("ERROR: on getting pagos, review parameters")
      }
  }

}