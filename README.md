# 方法拦截插件
在开发过程中，经常有需要拦截某个方法调用的需求。比如处理隐私政策中对隐私api的调用，处理对某些方法的频繁调用等。

## 功能特性
- 全AGP版本适配
- 支持INVOKEVIRTUAL
- 支持INVOKESTATIC

## 用法

1. 第一步，加入classpath
    ```groovy
    buildscript {
        dependencies {
            classpath "com.method.intercept:plugin:1.0.0"
        }
    }
    ```
2. 第二步，在app的build.gradle下应用插件
    ```groovy
    apply plugin:'com.method.intercept'
    ```

3. 第三步，写拦截配置

    在app目录下新建method_intercept.yaml文件

    __ownerClassName__ 是拦截方法的类名

    __methods.name__ 是拦截的方法名

    __methods.descriptor__ 是拦截方法的描述

    __methods.intercept__ 是指是否要拦截该方法
    
    ```yaml
    # 一个例子如下
    intercept:
      - ownerClassName: android/accounts/AccountManager
        methods:
          - name: getAccounts
            descriptor: "()[Landroid/accounts/Account;"
            intercept: true
          - name: getAccountsByType
            descriptor: "(Ljava/lang/String;)[Landroid/accounts/Account;"
            intercept: true
    ```

4. 第四步，编写拦截后调用的类

    拦截后的调用类类名需要与被拦截的类的类名保持一致，包名需要增加前缀`intercept(可以配置)`。
    
    - 拦截公有方法

        比如要拦截android.telephony.TelephonyManager的公有方法getDeviceId，你需要新建一个intercept.android.telephony.TelephonyManager的类，新建一个静态方法deviceId，它的第一个参数是被拦截类android.telephony.TelephonyManager的对象，后面的参数与原方法相同
        ```java
        package android.telephony;

        // 被拦截的类
        public class TelephonyManager {

            public String getDeviceId() {
                //method body
                return "deviceId";
            }

            public String getDeviceId(int id) {
                //method body
                return "deviceId";
            }
        }
        ```
        ```java
        package interceptor.android.telephony;//注意包名前缀

        // 用于拦截的类
        public class TelephonyManager {

            public static String getDeviceId(android.telephony.TelephonyManager manager) {
                //method body
                return "intercept-deviceId";
            }

            public static String getDeviceId(android.telephony.TelephonyManager manager, int id) {
                //method body
                return "intercept-deviceId";
            }
        }

        ```
    - 拦截类的静态方法

        比如要拦截类android.provider.Settings的静态方法getString，你需要新建一个类intercept.android.provider.Settings，新建一个静态方法getString，所有的参数与原方法的参数一致
        ```java
        package android.provider;

        // 被拦截的类
        public class Settings {

            public static String getString(android.content.ContentResolver resolver, String name) {
                //method body
                return "getString:" + name;
            }
        }
        ```
        ```java
        package interceptor.android.provider;//注意包名前缀

        // 用于拦截的类
        public class Settings {

            public static String getString(android.content.ContentResolver resolver, String name) {
                //method body
                return "intercept-getString:" + name;
            }
        }

        ```


## 可选配置
```groovy
methodIntercept {
    packageSuffix = "intercept"//可以配置拦截的调用包名前缀。默认是intercept
    blackList = ["androidx.*"]//可以配置不需要拦截的包名，可使用.*匹配子包名
}
```


