TaipeiTechApp
====

這是我自己的最後一版，歡迎有興趣的人繼續開發下去。20161120

[Play Store上的介紹](https://play.google.com/store/apps/details?id=com.taipeitech)
----

歡迎加入國立臺北科技大學！  
開始上課了，想查上課時間、地點、修課學分，這個App幫你到校園入口網站抓你想要的資訊，有任何想法都可以告訴我！

####App功能：

1.活動報報

讓社團及系學會發布活動資訊進行宣傳

2.行事曆

更新、離線瀏覽該學年度行事曆

3.課表查詢

讓在校生查詢學生學期課表，並提供離線瀏覽，與Widget結合在桌面瀏覽

4.學分計算

個人歷年學分計算及畢業學分標準查詢，提供博雅修課狀況瀏覽，可調整課程類別模擬學分數


本App非官方授權，請自行斟酌。

99 CSIE Alan Chiou 20150227

檔案介紹
----
Document資料夾內放著以下檔案

1. 過去每一版Release的Apk

2. 每次更新活動報報的檔案(圖檔和App讀取的格式)

3. App讀取的行事曆的格式

4. 學分計算與離線課表的教學圖檔

5. 各種icon的檔案

除了Ducument資料夾以外的檔案都是Android Studio Project檔案，直接下載Zip檔案大約50.8 MB。

單純程式碼的話只有1.3 MB。

已知的 Issue
----

1\. App本身在[AndroidManifest.xml](taipeiTech/src/main/AndroidManifest.xml)宣告了

`android:installLocation="auto"`

如果App被安裝到外部儲存空間，會造成部分功能無法使用，例如：課表Widget、活動報報的活動通知。

參考官方說明 https://developer.android.com/guide/topics/data/install-location.html#ShouldNot
