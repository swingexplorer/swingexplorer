:: swexpl.bat - launch a program with Swing Explorer attached
::
::   swexpl [--classpath <path>] [--no-agent] <main_class> [<user_program_args> ...]
::
:: This tool launches a user Java program with Swing Explorer attached. The
:: user program is specified using the classpath and main class name.
::
::   * --classpath <path>, -cp <path>
::
::      Defines the classpath for user classes. This is where you specify the
::      paths to your program's Java libraries (JARs). This does not have to
::      include the Swing Explorer JARs; they will be added automatically by
::      swexpl.
::
::      As an alternative, you may specify your class path in the CLASSPATH
::      environment variable.
::
::      The --classpath option takes precedence over the CLASSPATH environment
::      variable.
::
::   * --no-agent
::
::      Launches your program without the Swing Explorer agent enabled. This disables
::      some of its EDT diagnostic features.
::
::   * <main_class>
::
::      The fully-qualified name of the main Java class in your program. This is the
::      class that defines the main() method that you want to run.
::
::   * <user_program_args>
::
::      Additional arguments to pass on to the user program's main() method.

@echo off

set user_classpath=
set use_agent=y
set user_mainclass=

set bin_dir=%~dp0
set bin_dir=%bin_dir:~0,-1%

pushd %bin_dir%
cd /d ..
set dist_dir=%cd%
set lib_dir=%dist_dir%\lib
set /p swexpl_version=<VERSION
popd

:Loop
set was_option=n
if "%1"=="" GOTO Continue
if "%1"=="--no-agent" (
    set use_agent=n
	set was_option=y
)
if "%1"=="--classpath" (
    set user_classpath=%2
	shift
	set was_option=y
)
if "%1"=="-cp" (
    set user_classpath=%2
	shift
	set was_option=y
)
if "%was_option%"=="n" (
    set user_mainclass=%1
)
shift
GOTO Loop
:Continue

set java=java

set swexpl_classpath=%dist_dir%\swingexplorer-core-%swexpl_version%.jar;%lib_dir%\swing-layout-1.0.3.jar
set agent_jar_file=%dist_dir%\swingexplorer-agent-%swexpl_version%.jar
set agent_classpath=%agent_jar_file%;%lib_dir%\javassist-3.12.1.GA.jar
set eff_classpath=%swexpl_classpath%
if "%user_classpath"=="" (
    :: do nothing
	set dummy=xxx
) else (
    set eff_classpath=%eff_classpath%;%user_classpath%
)

if "%use_agent%"=="y" (
    %java% -javaagent:%agent_jar_file% -Xbootclasspath/a:%agent_classpath% -cp %eff_classpath% org.swingexplorer.launcher %user_mainclass% %1 %2 %3 %4 %5 %6 %7 %8 %9
) else (
    %java% -cp "%eff_classpath%;%agent_classpath%" org.swingexplorer.Launcher %user_mainclass% %1 %2 %3 %4 %5 %6 %7 %8 %9
)
