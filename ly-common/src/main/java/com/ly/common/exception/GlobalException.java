package com.ly.common.exception;

import com.ly.common.vo.CodeMsg;
import lombok.Data;

@Data
public class GlobalException extends RuntimeException {

    public CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg){
        super();
        this.codeMsg = codeMsg;
    }


}
