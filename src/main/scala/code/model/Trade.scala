package code.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field._
import net.liftweb.squerylrecord.KeyedRecord
import org.squeryl.annotations.Column

class Trade private() extends Record[Trade] with KeyedRecord[Long]{
  
  override def meta = Trade
  
  @Column(name="id")
  override val idField = new LongField(this)
  
  
  val tradeType = new EnumField(this, TradeType)
  val tradeDate = new DateTimeField(this)  
  
}

object Trade extends Trade with MetaRecord[Trade]

object TradeType extends Enumeration {
  val Equity = Value;
  val Option = Value;
}