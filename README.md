TabunAuth
=========
Небольшое приложение, отвечающее за хранение аккаунтов Табуна.

Использует libtabun, но возвращаемые ею токены можно использовать где угодно.

Проверялось на Android 2.3.3, но по идее, должно работать и с Android 2.0.
Ибо ниже просто нет аккаунтов.

##Использование
Чтобы запросить токен из своего приложения:

```java
try {
   // Запрашиваем токен
	startActivityForResult(new Intent("everypony.tabun.auth.TOKEN_REQUEST"), TOKEN_REQUEST_CODE);
} catch (ActivityNotFoundException e) {
	// Нет Табун.Auth, предлагаем скачать. 
	Intent download = new Intent(
		Intent.ACTION_VIEW,
		Uri.parse("https://play.google.com/store/apps/details?id=everypony.tabun.auth")
	);
	startActivity(download);
   finish();
}
```

Получать токен из запроса так:

```java
@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);

	if (requestCode == TOKEN_REQUEST_CODE){
		if (resultCode == RESULT_OK)
			String token = data.getStringExtra("everypony.tabun.cookie");
			// Радоваться токену
		if (resultCode == RESULT_CANCELED){
			// Что-то делать, когда токен не получен.
		}

	}

}
```