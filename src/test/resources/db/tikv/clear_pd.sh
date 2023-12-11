WORKSPACE=$(dirname $(readlink -f "$0") || (cd "$(dirname "$0")";pwd))
cd $WORKSPACE
rm -rf pd_data
rm pd.log
rm start_pd.out