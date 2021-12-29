echo "checking for log4j vulnerability..."
OUTPUT="$(locate log4j|grep -v log4js)"
if [ "$OUTPUT" ]; then
  echo "[WARNING] maybe vulnerable, those files contain the name:"
  echo "$OUTPUT"
fi
OUTPUT="$(dpkg -l|grep log4j|grep -v log4js)"
if [ "$OUTPUT" ]; then
  echo "[WARNING] maybe vulnerable, dpkg installed packages:"
  echo "$OUTPUT"
fi
if [ "$(command -v java)" ]; then
  echo "java is installed, so note that Java applications often bundle their libraries inside jar/war/ear files, so there still could be log4j in such applications."
fi
echo "If you see no output above this line, you are safe. Otherwise check the listed files and packages."
