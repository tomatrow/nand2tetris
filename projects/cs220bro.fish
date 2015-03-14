#! /usr/local/bin/fish

set buildDirectory "bin"
set mainClassName "Driver"
set sourceDirectory "src"
set classPath "./lib/*" # all the .jars 

function cs220bro -d "CS220 Build and Run, bro." -a option projectName
    set argCount (count $argv)
    if test $argCount -gt 2
        set javaArgs $argv[3..$argCount]
    else if test $argCount -lt 2
        echo "Not enough arguments."
        return 1
    end

    if test $option = "build"
        echo building...
        
        set files (find $sourceDirectory | grep -e '.*'$projectName/'.*\.java')
        set classPath $classPath:./$sourceDirectory

        for javaFile in $files 
            javac -verbose -d $buildDirectory -cp $classPath $javaFile 
        end 
    end
    
    if test $option = "run"
        echo running...

        set mainClassFile (find $buildDirectory | grep -e '.*'$projectName'/'$mainClassName'.class')
        set classPath $classPath:./$buildDirectory 

        # cut out the '.class' at the end and the './bin/' at the beginning 
        set trimmedMainClassFile (echo $mainClassFile | sed "s/.class\$//" | sed "s/^bin\///" ) 

        java -classpath $classPath $trimmedMainClassFile $javaArgs
    end 
end 

cs220bro $argv
