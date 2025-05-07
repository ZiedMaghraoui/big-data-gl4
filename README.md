copy batch dataset from host to container

    docker cp batch.csv hadoop-master:/root/batch.csv
package the class with its dependencies

    mvn clean package
copy resulting jar to container

    docker cp target\earthquake-batch-1.0-SNAPSHOT.jar hadoop-master:/root/earthquake-summary.jar
start hadoop (if not running)

    ./start-hadoop
load the batch dataset to hdfs

    hdfs dfs -mkdir -p input
    hdfs dfs -put batch.csv input
run task with spark

    spark-submit --class EarthquakeSummaryTask --master local --verbose earthquake-summary.jar input/batch.csv out-spark

check result

    hdfs dfs -ls out-spark

