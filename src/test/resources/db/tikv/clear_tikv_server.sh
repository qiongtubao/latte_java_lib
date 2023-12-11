WORKSPACE=$(dirname $(readlink -f "$0") || (cd "$(dirname "$0")";pwd))
cd $WORKSPACE
rm -rf tikv$1_data
rm -rf tikv$1.log
rm -rf start_tikv_server$1.out