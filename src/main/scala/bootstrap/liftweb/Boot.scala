package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._
import common._
import http._
import sitemap._
import Loc._
import net.liftweb.squerylrecord.SquerylRecord
import org.squeryl.Session
import org.squeryl.adapters.H2Adapter
import java.sql.DriverManager
import code.model.Trade
import net.liftweb.squerylrecord.RecordTypeMode._
import code.model.SquerylSchema

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {

  def boot {

    // initialise database
    SquerylRecord.initWithSquerylSession(Session.create(
      DriverManager.getConnection(
        Props.get("db.url") openOr "jdbc:h2:mem:potfolio-manager;DB_CLOSE_DELAY=-1",
        Props.get("db.user") openOr "sa",
        Props.get("db.password") openOr ""),
      new H2Adapter))

    // initialise schema
    transaction {
      try {
        SquerylSchema.printDdl
        SquerylSchema.dropAndCreate()
      } catch {
        case e =>
          e.printStackTrace()
          throw e;
      }
    }  
      
    S.addAround(new LoanWrapper {
      override def apply[T](f: => T): T = {
        inTransaction {
          f
        }
      }
    })
        
    // configure lift rules   
    LiftRules.unloadHooks.append(() => Session.currentSession.close) // this doesn't seem to help my issue
    LiftRules.addToPackages("code")
    LiftRules.setSiteMapFunc(() => sitemap)
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    def sitemap = {
      val entries =
        Menu("Home") / "index" :: Nil

      SiteMap(entries: _*)
    }
  }
}
