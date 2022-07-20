package com.peach.privacy.api;

public class Utils {

    public static boolean intercept(
            String source,
            String className,
            int curLine,
            int opcode,
            String owner,
            String name,
            String descriptor,
            Boolean isInterface) {
        InvokeMethod invokeMethod = new InvokeMethod(opcode, owner, name, descriptor, isInterface);
        InvokeContext invokeContext = new InvokeContext(source, className, curLine, invokeMethod);
        DisallowMethodInterceptor byOwnerClassName = Interceptors.INSTANCE.findByOwnerClassName(owner);
        if (byOwnerClassName == null) return false;
        return byOwnerClassName.intercept(invokeContext);
    }
}
