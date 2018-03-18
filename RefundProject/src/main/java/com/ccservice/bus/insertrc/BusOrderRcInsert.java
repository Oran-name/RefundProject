
/**  
* @Title: BusOrderRcInsert.java
* @Package com.ccservice.bus.insertrc
*/

package com.ccservice.bus.insertrc;

import com.ccservice.Util.db.DBHelper;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年2月2日 下午3:32:11 
* 类说明 
*/

/**
* @ClassName: BusOrderRcInsert
* @Description: TODO
* @author 朱元元
*
*
*/

public class BusOrderRcInsert {
    public static void insertRcInfo(Long orderId, String content, String userName) {
        String sql = String.format(
                "insert into busOrderRc (OrderId ,ContentRc , CreateTime ,RightType ,OptName ) values (%d,'%s',getDate(),1,'%s') ",
                orderId, content, userName);
        DBHelper.executeSql(sql);
    }
}
