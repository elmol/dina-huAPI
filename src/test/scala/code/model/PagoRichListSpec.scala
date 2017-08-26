package code.model

import java.util.Date

import org.scalatest.FlatSpec
import org.scalatest.Matchers

import PagoRichList.listTopagoRichList

class PagoRichListSpec extends FlatSpec with Matchers {

  "A pago rich list object" should "allows to sum all pagos contained in the list" in {

    val map0 = Map("sum" -> None)
    Pago.getPagos().sumPagos(map0) shouldEqual Pago.getPagos().map(_.neto).sum

    val map1 = Map("neto" -> None)
    Pago.getPagos().sumPagos(map1) shouldEqual 0

  }

  it should "allows to sort the list by amount of Neto" in {

    val map0 = Map("from" -> Some("04/04/2017"), "to" -> Some("09/04/2017"), "-neto" -> None, "neto" -> None)
    Pago.getPagos().sortPagosBy(map0) shouldEqual Pago.getPagos().sortBy(-_.neto).sortBy(+_.neto)

    val map1 = Map("-neto" -> None)
    Pago.getPagos().sortPagosBy(map1) shouldEqual Pago.getPagos().sortBy(-_.neto)

    val map2 = Map("neto" -> None)
    Pago.getPagos().sortPagosBy(map2) shouldEqual Pago.getPagos().sortBy(_.neto)

    val map3 = Map("other" -> None)
    Pago.getPagos().sortPagosBy(map3) shouldEqual Pago.getPagos()

  }

  it should "allows to filter the list by a period" in {
    val format = new java.text.SimpleDateFormat("dd/MM/yyyy")

    def string2Date(map: Map[String, Option[String]], str: String): Date = format.parse(map.getOrElse(str, None).get)
    def fromDate(map: Map[String, Option[String]]): Date = string2Date(map, "from")
    def toDate(map: Map[String, Option[String]]): Date = string2Date(map, "to")

    val map0 = Map("from" -> Some("04/04/2017"), "to" -> Some("09/04/2017"))
    Pago.getPagos().searchPagosBy(map0) shouldEqual Pago.getPagos().filter(d => d.fecha.isDefined && !d.fecha.get.before(fromDate(map0))).filter(d => d.fecha.isDefined && !d.fecha.get.after(toDate(map0)))

    val map1 = Map("from" -> Some("02/04/2017"), "to" -> Some("11/04/2017"))
    Pago.getPagos().searchPagosBy(params = map1) shouldEqual Pago.getPagos().filter(d => d.fecha.isDefined && !d.fecha.get.before(fromDate(map1))).filter(d => d.fecha.isDefined && !d.fecha.get.after(toDate(map1)))

    val map2 = Map("from" -> Some("02/04/2017"))
    Pago.getPagos().searchPagosBy(params = map2) shouldEqual Pago.getPagos().filter(d => d.fecha.isDefined && !d.fecha.get.before(fromDate(map2)))

    val map3 = Map("to" -> Some("09/04/2017"))
    Pago.getPagos().searchPagosBy(params = map3) shouldEqual Pago.getPagos().filter(d => d.fecha.isDefined && !d.fecha.get.after(toDate(map3)))
  }

  it should "allows to search in the list by different parameters" in {

    val map0 = Map("ctacte" -> Some("Su"), "tipo" -> Some("GAC"))
    Pago.getPagos().searchPagosBy(params = map0) shouldEqual Pago.getPagos().filter(_.ctacte.toLowerCase().contains("su")).filter(_.tipo.toLowerCase().contains("gac"))

    val map1 = Map("ctacte" -> Some("PE"))
    Pago.getPagos().searchPagosBy(params = map1) shouldEqual Pago.getPagos().filter(_.ctacte.toLowerCase().contains("pe"))

    val map2 = Map("tipo" -> Some("Otro"));
    Pago.getPagos().searchPagosBy(params = map2) shouldEqual Pago.getPagos().filter(_.tipo.toLowerCase().contains("otro"))

    val map3: Map[String, Option[String]] = Map();
    Pago.getPagos().searchPagosBy(params = map3) shouldEqual Pago.getPagos()
  }

} 