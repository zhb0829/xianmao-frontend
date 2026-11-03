package cn.edu.gxu.Basic;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  这里用lombok插件是一个在Java开发过程中用注解的方式，简化了 JavaBean 的编写，避免了冗余和样板式代码而出现的插件，让编写的类更加简洁,就是不用写getter后
 *
 */
@Data
@NoArgsConstructor
//@NoArgsConstructor  生成无参构造函数
@AllArgsConstructor
//@AllArgsConstructor  生成所有构造函数

public class Result {

    private String code;
    private String msg;
    private Object data;
    public static Result success() {
        return new Result(Constants.CODE_200, "", null);
    }

    public static Result success(Object data) {
        return new Result(Constants.CODE_200, "", data);
    }

    public static Result error(String code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result error() {
        return new Result(Constants.CODE_500, "系统错误", null);
    }

}
