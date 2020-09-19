@echo off
for %%f in (target/onlint-server-*.jar) do (
  @echo on
  java -jar target/%%f
  pause
)