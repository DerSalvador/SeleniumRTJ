call "Y:\Workspace\bin\setJavaHome.cmd"
set PROGRAM_PARAMS=-jar DownloadKosmosArchive.jar 
set PROGRAM_PARAMS=%PROGRAM_PARAMS% -versions 3.6.2/3.6.2.1, 3.6.11/3.6.11, 3.7/3.7 -downloadFolder w:\Download\KosmosArchives -KosmosLoginPage https://download.kosmos-banking.com
set VM_OPTIONS=-Dhttp.proxyHost=bcinternet.juliusbaer.com -Dhttp.proxyPort=8080 -Dwebdriver.chrome.chrome=org.openqa.selenium.chrome.ChromeDriver -Dwebdriver.chrome.bin="C:\Users\u36342\AppData\Local\Google\Chrome\Application\chrome.exe" -Dwebdriver.chrome.driver="Y:\Workspace\Chrome Selenium Driver\2.16 chromedriver_win32\chromedriver.exe"
java %VM_OPTIONS% %PROGRAM_PARAMS% 