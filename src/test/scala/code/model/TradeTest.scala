package code.model


import net.liftweb.common.Logger
import net.liftweb.squerylrecord.RecordTypeMode._
import SquerylSchema.{ trades }
import DBHelper.{ TestData => td }
import net.liftweb.record.{ BaseField, Record }
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class TradeTest extends Spec with ShouldMatchers with Logger {

  DBHelper.initSquerylRecordWithInMemoryDB()
  DBHelper.createSchema()
  
  describe("Schema(trades)") {
    
    it("should load trade by id") {      
      transaction {
        val trade = trades.lookup(1L).get
        trade should equal (td.t1)
      } 
    }            
  }
}