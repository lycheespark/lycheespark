package com.spark.exception;

/**
 * @author LYCHEE
 * 自定义异常类
 */
public class LycheeSparkException extends RuntimeException{


    /**
     * 无参构造函数
     */
    public LycheeSparkException() {
        super();
    }

    /**
     * 仅接收消息的构造函数
     * @param message 消息
     */
    public LycheeSparkException(String message) {
        super(message);
    }

    /**
     * 接收消息和原因的构造函数
     * @param message 消息
     * @param cause 原因
     */
    public LycheeSparkException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 接收原因的构造函数
     * @param cause 原因
     */
    public LycheeSparkException(Throwable cause) {
        super(cause);
    }

    /**
     * 支持启用/禁用抑制及可写堆栈跟踪的构造函数
     * @param message 消息
     * @param cause 原因
     * @param enableSuppression 控制是否启用异常的 "抑制" 功能
     *        - 抑制异常: 抑制异常指的是在一个异常发生时，另一个异常（通常是在 try-with-resources 块中自动关闭资源时发生的异常）是否会被添加到原始异常的抑制异常列表中。
     *        - 使用场景: 如果 enableSuppression 为 true，抑制的异常会被添加到原异常的抑制列表中，并可以通过 Throwable.getSuppressed() 方法访问它们。否则，抑制的异常将不会被添加。
     * @param writableStackTrace 控制异常对象的堆栈跟踪信息是否可写
     *        - 堆栈跟踪: 当异常被抛出时，JVM 会生成一个堆栈跟踪，记录异常发生时调用栈中的方法顺序。默认情况下，这些信息是可写的。、
     *        - 使用场景: 如果 writableStackTrace 为 true，异常对象将生成并存储完整的堆栈跟踪信息。如果为 false，则异常对象不会生成堆栈跟踪信息。这在某些场景下可以提高性能，或者在不需要堆栈跟踪的情况下减少内存占用。
     */
    public LycheeSparkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
