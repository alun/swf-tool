JARS=
for f in `ls $APPARAT_HOME/*.jar` ; do
	JARS="$JARS:$f"
done
JARS=${JARS:1}
