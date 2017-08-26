package code.model
import scala.language.implicitConversions
object PagoRichList {
  implicit def listTopagoRichList(input: List[Pago]) = new PagoRichList(input)
}

class PagoRichList(val pagos: List[Pago]) {

  def sumPagos(params: Map[String, Option[String]]): Double = params.find("sum" == _._1) match {
    case Some(_) => pagos.map(_.neto).sum
    case None    => 0
  }

  def sortPagosBy(params: Map[String, Option[String]]): List[Pago] = {
    def r[A, B](f: ((A, B) => (A, B)) => ((A, B) => (A, B))): (A, B) => (A, B) = f(r(f))(_, _)
    val recursiveSort = r[Iterable[String], List[Pago]](f => (params, pagos) => {
      if (params.isEmpty) {
        (params, pagos)
      } else {
        f(params.drop(1), params.head match {
          case "-neto" => pagos.sortBy(-_.neto)
          case "neto"  => pagos.sortBy(+_.neto)
          case _       => pagos
        })
      }
    })
    recursiveSort(params.filter(_._2.isEmpty).keys, pagos)._2
  }

  def searchPagosBy(params: Map[String, Option[String]]): List[Pago] = {
    pagos.filter(x => {
      val sequ = params.filter(_._2.isDefined).seq
      (for (i <- sequ) yield {
        i._1 match {
          case "ctacte" => { x.ctacte.toLowerCase().contains(i._2.get.toLowerCase()) }
          case "tipo"   => { x.tipo.toLowerCase().contains(i._2.get.toLowerCase()) }
          case "from"   => { x.fecha.isDefined && !x.fecha.get.before(Pago.format.parse(i._2.get)) }
          case "to"     => { x.fecha.isDefined && !x.fecha.get.after(Pago.format.parse(i._2.get)) }
          case _        => true
        }
      }).forall(x => x)
    })
  }

}
