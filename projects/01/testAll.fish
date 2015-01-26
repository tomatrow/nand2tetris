#! /usr/local/bin/fish
for i in (ls *.tst); echo -n $i; HardwareSimulator.sh $i;end
