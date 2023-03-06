# telegram bot for getting actual SPb currencies

Bot name is "spbrates_bot"

### How to register new bot
Open telegram &rarr; find official @BotFather &rarr; /start &rarr; /newbot   
Then need to create new bot by name and username.   
Private store token from @BotFather.   

Better to load received token as user environment variable TOKEN.

### How to build
Just run on cmd: ```gradlew.bat build```

### How to run at IDE
Create spring boot configuration on IDE.   
User environment variable TOKEN could be defined additionally.   

### How to run at CMD
#### Create structure
Create working folder, e.g. "spbrates-bot-runner", and go into.   
Create sub-folders \bin and \libs.   
Copy project artifact "spbrates-bot-1.0.0-SNAPSHOT.jar" into \libs.   
Create \bin\start.bat with content:   
```
# update path to your local java with version at least 19
# set correct token value
set JAVA_PATH=C:\Program Files\Java\openjdk-19.0.2
set TOKEN=...
echo "Java is %JAVA_PATH%"
"%JAVA_PATH%\bin\java.exe" -jar ../libs/spbrates-bot-1.0.0-SNAPSHOT.jar
```

#### Run as application
Action: ```cd spbrates-bot-runner\bin && start.bat```   
Logs: Check "main.log" at \logs folder, e.g. "spbrates-bot-runner\logs\main.log"