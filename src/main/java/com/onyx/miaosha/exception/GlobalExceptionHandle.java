package com.onyx.miaosha.exception;

import com.onyx.miaosha.result.CodeMsg;
import com.onyx.miaosha.result.Result;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    public Result<Object>  exceptionHandle(HttpServletRequest request,Exception e){

        e.printStackTrace();
        if(e instanceof BindException){
            BindException bindException = (BindException) e;
            String message = bindException.getAllErrors().get(0).getDefaultMessage();
            return Result.fail(CodeMsg.BIND_ERROR.fillArgs(message));
        }else if(e instanceof GlobalException){
            GlobalException exception = (GlobalException) e;
            return Result.fail(exception.getCodeMsg());
        }else {
            return null;
        }

    }



}
