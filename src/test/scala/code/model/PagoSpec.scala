package code.model

import org.scalatest._
import net.liftweb.json._
import org.mozilla.javascript.ast.Yield
import java.util.Date

class PagoSpec extends FlatSpec with Matchers {

  "A pago object" should "be added " in {
    val pago = Pago.addPago(Pago(ctacte = "10092 - Sueldos y Jornales", neto = 25.50))
    pago shouldEqual Pago.store.filter(_.id == pago.id).head;
  }

  it should "be getted" in {
    val newPago = new Pago(2020, 566, None, "PPGACP", "10092 - Sueldos y Jornales", "", 945297)
    Pago.store ::= newPago
    Pago.getPago(newPago.id).get shouldEqual newPago
  }

  it should "manages correctly the date formats" in {
    val json = """             {
                   "id": 5135,
                   "nro": 546,
                   "fecha": "03/04/2017",
                   "tipo": "PPGACP",
                   "ctacte": "10381 - Poujardieu Christian Eduardo",
                   "expediente": "",
                   "neto": 5595
                 }"""
    val format = new java.text.SimpleDateFormat("dd/MM/yyyy")
    format.format(Pago.fromJSON(parse(json)).fecha.get) shouldEqual "03/04/2017"
  }

  it can "get all pagos" in {
    Pago.getPagos() shouldEqual Pago.store
  }

  it should "allows to search by different parameters" in {

    val map0 = Map("ctacte" -> Some("Su"), "tipo" -> Some("GAC"))
    Pago.searchPagosBy(params = map0) shouldEqual Pago.getPagos().filter(_.ctacte.toLowerCase().contains("su")).filter(_.tipo.toLowerCase().contains("gac"))

    val map1 = Map("ctacte" -> Some("PE"))
    Pago.searchPagosBy(params = map1) shouldEqual Pago.getPagos().filter(_.ctacte.toLowerCase().contains("pe"))

    val map2 = Map("tipo" -> Some("Otro"));
    Pago.searchPagosBy(params = map2) shouldEqual Pago.getPagos().filter(_.tipo.toLowerCase().contains("otro"))

    val map3: Map[String, Option[String]] = Map();
    Pago.searchPagosBy(params = map3) shouldEqual Pago.getPagos()
  }

  it should "allows to filter by a period" in {
    val format = new java.text.SimpleDateFormat("dd/MM/yyyy")

    def string2Date(map: Map[String, Option[String]], str: String): Date = format.parse(map.getOrElse(str, None).get)
    def fromDate(map: Map[String, Option[String]]): Date = string2Date(map, "from")
    def toDate(map: Map[String, Option[String]]): Date = string2Date(map, "to")

    val map0 = Map("from" -> Some("04/04/2017"), "to" -> Some("09/04/2017"))
    Pago.searchPagosBy(params = map0) shouldEqual Pago.getPagos().filter(d => d.fecha.isDefined && !d.fecha.get.before(fromDate(map0))).filter(d => d.fecha.isDefined && !d.fecha.get.after(toDate(map0)))

    val map1 = Map("from" -> Some("02/04/2017"), "to" -> Some("11/04/2017"))
    Pago.searchPagosBy(params = map1) shouldEqual Pago.getPagos().filter(d => d.fecha.isDefined && !d.fecha.get.before(fromDate(map1))).filter(d => d.fecha.isDefined && !d.fecha.get.after(toDate(map1)))

    val map2 = Map("from" -> Some("02/04/2017"))
    Pago.searchPagosBy(params = map2) shouldEqual Pago.getPagos().filter(d => d.fecha.isDefined && !d.fecha.get.before(fromDate(map2)))

    val map3 = Map("to" -> Some("09/04/2017"))
    Pago.searchPagosBy(params = map3) shouldEqual Pago.getPagos().filter(d => d.fecha.isDefined && !d.fecha.get.after(toDate(map3)))

  }

  it should "allows to sort by amount of Neto" in {

    val map0 = Map("from" -> Some("04/04/2017"), "to" -> Some("09/04/2017"), "-neto" -> None, "neto" -> None)
    Pago.sortPagosBy(map0, Pago.store) shouldEqual Pago.getPagos().sortBy(-_.neto).sortBy(+_.neto)

    val map1 = Map("-neto" -> None)
    Pago.sortPagosBy(map1, Pago.store) shouldEqual Pago.getPagos().sortBy(-_.neto)

    val map2 = Map("neto" -> None)
    Pago.sortPagosBy(map2, Pago.store) shouldEqual Pago.getPagos().sortBy(_.neto)
    
    val map3 = Map("other" -> None)
    Pago.sortPagosBy(map3, Pago.store) shouldEqual Pago.getPagos()
    
    val map4 = Map("-neto" -> None, "neto" -> None)
    Pago.sortPagosBy(map4, List.empty) shouldEqual List.empty

  }
  
  it should "allows to sum all items" in {
    val map0 = Map("sum" -> None)
    Pago.sumPagos(map0, Pago.store) shouldEqual Pago.getPagos().map(_.neto).sum
    
    val map1 = Map("neto" -> None)
    Pago.sumPagos(map1, Pago.store) shouldEqual 0
  }
  
}