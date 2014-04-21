TabunAuth
=========
Библиотека интеграции аккаунтов Табуна в Android.

Использует libtabun, но возвращаемые ею токены можно использовать где угодно.

##Настройка проекта
Добавьте AndroidManifest.xml в application этот кусок кода:

```xml

<!--Tabun.Auth-->
<activity android:name="everypony.tabun.auth.TabunAccountSelector"/>
<activity android:name="everypony.tabun.auth.TabunAuthActivity"/>
<activity android:name="everypony.tabun.auth.TabunTokenGetterActivity"/>

<service android:name="everypony.tabun.auth.TabunAuthService"
         android:permission="android.permission.AUTHENTICATE_ACCOUNTS">
    <intent-filter>
        <action android:name="android.accounts.AccountAuthenticator"/>
    </intent-filter>
</service>

```
В project.properties вашего проекта добавьте путь к папке с библиотекой:

```properties
android.library.reference.1=путь/к/папке/TabunAuth
```
       
##Использование
Чтобы запросить токен, нужно запустить для получения результата `TabunTokenGetterActivity`:

```java
   startActivityForResult(new Intent(this, TabunTokenGetterActivity.class), 42);
```

В onActivityResult придёт уже проверенный токен, который можно передать в создание `AccessProfile.parseString()`.
Он имеет вид `tabun.everypony.ru:80@Cookies`. 