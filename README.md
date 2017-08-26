# Dina-Hu*API*

Rest API written in [Scala](https://www.scala-lang.org/) that I've created for the sole purpose of learning.

The idea of this project is to support my learning of Scala language and relative tools and frameworks.
The challenge is, basically, to build an API REST that allows to get and make operations over the expenses of the city hall of [Dina Huapi](https://en.wikipedia.org/wiki/Dina_Huapi) (where I live)
Currently, I'm using the following Scala relative frameworks and tools

- [Lift](https://liftweb.net/)
- [ScalaTest](http://www.scalatest.org/)
- [SBT](http://www.scala-sbt.org/)
- [Scala IDE for Eclipse](http://scala-ide.org/)
    
How this project is just for learning and I am still learning, you could find incongruent code and written in different ways.

## Installation and Usage

Build Eclipse project

`$ sbt eclipse`

Start and stop the API Rest service using sbt and jetty

`$ sbt jetty:start`

`$ sbt jetty:stop` 

Run Tests

`$sbt test`

## Rest API usage

To get, query and sum the city hall expense
`GET $url/api/pagos?
 
    to filter: ctacte=${term}, tipo=${term}, from=${dd/mm/yy} , to=${dd/mm/yy}
    
    to sort: -neto (desc) or neto (asc)
    
    to sum: sum`
    
Example

`GET $url/api/pagos?ctacte=sue&tipo=Otro&from=03/04/2017&to=03/05/2017&-neto&sum`

