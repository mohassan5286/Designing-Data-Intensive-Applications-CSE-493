@echo off
setlocal enabledelayedexpansion

for /d %%D in (..\*) do (
    if exist "%%D\mvnw" (
        echo Starting service in %%D...
        start cmd /c "cd /d %%D && .\mvnw spring-boot:run"
    )
)

echo All services started.
