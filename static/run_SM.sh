#!/bin/bash

mkdir SM

for (( i=1; i<62;i++))
do
	echo "+Group=\"GRAD\"
+Project=\"ARCHITECTURE\"
+ProjectDescription=\"Test\"
universe=vanilla
coresize=0
getenv=true
Rank=Memory
notification=Error
input=
output=CONDOR.lru.OUT
error=CONDOR.lru.ERR
Log = CONDOR.lru.LOG
notify_user = kevinyantx@utexas.edu
initialdir = /scratch/cluster/akanksha/cbp_traces
executable = /scratch/cluster/akanksha/cbp_traces/SM${i}.sh
queue" >> SM/SM${i}.condor
	echo "#!/bin/bash
gunzip -k trainingTraces/SHORT_MOBILE-${i}.bt9.trace.gz
java Simulator trainingTraces/SHORT_MOBILE-${i}.bt9.trace trainingOutput/SHORT_MOBILE-${i}.txt" >> SM/SM${i}.sh
	chmod +x SM/SM${i}.sh
	
	#/lusr/opt/condor/bin/condor_submit SM/SM${i}.condor
done
