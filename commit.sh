#! /bin/bash
echo -n "Please tell me the commit message: "
read -e message
MSG="'${message}'"
git add config
git add src
COMMIT="git commit -m ${MSG}"
echo $COMMIT
git push origin master
