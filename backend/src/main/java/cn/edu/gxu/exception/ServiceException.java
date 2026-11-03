package cn.edu.gxu.exception;

/**
 * 自定义服务异常
 */
public class ServiceException extends  RuntimeException{
    private String code;
   public ServiceException(String code,String msg){
        super(msg);
        this.code =code;
   }
}
