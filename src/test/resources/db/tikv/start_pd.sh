WORKSPACE=$(dirname $(readlink -f "$0") || (cd "$(dirname "$0")";pwd))
cd $WORKSPACE
nohup ./pd-server --name=pd  --config=./pd.toml --data-dir=./pd_data --client-urls="http://127.0.0.1:22379" --peer-urls="http://127.0.0.1:22380" --initial-cluster="pd=http://127.0.0.1:22380" --log-file=pd.log > start_pd.out 2>&1 &
