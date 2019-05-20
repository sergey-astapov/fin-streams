package io.fin.streams

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.apache.kafka.streams.kstream.ValueJoiner

import scala.xml.transform.{RewriteRule, RuleTransformer}
import scala.xml.{Elem, Node}

package object api {
  sealed trait Message {
    val account: String
    val amount: String
    val currency: String
    val date: String
  }

  case class GcgMessage(xml: Node) extends Message {
    lazy val account: String = (xml \\ "CdtrAcct" \ "Id" \ "Othr" \ "Id" map(_.text)).head

    lazy val amount: String = (xml \\ "InstdAmt" map(_.text)).head

    lazy val currency: String = (xml \\ "InstdAmt" \ "@Ccy" map(_.text)).head

    lazy val date: String = (xml \\ "initiatedTimestamp")
      .map(_.text)
      .map(_.split("\\+")(0))
      .map(LocalDateTime.parse(_, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
      .map(_.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
      .head

    def withAccount(acct: String): GcgMessage = {
      val rule = new RewriteRule {
        override def transform(n: Node): Seq[Node] = n match {
          case e @ <CdtrAcct>{_*}</CdtrAcct> =>
            <doc:CdtrAcct>
              <doc:Id>
                <doc:Othr>
                  <doc:Id>{acct}</doc:Id>
                </doc:Othr>
              </doc:Id>
            </doc:CdtrAcct>
          case _ => n
        }
      }
      GcgMessage(new RuleTransformer(rule).transform(xml).head)
    }
  }
  case class IbnkMessage(xml: Elem) extends Message {
    lazy val account: String = (xml \\ "CdtrAcct" \ "Id" \ "Othr" \ "Id" map(_.text)).head

    lazy val amount: String = (xml \\ "InstdAmt" map(_.text)).head

    lazy val currency: String = (xml \\ "InstdAmt" \ "@Ccy" map(_.text)).head

    lazy val date: String = (xml \\ "initiatedTimestamp")
      .map(_.text)
      .map(_.split("\\+")(0))
      .map(LocalDateTime.parse(_, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
      .map(_.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
      .head
  }
  case class RtaMessage(xml: Elem) extends Message {
    lazy val account: String = (xml \\ "CdtrAcct" \ "Id" \ "Othr" \ "Id" map(_.text)).head

    lazy val amount: String = (xml \\ "InstdAmt" map(_.text)).head

    lazy val currency: String = (xml \\ "InstdAmt" \ "@Ccy" map(_.text)).head

    lazy val date: String = (xml \\ "initiatedTimestamp")
      .map(_.text)
      .map(_.split("\\+")(0))
      .map(LocalDateTime.parse(_, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")))
      .map(_.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
      .head
  }

  class GcgProxyAccountJoiner extends ValueJoiner[String, GcgMessage, GcgMessage] {
    override def apply(acct: String, msg: GcgMessage): GcgMessage = msg.withAccount(acct)
  }

  case class Key(account: String, amount: String, currency: String, date: String)

  object Key {
    def apply(gcg: GcgMessage): Key = Key(gcg.account, gcg.amount, gcg.currency, gcg.date)
    def apply(ibnk: IbnkMessage): Key = Key(ibnk.account, ibnk.amount, ibnk.currency, ibnk.date)
    def apply(rta: RtaMessage): Key = Key(rta.account, rta.amount, rta.currency, rta.date)
  }

  case class ProxyAccount(proxy: String, account: String)
}
