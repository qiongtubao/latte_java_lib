for pid in `ps -ef | grep pd-server | grep -v grep | grep 12379 | awk '{print $2}'`
do
    kill -9 $pid
done
echo "pd stopped successfully!"