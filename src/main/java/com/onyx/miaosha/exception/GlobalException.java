package com.onyx.miaosha.exception;

import com.onyx.miaosha.result.CodeMsg;

public class GlobalException extends RuntimeException {

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg=codeMsg;
    }
}
