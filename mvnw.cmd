@echo off
setlocal
set "MVNW_REPOURL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip"
set "MVNW_DIR=%~dp0.mvn\apache-maven-3.9.9"
set "MVNW_ZIP=%~dp0.mvn\apache-maven-3.9.9-bin.zip"
if not exist "%MVNW_DIR%\bin\mvn.cmd" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "New-Item -ItemType Directory -Force -Path '%~dp0.mvn' | Out-Null; Invoke-WebRequest -Uri '%MVNW_REPOURL%' -OutFile '%MVNW_ZIP%'; Expand-Archive -Force '%MVNW_ZIP%' '%~dp0.mvn'"
)
"%MVNW_DIR%\bin\mvn.cmd" %*
endlocal
