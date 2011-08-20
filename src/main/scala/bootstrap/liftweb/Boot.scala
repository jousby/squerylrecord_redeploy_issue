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
import com.mchange.v2.c3p0.ComboPooledDataSource

/**
 * A class that's instantiated early and run. It allows the application
 * to modify lift's environment
 */
class Boot {

  def boot {

    // initialise database
    val cpds = new ComboPooledDataSource 
    cpds.setDriverClass(Props.get("db.driver") openOr "org.h2.Driver") 
    cpds.setJdbcUrl(Props.get("db.url") openOr "jdbc:h2:tcp://localhost//usr/dev/data/portfolio-manager") 
    cpds.setUser(Props.get("db.user") openOr "sa") 
    cpds.setPassword(Props.get("db.password") openOr "")
    
    SquerylRecord.initWithSquerylSession(
        Session.create(cpds.getConnection, new H2Adapter))

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
    LiftRules.unloadHooks.append(() => cpds.close())
    LiftRules.addToPackages("code")
    LiftRules.setSiteMapFunc(() => sitemap)
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    def sitemap = {
      val entries =
        Menu("Home") / "index" :: 
        Trade.menus

      SiteMap(entries: _*)
    }
  }
}
