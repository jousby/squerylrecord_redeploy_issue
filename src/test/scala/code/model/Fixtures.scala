package code.model

import net.liftweb.squerylrecord.RecordTypeMode._
import net.liftweb.record.{ MetaRecord, Record, TypedField, MandatoryTypedField }
import net.liftweb.common.{ Box, Full }
import net.liftweb.record.field._
import net.liftweb.record.Field
import net.liftweb.json.JsonAST.{ JValue, JString }
import net.liftweb.http.js.JE.Str
import org.squeryl.{ SessionFactory, Session, Schema }
import org.squeryl.adapters.H2Adapter
import org.squeryl.annotations.Column
import org.squeryl.internals.AutoIncremented
import org.squeryl.internals.PrimaryKey
import org.squeryl.dsl.CompositeKey2
import org.squeryl.KeyedEntity
import java.math.MathContext
import java.sql.DriverManager
import java.util.Calendar
import net.liftweb.squerylrecord.SquerylRecord
import net.liftweb.squerylrecord.KeyedRecord
import net.liftweb.squerylrecord.SquerylRecordField
import net.liftweb.common.Logger
import SquerylSchema.{ trades }

object DBHelper extends Logger {
  
  def initSquerylRecordWithInMemoryDB() {
    SquerylRecord.initWithSquerylSession { 
      val session = Session.create(DriverManager.getConnection("jdbc:h2:mem:testDB;DB_CLOSE_DELAY=-1", "sa", ""), new H2Adapter)
      session
    }
  }

  /**
   * Creates the test schema in a new transaction. Drops an old schema if
   * it exists.
   */
  def createSchema() {
    inTransaction {
        try {
          info("DDL for Schema")
          SquerylSchema.printDdl
          SquerylSchema.dropAndCreate()
          createTestData()
          info("Schema created")
        } catch {
            case e => e.printStackTrace()
              throw e;
        }
    }
  }
    /**
   * Creates some test instances of companies and employees
   * and saves them in the database.
   */
  def createTestData() {
    import TestData._

    allTrades.foreach(trades.insert(_))

  }

  object TestData {
    val t1 = Trade.createRecord.tradeType(TradeType.Option).tradeDate(new java.util.GregorianCalendar())

    val allTrades = List(t1)

  }
}

