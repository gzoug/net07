 cat * | egrep -o "^<results.*$" | sed -n -e "s/^.*exercise-id=\"\([0-9]*\).*received=\"\([^\"]*\)\".*student=\"\([0-9]*\)\".*$/\1\ \2\ \3/p;" | sort
