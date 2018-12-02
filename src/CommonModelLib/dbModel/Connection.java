/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CommonModelLib.dbModel;

import DBmethodsLib.DBmethodsCommon;

/**
 *
 * 
 */
public class Connection {

 private static DBmethodsCommon readOnlyMasterDb;

   public static void setReadOnlyMasterDb(DBmethodsCommon readOnlyMasterDb) {
       Connection.readOnlyMasterDb = readOnlyMasterDb;
   }

   public static DBmethodsCommon getReadOnlyMasterDb() {
       return readOnlyMasterDb;
   }
}
