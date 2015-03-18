#! /usr/local/bin/fish


./cs220bro.fish build project07and08

set -l VMDirectories 07 08

for i in $VMDirectories
    for s in (ls $i)
        for g in (ls $i/$s)
            set vmUnitTestDirectories $vmUnitTestDirectories $i/$s/$g
        end
    end
end

set fullResult "Success"
set errorFileName '/tmp/cs220testError.tmp'
if not test -e "$errorFileName"
    mktemp $errorFileName
end 

for dir in $vmUnitTestDirectories
    set unitTestName (basename $dir)
    set testScript $dir'/'$unitTestName'.tst'
    ./cs220bro.fish run project07and08 $vmUnitTestDirectories 
    set result (CPUEmulator.sh $testScript ^ $errorFileName) 
    set error (cat $errorFileName)

    if test -n "$error"
        echo 'Failure: '$dir'/'$unitTestName
        echo '    Error: '$error
        set fullResult "Failure"
    end 
end

echo 'Final Result: '$fullResult
