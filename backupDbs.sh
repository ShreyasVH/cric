ORIGINAL_PATH=$PATH

rm -rf $HOME/workspace/myProjects/cric-db-backups/cric.sql

MYSQL_PLAY_SOURCE_VERSION=$MYSQL_VERSION
MYSQL_PLAY_SOURCE_NAME=$MYSQL_DB_NAME
MYSQL_PLAY_SOURCE_PORT=$(grep -E '^ *port=' $HOME/workspace/myProjects/config-samples/mysql/$MYSQL_PLAY_SOURCE_VERSION/$OS/my.cnf | awk -F= '{print $2}' | tr -d ' ')
MYSQL_PLAY_SOURCE_SOCKET="$HOME/programs/mysql/$MYSQL_PLAY_SOURCE_VERSION/data/mysql_"$(echo "$MYSQL_PLAY_SOURCE_VERSION" | sed 's/\./_/g')".sock"
MYSQL_PLAY_SOURCE_PASSWORD=password
MYSQL_PLAY_SOURCE_USER=shreyas

export PATH=$HOME/programs/mysql/$MYSQL_PLAY_SOURCE_VERSION/bin:$ORIGINAL_PATH
printf "Creating backup for $MYSQL_PLAY_SOURCE_NAME\n"
export MYSQL_PWD=$MYSQL_PLAY_SOURCE_PASSWORD
mysqldump -P $MYSQL_PLAY_SOURCE_PORT -u $MYSQL_PLAY_SOURCE_USER $MYSQL_PLAY_SOURCE_NAME -S $MYSQL_PLAY_SOURCE_SOCKET > "$HOME/workspace/myProjects/cric-db-backups/$MYSQL_PLAY_SOURCE_NAME.sql"