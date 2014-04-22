TabunAuth
=========
Небольшое приложение, отвечающее за хранение аккаунтов Табуна.

Использует libtabun, но возвращаемые ею токены можно использовать где угодно.

Проверялось на Android 2.3.3, но по идее, должно работать и с Android 2.0.
Ибо ниже просто нет аккаунтов.

##Использование
Чтобы запросить токен из своего приложения, нужно запустить для получения результата `TabunTokenGetterActivity`:

```java
   startActivityForResult(new Intent("everypony.tabun.auth.TOKEN_REQUEST"), TOKEN_REQUEST_CODE);;
```

В onActivityResult придёт уже проверенный токен под ключом `TabunAccount.COOKIE_TOKEN_TYPE`, который можно передать в создание `AccessProfile.parseString()`.
Он имеет вид `tabun.everypony.ru:80@Cookies`. 
