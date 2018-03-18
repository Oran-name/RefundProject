
/**  
* @Title: WriteLogThread.java
* @Package com.ccservice.bus.log
*/

package com.ccservice.bus.log;

import com.ccservice.Util.file.WriteLog;

/** 
* @author 作者 E-mail: 
* @version 创建时间：2018年2月2日 下午4:14:12 
* 类说明 
*/

/**
* @ClassName: WriteLogThread
* @Description: TODO
* @author 朱元元
*
*
*/

public class WriteLogThread extends Thread {
    private String logName;

    private String logContent;
    /* (非 Javadoc)
    * 
    * 
    * @see java.lang.Thread#run()
    */

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        WriteLog.write(logName, logContent);
    }

    /**
     * 创建一个新的实例 WriteLogThread.
     *
     * @param logName
     * @param logContent
     */

    public WriteLogThread(String logName, String logContent) {
        super();
        this.logName = logName;
        this.logContent = logContent;
    }

}
