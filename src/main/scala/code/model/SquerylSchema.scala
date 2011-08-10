package code.model

import org.squeryl.Schema
import net.liftweb.squerylrecord.RecordTypeMode._

object SquerylSchema extends Schema {
  val trades = table[Trade]
  
  /**
   * Drops an old schema if exists and then creates
   * the new schema.
   */
  def dropAndCreate() {
    drop
    create
  }
}