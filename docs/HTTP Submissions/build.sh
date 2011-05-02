echo "<?xml version=\"1.0\"?>" > .db.xml 
echo "<db>" >> .db.xml
xml sel -t -m "/" -c "." *.xml >> .db.xml  
echo "</db>" >> .db.xml

xml sel -D  -I -E "iso8859-7" -t -m "//results" -s A:T:U "@submited" -c "." .db.xml | xml fo

