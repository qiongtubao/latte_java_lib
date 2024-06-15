
WORKSPACE=$(dirname $(readlink -f "$0") || (cd "$(dirname "$0")";pwd))
cd $WORKSPACE
chmod 777 pd-ctl
./pd-ctl $@