#!/bin/sh

for test in `find ../../../../../../samples/input/sadx -name "*.xml"` 
do
	name=`basename $test | sed -e "s/.xml//"`
	#cp NarratorSADX.java NarratorSADX-$name.java
	sed -e "s/%SADX%/$name/" NarratorSADX.template > NarratorSADX_$name.java
	echo $name
done