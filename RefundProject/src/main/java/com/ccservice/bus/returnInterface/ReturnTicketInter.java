package com.ccservice.bus.returnInterface;

import com.ccservice.bus.bean.BaseRequestDto;
import com.ccservice.bus.bean.BaseResponseDto;
import com.ccservice.bus.exection.ReturnTicketExection;

/** 所有的退票都实现该类。
 * 
    * @ClassName: ReturnTicketInter
    * @Description: TODO
    * @author 朱元元
    *
    *
 */
public interface ReturnTicketInter {

    /**
     * 
        * @Title: getResponseResult   调用退票
        * @Description: TODO(这里用一句话描述这个方法的作用)
        * @param @param requestDto  请求的信息
        * @param @return
        * @param @throws ReturnTicketExection    参数  退票返回的结果
        * @return BaseResponseDto    返回类型
        * @throws
     */
    public BaseResponseDto getResponseResult(BaseRequestDto requestDto) throws ReturnTicketExection;
}
