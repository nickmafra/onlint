@echo off
if not DEFINED IS_MINIMIZED set IS_MINIMIZED=1 && start "Onlint client console" /min "%~dpnx0" %* && exit
for %%f in (target/onlint-awt-*.jar) do (
  @echo on
  java -jar target/%%f
  pause
)
exit