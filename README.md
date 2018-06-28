# rregister---动态广播处理

[![License](https://img.shields.io/aur/license/yaourt.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![Download](https://api.bintray.com/packages/lovingning/maven/rregister/images/download.svg)](https://bintray.com/lovingning/maven/rregister/_latestVersion)

**_注:当前库只支持广播动态绑定activity_**

## 一 引入依赖
在项目**build.gradle**中添加依赖：

```
   compile 'com.knowledge.mnlin:rregister:latest'
```

下面显示的module都是库本身需要的,如果已经提供了,则可以强制指定rregister库不依赖传递;(一般只需要引用上面的库本身即可)

```
    //RxJava与RxAndroid
    compile 'io.reactivex.rxjava2:rxjava:2.1.3'
    compile 'io.reactivex.rxjava2:rxandroid:2.0.1'

    //注解
    implementation 'com.android.support:support-annotations:27.1.1'

    //AndroidUtilCode
    implementation 'com.blankj:utilcode:1.12.1'

```

## 二 使用固有的广播

框架中目前集成了如下广播:(广播注册工具类为: [BCRMachine](./src/main/java/com/knowledge/mnlin/rregister/util/BCRMachine.java))

    * 短信 registerSMSReceiver
    * 电池状态 registerBatteryChange
    * 语言区域 registerLocale
    * 网络状态 registerNetStatus
    * 屏幕亮灭 registerScreen
    * Home等特殊键监听 registerHome

如果使用这五种广播,则可以引用对应的静态方法来实现,具体使用步骤如下:

#### 1 项目中Activity继承自[BaseActivity](./src/main/java/com/knowledge/mnlin/rregister/util/BaseActivity.java)

因为需要动态移除广播,因此需要监听Activity的生命周期:

---

    public class MyActivity extends BaseActivity{
        //... 处理固有逻辑
    }

---

当然,如果已经有了活动基类,无法之间直接去继承[BaseActivity](./src/main/java/com/knowledge/mnlin/rregister/util/BaseActivity.java),那么就需要将[BaseActivity](./src/main/java/com/knowledge/mnlin/rregister/util/BaseActivity.java)中的代码拷贝到自己的Activity基类中,同时让Activity实现[ListenerInActivity](./src/main/java/com/knowledge/mnlin/rregister/util/ListenerInActivity.java)接口.

```

    public abstract class MyBaseActivity extends AppCompatActivity implements ListenerInActivity {
        /**
         * 注册监听器,当销毁或者是暂停的时候,用于执行回调
         */
        private HttpCallback<Object> callbackOnDestroy, callbackOnStop;
                
        /**
         * 当activity销毁时候,关闭资源
         */
        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (callbackOnDestroy != null) {
                callbackOnDestroy.run(null);
            }
        }
    
    
        @Override
        protected void onStop() {
            super.onStop();
            if (callbackOnStop != null) {
                callbackOnStop.run(null);
            }
        }
    
    
        /**
         * 当activity被关掉时,添加监听
         */
        @Override
        public void listenerOnDestroy(HttpCallback<Object> callback) {
            callbackOnDestroy = callback;
        }
    
    
        /**
         * 当activity不可见时,添加监听
         */
        @Override
        public void listenerOnStop(HttpCallback<Object> callback) {
            callbackOnStop = callback;
        }
    }
```  


然后在自己的Activity中需要监听广播的时候启动监听:
比如,现在需要监听屏幕的打开和关闭,那么可以在需要的地方做逻辑处理:

```

    BCRMachine.registerSMSReceiver(this, this, RemoveTime.onDestroy, pair ->{
        System.out.println(pair.first + " " + pair.second);
    } );
    
```

库中提供的静态方法,一般有四个参数:Activity,ListenerInActivity,[RemoveTime](./src/main/java/com/knowledge/mnlin/rregister/util/RemoveTime.java),[HttpCallback](./src/main/java/com/knowledge/mnlin/rregister/util/HttpCallback.java)

    1. Activity,即当前需要注册监听器的活动对象
    2. ListenerInActivity接口实现类,一般也是Activity
    3. RemoveTime枚举类型,用来指定该监听器自动移除的时间,可选onDestroy时和onStop时
    4. HttpCallback回调,需要指定泛型来表示广播接收器处理后返回的结果(可参考源码中注释)
    
## 三 自定义广播

如果库中提供的 广播不足,则可以 自定义广播接受类,自定义广播也比较简单;
同样的,需要先继承[BaseActivity](./src/main/java/com/knowledge/mnlin/rregister/util/BaseActivity.java)类,然后:

#### 1 自定义广播

自定义广播时需继承库中提供的基类[BaseReceiver](./src/main/java/com/knowledge/mnlin/rregister/util/BaseReceiver.java)

---
    public class MyReceiver extends BaseReceiver<MyObject> {
    
        /**
         * 自定义构造函数
         */
        public NetReceiver(@NonNull HttpCallback<MyObject> httpCallback) {
            super(httpCallback,"my_intent");
        }
    
        /**
         * 处理短信结果
         */
        protected MyObject apply(Intent intent) {
            return NetworkUtils.getNetworkType();
        }
    }
---
    
模仿这种形式,先继承[BaseReceiver](./src/main/java/com/knowledge/mnlin/rregister/util/BaseReceiver.java),同时指定一个泛型类,这里假设为 MyObject 类型;

然后添加构造函数,构造函数格式固定,只能有一个 [HttpCallback](./src/main/java/com/knowledge/mnlin/rregister/util/HttpCallback.java) 类型的参数,[HttpCallback](./src/main/java/com/knowledge/mnlin/rregister/util/HttpCallback.java)需要指定泛型,与[BaseReceiver](./src/main/java/com/knowledge/mnlin/rregister/util/BaseReceiver.java)指定的泛型类型相同.
同时主动调用父类的构造方法,第一个参数很明显,第二个参数为可变长度的String类型,即一个**String数组**,将该广播监听器需要拦截的Action都传进去.

然后重写**apply**方法,从**intent**中取出数据,或者做其他处理,来返回一个**MyObject**类型的对象,也就是之前泛型指定的类型.

此时,自定义广播接收器结束.

#### 2 在自己的Activity中进行调用

调用自定义的广播接收器,和之前的方法差不多,只不过需要调用广播注册工具类[BCRMachine](./src/main/java/com/knowledge/mnlin/rregister/util/BCRMachine.java))的静态方法**registerBroadcast**

在自己的Activity中:

---

    try {
        BCRMachine.registerBroadcast(this, this, RemoveTime.onDestroy, (HttpCallback<MyOject>) o -> {
            //...自己处理广播返回的结果
        }, MyReceiver.class);
    } catch (NoSuchMethodException e) {
        //..反射调用方法时可能出现异常
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        //..反射调用方法时可能出现异常
        e.printStackTrace();
    } catch (InvocationTargetException e) {
        //..反射调用方法时可能出现异常
        e.printStackTrace();
    } catch (InstantiationException e) {
        //..自定义的泛型参数前后不一致,或其他异常
        e.printStackTrace();
    }

---

该静态方法,前4个参数与上面提到的静态方法相同,第5个参数需要提供自定义的**广播接收器自身class类**;

同时,需要自己处理可能出现的异常.


_CSDN博客地址: ***(暂未指定)_


    
  









